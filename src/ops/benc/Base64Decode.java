package com.jexad.ops.benc;

import com.jexad.base.*;

public class Base64Decode extends Buf {

    public static Fun fun = new Fun.ForClass(Base64Decode.class, "decodes base64 to bytes");

    Buf source;

    public Base64Decode(Buf source) { this.source = source; }

    @Override
    public Obj[] arguments() { return new Obj[] {source}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        source.update();
        raw = new byte[0];
    }

}
