package com.jexad.ops.math;

import com.jexad.base.*;

public class Div extends Num {

    public static final Fun fun = new Fun.ForClass(Div.class, "divide tow numbers (first by second)");

    Num l, r;
    public Div(Num l, Num r) {
        this.l = l;
        this.r = r;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {l, r}; }

    @Override
    public void update() {
        Num.MaybePromoted m = new Num.MaybePromoted(l, r);
        if (dec = m.dec) dv = m.dvs[0].divide(m.dvs[1]);
        else iv = m.ivs[0].divide(m.ivs[1]);
    }

}
