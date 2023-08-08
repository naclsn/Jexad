package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Fun;

public class Parse extends Num {

    public static final Fun fun = new Fun.ForClass(Parse.class, "parse from a buffer slice (default little endian); does not check the size (for now only does 4-bytes signed integer)");

    Buf under;
    Endian endian;
    //Kind width;

    public enum Endian { LITTLE, BIG }
    //public enum Kind { BYTE, SHORT, INT, LONG, HALF, FLOAT, DOUBLE }

    // XXX: argument type is not Obj (ie. Buf/Num/Lst)
    public Parse(Buf under, Endian endian) {
        this.under = under;
        this.endian = endian;
    }

    public Parse(Buf under) { this(under, Endian.LITTLE); }

    @Override
    public Obj[] arguments() { return new Obj[] {under}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        val = toInt(under.raw, endian);
    }

    public static byte toByte(byte[] one, Endian endian) {
        return one[0];
    }

    public static short toShort(byte[] two, Endian endian) {
        return (short)(Endian.BIG == endian
            ? ((0xff&two[0]) << (8*1))
            | ((0xff&two[1]) << (8*0))
            : ((0xff&two[0]) << (8*0))
            | ((0xff&two[1]) << (8*1))
            );
    }
    public static boolean testToShort() {
        return Short.MAX_VALUE == toShort(new byte[] {(byte)0xff, (byte)0x7f}, Endian.LITTLE)
            && Short.MAX_VALUE == toShort(new byte[] {(byte)0x7f, (byte)0xff}, Endian.BIG)
            && Short.MIN_VALUE == toShort(new byte[] {(byte)0x00, (byte)0x80}, Endian.LITTLE)
            && Short.MIN_VALUE == toShort(new byte[] {(byte)0x80, (byte)0x00}, Endian.BIG)
            && (short)3084 == toShort(new byte[] {12, 12}, Endian.BIG)
            ;
    }

    public static int toInt(byte[] four, Endian endian) {
        return Endian.BIG == endian
            ? ((0xff&four[0]) << (8*3))
            | ((0xff&four[1]) << (8*2))
            | ((0xff&four[2]) << (8*1))
            | ((0xff&four[3]) << (8*0))
            : ((0xff&four[0]) << (8*0))
            | ((0xff&four[1]) << (8*1))
            | ((0xff&four[2]) << (8*2))
            | ((0xff&four[3]) << (8*3))
            ;
    }
    public static boolean testToInt() {
        return Integer.MAX_VALUE == toInt(new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0x7f}, Endian.LITTLE)
            && Integer.MAX_VALUE == toInt(new byte[] {(byte)0x7f, (byte)0xff, (byte)0xff, (byte)0xff}, Endian.BIG)
            && Integer.MIN_VALUE == toInt(new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80}, Endian.LITTLE)
            && Integer.MIN_VALUE == toInt(new byte[] {(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00}, Endian.BIG)
            && 202116108 == toInt(new byte[] {12, 12, 12, 12}, Endian.BIG)
            ;
    }

    public static long toLong(byte[] eight, Endian endian) {
        return Endian.BIG == endian
            ? ((0xffL&eight[0]) << (8L*7))
            | ((0xffL&eight[1]) << (8L*6))
            | ((0xffL&eight[2]) << (8L*5))
            | ((0xffL&eight[3]) << (8L*4))
            | ((0xffL&eight[4]) << (8L*3))
            | ((0xffL&eight[5]) << (8L*2))
            | ((0xffL&eight[6]) << (8L*1))
            | ((0xffL&eight[7]) << (8L*0))
            : ((0xffL&eight[0]) << (8L*0))
            | ((0xffL&eight[1]) << (8L*1))
            | ((0xffL&eight[2]) << (8L*2))
            | ((0xffL&eight[3]) << (8L*3))
            | ((0xffL&eight[4]) << (8L*4))
            | ((0xffL&eight[5]) << (8L*5))
            | ((0xffL&eight[6]) << (8L*6))
            | ((0xffL&eight[7]) << (8L*7))
            ;
    }
    public static boolean testToLong() {
        return Long.MAX_VALUE == toLong(new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x7f}, Endian.LITTLE)
            && Long.MAX_VALUE == toLong(new byte[] {(byte)0x7f, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}, Endian.BIG)
            && Long.MIN_VALUE == toLong(new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80}, Endian.LITTLE)
            && Long.MIN_VALUE == toLong(new byte[] {(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00}, Endian.BIG)
            && 868082074056920076L == toLong(new byte[] {12, 12, 12, 12, 12, 12, 12, 12}, Endian.BIG)
            ;
    }

    public static float toHalf(byte[] two, Endian endian) {
        short bits = toShort(two, endian);
        short s = (short)(0b1000000000000000 & bits)
            , e = (short)(0b0111110000000000 & bits)
            , m = (short)(0b0000001111111111 & bits)
            ;
        // XXX: does that even work?
        return Float.intBitsToFloat
            ( (s << 16)
            | (e << 13)
            | (m <<  0)
            );
    }

    public static float toFloat(byte[] four, Endian endian) {
        return Float.intBitsToFloat(toInt(four, endian));
    }

    public static double toDouble(byte[] eight, Endian endian) {
        return Double.longBitsToDouble(toLong(eight, endian));
    }

}
