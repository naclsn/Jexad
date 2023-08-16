package com.jexad.ops.benc;

import com.jexad.base.*;

public class Base64Encode extends Buf {

    public static Fun fun = new Fun.ForClass(Base64Encode.class, "encodes bytes to base64; does not MIME tho");

    static byte[] to64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();

    Buf source;

    public Base64Encode(Buf source) {
        this.source = source;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {source}; }

    @Override
    public void update() {
        byte[] s = source.raw;

        int mod = s.length%3;
        int pad = 0 != mod ? 3-mod : 0;
        raw = new byte[(s.length + pad) * 4/3];

        int i, j;
        for (i = 0, j = 0; i < s.length-2; i+= 3, j+= 4) {
            int n
                = (s[i+0] << (8*2))
                | (s[i+1] << (8*1))
                | (s[i+2] << (8*0))
                ;

            raw[j+0] = to64[(n >> (6*3)) & 0b111111];
            raw[j+1] = to64[(n >> (6*2)) & 0b111111];
            raw[j+2] = to64[(n >> (6*1)) & 0b111111];
            raw[j+3] = to64[(n >> (6*0)) & 0b111111];
        }

        if (0 != pad) {
            int n = 1 == pad
                ? (s[i+0] << (8*2))
                | (s[i+1] << (8*1))
                | 0
                : (s[i+0] << (8*2))
                | 0
                | 0
                ;

            raw[j+0] = to64[(n >> (6*3)) & 0b111111];
            raw[j+1] = to64[(n >> (6*2)) & 0b111111];
            raw[j+2] = to64[(n >> (6*1)) & 0b111111];
            raw[j+3] = to64[(n >> (6*0)) & 0b111111];

            raw[raw.length-1] = '=';
            if (2 == pad) raw[raw.length-2] = '=';
        }
    }

    public static boolean test() {
        return Util.cmpBuf
                ( new Base64Encode(new Buf(new byte[] {'l', 'i', 'g', 'h', 't', ' ', 'w'}))
                , new Buf(new byte[] {'b', 'G', 'l', 'n', 'a', 'H', 'Q', 'g', 'd', 'w', '=', '='})
                )
            && Util.cmpBuf
                ( new Base64Encode(new Buf(new byte[] {'l', 'i', 'g', 'h', 't', ' ', 'w', 'o'}))
                , new Buf(new byte[] {'b', 'G', 'l', 'n', 'a', 'H', 'Q', 'g', 'd', '2', '8', '='})
                )
            && Util.cmpBuf
                ( new Base64Encode(new Buf(new byte[] {'l', 'i', 'g', 'h', 't', ' ', 'w', 'o', 'r'}))
                , new Buf(new byte[] {'b', 'G', 'l', 'n', 'a', 'H', 'Q', 'g', 'd', '2', '9', 'y'})
                )
            ;
    }

}
