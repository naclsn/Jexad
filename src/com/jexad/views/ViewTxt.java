package com.jexad.views;

import com.jexad.base.*;

public class ViewTxt extends Buf {

    public static final Fun fun = new Fun.ForClass(ViewTxt.class, "view buffer as simple text");

    TxtView vw;
    byte[] detached;

    public ViewTxt(Buf content, Buf title) {
        vw = new TxtView(content, title.decode());
    }
    public ViewTxt(Buf content) {
        vw = new TxtView(content, null);
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
