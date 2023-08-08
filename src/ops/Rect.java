package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Fun;
import com.jexad.base.Util;

public class Rect extends Lst<Buf> {

    public static final Fun fun = new Fun.ForClass(Rect.class, "slices at regular interval into a list of same-size buffers with optional padding");

    Buf under;
    Num it_len;
    Num it_pad;

    public Rect(Buf under, Num it_len, Num it_pad) {
        super(Buf.class);
        this.under = under;
        this.it_len = it_len;
        this.it_pad = it_pad;
    }

    public Rect(Buf under, Num it_len) { this(under, it_len, new Num(0)); }

    @Override
    public Obj[] arguments() { return new Obj[] {under, it_len, it_pad}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        it_len.update();
        it_pad.update();

        int w = it_len.val + it_pad.val;
        int count = under.raw.length / w;
        arr = new Buf[count];
        for (int k = 0; k < count; k++) {
            arr[k] = new Buf(new byte[it_len.val]);
            System.arraycopy(under.raw, w*k, arr[k].raw, 0, arr[k].raw.length);
        }
    }

    public static boolean test() {
        return Util.cmpLst
                ( new Rect(new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), new Num(3))
                , new Lst(Buf.class, new Buf[]
                    { new Buf(new byte[] {1, 2, 3})
                    , new Buf(new byte[] {4, 5, 6})
                    , new Buf(new byte[] {7, 8, 9})
                    })
                )
            && Util.cmpLst
                ( new Rect(new Buf(new byte[0]), new Num(3))
                , new Lst(Buf.class, new Buf[0])
                )
            && Util.cmpLst
                ( new Rect(new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), new Num(2), new Num(1))
                , new Lst(Buf.class, new Buf[]
                    { new Buf(new byte[] {1, 2})
                    , new Buf(new byte[] {4, 5})
                    , new Buf(new byte[] {7, 8})
                    })
                )
            ;
    }

}
