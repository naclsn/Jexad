package com.jexad.util;

import com.jexad.base.*;

// as of now, the `cmp[..]` are to use with 'unit-test'-like
public class Util {

    public static <T extends Obj> T show(T obj) {
        if (null == obj) {
            System.out.println("(show null)");
            return null;
        }
        System.out.printf("(show %s) %s\n", obj.getClass().getSimpleName(), obj);
        return obj;
    }

    public static boolean cmpBuf(Buf l, int l_st, Buf r, int r_st, int len) {
        int l_len = l.raw.length-l_st, r_len = r.raw.length-r_st;
        if (l_len < len) {
            System.out.printf("buffer left too short: %d (+%d) < %d\n", l_len, l_st, len);
            return false;
        }
        if (r_len < len) {
            System.out.printf("buffer right too short: %d (+%d) < %d\n", r_len, r_st, len);
            return false;
        }
        for (int k = 0; k < len; k++) {
            byte l_by = l.raw[l_st+k], r_by = r.raw[r_st+k];
            if (l.raw[l_st+k] != r.raw[r_st+k]) {
                System.out.printf("buffers differ on offsets %d and %d: %x != %x\n", l_st+k, r_st+k, l_by, r_by);
                return false;
            }
        }
        return true;
    }

    public static boolean cmpBuf(Buf l, Buf r) {
        if (l.raw.length != r.raw.length) {
            System.out.printf("buffer lengths differ: %d != %d\n", l.raw.length, r.raw.length);
            return false;
        }
        for (int k = 0; k < l.raw.length; k++) {
            if (l.raw[k] != r.raw[k]) {
                System.out.printf("buffers differ on offset %d: %x != %x\n", k, l.raw[k], r.raw[k]);
                return false;
            }
        }
        return true;
    }

    public static boolean cmpNum(Num l, Num r) {
        switch ((l.dec ? 0b10 : 0) | (r.dec ? 0b01 : 0)) {
            case 0b00:
                if (l.iv.equals(r.iv)) return true;
                break;
            case 0b11:
                if (l.dv.equals(r.dv)) return true;
                break;
            case 0b10:
                System.out.printf("number type differ: left is decimal\n");
                break;
            case 0b01:
                System.out.printf("number type differ: right is decimal\n");
                break;
        }
        System.out.printf("numbers differ: %d != %d\n", l, r);
        return false;
    }

    public static boolean cmpLst(Lst l, Lst r) {
        if (l.arr.length != r.arr.length) {
            System.out.printf("list lengths differ: %d != %d\n", l.arr.length, r.arr.length);
            return false;
        }
        for (int k = 0; k < l.arr.length; k++) {
            if (!cmpObj(l.arr[k], r.arr[k])) {
                System.out.printf(".. in list at %d\n", k);
                return false;
            }
        }
        return true;
    }

    // note: does not compare `Fun`s
    public static boolean cmpObj(Obj l, Obj r) {
        Class cl = l.baseClass(), cr = r.baseClass();
        if (cl != cr) {
            System.out.printf("object classes differ: %s != %s\n", cl, cr);
            return false;
        }
        return
            ( Buf.class == cl ? cmpBuf((Buf)l, (Buf)r)
            : Num.class == cl ? cmpNum((Num)l, (Num)r)
            : Lst.class == cl ? cmpLst((Lst)l, (Lst)r)
            : false
            );
    }

}
