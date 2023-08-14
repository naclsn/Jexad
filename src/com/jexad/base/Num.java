package com.jexad.base;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Num extends Obj {

    public boolean dec; // readonly
    public BigInteger iv; // readonly
    public BigDecimal dv; // readonly

    public Num(BigInteger iv) { this.iv = iv; dec = false; }
    public Num(BigDecimal dv) { this.dv = dv; dec = true; }
    public Num() { this(BigInteger.ZERO); }

    public Num(byte val) { this(BigInteger.valueOf(val)); }
    public Num(short val) { this(BigInteger.valueOf(val)); }
    public Num(int val) { this(BigInteger.valueOf(val)); }
    public Num(long val) { this(BigInteger.valueOf(val)); }
    public Num(float val) { this(BigDecimal.valueOf(val)); }
    public Num(double val) { this(BigDecimal.valueOf(val)); }

    public byte asByte() { return dec ? dv.byteValue() : iv.byteValue(); }
    public short asShort() { return dec ? dv.shortValue() : iv.shortValue(); }
    public int asInt() { return dec ? dv.intValue() : iv.intValue(); }
    public long asLong() { return dec ? dv.longValue() : iv.longValue(); }
    public float asFloat() { return dec ? dv.floatValue() : dv.floatValue(); }
    public double asDouble() { return dec ? dv.doubleValue() : dv.doubleValue(); }

    @Override
    public String toString() {
        return dec ? dv.toString() : iv.toString();
    }

}
