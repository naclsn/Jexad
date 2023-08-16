package com.jexad.views;

import com.jexad.base.*;

public class ViewHex extends Buf {

    public static final Fun fun = new Fun.ForClass(ViewHex.class, "view buffer as hex bytes");

    HexView vw;
    byte[] detached;

    public ViewHex(Buf content, Buf title) {
        vw = new HexView(content, title.decode());
        init();
    }
    public ViewHex(Buf content) {
        vw = new HexView(content, null);
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
