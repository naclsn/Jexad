package com.jexad.ops;

import com.jexad.base.*;
import java.util.ArrayList;

public class Split extends Lst<Buf> {

    public static final Fun fun = new Fun.ForClass(Split.class, "split on separator (exclusive); default is {'\\0'} ie C-string");

    ArrayList<Buf> al = new ArrayList();
    Buf under;
    Buf sep;

    public Split(Buf under, Buf sep) {
        this.under = under;
        this.sep = sep;
    }

    public Split(Buf under) { this(under, new Buf(new byte[] {0})); }

    @Override
    public Obj[] arguments() { return new Obj[] {under, sep}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        byte[] u = under.raw;
        if (0 == u.length) {
            arr = new Buf[0];
            return;
        }

        sep.update();
        byte[] d = sep.raw;

        al.clear();
        int last = 0;

        for (int i = 0; i < u.length - d.length + 1; i++) {
            boolean found = true;
            for (int j = 0; j < d.length; j++) {
                if (u[i+j] != d[j]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                int len = i - last;
                Buf b = new Buf(new byte[len]);
                System.arraycopy(u, last, b.raw, 0, len);

                al.add(b);
                i+= d.length;
                last = i;
                i--;
            }
        }

        int size = al.size();
        arr = new Buf[size+1];
        al.toArray(arr);

        int len = u.length - last;
        arr[size] = new Buf(new byte[len]);
        System.arraycopy(u, last, arr[size].raw, 0, len);
    }

    public static boolean test() {
        return Util.cmpLst
                ( new Split(new Buf(new byte[] {1, 2, 3, 0, 4, 5, 0, 6, 7, 8, 0, 9}))
                , new Lst(new Buf[]
                    { new Buf(new byte[] {1, 2, 3})
                    , new Buf(new byte[] {4, 5})
                    , new Buf(new byte[] {6, 7, 8})
                    , new Buf(new byte[] {9})
                    })
                )
            && Util.cmpLst
                ( new Split(new Buf(new byte[] {42, 12, 42, 12, 42, 12}), new Buf(new byte[] {42, 12}))
                , new Lst(new Buf[]
                    { new Buf(new byte[0])
                    , new Buf(new byte[0])
                    , new Buf(new byte[0])
                    , new Buf(new byte[0])
                    })
                )
            && Util.cmpLst
                ( new Split(new Buf(new byte[0]))
                , new Lst(new Buf[0])
                )
            ;
    }

}
