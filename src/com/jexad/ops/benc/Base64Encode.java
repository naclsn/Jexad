package com.jexad.ops.benc;

import com.jexad.base.*;

public class Base64Encode extends Buf {

    public static Fun fun = new Fun.ForClass(Base64Encode.class, "encodes bytes to base64");

    static byte[] to64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();

    Buf source;

    public Base64Encode(Buf source) { this.source = source; }

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
