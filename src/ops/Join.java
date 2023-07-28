package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Ops;

public class Join extends Buf implements Ops {

    public String getHelp() { return "join with separator (default empty)"; }

    Lst<Buf> list;
    Buf sep;

    public Join(Lst<Buf> list, Buf sep) {
        this.list = list;
        this.sep = sep;
    }

    public Join(Lst<Buf> list) { this(list, new Buf(new byte[0])); }

    @Override
    public void update() {
        list.update();
        int list_len = list.length();

        if (0 == list_len) {
            raw = new byte[0];
            return;
        }

        if (1 < list_len) sep.update();
        Buf first = list.at(0);

        first.update();
        int total_len = first.raw.length;
        for (int k = 1; k < list_len; k++) {
            Buf it = list.at(k);
            it.update();
            total_len+= sep.raw.length + it.raw.length;
        }
        raw = new byte[total_len];

        int at = first.raw.length;
        System.arraycopy(first.raw, 0, raw, 0, at);

        for (int k = 1; k < list_len; k++) {
            System.arraycopy(sep.raw, 0, raw, at, sep.raw.length);
            at+= sep.raw.length;

            Buf it = list.at(k);
            int len = it.raw.length;
            System.arraycopy(it.raw, 0, raw, at, len);
            at+= len;
        }
    }

}
