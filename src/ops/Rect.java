package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Ops;
import com.jexad.ops.Slice;
import com.jexad.base.Util;

public class Rect extends Lst<Buf> implements Ops {

    public String getHelp() { return "slices at regular interval into a list of same-size buffers"; }

    Buf under;
    Num it_len;

    public Rect(Buf under, Num it_len) {
        super(Buf.class);
        this.under = under;
        this.it_len = it_len;
    }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        it_len.update();
        under.update();

        int len = it_len.val;
        int count = under.raw.length / len;
        arr = new Buf[count];
        for (int k = 0; k < count; k++) {
            arr[k] = new Slice(under, new Num(k * len), new Num((k+1) * len));
        }
    }

    public static boolean notest() {
        return false; // TODO: test
    }

}
