package com.jexad.ops.png;

import com.jexad.base.*;

public class PngChunk extends Buf {

    public static final Fun fun = new Fun.ForClass(PngChunk.class, "get a chunk by name");

    Num pnghandle;
    int chk;

    public PngChunk(Num pnghandle, Sym chunk) {
        this.pnghandle = pnghandle;
        chk = chunk.str.charAt(0)
            | chunk.str.charAt(1)
            | chunk.str.charAt(2)
            | chunk.str.charAt(3)
            ;
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {pnghandle}; }

    @Override
    public void update() {
        PngDecode.Handle h = PngDecode.pngs.get(pnghandle.asInt()); // TODO: errs and such...
        raw = h.chunk(chk);
    }

}
