package com.jexad.test;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Comparator;

import com.jexad.base.*;
import com.jexad.inter.*;
import com.jexad.ops.*;
//import ... zip.*;
//import ... png.*;

class Cases {

    static String readFile(String filename) {
        Buf b = new Read(Buf.encode(filename));
        b.update();
        return b.decode();
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

            if (0 == (Modifier.STATIC & all[k].getModifiers())) {
                System.out.printf("%s#%s: not static\n", gname, name);
                continue;
            }

            done++;
            try {
                if ((boolean)all[k].invoke(null)) {
                    System.out.printf("%s#%s: \033[32mok\033[m\n", gname, name);
                } else {
                    System.out.printf("%s#%s: \033[31mfailed!\033[m\n", gname, name);
                    fails++;
                }
            } catch (Exception e) {
                System.out.printf("%s#%s: \033[31mcaught:\033[m\n", gname, name);
                Throwable t = e;
                while (null != t.getCause()) t = t.getCause();
                t.printStackTrace(System.out);
                fails++;
            }
        }

        if (0 == done)
            System.out.printf("%s: \033[33mno test\033[m\n", gname);

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

        Buf binfile_buf = new Read(Buf.encode(filename));

        // to make an editable copy:
        //binfile_buf.update();
        //binfile_buf = new Buf(binfile_buf.raw);

        Buf list_buf = new Slice(binfile_buf, new Num(list_off), new Num(list_off + list_len*4));
        Lst<Buf> list_32b = new Rect(list_buf, new Num(4));
        Lst<Num> pointers = new Map<Num>(new Fun.ForClass(Parse.class, "doc"), list_32b);

        Lst<Buf> strings_starts = new Map<Buf>(new Fun.ForClass(Slice.class, "doc"), new Repeat<Buf>(binfile_buf, new Num(list_len)), pointers);
        Lst<Buf> strings = new Map<Buf>(new Fun.ForClass(Delim.class, "doc"), strings_starts); // new Repeat<Buf>(new Buf(new byte[] {0}), new Num(list_len)));

        Buf result = new Join(strings, new Buf(new byte[] {'\n'}));

        result.update();
        String res = result.decode();

        //binfile_buf.raw[list_off]++;
        //result.update();
        //log("result:\n'''\n" + result.decode() + "\n'''");

        return "that's\nall\nfolks".equals(res);
    }

    static boolean caseAScript() throws Lang.LangException {
        HashMap<String, Obj> scope = new HashMap();
        scope.put("filename", Buf.encode("test/A/some.bin"));
        scope.put("list_off", new Num(0x42));
        scope.put("list_len", new Num(3));
        scope.put("list_end", new Num(0x42 + 3*4));

        String script
            = "filebuf = read filename;\n"
            + "lst = rect (slice filebuf list_off list_end) 4;\n"
            + "ptrs = map parse lst;\n"
            + "\n"
            + "starts = map slice (repeat filebuf list_len) ptrs;\n"
            + "strs = map delim starts;\n"
            + "\n"
            + "return = join strs \"\\n\";\n"
            + "#_ = view_txt return;\n"
            ;

        Lang.Lookup ops = new Lang.Lookup.ClassesUnder("com.jexad.ops");
        Lang res = new Lang(script, new Lang.Lookup[] {ops}, scope);

        if (null == res.obj) {
            System.out.println("script result is null");
            return false;
        }

        res.obj.update();
        return "that's\nall\nfolks".equals(((Buf)res.obj).decode());
    }

    // B)
    //  a ZIP archive contains an entry with path "res/image.png" to a PNG file
    //  (3-bytes RGB) in which each channel is to be interpreted as (ASCII)
    //  text
    //  - open a buffer on the file
    //  - extract the bytes for "res/image.png"
    //  - decode the PNG image into bytes
    //  - for each channel, select only its bytes
    static boolean caseB() { return false; } /*{
        final String filename = "test/B/some.zip";
        final String respath = "res/image.png";
        final String r_txt = readFile("test/B/r.txt");
        final String g_txt = readFile("test/B/g.txt");
        final String b_txt = readFile("test/B/b.txt");

        Buf zipfilebytes = new Read(Buf.encode(filename));
        Lst<Buf> zipbufs = new ZipDecode(zipfilebytes);
        Buf pngfilebytes = new ZipEntry(zipbufs, Buf.encode(respath));
        Buf pngbuf = new PngDecode(pngfilebytes);
        Lst<Buf> rgbbufs = new PngRGB(pngbuf);
        Buf r = new Nth(rgbbufs, new Num(0));
        Buf g = new Nth(rgbbufs, new Num(1));
        Buf b = new Nth(rgbbufs, new Num(2));

        return r_txt.equals(r.decode())
            && g_txt.equals(g.decode())
            && b_txt.equals(b.decode())
            ;
    }/*-*/

}
