package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Ops;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

public class Read extends Buf implements Ops {

    public String getHelp() { return "read from file, empty if no exist"; }

    Buf filename;

    public Read(Buf filename) {
        this.filename = filename;
        this.raw = new byte[0];
    }

    // TODO: this shows that it is not good as-is, either it needs a
    // `invalidate` of some sort, or smarter calls to `update`
    @Override
    public void update() {
        filename.update();

        try {
            FileInputStream f = new FileInputStream(new File(filename.decode()));
            System.out.println(filename.decode() + " size: " + f.available());
            raw = new byte[f.available()]; // == file size in case of actual on-disk file
            f.read(raw);
            f.close();
        } catch (IOException e) {
            System.err.println("Read: " + e);
        }
    }

}
