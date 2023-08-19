package com.jexad.ops.png;

import com.jexad.base.*;
import com.jexad.util.Decoder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class PngDecode extends Num {

    public static final Fun fun = new Fun.ForClass(PngDecode.class, "decodes bytes as a png file");

    public static class Handle {

        private int w;
        private int h;
        private HashMap<Integer, byte[]> chks = new HashMap<Integer, byte[]>();

        public static int toChk(byte i, byte d, byte a, byte t) {
            return (i << 24) | (d << 16) | (a << 8) | t;
        }
        public static byte[] fromChk(int idat) {
            return new byte[]
                { (byte)((idat >> 24) & 0xff)
                , (byte)((idat >> 16) & 0xff)
                , (byte)((idat >> 8) & 0xff)
                , (byte)(idat & 0xff)
                };
        }

        private class PngDecoder extends Decoder {

            // TODO

        }

        private PngDecoder d = new PngDecoder();

        private void update(byte[] raw) {
            d.bytes = raw;
            d.index = 0;
            chks.clear();
        }

        int width() {
            System.out.println("NIY: Handle.width");
            return 0;
        }

        int height() {
            System.out.println("NIY: Handle.height");
            return 0;
        }

        byte[] chunk(int chk) {
            return chks.get(chk);
        }

        byte[] argb() {
            System.out.println("NIY: Handle.argb");
            return new byte[0];
        }

    } // class Handle

    public static ArrayList<Handle> pngs = new ArrayList<Handle>();

    Buf pngbytes;

    public PngDecode(Buf pngbytes) {
        this.pngbytes = pngbytes;
        dec = false;
        iv = BigInteger.valueOf(pngs.size());
        pngs.add(new Handle());
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {pngbytes}; }

    @Override
    public void update() {
        pngs.get(iv.intValue()).update(pngbytes.raw);
    }

}
