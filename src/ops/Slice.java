package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Fun;
import com.jexad.base.Util;

public class Slice extends Buf {

    public static final Fun fun = new Fun.ForClass(Slice.class, "[begin:end], defaults are begin=0 and end=length; begin and end can be <0");

    Buf under;
    Num begin;
    Num end; // nullable, in which case uses length

    public Slice(Buf under, Num begin, Num end) {
        this.under = under;
        this.begin = begin;
        this.end = end;
    }

    public Slice(Buf under, Num begin) { this(under, begin, null); }
    public Slice(Buf under) { this(under, new Num(0)); }

    @Override
    public Obj[] arguments() {
        return null == end
            ? new Obj[] {under, begin}
            : new Obj[] {under, begin, end}
            ;
    }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        int ulen = under.raw.length;
        int pbegin, pend;

        begin.update();
        pbegin = begin.val < 0 ? ulen + begin.val : begin.val;
        if (null != end) {
            end.update();
            pend = end.val < 0 ? ulen + end.val : end.val;
        } else pend = ulen;

        int len = pend - pbegin;
        raw = new byte[len];
        System.arraycopy(under.raw, pbegin, raw, 0, len);
    }

    public static boolean test() {
        return Util.cmpBuf
                ( new Slice(new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}))
                , new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9})
                )
            && Util.cmpBuf
                ( new Slice(new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), new Num(2), new Num(6))
                , new Buf(new byte[] {3, 4, 5, 6})
                )
            && Util.cmpBuf
                ( new Slice(new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), new Num(2), new Num(-3))
                , new Buf(new byte[] {3, 4, 5, 6})
                )
            ;
    }

}
