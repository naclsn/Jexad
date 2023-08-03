package com.jexad.inter;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    public class LangException extends Exception {

        char[] s;
        int i;

        LangException(String message, char[] s, int i) {
            super(message);
            this.s = s;
            this.i = i;
        }

        int[] getLineCol() {
            System.err.println("NIY: get line and col from source and index");
            return new int[] {0, 0};
        }

    }

    public interface Lookup { public Class lookup(String name); }

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
        int a = ++i;
        while (i < s.length) {
            if ('\\' == s[i]) i++;
            else if ('"' == s[i])
                return Buf.encode(new String(s, a, i++)
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
    Num scanNum() throws LangException { // XXX: int
        int b = 10;
        if ('\'' == s[i]) {
            if (i+2 < s.length || '\'' != s[i+2])
                fail("missing end quote in character literal");
            i+= 2;
            return new Num(s[i-1]);
        }
        if ('0' == s[i]) {
            if (++i < s.length) return new Num(0);
            char c = s[i];
            if (c < '0' || '9' < c) {
                switch (c) {
                    case 'x': b = 16; break;
                    case 'o': b =  8; break;
                    case 'b': b =  2; break;
                    default: return new Num(0);
                }
                if (++i < s.length)
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
        return new Num(Integer.parseInt(new String(s, a, i), b));
    }

    // <lst> ::= '{' {<expr> ','} '}'
    Lst scanLst() throws LangException {
        ArrayList<Obj> l = new ArrayList();
        while (i < s.length && '}' != s[i]) {
            l.add(processExpr());
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

    // <atom> ::
    //   = <str>
    //   | <num>
    //   | <lst>
    //   | <name>
    //   | '(' <expr> ')'
    Obj scanAtom() throws LangException {
        if (i >= s.length) fail("expected atom");
        char c = s[i];
        switch (s[i]) {
            case '"': return scanStr();
            case '{': return scanLst();
            case '(':
                Obj r = processExpr();
                skipBlanks();
                if (i >= s.length || ')' != s[i]) fail("missing matching closing () in atom");
                return r;
        }
        if ('0' <= c && c <= '9') return scanNum();
        if ('_' == c || 'a' <= c && c <= 'z') {
            String n = scanName();
            Obj r = scope.get(n);
            if (null == r) fail("name not in scope: '"+n+"'");
            return r;
        }
        fail("expected atom, got '"+c+"'");
        return null;
    }

    // <name> ::= /[a-z_]+/
    String scanName() {
        int a = 0;
        while (i < s.length) {
            char c = s[i];
            System.out.println("char: " + c);
            if ('_' != c || c < 'a' || 'z' < c) break;
            i++;
        }
        return new String(s, a, i);
    }

    // <unop> ::= '+' | '-' | '!' | '~'
    // <binop> ::= ',' | '+' | '-' | '*' | '/' | '**' | '//' | '%' | '&' | '|' | '^' | '&&' | '||' | '^^' | '==' | '!=' | '<=' | '>=' | '<' | '>'
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

    // <script> ::= {statement ';'}
    void processScript() throws LangException {
        skipBlanks();
        do {
            processStatement();
            skipBlanks();
            if (i >= s.length || ';' != s[i]) fail("expected ';' after statement");
            skipBlanks();
        } while (i < s.length);
    }

    // <statement> ::
    //   = <name> '=' <expr>
    //   | <call>
    // <call> ::= <name> ['.' <name>] {<expr>}
    void processStatement() throws LangException {
        String n = scanName();
        skipBlanks();
        if (i < s.length && '=' == s[i]) {
            i++;
            skipBlanks();
            scope.put(n, processExpr());
            return;
        }
        ArrayList<Obj> l = new ArrayList();
        while (i < s.length && ';' != s[i]) {
            l.add(processExpr());
            skipBlanks();
        }
        fail("NIY: call " + n + " with " + l.size() + " argument+s");
    }

    // <expr> ::
    //   = <atom>
    //   | <call>
    //   //| <unop> <expr>
    //   //| <expr> <binop> <expr>
    // <call> ::= <name> {<expr>}
    Obj processExpr() throws LangException {
        if (i >= s.length) fail("expected expression");
        int a = i;
        try { return scanAtom(); }
        catch (LangException e) { }
        i = a;
        int c = s[i];
        if ('_' == c || 'a' <= c && c <= 'z') {
            String n = scanName();
            skipBlanks();
            ArrayList<Obj> l = new ArrayList();
            while (i < s.length && ';' != (c = s[i]) && ')' != c && ',' != c && '}' != c) {
                l.add(processExpr());
                skipBlanks();
            }
            fail("NIY: call " + n + " with " + l.size() + " argument+s");
        }
        fail("expected expression, got '"+c+"'");
        return null;
    }

}
