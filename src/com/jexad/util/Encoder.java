package com.jexad.util;

public class Encoder {

    int index;
    byte[] bytes;

    public Encoder(byte[] bytes, int index) {
        this.bytes = bytes;
        this.index = index;
    }
    public Encoder(byte[] bytes) { this(bytes, 0); }

    public void reserve(int canlen) {
        if (bytes.length < canlen) {
            int newlen = bytes.length * 2;
            while (newlen < canlen) newlen*= 2;
            exact(newlen);
        }
    }
    public void more(int addlen) {
        reserve(bytes.length + addlen);
    }
    public void exact(int newlen) {
        byte[] niw = new byte[newlen];
        System.arraycopy(bytes, 0, niw, 0, newlen < bytes.length ? newlen : bytes.length);
        bytes = niw;
    }

    public boolean ok() { return index < bytes.length; }
    public void put(byte b) { bytes[index++] = b; }
    public void skip(int n) { index+= n; }

    public void puts(byte... l) {
        for (int k = 0; k < l.length; k++)
            bytes[index++] = l[k];
    }

    public void leShort(short v) {
        bytes[index++] = (byte)((v >> (0*8)) & 0xff);
        bytes[index++] = (byte)((v >> (1*8)) & 0xff);
    }
    public void beShort(short v) {
        bytes[index++] = (byte)((v >> (1*8)) & 0xff);
        bytes[index++] = (byte)((v >> (0*8)) & 0xff);
    }

    public void leInt(int v) {
        bytes[index++] = (byte)((v >> (0*8)) & 0xff);
        bytes[index++] = (byte)((v >> (1*8)) & 0xff);
        bytes[index++] = (byte)((v >> (2*8)) & 0xff);
        bytes[index++] = (byte)((v >> (3*8)) & 0xff);
    }
    public void beInt(int v) {
        bytes[index++] = (byte)((v >> (3*8)) & 0xff);
        bytes[index++] = (byte)((v >> (2*8)) & 0xff);
        bytes[index++] = (byte)((v >> (1*8)) & 0xff);
        bytes[index++] = (byte)((v >> (0*8)) & 0xff);
    }

    public void leLong(long v) {
        bytes[index++] = (byte)((v >> (0*8)) & 0xff);
        bytes[index++] = (byte)((v >> (1*8)) & 0xff);
        bytes[index++] = (byte)((v >> (2*8)) & 0xff);
        bytes[index++] = (byte)((v >> (3*8)) & 0xff);
        bytes[index++] = (byte)((v >> (4*8)) & 0xff);
        bytes[index++] = (byte)((v >> (5*8)) & 0xff);
        bytes[index++] = (byte)((v >> (6*8)) & 0xff);
        bytes[index++] = (byte)((v >> (7*8)) & 0xff);
    }
    public void beLong(long v) {
        bytes[index++] = (byte)((v >> (7*8)) & 0xff);
        bytes[index++] = (byte)((v >> (6*8)) & 0xff);
        bytes[index++] = (byte)((v >> (5*8)) & 0xff);
        bytes[index++] = (byte)((v >> (4*8)) & 0xff);
        bytes[index++] = (byte)((v >> (3*8)) & 0xff);
        bytes[index++] = (byte)((v >> (2*8)) & 0xff);
        bytes[index++] = (byte)((v >> (1*8)) & 0xff);
        bytes[index++] = (byte)((v >> (0*8)) & 0xff);
    }

    public void leFloat(float v) {
        leInt(Float.floatToRawIntBits(v));
    }
    public void beFloat(float v) {
        beInt(Float.floatToRawIntBits(v));
    }

    public void leDouble(double v) {
        leLong(Double.doubleToRawLongBits(v));
    }
    public void beDouble(double v) {
        beLong(Double.doubleToRawLongBits(v));
    }

    public void cString(String c) {
        puts(c.getBytes());
        put((byte)0);
    }

}
