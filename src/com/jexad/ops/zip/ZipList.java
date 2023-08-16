package com.jexad.ops.zip;

import com.jexad.base.*;
import java.util.Arrays;
import java.util.Set;

public class ZipList extends Lst<Buf> {

    public static final Fun fun = new Fun.ForClass(ZipList.class, "list the entries in the zip");

    Num ziphandle;

    public ZipList(Num ziphandle) {
        this.ziphandle = ziphandle;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {ziphandle}; }

    @Override
    public void update() {
        ZipDecode.Handle h = ZipDecode.zips.get(ziphandle.asInt()); // TODO: errs and such...
        Set<String> all = h.paths();
        String[] sorted = new String[all.size()];
        Arrays.sort(all.toArray(sorted));

        arr = new Buf[sorted.length];
        for (int k = 0; k < arr.length; k++)
            arr[k] = Buf.encode(sorted[k]);
    }

}
