package com.jexad.ops.math;

import com.jexad.base.*;

public class Add extends Num {

    public static final Fun fun = new Fun.ForClass(Add.class, "todo");

    Num l, r;
    public Add(Num l, Num r) { this.l = l; this.r = r; }

    @Override
    public Obj[] arguments() { return new Obj[] {l, r}; }

    @Override
    public void update() {
        l.update();
        r.update();

        if (!l.dec && !r.dec) {
            dec = false;
            iv = l.iv.add(r.iv);
            return;
        }
    }

}
