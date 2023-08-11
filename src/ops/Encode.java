package com.jexad.ops;

import com.jexad.base.*;
import java.util.Arrays;

public class Encode extends Buf {

    public static final Fun fun = new Fun.ForClass(Encode.class, "encode a number to bytes (default little endian)");

    Num under;
    Endian endian;

    public enum Endian { LITTLE, BIG }
    //public enum Kind { BYTE, SHORT, INT, LONG, HALF, FLOAT, DOUBLE }

    public Encode(Num under, Endian endian) {
        this.under = under;
        this.endian = endian;
    }

    public Encode(Num under) { this(under, Endian.LITTLE); }

    @Override
    public Obj[] arguments() { return new Obj[] {under}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        under.update();
        raw = fromInt(under.val, endian);
    }

    public static byte[] fromByte(byte one, Endian endian) {
        return new byte[] {one};
    }

    public static byte[] fromShort(short two, Endian endian) {
        return Endian.BIG == endian
            ? new byte[]
            { (byte)(0xff&(two >> (8*1)))
            , (byte)(0xff&(two >> (8*0)))
            }
            : new byte[]
            { (byte)(0xff&(two >> (8*0)))
            , (byte)(0xff&(two >> (8*1)))
            }
            ;
    }
    public static boolean testFromShort() {
        return Arrays.equals(fromShort(Short.MAX_VALUE, Endian.LITTLE), new byte[] {(byte)0xff, (byte)0x7f})
            && Arrays.equals(fromShort(Short.MAX_VALUE, Endian.BIG   ), new byte[] {(byte)0x7f, (byte)0xff})
            && Arrays.equals(fromShort(Short.MIN_VALUE, Endian.LITTLE), new byte[] {(byte)0x00, (byte)0x80})
            && Arrays.equals(fromShort(Short.MIN_VALUE, Endian.BIG   ), new byte[] {(byte)0x80, (byte)0x00})
            && Arrays.equals(fromShort((short)3084, Endian.BIG), new byte[] {12, 12})
            ;
    }

    public static byte[] fromInt(int four, Endian endian) {
        return Endian.BIG == endian
            ? new byte[]
            { (byte)(0xff&(four >> (8*3)))
            , (byte)(0xff&(four >> (8*2)))
            , (byte)(0xff&(four >> (8*1)))
            , (byte)(0xff&(four >> (8*0)))
            }
            : new byte[]
            { (byte)(0xff&(four >> (8*0)))
            , (byte)(0xff&(four >> (8*1)))
            , (byte)(0xff&(four >> (8*2)))
            , (byte)(0xff&(four >> (8*3)))
            }
            ;
    }
    public static boolean testFromInt() {
        return Arrays.equals(fromInt(Integer.MAX_VALUE, Endian.LITTLE),  new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0x7f})
            && Arrays.equals(fromInt(Integer.MAX_VALUE, Endian.BIG   ),  new byte[] {(byte)0x7f, (byte)0xff, (byte)0xff, (byte)0xff})
            && Arrays.equals(fromInt(Integer.MIN_VALUE, Endian.LITTLE),  new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80})
            && Arrays.equals(fromInt(Integer.MIN_VALUE, Endian.BIG   ),  new byte[] {(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00})
            && Arrays.equals(fromInt(202116108, Endian.BIG), new byte[] {12, 12, 12, 12})
            ;
    }

    public static byte[] fromLong(long eight, Endian endian) {
        return Endian.BIG == endian
            ? new byte[]
            { (byte)(0xffL&(eight >> (8L*7)))
            , (byte)(0xffL&(eight >> (8L*6)))
            , (byte)(0xffL&(eight >> (8L*5)))
            , (byte)(0xffL&(eight >> (8L*4)))
            , (byte)(0xffL&(eight >> (8L*3)))
            , (byte)(0xffL&(eight >> (8L*2)))
            , (byte)(0xffL&(eight >> (8L*1)))
            , (byte)(0xffL&(eight >> (8L*0)))
            }
            : new byte[]
            { (byte)(0xffL&(eight >> (8L*0)))
            , (byte)(0xffL&(eight >> (8L*1)))
            , (byte)(0xffL&(eight >> (8L*2)))
            , (byte)(0xffL&(eight >> (8L*3)))
            , (byte)(0xffL&(eight >> (8L*4)))
            , (byte)(0xffL&(eight >> (8L*5)))
            , (byte)(0xffL&(eight >> (8L*6)))
            , (byte)(0xffL&(eight >> (8L*7)))
            }
            ;
    }
    public static boolean testFromLong() {
        return Arrays.equals(fromLong(Long.MAX_VALUE, Endian.LITTLE), new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x7f})
            && Arrays.equals(fromLong(Long.MAX_VALUE, Endian.BIG   ), new byte[] {(byte)0x7f, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff})
            && Arrays.equals(fromLong(Long.MIN_VALUE, Endian.LITTLE), new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80})
            && Arrays.equals(fromLong(Long.MIN_VALUE, Endian.BIG   ), new byte[] {(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00})
            && Arrays.equals(fromLong(868082074056920076L, Endian.BIG) , new byte[] {12, 12, 12, 12, 12, 12, 12, 12})
            ;
    }

    public static byte[] fromHalf(float two, Endian endian) {
        int bits = Float.floatToRawIntBits(two);
        int s = 0b10000000000000000000000000000000 & bits
          , e = 0b01111111100000000000000000000000 & bits
          , m = 0b00000000011111111111111111111111 & bits
          ;
        // XXX: does that even work?
        return fromShort
            ( (short)
            ( (short)(0b1000000000000000 & (s >> 16))
            | (short)(0b0111110000000000 & (e >> 13))
            | (short)(0b0000001111111111 & (m >>  0))
            ), endian);
    }

    public static byte[] fromFloat(float four, Endian endian) {
        return fromInt(Float.floatToRawIntBits(four), endian);
    }

    public static byte[] fromDouble(double eight, Endian endian) {
        return fromLong(Double.doubleToRawLongBits(eight), endian);
    }

}
