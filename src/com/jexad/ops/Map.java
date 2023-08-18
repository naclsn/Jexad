package com.jexad.ops;

import com.jexad.base.*;

public class Map extends Lst {

    public static final Fun fun = new Fun.ForClass(Map.class, "makes a new list, applying the operation to each item");

    Fun op;
    Lst args;

    public Map(Fun op, Lst args) {
        this.op = op;
        this.args = args;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {op, args}; }

    @Override
    public void update() {
        arr = new Obj[args.arr.length];
        for (int k = 0; k < arr.length; k++) {
            try {
                arr[k] = op.call(new Obj[] {args.arr[k]});
            } catch (Exception e) {
                System.err.println("Map: " + e);
                return; // XXX: errs and such...
            }
        }
    }

}
