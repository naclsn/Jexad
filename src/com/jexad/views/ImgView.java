package com.jexad.views;

import com.jexad.base.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class ImgView extends View<Buf> {

    public static final Fun fun = new Fun.ForClass(ImgView.class, "view buffer as an image");

    public ImgView(Buf content, String title) { super(content, title); }
    public ImgView(Buf content, Buf title) { super(content, title.decode()); }
    public ImgView(Buf content) { super(content, null); }

    BufferedImage img;
    AffineTransform tr;
    int w;
    int h;

    @Override
    protected void update() {
        byte[] b = content.raw;
        w = h = (int)Math.sqrt(b.length/3);

        SampleModel sampmodel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, w, h
            , 3 // pixelStride
            , 3*w // scanlineStride
            , new int[] {0, 1, 2} // bandOffsets
            );
        DataBuffer databuffer = new DataBufferByte(b, b.length);
        Raster raster = Raster.createRaster(sampmodel, databuffer, null);

        img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        img.setData(raster);

        tr = new AffineTransform(
            10, 0,
            0, 10,
            0, 0); // rem: transposed because ctor arg order
    }

    @Override
    protected void render(Graphics2D g) {
        g.translate(-scroll.hz*scroll.unitHz, -scroll.ve*scroll.unitVe);
        g.drawImage(img, new AffineTransformOp(tr, AffineTransformOp.TYPE_NEAREST_NEIGHBOR), 0, 0);
    }

}
