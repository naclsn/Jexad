package com.jexad.base;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Buf extends Obj {

    public byte[] raw; // readonly
    public Buf(byte[] raw) { this.raw = raw; }
    public Buf() { this(null); }

    @Override
    public String toString() {
        return Arrays.toString(raw);
    }

    public static Buf encode(String str) {
        try {
            return new Buf(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("Buf.encode: " + e);
            return new Buf(str.getBytes());
        }
    }

    public String decode() {
        try {
            return new String(raw, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Buf.decode: " + e);
            return new String(raw);
        }
    }

}
