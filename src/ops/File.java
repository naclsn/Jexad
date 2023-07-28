package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Ops;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class File extends Buf implements Ops {

    public String getHelp() { return "read from file, empty if no exist"; }

    Buf filename;

    public File(Buf filename) {
        this.filename = filename;
        this.raw = new byte[0];
    }

    @Override
    public void update() {
        filename.update();
        Path path = Paths.get(filename.decode());

        try {
            raw = Files.readAllBytes(path);
        } catch (IOException e) {
        }
    }

}
