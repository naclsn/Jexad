package com.jexad.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import com.jexad.base.*;
import com.jexad.ops.*;

class Cases {

    public static void log(String fmt, Object... args) {
        System.out.printf("log: " + fmt.replace("\n", "\n     ") + "\n", args);
    }

    public static <T extends Obj> T show(T obj) {
        obj.update();
        if (obj instanceof Lst) {
            Lst lst = (Lst)obj;
            for (int k = 0; k < lst.arr.length; k++)
                lst.arr[k].update();
        }
        log("-> " + obj);
        return obj;
    }

    public static void main(String[] args) {
        Method[] all = Cases.class.getDeclaredMethods();
        Arrays.sort(all, new Comparator<Method>() {
            public int compare(Method l, Method r) {
                return l.getName().compareTo(r.getName());
            }
        });

        int fails = 0;
        for (int k = 0; k < all.length; k++) {
            String testcase = all[k].getName();
            if (!testcase.startsWith("case")) continue;

            log("### " + testcase);
            try {
                if ((boolean)all[k].invoke(null)) {
                    log("... " + testcase);
                } else {
                    log("!!! " + testcase + ": failed");
                    fails++;
                }
            } catch (Exception e) {
                log("!!! " + testcase + ": caught " + e);
                fails++;
            }
        }

        System.exit(fails);
    }

    // A)
    //  a bin file contains at offset `list_off` a list of `list_len` 32-bits
    //  signed integers (little endian) which are pointers to static ('\0'
    //  terminated) C-strings in the same file
    //  - open a buffer on the file
    //  - make the list of integers
    //  - read each C-strings
    //  - concatenate them
    //  - join with new-lines
    public static boolean caseA() {
        final String filename = "test/A/some.bin";
        final int list_off = 0x42;
        final int list_len = 3;

        log("open a buffer on the file");
        Buf binfile_buf = new File(Buf.encode(filename));
        //binfile_buf.update();
        //binfile_buf = new Buf(binfile_buf.raw);

        log("make the list of integers");
        Buf list_buf = new Slice(binfile_buf, new Num(list_off), new Num(list_off + list_len*4));
        Lst<Buf> list_32b = new Rect(list_buf, new Num(4));
        Lst<Num> pointers = new Map<Num>(Parse.class, list_32b);

        log("read each C-strings");
        Lst<Buf> strings_starts = new Map<Buf>(Slice.class, new Repeat<Buf>(binfile_buf, new Num(list_len)), pointers);
        Lst<Buf> strings = new Map<Buf>(Delim.class, strings_starts); // new Repeat<Buf>(new Buf(new byte[] {0}), new Num(list_len)));

        log("join with new-lines");
        Buf result = new Join(strings, new Buf(new byte[] {'\n'}));

        result.update();
        String res = result.decode();
        log("result:\n'''\n" + res + "\n'''");

        //binfile_buf.raw[list_off]++;
        //result.update();
        //log("result 2:\n'''\n" + result.decode() + "\n'''");

        return "that's\nall\nfolks".equals(res);
    }

    public static boolean caseB() {
        return false;
    }

}
