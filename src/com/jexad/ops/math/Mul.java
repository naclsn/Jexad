package com.jexad.ops.math;

import com.jexad.base.*;

public class Mul extends Num {

    public static final Fun fun = new Fun.ForClass(Mul.class, "multiply two numbers");

    Num l, r;
    public Mul(Num l, Num r) {
        this.l = l;
        this.r = r;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {l, r}; }

    @Override
    public void update() {
        Num.MaybePromoted m = new Num.MaybePromoted(l, r);
        if (dec = m.dec) dv = m.dvs[0].multiply(m.dvs[1]);
        else iv = m.ivs[0].multiply(m.ivs[1]);
    }

}
