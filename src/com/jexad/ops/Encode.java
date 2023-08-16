package com.jexad.ops;

import com.jexad.base.*;

public class Encode extends Buf {

    public static final Fun fun = new Fun.ForClass(Encode.class, "encode a number to bytes (default little endian) to the given byte width (truncates if needed)");

    Num under;
    Num width;
    Endian endian;

    public enum Endian { LITTLE, BIG }

    public Encode(Num under, Num width, Endian endian) {
        this.under = under;
        this.width = width;
        this.endian = endian;
        init();
    }

    public Encode(Num under, Num width) { this(under, width, Endian.LITTLE); }

    @Override
    public Obj[] arguments() { return new Obj[] {under}; }

    @Override
    public void update() {
        raw = new byte[width.asInt()];

        if (under.dec) {
            System.err.println("Encode: no support for decimals as of now");
            return; // TODO: ?
        }

        else {
            byte[] b = under.iv.toByteArray();
            if (Endian.LITTLE == endian) {
                for (int k = 0; k < b.length && k < raw.length; k++)
                    raw[k] = b[b.length-1-k];
            } else {
                int c = b.length < raw.length ? b.length : raw.length;
                System.arraycopy(b, b.length-c, raw, raw.length-c, c);
            }
        }
    }

}
