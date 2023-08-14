package com.jexad.ops.math;

import com.jexad.base.*;

public class Floor extends Num {

    public static final Fun fun = new Fun.ForClass(Floor.class, "todo");

    Num f;
    public Floor(Num f) { this.f = f; }

    @Override
    public Obj[] arguments() { return new Obj[] {f}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        f.update();

        dec = false;
        iv = f.dec ? f.dv.toBigInteger() : f.iv;
    }

}
