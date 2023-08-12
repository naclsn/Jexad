package com.jexad.ops;

import com.jexad.base.*;

public class Fold extends Fun {

    public static final Fun fun = new Fold();

    @Override
    public String help() { return "fold/reduce a list with a binary operation, with optional initial element"; }

    @Override
    public Obj call(Obj... args) throws Fun.InvokeException {
        if ((2 == args.length || 3 == args.length)
                && args[0] instanceof Fun
                && args[args.length-1] instanceof Lst) {
            Fun op = (Fun)args[0];
            Lst lst = (Lst)args[args.length-1];

            op.update();
            lst.update();

            int k = 0;
            Obj it = 2 == args.length ? args[1] : lst.at(k++);

            for (; k < lst.length(); k++)
                it = op.call(it, lst.at(k));

            return it;
        }

        throw new Fun.InvokeException("wrong arguments and such (should be Fun,Lst or Fun,Obj,Lst)");
    }

    @Override
    public Class ret() { return Obj.class; }

}
