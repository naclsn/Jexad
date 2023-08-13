package com.jexad.inter;

import com.jexad.base.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    public static class LangException extends Exception {

        private char[] s;
        private int i;

        private LangException(String message, char[] s, int i) {
            super(message);
            this.s = s;
            this.i = i < s.length ? i : s.length;
        }

        // {lineNr, columnNr, lineStartOff, lineEndOff}
        int[] getLineInfo() {
            int l = 1, c = 1;
            int st = 0, ed = 0;
            for (int k = 0; k < i; k++) {
                c++;
                if ('\n' == s[k]) {
                    l++;
                    c = 1;
                    st = k+1;
                }
            }
            for (int k = i; k < s.length; k++) {
                if ('\n' == s[k]) {
                    ed = k;
                    break;
                }
            }
            return new int[] {l, c, st, ed <= st ? s.length : ed};
        }

        public void printLocationInfo(PrintStream o) {
            int[] i = getLineInfo();
            o.printf("ERROR %s:%d:%d\n", "<script>", i[0], i[1]);
            o.printf("%4d | %s\n", i[0], new String(s, i[2], i[3]-i[2]));
            o.printf("%4d | %"+i[1]+"s\n", i[0]+1, "^");
        }

    } // class LangException

    public static interface Lookup {

        Fun lookup(String name);
        //Fun[] known();

        public static class ClassesUnder implements Lookup {

            String base;

            public ClassesUnder(String base) { this.base = base + "."; }

            public Fun lookup(String name) {
                char[] a = name.toCharArray();
                boolean f = true;
                int w = 0;
                for (int k = 0; k < a.length-w; k++) {
                    char c = a[k];
                    if (f) {
                        if ('a' <= c && c <= 'z') a[k]-= 'a'-'A';
                        f = false;
                    } else if ('_' == c) {
                        w++;
                        System.arraycopy(a, k+1, a, k, a.length-k-1);
                        k--;
                        f = true;
                    }
                }
                try {
                    Class cl = Class.forName(base + new String(a, 0, a.length-w));
                    return (Fun)cl.getField("fun").get(null);
                } catch (Exception e) { return null; }
            }

        } // class ClassesUnder

    } // interface Lookup

    public Map<String, Obj> scope;
    public Lookup[] lookups;
    public Obj obj;

    public Lang(String script, Lookup[] lookups, Map<String, Obj> scope) throws LangException {
        this.scope = null == scope ? new HashMap() : scope;
        this.lookups = lookups;

        s = script.toCharArray();
        i = 0;

        processScript();
        obj = scope.get("return");
    }

    char[] s;
    int i;

    void fail(String message) throws LangException { throw new LangException(message, s, i); }

    void skipBlanks() {
        while (i < s.length) {
            char c = s[i];
            if ('#' == c) while (++i < s.length && '\n' != s[i]) ;
            else if ('\t' != c && '\n' != c && '\r' != c && ' ' != c) break;
            i++;
        }
    }

    // <str> ::= '"' /[^"]/ '"'
    Buf scanStr() throws LangException {
        int a = i++;
        while (i < s.length) {
            // TODO: truly handle escapes, fail on unknown/erroneous ones
            if ('\\' == s[i]) i++;
            else if ('"' == s[i])
                return Buf.encode(new String(s, a+1, i++-a-1)
                    .replace("\\t", "\t")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\e", "\033")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\")
                );
            i++;
        }
        i = a;
        fail("missing end quote in string literal");
        return null;
    }

    // TODO: size specifiers (integral: [bsil], floating: [hfd])
    // <num> ::= /0x[0-9A-Fa-f_]+|0o[0-8_]+|0b[01_]+|[0-9_](\.[0-9_])?|'.'/ /[bsilhfd]/
    Num scanNum() throws LangException {
        int b = 10;
        if ('\'' == s[i]) {
            if (i+2 >= s.length || '\'' != s[i+2])
                fail("missing end quote in character literal");
            i+= 2;
            return new Num(s[i-1]);
        }
        if ('0' == s[i]) {
            if (++i >= s.length) return new Num(0);
            char c = s[i];
            if (c < '0' || '9' < c) {
                switch (c) {
                    case 'x': b = 16; break;
                    case 'o': b =  8; break;
                    case 'b': b =  2; break;
                    default: return new Num(0);
                }
                i++;
            }
        }
        int a = i;
        while (i < s.length) {
            char c = s[i];
            boolean k = false;
            switch (b) {
                case 16: k = 'A' <= c && c <= 'F' || 'a' <= c && c <= 'f';
                case 10: k = k || '8' == c || '9' == c;
                case  8: k = k || '2' <= c && c <= '7';
                case  2: k = k || '0' == c || '1' == c || '_' == c;
            }
            if (!k) break;
            i++;
        }
        if (a == i) fail("missing digit after base hint '0"+s[i-1]+"'");
        int d = 0;
        if (10 == b && i < s.length && '.' == s[i]) {
            d = ++i;
            while (i < s.length) {
                char c = s[i];
                if (c < '0' || '9' < c) break;
                i++;
            }
            if (d == i) fail("missing digit after decimal separator");
        }
        return new Num(Integer.parseInt(new String(s, a, i-a).replace("_", ""), b));
    }

    // <lst> ::= '{' [<atom> {',' <atom>}] '}'
    Lst scanLst() throws LangException {
        int a = i++;
        ArrayList<Obj> l = new ArrayList();
        skipBlanks();
        while (true) {
            if (i >= s.length) {
                i = a;
                fail("missing matching closing {} in list");
            }
            Obj w = scanAtom();
            l.add(w);
            skipBlanks();
            if (i >= s.length) {
                i = a;
                fail("missing matching closing {} in list");
            }
            if ('}' == s[i]) break;
            if (',' != s[i])
                fail("expected ',' between list elements, got '"+s[i]+"'");
            i++;
            skipBlanks();
        }
        i++;
        Obj[] r = new Obj[l.size()];
        l.toArray(r);
        return new Lst(r);
    }

    // <fun> ::= /[A-Z][0-9A-Z]+/
    Fun scanFun() throws LangException {
        int a = i;
        char c;
        while (++i < s.length &&
            (  '0' <= (c = s[i]) && c <= '9'
            || 'A' <= c && c <= 'Z'
            || 'a' <= c && c <= 'z'
            ));
        String name = new String(s, a, i-a);
        for (int k = lookups.length-1; k >= 0; k--) {
            Fun r = lookups[k].lookup(name);
            if (null != r) return r;
        }
        i = a;
        fail("unknown function '"+name+"'");
        return null;
    }

    // <sym> ::= ':' /[0-9A-Za-z_]+/
    Sym scanSym() throws LangException {
        int a = ++i;
        char c = ':';
        while (i < s.length &&
            ( '_' == (c = s[i])
            || '0' <= c && c <= '9'
            || 'A' <= c && c <= 'Z'
            || 'a' <= c && c <= 'z'
            )) i++;
        if (a == i) fail("expected symbol name to start with 0-9, A-Z, a-z or _, got '"+c+"'");
        return new Sym(new String(s, a, i-a));
    }

    // <var> ::= /[a-z_][0-9a-z_]+/
    String scanVarName() throws LangException {
        int a = i;
        char c;
        while (++i < s.length && ('_' == (c = s[i]) || 'a' <= c && c <= 'z'));
        return new String(s, a, i-a);
    }

    // <atom> ::= <str> | <num> | <lst> | <fun> | <sym> | <var> | '(' <expr> ')'
    Obj scanAtom() throws LangException {
        if (i >= s.length) fail("expected atom");
        int a = i;
        char c = s[i];
        switch (s[i]) {
            case '"': return scanStr();
            case '{': return scanLst();
            case ':': return scanSym();
            case '(':
                i++;
                Obj r = processExpr(true);
                skipBlanks();
                if (i >= s.length || ')' != s[i]) {
                    i = a;
                    fail("missing matching closing () in atom");
                }
                i++;
                return r;
        }
        if ('\'' == c || '0' <= c && c <= '9') return scanNum();
        if ('_' == c || 'a' <= c && c <= 'z') {
            String name = scanVarName();
            Obj r = scope.get(name);
            if (null == r) {
                i = a;
                fail("unknown variable '"+name+"'");
            }
            return r;
        }
        if ('A' <= c && c <= 'Z') return scanFun();
        fail("expected atom, got '"+c+"'");
        return null;
    }

    // <script> ::= <var> '=' <expr> {';' <var> '=' <expr>} [';']
    void processScript() throws LangException {
        skipBlanks();
        do {
            if (i >= s.length) fail("expected variable name");
            char c = s[i];
            if ('_' != c && (c < 'a' || 'z' < c))
                fail("expected variable name to start with a-z_, got '"+c+"'");
            String n = scanVarName();
            skipBlanks();
            if (i >= s.length || '=' != s[i]) fail("expected '=' after name in statement");
            i++;
            skipBlanks();
            Obj w = processExpr(true);
            scope.put(n, w);
            if (i >= s.length) break;
            int a = i;
            skipBlanks();
            if (';' != s[i]) {
                i = a;
                fail("expected ';' after statement");
            }
            i++;
            skipBlanks();
        } while (i < s.length);
    }

    // <expr> ::= <atom> | <fun> {<expr>} | <expr> ',' <expr>
    // FIXME: fixme
    Obj processExpr(boolean exprStart) throws LangException {
        if (i >= s.length) fail("expected expression");
        int a = i;
        Obj r = scanAtom();
        if (!exprStart || !(r instanceof Fun)) return r; // FIXME: ',' after a non-function atom (turn this condition the other way around)
        Fun f = (Fun)r;
        skipBlanks();
        ArrayList<Obj> l = new ArrayList();
        char c = 0;
        while (i < s.length && ';' != (c = s[i]) && ')' != c && ',' != c && '}' != c) {
            l.add(processExpr(false));
            skipBlanks();
        }
        Obj[] g = new Obj[l.size()];
        l.toArray(g);
        Class[] gcl = new Class[g.length];
        for (int k = 0; k < g.length; k++)
            gcl[k] = g[k].baseClass();
        try {
            Obj o = f.call(g);
            if (',' == c) {
                scope.put("_", o);
                i++;
                skipBlanks();
                return processExpr(true);
            }
            return o;
        } catch (Fun.InvokeException e) { // TODO: properly bubble whatever this is
            i = a;
            if (0 == g.length)
                fail("cannot call function with not argument");
            String args = "";
            for (int k = 0; k < gcl.length; k++)
                args+= (0 == k ? "" : ", ") + gcl[k].getName().substring(15);
            fail("cannot call function with arguments: "+args);
            return null; // unreachable
        }
    } // processExpr

}
