package com.jexad.ops.png;

import com.jexad.base.Buf;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Fun;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class PngDecode extends Num {

    public static final Fun fun = new Fun.ForClass(PngDecode.class, "decodes bytes as a png file");

    public static class Handle {

        private static ImageReader imr = ImageIO.getImageReadersBySuffix("png").next();

        private BufferedImage img;
        private IIOMetadata ma;

        private void update(byte[] raw) {
            imr.setInput(new MemoryCacheImageInputStream(new ByteArrayInputStream(raw)));
            try {
                img = imr.read(0);
                ma = imr.getImageMetadata(0);
                // 0: eg. gif with multiple frames, also: multiple resolutions
            } catch (Exception e) {
                System.err.println("PngDecode: " + e);
                // XXX: errs and such...
            }
        }

        BufferedImage image() { return img; }

        IIOMetadata meta() { return ma; }

    }
    public static ArrayList<Handle> pngs = new ArrayList();

    Buf pngbytes;

    public PngDecode(Buf pngbytes) {
        this.pngbytes = pngbytes;
        val = pngs.size();
        pngs.add(new Handle());
    }

    @Override
    public Obj[] arguments() { return new Obj[] {pngbytes}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        pngbytes.update();
        pngs.get(val).update(pngbytes.raw);
    }

    public static boolean notest() {
        return false; // TODO: test
    }

}