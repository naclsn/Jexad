package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import java.util.ArrayList;

public class Split extends Lst<Buf> {

    public String getHelp() { return "split on separator (exclusive); default is {'\\0'} ie C-string"; }

    ArrayList<Buf> al = new ArrayList();
    Buf under;
    Buf sep;

    public Split(Buf under, Buf sep) {
        super(Buf.class);
        this.under = under;
        this.sep = sep;
    }

    public Split(Buf under) { this(under, new Buf(new byte[] {0})); }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        sep.update();

        byte[] u = under.raw;
        byte[] d = sep.raw;

        al.clear();
        int last = 0;

        for (int i = 0; i < u.length - d.length; i++) {
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
            }
        }

        int len = d.length - last;
        Buf b = new Buf(new byte[len]);
        System.arraycopy(u, last, b.raw, 0, len);
        al.add(b);

        arr = new Buf[al.size()];
        al.toArray(arr);
    }

    public static boolean test() {
        return true;
    }

}
