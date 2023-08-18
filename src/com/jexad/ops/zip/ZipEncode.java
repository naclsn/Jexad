package com.jexad.ops.zip;

import com.jexad.base.*;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class ZipEncode extends Buf {

    public static final Fun fun = new Fun.ForClass(ZipEncode.class, "encodes entries into zip file as bytes");

    Lst entries;

    public ZipEncode(Lst entries) {
        this.entries = entries;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {entries}; }

    @Override
    public void update() {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        ZipOutputStream ztr = new ZipOutputStream(str);

        int len = entries.arr.length;
        for (int k = 0; k < len; k++) {
            Lst entry = entries.arr[k].<Lst>as("zip entry %d", k);
            Buf path = entry.arr[0].<Buf>as("zip entry %d's path", k);
            Buf bytes = entry.arr[0].<Buf>as("zip entry %d's bytes", k);

            try {
                ztr.putNextEntry(new ZipEntry(path.decode()));
                ztr.write(bytes.raw);
            } catch (Exception e) {
                System.err.println("ZipEncode: " + e);
                return; // XXX: errs and such...
            }
        }

        raw = str.toByteArray();
    }

}
