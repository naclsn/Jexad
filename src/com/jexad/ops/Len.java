package com.jexad.ops;

import com.jexad.base.*;
import java.math.BigInteger;

public class Len extends Num {

    public static final Fun fun = new Fun.ForClass(Len.class, "length of a buffer");

    Buf buf;

    public Len(Buf buf) { this.buf = buf; }

    @Override
    public Obj[] arguments() { return new Obj[] {buf}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        buf.update();
        dec = false;
        iv = BigInteger.valueOf(buf.raw.length);
    }

    public static boolean test() {
        return Util.cmpNum
                ( new Len(new Buf(new byte[] {'c', 'o', 'u', 'c', 'o', 'u'}))
                , new Num(6)
                )
            && Util.cmpNum
                ( new Len(new Buf(new byte[0]))
                , new Num(0)
                )
            ;
    }

}
