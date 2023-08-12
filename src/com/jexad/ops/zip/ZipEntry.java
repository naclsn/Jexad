package com.jexad.ops.zip;

import com.jexad.base.*;

public class ZipEntry extends Buf {

    public static final Fun fun = new Fun.ForClass(ZipEntry.class, "address entry at the given path in the zip");

    Num ziphandle;
    Buf path;

    public ZipEntry(Num ziphandle, Buf path) {
        if (!(ziphandle instanceof ZipDecode)) {
            System.err.println("ziphandle is not of ZipDecode!!");
            return; // XXX: errs and such...
        }
        this.ziphandle = ziphandle;
        this.path = path;
    }

    @Override
    public Obj[] arguments() { return new Obj[] {ziphandle, path}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        ziphandle.update();
        ZipDecode.Handle h = ZipDecode.zips.get(ziphandle.val); // TODO: errs and such...
        path.update();
        raw = h.bytes(path.decode()); // TODO: errs and such...
    }

}
