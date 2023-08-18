package com.jexad.ops;

import com.jexad.base.*;

public class Rect extends Lst {

    public static final Fun fun = new Fun.ForClass(Rect.class, "slices at regular interval into a list of same-size buffers with optional padding");

    Buf under;
    Num it_len;
    Num it_pad;

    public Rect(Buf under, Num it_len, Num it_pad) {
        this.under = under;
        this.it_len = it_len;
        this.it_pad = it_pad;
        init();
    }

    public Rect(Buf under, Num it_len) { this(under, it_len, new Num(0)); }

    @Override
    public Obj[] arguments() { return new Obj[] {under, it_len, it_pad}; }

    @Override
    public void update() {
        int len = it_len.asInt();
        int w = len + it_pad.asInt();
        int count = under.raw.length / w;
        arr = new Buf[count];
        for (int k = 0; k < count; k++) {
            Buf b = new Buf(new byte[len]);
            arr[k] = b;
            System.arraycopy(under.raw, w*k, b.raw, 0, b.raw.length);
        }
    }

    public static boolean test() {
        return Util.cmpLst
                ( new Rect(new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), new Num(3))
                , new Lst(new Buf[]
                    { new Buf(new byte[] {1, 2, 3})
                    , new Buf(new byte[] {4, 5, 6})
                    , new Buf(new byte[] {7, 8, 9})
                    })
                )
            && Util.cmpLst
                ( new Rect(new Buf(new byte[0]), new Num(3))
                , new Lst(new Buf[0])
                )
            && Util.cmpLst
                ( new Rect(new Buf(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), new Num(2), new Num(1))
                , new Lst(new Buf[]
                    { new Buf(new byte[] {1, 2})
                    , new Buf(new byte[] {4, 5})
                    , new Buf(new byte[] {7, 8})
                    })
                )
            ;
    }

}
