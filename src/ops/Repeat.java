package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Fun;

import com.jexad.base.Util;
@SuppressWarnings("unchecked")
public class Repeat<T extends Obj> extends Lst<T> {

    public static final Fun fun = new Fun.ForClass(Repeat.class, "repeat the object");

    T under;
    Num count;

    Repeat(T under, Num count) {
        super(under instanceof Buf ? Buf.class
            : under instanceof Num ? Num.class
            : under instanceof Lst ? Lst.class
            : null);
        this.under = under;
        this.count = count;
    }

    // needed for `getConstructor` resolutions...
    public Repeat(Buf under, Num count) { this((T)under, count); }
    public Repeat(Num under, Num count) { this((T)under, count); }
    public Repeat(Lst under, Num count) { this((T)under, count); }

    @Override
    public Obj[] arguments() { return new Obj[] {under, count}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        //under.update();
        count.update();

        arr = (T[])new Obj[count.val];
        for (int k = 0; k < arr.length; k++) arr[k] = under;
    }

    public static boolean test() {
        return true;
    }

}
