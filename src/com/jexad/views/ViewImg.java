package com.jexad.views;

import com.jexad.base.*;

public class ViewImg extends Buf {

    public static final Fun fun = new Fun.ForClass(ViewImg.class, "view buffer as an image");

    ImgView vw;
    byte[] detached;

    public ViewImg(Buf content, Buf title) {
        vw = new ImgView(content, title.decode());
    }
    public ViewImg(Buf content) {
        vw = new ImgView(content, null);
    }

    @Override
    public Obj[] arguments() {
        return null != detached
            ? new Obj[0]
            : new Obj[] {vw.getContent()}
            ;
    }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        if (null != detached)
            raw = detached;
        else {
            Buf c = vw.getContent();
            c.update();
            raw = c.raw;
        }
    }

}
