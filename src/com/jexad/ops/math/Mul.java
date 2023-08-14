package com.jexad.ops.math;

import com.jexad.base.*;

public class Mul extends Num {

    public static final Fun fun = new Fun.ForClass(Mul.class, "todo");

    Num l, r;
    public Mul(Num l, Num r) { this.l = l; this.r = r; }

    @Override
    public Obj[] arguments() { return new Obj[] {l, r}; }

    @Override
    public void update() {
        l.update();
        r.update();

        if (!l.dec && !r.dec) {
            dec = false;
            iv = l.iv.multiply(r.iv);
            return;
        }
    }

}
