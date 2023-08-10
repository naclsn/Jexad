package com.jexad.ops;

import com.jexad.base.*;

public class At extends Fun {

    public static final Fun fun = new At();

    @Override
    public String help() { return "get element from list at index"; }

    @Override
    public Obj call(Obj... args) throws Fun.InvokeException {
        if (2 == args.length && args[0] instanceof Lst && args[1] instanceof Num) {
            Lst lst = (Lst)args[0];
            Num num = (Num)args[1];
            lst.update();
            num.update();
            return lst.at(num.val);
        }
        throw new Fun.InvokeException("wrong argument and such (should be Lst, Num)");
    }

    @Override
    public Class ret() { return Obj.class; }

}
