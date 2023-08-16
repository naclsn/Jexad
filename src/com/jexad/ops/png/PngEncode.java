package com.jexad.ops.png;

import com.jexad.base.*;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

public class PngEncode extends Buf {

    public static final Fun fun = new Fun.ForClass(PngEncode.class, "encodes pixels into a png file as bytes");

    static ImageWriter imw = ImageIO.getImageWritersBySuffix("png").next();

    public PngEncode(Buf bytes) {
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[0]; }

    @Override
    public void update() {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        imw.setOutput(str);

        raw = new byte[0];
        System.err.println("PngEncode: NIY");
    }

}
