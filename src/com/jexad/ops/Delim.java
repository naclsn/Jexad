package com.jexad.ops;

import com.jexad.base.*;
import com.jexad.util.Util;

public class Delim extends Buf {

    public static final Fun fun = new Fun.ForClass(Delim.class, "slice until delim (default exclusive), default is {'\\0'} ie C-string");

    Buf under;
    Buf delim;
    Bound bound;

    public enum Bound { EXCLUSIVE, INCLUSIVE }

    public Delim(Buf under, Buf delim, Bound bound) {
        this.under = under;
        this.delim = delim;
        this.bound = bound;
        init();
    }

    public Delim(Buf under, Buf delim) { this(under, delim, Bound.EXCLUSIVE); }
    public Delim(Buf under) { this(under, new Buf(new byte[] {0})); }

    @Override
    public Obj[] arguments() { return new Obj[] {under, delim}; }

    @Override
    public void update() {
        byte[] u = under.raw;
        byte[] d = delim.raw;

        for (int i = 0; i < u.length - d.length; i++) {
            boolean found = true;
            for (int j = 0; j < d.length; j++) {
                if (u[i+j] != d[j]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                int len = Bound.INCLUSIVE == bound ? i+d.length : i;
                raw = new byte[len];
                System.arraycopy(u, 0, raw, 0, len);
                return;
            }
        }

        raw = u; // delim not found
    }

    public static boolean test() {
        Buf u = new Buf(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        return Util.cmpBuf(new Delim(u), new Buf(new byte[0]))
            && Util.cmpBuf(new Delim(u, new Buf(new byte[] {6, 7})), new Buf(new byte[] {0, 1, 2, 3, 4, 5}))
            && Util.cmpBuf(new Delim(u, new Buf(new byte[] {4, 5}), Bound.INCLUSIVE), new Buf(new byte[] {0, 1, 2, 3, 4, 5}))
            && Util.cmpBuf(new Delim(u, new Buf(new byte[] {8, 3})), u)
            ;
    }

}
