package com.jexad.ops.zip;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import java.util.Arrays;
import java.util.Set;

public class ZipList extends Lst<Buf> {

    Num ziphandle;

    public ZipList(Num ziphandle) {
        super(Buf.class);
        this.ziphandle = ziphandle;
    }

    @Override
    public Obj[] arguments() { return new Obj[] {ziphandle}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        ziphandle.update();
        ZipDecode.Handle h = ZipDecode.zips.get(ziphandle.val); // TODO: errs and such...
        Set<String> all = h.all();
        String[] sorted = new String[all.size()];
        Arrays.sort(all.toArray(sorted));

        arr = new Buf[sorted.length];
        for (int k = 0; k < arr.length; k++)
            arr[k] = Buf.encode(sorted[k]);
    }

}
