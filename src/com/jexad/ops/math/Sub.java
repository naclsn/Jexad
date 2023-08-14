package com.jexad.ops.math;

import com.jexad.base.*;

public class Sub extends Num {

    public static final Fun fun = new Fun.ForClass(Sub.class, "todo");

    Num l, r;
    public Sub(Num l, Num r) { this.l = l; this.r = r; }

    @Override
    public Obj[] arguments() { return new Obj[] {l, r}; }

    @Override
    public void update() {
        l.update();
        r.update();

        if (!l.dec && !r.dec) {
            dec = false;
            iv = l.iv.subtract(r.iv);
            return;
        }
    }

}
