package com.jexad.inter;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    public static class LangException extends Exception {

        char[] s;
        int i;

        LangException(String message, char[] s, int i) {
            super(message);
            this.s = s;
            this.i = i < s.length ? i : s.length;
        }

        int[] getLineCol() {
            int l = 1, c = 1;
            for (int k = 0; k < i; k++) {
                c++;
                if ('\n' == s[k]) {
                    l++;
                    c = 1;
                }
            }
            return new int[] {l, c};
        }

    }

    public static interface Lookup { public Class lookup(String name); }

    public static class LookupClassesUnder implements Lookup {
        String base;
        public LookupClassesUnder(String base) { this.base = base + "."; }
        public Class lookup(String name) {
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
            try { return Class.forName(base + new String(a, 0, a.length-w)); }
            catch (Exception e) { return null; }
        }
    }

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

    public Class tryLookup(String name) {
        for (int k = lookups.length-1; k >= 0; k--) {
            Class r = lookups[k].lookup(name);
            if (null != r) return r;
        }
        return null;
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
        int a = ++i;
        while (i < s.length) {
            if ('\\' == s[i]) i++;
            else if ('"' == s[i])
                return Buf.encode(new String(s, a, i++-a)
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

    // <num> ::= /0x[0-9A-Fa-f_]+|0o[0-8_]+|0b[01_]+|[0-9_](\.[0-9_])?|'.'/
    Num scanNum() throws LangException {
        int b = 10;
        if ('\'' == s[i]) {
            if (i+2 < s.length || '\'' != s[i+2])
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
                if (++i >= s.length)
                    fail("missing digit after base hint '0"+s[i-1]+s[i]+"'");
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
                case  2: k = k || '0' == c || '1' == c;
            }
            if (!k) break;
            i++;
        }
        int d = 0;
        if (10 == b && i < s.length && '.' == s[i]) {
            d = i++;
            if (i < s.length) fail("missing digit after decimal separator");
            while (i < s.length) {
                char c = s[i];
                if (c < '0' || '9' < c) break;
                i++;
            }
        }
        // TODO: handle '_'s (maybe?) and floats
        return new Num(Integer.parseInt(new String(s, a, i-a), b));
    }

    // <lst> ::= '{' {<expr> ','} '}'
    Lst scanLst() throws LangException {
        ArrayList<Obj> l = new ArrayList();
        while (i < s.length && '}' != s[i]) {
            Object w = processExpr(true);
            if (w instanceof Obj) l.add((Obj)w);
            else fail("lists of other than objects are not supported");
            skipBlanks();
            if (i >= s.length || ',' != s[i]) break;
            skipBlanks();
        }
        if (i >= s.length) fail("missing matching closing {} in list");
        if (0 == l.size()) fail("empty list are not supported");
        Class o = l.get(0).getClass();
        Obj[] r = new Obj[l.size()];
        l.toArray(r);
        return new Lst
            ( Buf.class.isAssignableFrom(o) ? Buf.class
            : Num.class.isAssignableFrom(o) ? Num.class
            : Lst.class.isAssignableFrom(o) ? Lst.class
            : null, r);
    }

    // <name> ::= /[a-z_][0-9a-z_]+/
    String scanName() throws LangException {
        int a = i;
        char c;
        if (i >= s.length) fail("expected name");
        if ('_' != (c = s[i]) && (c < 'a' || 'z' < c))
            fail("expected name to start with a-z_, got '"+c+"'");
        while (++i < s.length && ('_' == (c = s[i]) || 'a' <= c && c <= 'z')) ;
        return new String(s, a, i-a);
    }

    // <atom> ::
    //   = <str>
    //   | <num>
    //   | <lst>
    //   | <name>
    //   | '(' <expr> ')'
    Object scanAtom() throws LangException {
        if (i >= s.length) fail("expected atom");
        char c = s[i];
        switch (s[i]) {
            case '"': return scanStr();
            case '{': return scanLst();
            case '(':
                i++;
                Object r = processExpr(true);
                skipBlanks();
                if (i >= s.length || ')' != s[i])
                    fail("missing matching closing () in atom");
                i++;
                return r;
        }
        if ('0' <= c && c <= '9') return scanNum();
        if ('_' == c || 'a' <= c && c <= 'z') {
            String n = scanName();
            Obj r1 = scope.get(n);
            if (null != r1) return r1;
            Class r2 = tryLookup(n);
            if (null != r2) return r2;
            fail("unknown name: '"+n+"'");
        }
        fail("expected atom, got '"+c+"'");
        return null;
    }

    // <unop> ::= '+' | '-' | '!' | '~'
    // <binop> ::= ',' | '+' | '-' | '*' | '/' | '**' | '//' | '%' | '&' | '|' | '^' | '&&' | '||' | '^^' | '==' | '!=' | '<=' | '>=' | '<' | '>'
    // YYY: dead code
    int scanOp() throws LangException {
        char c = s[i++];
        switch (c) {
            case ',':
            case '+':
            case '-':
            case '%':
            case '~':
                return c;
            case '*':
            case '/':
            case '&':
            case '|':
            case '^':
                return i < s.length && c == s[i]
                    ? c | (s[i++] << 8)
                    : c;
            case '!':
            case '<':
            case '>':
                return i < s.length && '=' == s[i]
                    ? c | (s[i++] << 8)
                    : c;
            case '=':
                if (i < s.length && '=' == s[i])
                    return c | (s[i++] << 8);
        }
        fail("unexpected operator '"+c+"'");
        return 0;
    }

    // <script> ::= <name> '=' <expr> {';' <name> '=' <expr>} [';']
    void processScript() throws LangException {
        skipBlanks();
        do {
            String n = scanName();
            skipBlanks();
            if (i >= s.length || '=' != s[i]) fail("expected '=' after name in statement");
            i++;
            skipBlanks();
            Object w = processExpr(true);
            if (w instanceof Obj) scope.put(n, (Obj)w);
            else fail("storing other than object is not supported");
            if (i >= s.length) break;
            skipBlanks();
            if (';' != s[i]) fail("expected ';' after statement");
            i++;
            skipBlanks();
        } while (i < s.length);
    }

    // <expr> ::
    //   = <atom>
    //   | <call>
    // <call> ::= <name> {<expr>}
    Object processExpr(boolean exprStart) throws LangException {
        if (i >= s.length) fail("expected expression");
        int a = i;
        Object r = scanAtom();
        if (r instanceof Obj || !exprStart) return r;
        // else Class
        skipBlanks();
        ArrayList<Object> l = new ArrayList();
        char c;
        while (i < s.length && ';' != (c = s[i]) && ')' != c && ',' != c && '}' != c) {
            l.add(processExpr(false));
            skipBlanks();
        }
        //for (int k = 0; k < l.size(); k++) System.out.println(" - " + l.get(k));
        Object[] g = new Object[l.size()];
        l.toArray(g);
        Class[] gcl = new Class[g.length];
        for (int k = 0; k < g.length; k++) {
            gcl[k]
                = g[k] instanceof Buf ? Buf.class
                : g[k] instanceof Num ? Num.class
                : g[k] instanceof Lst ? Lst.class
                : Class.class;
        }
        try {
            Object o = ((Class)r).getConstructor(gcl).newInstance((Object[])g);
            if (o instanceof Obj) return o;
            return new Num(0);
        } catch (Exception e) {
            Throwable t = e;
            while (null != t.getCause()) t = t.getCause();
            fail("java exception in call expression: " + t);
        }
        return null; // unreachable
    }

}
