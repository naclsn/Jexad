package com.jexad.inter;

import com.jexad.base.*;
import com.jexad.ops.Bind;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    public static class LangException extends Exception {

        private byte[] s;
        private int i;

        private LangException(String message, byte[] s, int i, Throwable cause) {
            super(message, cause);
            this.s = s;
            this.i = i < s.length ? i : s.length;
        }

        private LangException(String message, byte[] s, int i) {
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
        //String[] known();

        public static class ClassesUnder implements Lookup {

            String base;

            public ClassesUnder(String base) { this.base = base + "."; }

            public Fun lookup(String name) {
                try {
                    return (Fun)Class.forName(base+name).getField("fun").get(null);
                } catch (Exception e) {
                    return null;
                }
            }

        } // class ClassesUnder

    } // interface Lookup

    public Map<String, Obj> scope;
    public Lookup[] lookups;
    public Obj obj;

    public Lang(String script, Lookup[] lookups, Map<String, Obj> scope) throws LangException {
        this.scope = null == scope ? new HashMap<String, Obj>() : scope;
        this.lookups = lookups;

        try {
            s = script.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            s = script.getBytes();
            if (null == s) fail("encoding error when reading the source");
        }
        i = 0;

        processScript();
        obj = scope.get("return");
    }

    byte[] s;
    int i;

    void fail(String message, Throwable cause) throws LangException { throw new LangException(message, s, i, cause); }
    void fail(String message) throws LangException { throw new LangException(message, s, i); }

    void skipBlanks() {
        while (i < s.length) {
            byte c = s[i];
            if ('#' == c) while (++i < s.length && '\n' != s[i]) ;
            else if ('\t' != c && '\n' != c && '\r' != c && ' ' != c) break;
            i++;
        }
    }

    byte decodeEscape() throws LangException {
        if (++i >= s.length) fail("incomplete escape sequence");

        byte c = s[i];
        switch (c) {
            case 'a': return 0x07;
            case 'b': return 0x08;
            case 'e': return 0x1B;
            case 'f': return 0x0C;
            case 'n': return 0x0A;
            case 'r': return 0x0D;
            case 't': return 0x09;
            case 'v': return 0x0B;
            case '\\': return 0x5C;
            case '\'': return 0x27;
            case '"': return 0x22;

            case 'x':
                if (i+2 >= s.length) fail("incomplete escape sequence (must be 2 characters)");
                byte hi = s[++i];
                if (!( '0' <= hi && hi <= '9'
                    || 'A' <= hi && hi <= 'F'
                    || 'a' <= hi && hi <= 'f'
                    )) fail("expected hexadecimal digit in escape sequence");
                byte lo = s[++i];
                if (!( '0' <= lo && lo <= '9'
                    || 'A' <= lo && lo <= 'F'
                    || 'a' <= lo && lo <= 'f'
                    )) fail("expected hexadecimal digit in escape sequence");
                return (byte)(
                    ( (hi-(hi<='9'?'0':(hi<='F'?'A':'a')+10)) << 4 )
                    | (lo-(lo<='9'?'0':(lo<='F'?'A':'a')+10))
                    );

            case 'u':
                if (i+4 >= s.length) fail("incomplete escape sequence (must be 4 characters)");
                fail("NIY: unicode escapes");
                break;

            case 'U':
                if (i+8 >= s.length) fail("incomplete escape sequence (must be 8 characters)");
                fail("NIY: unicode escapes");
                break;

            default:
                if ('0' <= c && c <= '7') {
                    if (i+3 >= s.length) fail("incomplete escape sequence (must be 3 characters)");
                    fail("NIY: octal escapes");
                    return 0;
                }
                fail("unknown escape sequence character '"+(char)s[i]+"'");
        }
        return -1;
    } // decodeEscape

    // <str> ::= '"' /[^"]/ '"'
    Buf scanStr() throws LangException {
        int a = i++;

        ByteArrayOutputStream w = new ByteArrayOutputStream();
        int st = i, ln = 0;

        while (i < s.length) {
            if ('"' == s[i]) {
                w.write(s, st, ln);
                i++;
                return new Buf(w.toByteArray());
            }

            if ('\\' == s[i]) {
                w.write(s, st, ln);
                w.write(decodeEscape());
                st = i+1;
                ln = 0;
            } else ln++;

            i++;
        }
        i = a;
        fail("missing end quote in string literal");
        return null;
    }

    // <num> ::= ['-'] /0x[0-9A-Fa-f_]+|0o[0-8_]+|0b[01_]+|[0-9_](\.[0-9_])?|'.'/
    Num scanNum() throws LangException {
        boolean sign = '-' == s[i];
        if (sign && ++i >= s.length) fail("missing digit after '-'");

        if ('\'' == s[i]) {
            if (++i >= s.length) fail("missing end quote in character literal");

            int v = s[i];
            if ('\\' == v) v = decodeEscape();

            if (++i >= s.length || '\'' != s[i]) fail("missing end quote in character literal");
            i++;
            return new Num(sign ? -v : v);
        }

        int b = 10;

        if ('0' == s[i]) {
            if (++i >= s.length) return new Num(0);

            byte c = s[i];
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
            byte c = s[i];

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
        if (a == i) fail("missing digit after base hint '0"+(char)s[i-1]+"'");

        int d = 0;
        if (10 == b && i < s.length && '.' == s[i]) {
            d = ++i;
            while (i < s.length) {
                byte c = s[i];
                if (c < '0' || '9' < c) break;
                i++;
            }
            if (d == i) fail("missing digit after decimal separator");
        }

        String z = new String(s, a, i-a).replace("_", "");
        if (sign) z = '-'+z;
        return 0 == d
            ? new Num(new BigInteger(z, b))
            : new Num(new BigDecimal(z))
            ;
    }

    // <lst> ::= '{' [<atom> {',' <atom>}] '}'
    Lst scanLst() throws LangException {
        int a = i++;
        skipBlanks();
        if ('}' == s[i]) {
            i++;
            return new Lst();
        }

        ArrayList<Obj> l = new ArrayList<Obj>();
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
                fail("expected ',' between list elements, got '"+(char)s[i]+"'");

            i++;
            skipBlanks();
        }
        i++;

        Obj[] r = new Obj[l.size()];
        return new Lst(l.toArray(r));
    }

    // <fun> ::= /[A-Z][0-9A-Z]+/
    Fun scanFun() throws LangException {
        int a = i;
        byte c;
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
        byte c = ':';
        while (i < s.length &&
            (  '_' == (c = s[i])
            || '0' <= c && c <= '9'
            || 'A' <= c && c <= 'Z'
            || 'a' <= c && c <= 'z'
            )) i++;

        if (a == i) fail("expected symbol name to start with 0-9, A-Z, a-z or _, got '"+(char)c+"'");
        return new Sym(new String(s, a, i-a));
    }

    // <var> ::= /[a-z_][0-9a-z_]+/
    String scanVarName() throws LangException {
        int a = i;
        byte c;
        while (++i < s.length &&
            (  '_' == (c = s[i])
            || '0' <= c && c <= '9'
            || 'a' <= c && c <= 'z'
            ));
        return new String(s, a, i-a);
    }

    // <atom> ::= <str> | <num> | <lst> | <fun> | <sym> | <var> | '(' <expr> ')'
    // (ex) <atom> ::= ... | '(=' <math> ')' | '($' <bind> ')'
    Obj scanAtom() throws LangException {
        if (i >= s.length) fail("expected atom");

        int a = i;
        byte c = s[i];
        switch (s[i]) {
            case '"': return scanStr();
            case '{': return scanLst();
            case ':': return scanSym();
            case '(':
                switch (i+1 < s.length ? s[++i] : 0) {
                    case '=': return processExMath();
                    case '$': return processExBind();
                }

                Obj r = processExpr(true);
                skipBlanks();
                if (i >= s.length || ')' != s[i]) {
                    i = a;
                    fail("missing matching closing () in atom");
                }
                i++;
                return r;
        }

        if ('\'' == c || '-' == c || '0' <= c && c <= '9') return scanNum();

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

        fail("expected atom, got '"+(char)c+"'");
        return null;
    } // scanAtom

    // <script> ::= <var> '=' <expr> {';' <var> '=' <expr>} [';']
    void processScript() throws LangException {
        skipBlanks();

        do {
            if (i >= s.length) fail("expected variable name");
            byte c = s[i];
            if ('_' != c && (c < 'a' || 'z' < c))
                fail("expected variable name to start with a-z_, got '"+(char)c+"'");
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
    } // processScript

    // <expr> ::= <atom> | <fun> {<expr>} | <expr> ',' <expr>
    Obj processExpr(boolean exprStart) throws LangException {
        if (i >= s.length) fail("expected expression");

        int a = i;
        Obj r = scanAtom();

        if (!exprStart) return r;

        skipBlanks();

        ArrayList<Obj> l = new ArrayList<Obj>();
        byte c = 0;
        while (i < s.length && ';' != (c = s[i]) && ')' != c && ',' != c && '}' != c) {
            l.add(processExpr(false));
            skipBlanks();
        }

        if (0 < l.size()) {
            if (!(r instanceof Fun))
                fail("'"+r.baseClass().getSimpleName()+"' is not a function");
            Fun f = (Fun)r;

            Obj[] g = new Obj[l.size()];
            l.toArray(g);

            try {
                r = f.call(g);
            } catch (Fun.InvokeException e) {
                i = a;
                if (0 == g.length)
                    fail("could not call function with no argument", e);

                String args = "";
                for (int k = 0; k < g.length; k++)
                    args+= (0 == k ? "" : ", ") + g[k].baseClass().getSimpleName();
                fail("could not call function with arguments: "+args, e);

                return null; // unreachable
            }
        }

        if (',' == c) {
            scope.put("_", r);
            i++;
            skipBlanks();
            r = processExpr(true);
        }
        return r;
    } // processExpr

    // <math> ::
    //     = <unop> <atom>
    //     | <atom> <binop> <atom>
    //     | <atom> '?' <atom> ':' <atom>
    //     | <atom> '[' <atom> [':' [<atom>]] ']'
    // <unop> ::= '+' '-' '!' '~'
    // <binop> ::= '+' '-' '*' '/' '%' '//' '**' '==' '!=' '>' '<' '>=' '<=' '<=>' '&' '|' '^' '<<' '>>'
    Obj processExMath() throws LangException {
        if (++i >= s.length) fail("expected math expression");
        fail("NIY: (= )");
        return null;
    }

    // <bind> ::= <fun> {<atom>}
    Obj processExBind() throws LangException {
        int a = i-1;
        if (++i >= s.length) fail("expected bind expression");

        Obj obj = scanAtom();
        if (!(obj instanceof Fun))
            fail("expected a function, got '"+obj.baseClass().getSimpleName()+"'");
        Fun fun = (Fun)obj;
        skipBlanks();

        ArrayList<Obj> args = new ArrayList<Obj>();
        while (i < s.length && ')' != s[i]) {
            args.add(scanAtom());
            skipBlanks();
        }

        if (')' != s[i]) {
            i = a;
            fail("missing matching closing () in bind expression");
        }
        i++;

        Obj[] largs = new Obj[args.size()];
        try {
            return new Bind(fun, new Lst(args.toArray(largs)));
        } catch (Fun.InvokeException e) {
            fail("failed to construct bind expression", e);
        }
        return null;
    }

}
