package com.jexad.base;

// to use with 'unit-test'-like
public class Util {

    public static <T extends Obj> T show(T obj) {
        if (null == obj) {
            System.out.println("(show null)");
            return null;
        }
        obj.update();
        if (obj instanceof Lst) {
            Lst lst = (Lst)obj;
            for (int k = 0; k < lst.arr.length; k++)
                lst.arr[k].update();
        }
        String cln = obj.getClass().toString();
        System.out.println("(show " + cln.substring(cln.lastIndexOf('.')+1) + ") " + obj);
        return obj;
    }

    public static boolean cmpBuf(Buf l, int l_st, Buf r, int r_st, int len) {
        l.update();
        r.update();
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
        l.update();
        r.update();
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
        l.update();
        r.update();
        int l_va = l.val, r_va = r.val;
        if (l_va != r_va) {
            System.out.printf("numbers differ: %d != %d\n", l_va, r_va);
            return false;
        }
        return true;
    }

    public static <T extends Obj> boolean cmpLst(Lst<T> l, Lst<T> r) {
        l.update();
        r.update();
        if (l.length() != r.length()) {
            System.out.printf("list lengths differ: %d != %d\n", l.length(), r.length());
            return false;
        }
        Class c = l.getItemClass();
        for (int k = 0; k < l.length(); k++) {
            T l_it = l.at(k), r_it = r.at(k);
            if (!(Buf.class == c ? cmpBuf((Buf)l_it, (Buf)r_it)
                : Num.class == c ? cmpNum((Num)l_it, (Num)r_it)
                : Lst.class == c ? cmpLst((Lst)l_it, (Lst)r_it)
                : false)) return false;
        }
        return true;
    }

}
