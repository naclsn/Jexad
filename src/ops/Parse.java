package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Num;
import com.jexad.base.Ops;

public class Parse extends Num implements Ops {

    public String getHelp() { return "parse from a buffer slice (default little endian); does not check the size (for now only does 4-bytes signed integer)"; }

    Buf under;
    Endian endian;

    public enum Endian { LITTLE, BIG }

    // XXX: argument type is not Obj (ie. Buf/Num/Lst)
    public Parse(Buf under, Endian endian) {
        this.under = under;
        this.endian = endian;
    }

    public Parse(Buf under) { this(under, Endian.LITTLE); }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        val = toInt();
    }

    // byte(1) short(2) int(4) long(8)
    int toInt() {
        return Endian.BIG == endian
            ? ((under.raw[0]&0xff) << (8*3))
            | ((under.raw[1]&0xff) << (8*2))
            | ((under.raw[2]&0xff) << (8*1))
            | ((under.raw[3]&0xff) << (8*0))
            : ((under.raw[0]&0xff) << (8*0))
            | ((under.raw[1]&0xff) << (8*1))
            | ((under.raw[2]&0xff) << (8*2))
            | ((under.raw[3]&0xff) << (8*3))
            ;
    }

    // half(2) float(4) double(8)
    float toFloat() {
        return Float.intBitsToFloat(toInt());
    }

}
