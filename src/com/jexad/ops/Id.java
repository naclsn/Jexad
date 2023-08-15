package com.jexad.ops;

import com.jexad.base.*;

public class Id extends Fun {

    public static final Fun fun = new Id();

    @Override
    public String help() { return "the identitiy function"; }

    @Override
    public Class[][] overloads() {
        return new Class[][] {new Class[] {Obj.class}};
    }

    @Override
    public Class ret() { return Obj.class; }

    @Override
    public Obj call(Obj... args) throws Fun.InvokeException {
        if (1 == args.length)
            return args[0];

        throw new Fun.InvokeException("no such overload");
    }

    public static boolean test() throws Fun.InvokeException {
        Obj bidoof = new Num(42);
        return fun.call(bidoof) == bidoof;
    }

}
