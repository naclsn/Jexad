package com.jexad.ops.png;

import com.jexad.base.Buf;
import com.jexad.base.Fun;
import com.jexad.base.Obj;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

public class PngEncode extends Buf {

    public static final Fun fun = new Fun.ForClass(PngEncode.class, "encodes pixels into a png file as bytes");

    static ImageWriter imw = ImageIO.getImageWritersBySuffix("png").next();

    public PngEncode(Buf bytes) { }

    @Override
    public Obj[] arguments() { return new Obj[0]; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        ByteArrayOutputStream str = new ByteArrayOutputStream();
        imw.setOutput(str);

        raw = new byte[0];
    }

}
