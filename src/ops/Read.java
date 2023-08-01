package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Ops;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import com.jexad.base.Util;

public class Read extends Buf implements Ops {

    public String getHelp() { return "read from file, empty if no exist"; }

    Buf filename;

    public Read(Buf filename) {
        this.filename = filename;
        this.raw = new byte[0];
    }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        filename.update();

        try {
            FileInputStream f = new FileInputStream(new File(filename.decode()));
            raw = new byte[f.available()]; // == file size in case of actual on-disk file
            f.read(raw);
            f.close();
        } catch (IOException e) {
            System.err.println("Read: " + e);
        }
    }

    public static boolean notest() {
        return false; // TODO: test
    }

}
