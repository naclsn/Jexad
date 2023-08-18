package com.jexad.ops;

import com.jexad.base.*;

public class Join extends Buf {

    public static final Fun fun = new Fun.ForClass(Join.class, "join with separator; default is \"\\0\" ie C-string");

    Lst list;
    Buf sep;

    public Join(Lst list, Buf sep) {
        this.list = list;
        this.sep = sep;
        init();
    }

    public Join(Lst list) { this(list, new Buf(new byte[] {0})); }

    @Override
    public Obj[] arguments() { return new Obj[] {list, sep}; }

    @Override
    public void update() {
        int list_len = list.arr.length;

        if (0 == list_len) {
            raw = new byte[0];
            return;
        }

        Buf first = list.arr[0].<Buf>as("item 0");

        int total_len = first.raw.length;
        for (int k = 1; k < list_len; k++)
            total_len+= sep.raw.length + list.arr[k].<Buf>as("item %d", k).raw.length;
        raw = new byte[total_len];

        int at = first.raw.length;
        System.arraycopy(first.raw, 0, raw, 0, at);

        for (int k = 1; k < list_len; k++) {
            System.arraycopy(sep.raw, 0, raw, at, sep.raw.length);
            at+= sep.raw.length;

            Buf it = (Buf)list.arr[k];
            int len = it.raw.length;
            System.arraycopy(it.raw, 0, raw, at, len);
            at+= len;
        }
    }

    public static boolean test() {
        return Util.cmpBuf
                ( new Join(new Lst(new Buf[]
                    { new Buf(new byte[] {1, 2, 3})
                    , new Buf(new byte[] {4, 5})
                    , new Buf(new byte[] {6, 7, 8})
                    , new Buf(new byte[] {9})
                    }))
                , new Buf(new byte[] {1, 2, 3, 0, 4, 5, 0, 6, 7, 8, 0, 9})
                )
            && Util.cmpBuf
                ( new Join(new Lst(new Buf[]
                    { new Buf(new byte[0])
                    , new Buf(new byte[0])
                    , new Buf(new byte[0])
                    , new Buf(new byte[0])
                    }), new Buf(new byte[] {42, 12}))
                , new Buf(new byte[] {42, 12, 42, 12, 42, 12})
                )
            && Util.cmpBuf
                ( new Join(new Lst(new Buf[0]))
                , new Buf(new byte[0])
                )
            && Util.cmpBuf
                ( new Join(new Lst(new Buf[] { new Buf(new byte[0]) }))
                , new Buf(new byte[0])
                )
            ;
    }

}
