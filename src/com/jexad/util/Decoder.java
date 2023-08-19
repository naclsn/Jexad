package com.jexad.util;

public class Decoder {

    public int index;
    public byte[] bytes;

    public Decoder(byte[] bytes, int index) {
        this.bytes = bytes;
        this.index = index;
    }
    public Decoder(byte[] bytes) { this(bytes, 0); }
    public Decoder() { this(null); }

    public boolean ok() { return index < bytes.length; }
    public byte get() { return bytes[index++]; }
    public void skip(int n) { index+= n; }

    public int scan(byte find) {
        int len = bytes.length;
        int k = 0;
        while (index < len && bytes[index++] != find) k++;
        return k;
    }

    public short leShort() {
        return (short)
            ( (bytes[index++] << (1*8))
            | (bytes[index++] << (0*8))
            );
    }
    public short beShort() {
        return (short)
            ( (bytes[index++] << (0*8))
            | (bytes[index++] << (1*8))
            );
    }

    public int leInt() {
        return (int)
            ( (bytes[index++] << (3*8))
            | (bytes[index++] << (2*8))
            | (bytes[index++] << (1*8))
            | (bytes[index++] << (0*8))
            );
    }
    public int beInt() {
        return (int)
            ( (bytes[index++] << (0*8))
            | (bytes[index++] << (1*8))
            | (bytes[index++] << (2*8))
            | (bytes[index++] << (3*8))
            );
    }

    public long leLong() {
        return (long)
            ( (bytes[index++] << (7*8))
            | (bytes[index++] << (6*8))
            | (bytes[index++] << (5*8))
            | (bytes[index++] << (4*8))
            | (bytes[index++] << (3*8))
            | (bytes[index++] << (2*8))
            | (bytes[index++] << (1*8))
            | (bytes[index++] << (0*8))
            );
    }
    public long beLong() {
        return (long)
            ( (bytes[index++] << (0*8))
            | (bytes[index++] << (1*8))
            | (bytes[index++] << (2*8))
            | (bytes[index++] << (3*8))
            | (bytes[index++] << (4*8))
            | (bytes[index++] << (5*8))
            | (bytes[index++] << (6*8))
            | (bytes[index++] << (7*8))
            );
    }

    public float leFloat() {
        return Float.intBitsToFloat(leInt());
    }
    public float beFloat() {
        return Float.intBitsToFloat(beInt());
    }

    public double leDouble() {
        return Double.longBitsToDouble(leLong());
    }
    public double beDouble() {
        return Double.longBitsToDouble(beLong());
    }

    public String cString() {
        return new String(bytes, index, scan((byte)0));
    }

}
