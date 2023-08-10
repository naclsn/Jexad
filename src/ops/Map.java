package com.jexad.ops;

import com.jexad.base.*;

public class Map<Out extends Obj> extends Lst<Out> {

    public static final Fun fun = new Fun.ForClass(Map.class, "makes a new list, applying the operation to each item");

    Fun op;
    Lst args;

    public Map(Fun op, Lst args) {
        this.op = op;
        this.args = args;
    }

    @Override
    public Obj[] arguments() { return new Obj[] {op, args}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        op.update();
        args.update();

        arr = (Out[])new Obj[args.length()];
        for (int k = 0; k < arr.length; k++) {
            try {
                arr[k] = (Out)op.call(new Obj[] {args.at(k)});
                arr[k].update();
            } catch (Exception e) {
                System.err.println("Map: " + e);
                return; // XXX: errs and such...
            }
        }
    }

}
