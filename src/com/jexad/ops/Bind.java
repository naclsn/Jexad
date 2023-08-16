package com.jexad.ops;

import com.jexad.base.*;

public class Bind extends Fun {

    public static final Fun fun = new Fun.ForClass(Bind.class, "bind args to function, use :0 :1 and such for the arguments");

    Fun op;
    Lst bound;

    Class[][] ctors; // accessible ctors, with the bound arguments
    int ctors_count; // the len (past this is garbage)
    int sym_arg_count;

    public Bind(Fun op, Lst bound) throws InvokeException {
        this.op = op;
        this.bound = bound;

        // this is the only case where doing something at this point is safe; but at the
        // same time this should be the most common case, if not the only one: if not a
        // direct instance of Lst, it means the argument list is generated from
        // somewhere.. I don't recommend doing this too much and we can't give any hint
        // on typing (and none of what depends on it)
        // XXX: should it be moved into `overloads` directly?
        //      and check for uptodate flag on bound arguments?
        if (Lst.class == bound.getClass()) {
            ctors = op.overloads();
            int garbage = 0;
            for (int i = 0; i < ctors.length-garbage; i++) {
                if (ctors[i].length != bound.arr.length) {
                    // ctor not valid
                    ctors[i--] = ctors[ctors.length - ++garbage];
                    continue;
                }

                for (int j = 0; j < ctors[i].length; j++) {
                    Obj it = bound.arr[j];

                    if (it.baseClass() == ctors[i][j] || Obj.class == ctors[i][j])
                        continue;
                    if (it instanceof Sym) {
                        char c = ((Sym)it).str.charAt(0);
                        if ('0' <= c && c <= '9')
                            continue;
                    }

                    // else, ctor not valid
                    ctors[i--] = ctors[ctors.length - ++garbage];
                    break;
                } // for param
            } // for ctors
            ctors_count = ctors.length - garbage;

            if (0 == ctors_count) throw new InvokeException("no such overload for bound function");

            for (int n = '0'; n < '9'; n++) {
                boolean good = false;
                for (int k = 0; k < bound.arr.length; k++) {
                    Obj it = bound.arr[k];
                    if (it instanceof Sym && ((Sym)it).str.charAt(0) == n)
                        good = true;
                }
                if (!good) break;
                sym_arg_count++;
            }
        }

        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {op, bound}; }

    @Override
    public void update() {
        // XXX: ?
        // (updates on `op` will not be carried to the objects returned from `call`)
    }

    @Override
    public String help() { return "bound function: " + op.help(); }

    @Override
    public Class[][] overloads() {
        Class[][] r = new Class[ctors_count][];
        for (int i = 0; i < r.length; i++) {
            r[i] = new Class[sym_arg_count];

            for (char n = '0'; n < '0'+sym_arg_count; n++) {
                for (int j = 0; j < ctors[i].length; j++) {
                    Obj it = bound.arr[j];

                    if (it instanceof Sym && n == ((Sym)it).str.charAt(0)) {
                        r[i][n-'0'] = ctors[i][j];
                        break;
                    }
                } // for params
            } // for '0'..
        } // for ctors

        return r;
    }

    @Override
    public Class ret() { return op.ret(); }

    @Override
    public Obj call(Obj... args) throws InvokeException {
        int len = bound.length();
        Obj[] filled = new Obj[len];

        for (int k = 0; k < len; k++) {
            Obj it = bound.at(k);

            if (it instanceof Sym) {
                // YYY: for now let's settle with just 10 args...
                char ch = ((Sym)it).str.charAt(0);
                if ('0' <= ch && ch <= '9') {
                    int at = ch-'0';
                    it = args[at]; // XXX: errs and such...
                }
            }

            filled[k] = it;
        }

        return op.call(filled);
    }

}
