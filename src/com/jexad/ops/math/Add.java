package com.jexad.ops.math;

import com.jexad.base.*;

public class Add extends Num {

    public static final Fun fun = new Fun.ForClass(Add.class, "add two numbers");

    Num l, r;
    public Add(Num l, Num r) {
        this.l = l;
        this.r = r;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {l, r}; }

    @Override
    public void update() {
        Num.MaybePromoted m = new Num.MaybePromoted(l, r);
        if (dec = m.dec) dv = m.dvs[0].add(m.dvs[1]);
        else iv = m.ivs[0].add(m.ivs[1]);
    }

}
