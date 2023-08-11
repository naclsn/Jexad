package com.jexad.ops;

import com.jexad.base.*;

public class Bind extends Fun {

    public static final Fun fun = new Fun.ForClass(Bind.class, "bind args to function, use :0 :1 and such for the arguments");

    Fun op;
    Lst bound;

    public Bind(Fun op, Lst bound) {
        this.op = op;
        this.bound = bound;
    }

    @Override
    public Obj[] arguments() { return new Obj[] {op, bound}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        op.update();
        bound.update();
    }

    @Override
    public String help() { return "bound function"; }

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

    @Override
    public Class ret() { return op.ret(); }

}
