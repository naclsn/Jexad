package com.jexad.views;

import com.jexad.base.*;

public class ViewImg extends Buf {

    public static final Fun fun = new Fun.ForClass(ViewImg.class, "view buffer as an image");

    ImgView vw;
    byte[] detached;

    public ViewImg(Buf content, Buf title) {
        vw = new ImgView(content, title.decode());
        init();
    }
    public ViewImg(Buf content) {
        vw = new ImgView(content, null);
        init();
    }

    @Override
    public Obj[] arguments() {
        return new Obj[] {vw.getContent()};
    }

    @Override
    public void update() {
        if (null != detached)
            raw = detached;
        else {
            Buf c = vw.getContent();
            raw = c.raw;
        }
    }

}
