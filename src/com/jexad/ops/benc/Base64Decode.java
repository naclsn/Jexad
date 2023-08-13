package com.jexad.ops.benc;

import com.jexad.base.*;

public class Base64Decode extends Buf {

    public static Fun fun = new Fun.ForClass(Base64Decode.class, "decodes base64 to bytes; does not MIME tho");

    static String from64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    Buf source;

    public Base64Decode(Buf source) { this.source = source; }

    @Override
    public Obj[] arguments() { return new Obj[] {source}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        source.update();
        byte[] s = source.raw;

        if (0 != s.length%4) {
            System.err.println("Base64Decode: length is not a multiple of 4");
            return; // XXX: errs and such...
        }

        int pad
            = '=' == s[s.length-2] ? 2
            : '=' == s[s.length-1] ? 1
            : 0;
        raw = new byte[s.length/4*3 - pad];

        int i, j;
        for (i = 0, j = 0; i < raw.length-2; i+= 3, j+= 4) {
            int n
                = (from64.indexOf(s[j+0]) << (6*3))
                | (from64.indexOf(s[j+1]) << (6*2))
                | (from64.indexOf(s[j+2]) << (6*1))
                | (from64.indexOf(s[j+3]) << (6*0))
                ;

            raw[i+0] = (byte)((n >> (8*2))&0xff);
            raw[i+1] = (byte)((n >> (8*1))&0xff);
            raw[i+2] = (byte)((n >> (8*0))&0xff);
        }

        if (0 != pad) {
            int n = 1 == pad
                ? (from64.indexOf(s[j+0]) << (6*3))
                | (from64.indexOf(s[j+1]) << (6*2))
                | (from64.indexOf(s[j+2]) << (6*1))
                | 0
                : (from64.indexOf(s[j+0]) << (6*3))
                | (from64.indexOf(s[j+1]) << (6*2))
                | 0
                | 0
                ;

            raw[i+0] = (byte)((n >>> (8*2))&0xff);
            if (1 == pad) raw[i+1] = (byte)((n >>> (8*1))&0xff);
        }
    }

    public static boolean test() {
        return Util.cmpBuf
                ( new Base64Decode(new Buf(new byte[] {'b', 'G', 'l', 'n', 'a', 'H', 'Q', 'g', 'd', 'w', '=', '='}))
                , new Buf(new byte[] {'l', 'i', 'g', 'h', 't', ' ', 'w'})
                )
            && Util.cmpBuf
                ( new Base64Decode(new Buf(new byte[] {'b', 'G', 'l', 'n', 'a', 'H', 'Q', 'g', 'd', '2', '8', '='}))
                , new Buf(new byte[] {'l', 'i', 'g', 'h', 't', ' ', 'w', 'o'})
                )
            && Util.cmpBuf
                ( new Base64Decode(new Buf(new byte[] {'b', 'G', 'l', 'n', 'a', 'H', 'Q', 'g', 'd', '2', '9', 'y'}))
                , new Buf(new byte[] {'l', 'i', 'g', 'h', 't', ' ', 'w', 'o', 'r'})
                )
            ;
    }

}
