package com.jexad.ops.math;

import com.jexad.base.*;

public class Add extends Num {

    public static final Fun fun = new Fun.ForClass(Add.class, "todo");

    Num o;
    Num[] n;
    Add(Num o, Num... n) { this.o = o; this.n = n; }
    // for `getConstructor` resolution...
    public Add(Num a, Num b, Num c, Num d) { this(a, new Num[] {b, c, d}); }
    public Add(Num a, Num b, Num c) { this(a, new Num[] {b, c}); }
    public Add(Num a, Num b) { this(a, new Num[] {b}); }

    @Override
    public Obj[] arguments() {
        Num[] r = new Num[1+n.length];
        r[0] = o;
        System.arraycopy(n, 0, r, 1, n.length);
        return r;
    }

    @Override
    public void update() {
        o.update();
        val = o.val;
        for (int k = 0; k < n.length; k++) {
            n[k].update();
            val+= n[k].val;
        }
    }

}
