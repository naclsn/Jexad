package com.jexad.ops.math;

import com.jexad.base.*;

public class Trunc extends Num {

    public static final Fun fun = new Fun.ForClass(Trunc.class, "truncate a number (toward -inf), making it integral");

    Num f;
    public Trunc(Num f) { this.f = f; }

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
