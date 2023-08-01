package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Num;
import com.jexad.base.Ops;
import com.jexad.base.Util;

public class Slice extends Buf implements Ops {

    public String getHelp() { return "[begin:end], defaults are begin=0 and end=-1; only end can be <0"; }

    Buf under;
    Num begin;
    Num end;

    public Slice(Buf under, Num begin, Num end) {
        this.under = under;
        this.begin = begin;
        this.end = end;
    }

    public Slice(Buf under, Num begin) { this(under, begin, new Num(-1)); }
    public Slice(Buf under) { this(under, new Num(0)); }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        begin.update();
        end.update();
        under.update();

        int pend = end.val < 0 ? under.raw.length + 1 + end.val : end.val;
        int len = pend - begin.val;

        raw = new byte[len];
        System.arraycopy(under.raw, begin.val, raw, 0, len);
    }

    public static boolean notest() {
        return false; // TODO: test
    }

}
