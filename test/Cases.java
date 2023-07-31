package com.jexad.test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import com.jexad.base.*;
import com.jexad.ops.*;

class Cases {

    static <T extends Obj> T show(T obj) {
        obj.update();
        if (obj instanceof Lst) {
            Lst lst = (Lst)obj;
            for (int k = 0; k < lst.arr.length; k++)
                lst.arr[k].update();
        }
        System.out.println("-> " + obj);
        return obj;
    }

    static <T> T[] sortNamed(T[] arr) {
        try {
            Method get = arr.getClass().getComponentType().getMethod("getName");
            Arrays.sort(arr, new Comparator<T>() {
                public int compare(T l, T r) {
                    try {
                        return get.invoke(l).toString().compareTo(get.invoke(r).toString());
                    } catch (Exception e) { }
                    return 0;
                }
            });
        } catch (Exception e) { }
        return arr;
    }

    static int tryCallAll(Class c, String startsWith) {
        Method[] all = sortNamed(c.getDeclaredMethods());
        int fails = 0;
        int done = 0;

        String gname = c.getName();
        for (int k = 0; k < all.length; k++) {
            String name = all[k].getName();
            if (!name.startsWith(startsWith)) continue;

            done++;
            try {
                if ((boolean)all[k].invoke(null)) {
                    System.out.printf("%s#%s: ok\n", gname, name);
                } else {
                    System.out.printf("%s#%s: failed!\n", gname, name);
                    fails++;
                }
            } catch (Exception e) {
                System.out.printf("%s#%s: caught: '%s'\n", gname, name, e.toString());
                fails++;
            }
        }

        if (0 == done)
            System.out.printf("%s: no test\n", gname);

        return fails;
    }

    public static void main(String[] args) {
        int fails_total = 0;

        File[] ops = sortNamed(new File("src/ops").listFiles());
        for (int i = 0; i < ops.length; i++) {
            String name = ops[i].getName();
            if (!name.endsWith(".java")) continue;
            name = name.substring(0, name.length()-5);

            Class op;
            try { op = Class.forName("com.jexad.ops." + name); }
            catch (Exception e) { continue; }

            fails_total+= tryCallAll(op, "test");
        }

        if (0 == fails_total)
            fails_total+= tryCallAll(Cases.class, "case");

        System.exit(fails_total);
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
    static boolean caseA() {
        final String filename = "test/A/some.bin";
        final int list_off = 0x42;
        final int list_len = 3;

        Buf binfile_buf = new com.jexad.ops.File(Buf.encode(filename));

        // to make an editable copy:
        //binfile_buf.update();
        //binfile_buf = new Buf(binfile_buf.raw);

        Buf list_buf = new Slice(binfile_buf, new Num(list_off), new Num(list_off + list_len*4));
        Lst<Buf> list_32b = new Rect(list_buf, new Num(4));
        Lst<Num> pointers = new Map<Num>(Parse.class, list_32b);

        Lst<Buf> strings_starts = new Map<Buf>(Slice.class, new Repeat<Buf>(binfile_buf, new Num(list_len)), pointers);
        Lst<Buf> strings = new Map<Buf>(Delim.class, strings_starts); // new Repeat<Buf>(new Buf(new byte[] {0}), new Num(list_len)));

        Buf result = new Join(strings, new Buf(new byte[] {'\n'}));

        result.update();
        String res = result.decode();

        //binfile_buf.raw[list_off]++;
        //result.update();
        //log("result:\n'''\n" + result.decode() + "\n'''");

        return "that's\nall\nfolks".equals(res);
    }

    // B)
    //  a ZIP archive contains an entry with path "res/image.png" to a PNG file
    //  (3-bytes RGB) in which each channel is to be interpreted as (ASCII)
    //  text
    //  - open a buffer on the file
    //  - extract the bytes for "res/image.png"
    //  - decode the PNG image into bytes
    //  - for each channel, select only its bytes
    static boolean caseB() {
        return false;
    }

}
