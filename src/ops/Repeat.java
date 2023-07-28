package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Ops;

@SuppressWarnings("unchecked")
public class Repeat<T extends Obj> extends Lst<T> implements Ops {

    public String getHelp() { return "repeat the object"; }

    T under;
    Num count;

    public Repeat(T under, Num count) {
        super(under instanceof Buf ? Buf.class
            : under instanceof Num ? Num.class
            : under instanceof Lst ? Lst.class
            : null);
        this.under = under;
        this.count = count;
    }

    @Override
    public void update() {
        under.update();
        count.update();

        arr = (T[])new Obj[count.val];
        for (int k = 0; k < arr.length; k++) {
            arr[k] = under;
        }
    }

}
