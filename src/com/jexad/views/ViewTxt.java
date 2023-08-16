package com.jexad.views;

import com.jexad.base.*;

public class ViewTxt extends Buf {

    public static final Fun fun = new Fun.ForClass(ViewTxt.class, "view buffer as simple text");

    TxtView vw;
    byte[] detached;

    public ViewTxt(Buf content, Buf title) {
        vw = new TxtView(content, title.decode());
        init();
    }
    public ViewTxt(Buf content) {
        vw = new TxtView(content, null);
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
