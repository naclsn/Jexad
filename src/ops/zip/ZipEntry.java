package com.jexad.ops.zip;

import com.jexad.base.Buf;
import com.jexad.base.Num;
import com.jexad.base.Obj;

public class ZipEntry extends Buf {

    public String getHelp() { return "address entry at the given path in the zip"; }

    Num ziphandle;
    Buf path;

    public ZipEntry(Num ziphandle, Buf path) {
        if (!(ziphandle instanceof ZipDecode)) {
            System.err.println("ziphandle is not of ZipDecode!!");
            // XXX: errs and such...
            return;
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
        raw = h.get(path.decode()); // TODO: errs and such...
    }

    public static boolean notest() {
        return false; // TODO: test
    }

}
