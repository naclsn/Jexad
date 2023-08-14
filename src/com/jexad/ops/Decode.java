package com.jexad.ops;

import com.jexad.base.*;
import java.math.BigInteger;

public class Decode extends Num {

    public static final Fun fun = new Fun.ForClass(Decode.class, "decode a number from bytes (default little endian) default interpreted as integral");

    Buf under;
    Num isdec;
    Endian endian;

    public enum Endian { LITTLE, BIG }

    public Decode(Buf under, Num isdec, Endian endian) {
        this.under = under;
        this.isdec = isdec;
        this.endian = endian;
    }

    public Decode(Buf under, Num isdec) { this(under, isdec, Endian.LITTLE); }
    public Decode(Buf under) { this(under, new Num(0)); }

    @Override
    public Obj[] arguments() { return new Obj[] {under}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        isdec.update();

        dec = 0 != isdec.asByte();

        if (dec) {
            System.err.println("Decode: no support for decimals as of now");
            return; // TODO: ?
        }

        else {
            byte[] m = under.raw;
            if (Endian.LITTLE == endian) {
                m = new byte[m.length];
                for (int k = 0; k < m.length; k++)
                    m[k] = under.raw[m.length-1-k];
            }
            iv = new BigInteger(1, m);
        }
    }

}
