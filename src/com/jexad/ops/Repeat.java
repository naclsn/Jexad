package com.jexad.ops;

import com.jexad.base.*;

public class Repeat extends Lst {

    public static final Fun fun = new Fun.ForClass(Repeat.class, "repeat the object");

    Obj under;
    Num count;

    public Repeat(Obj under, Num count) {
        this.under = under;
        this.count = count;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {under, count}; }

    @Override
    public void update() {
        arr = new Obj[count.asInt()];
        for (int k = 0; k < arr.length; k++)
            arr[k] = under;
    }

    public static boolean test() {
        return true;
    }

}
