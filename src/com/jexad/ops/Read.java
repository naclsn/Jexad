package com.jexad.ops;

import com.jexad.base.*;
import com.jexad.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Read extends Buf {

    public static final Fun fun = new Fun.ForClass(Read.class, "read from file, empty if no exist");

    Buf filename;

    public Read(Buf filename) {
        this.filename = filename;
        this.raw = new byte[0];
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {filename}; }

    @Override
    public void update() {
        try {
            FileInputStream f = new FileInputStream(new File(filename.decode()));
            raw = new byte[f.available()]; // == file size in case of actual on-disk file
            f.read(raw);
            f.close();
        } catch (IOException e) {
            System.err.println("Read: " + e);
            // XXX: errs and such...
        }
    }

    public static boolean test() {
        Read b = new Read(Buf.encode("build.xml"));
        return Util.cmpBuf(b, 0, Buf.encode("<project name=\"Jexad\">\n"), 0, 23)
            && Util.cmpBuf(b, b.raw.length-11, Buf.encode("</project>\n"), 0, 11)
            ;
    }

}
