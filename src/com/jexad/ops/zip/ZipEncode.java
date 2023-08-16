package com.jexad.ops.zip;

import com.jexad.base.*;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class ZipEncode extends Buf {

    public static final Fun fun = new Fun.ForClass(ZipEncode.class, "encodes entries into zip file as bytes");

    Lst<Buf> paths;
    Lst<Buf> bytes;

    public ZipEncode(Lst<Buf> paths, Lst<Buf> bytes) {
        this.paths = paths;
        this.bytes = bytes;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {paths, bytes}; }

    @Override
    public void update() {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        ZipOutputStream ztr = new ZipOutputStream(str);
        int len = paths.length(); // XXX: check that lengths matches
        try {
            for (int k = 0; k < len; k++) {
                ztr.putNextEntry(new ZipEntry(paths.at(k).decode()));
                ztr.write(bytes.at(k).raw);
            }
        } catch (Exception e) {
            System.err.println("ZipEncode: " + e);
            return; // XXX: errs and such...
        }

        raw = str.toByteArray();
    }

}
