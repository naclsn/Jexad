package com.jexad.ops.zip;

import com.jexad.base.*;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class ZipDecode extends Num {

    public static final Fun fun = new Fun.ForClass(ZipDecode.class, "decodes bytes as a zip file");

    public static class Handle {

        private static class Pair { byte[] bytes; ZipEntry entry; Pair(byte[] bytes, ZipEntry entry) { this.bytes = bytes; this.entry = entry; } }
        private HashMap<String, Pair> loaded = new HashMap<String, Pair>();

        private void update(byte[] raw) {
            loaded.clear();

            ZipInputStream ztr = new ZipInputStream(new ByteArrayInputStream(raw));
            ZipEntry it;
            try {
                while ((it = ztr.getNextEntry()) != null) {
                    byte[] b = new byte[(int)it.getSize()];
                    loaded.put(it.getName(), new Pair(b, it));

                    int off = 0, len = b.length;
                    while (0 != len) {
                        int red = ztr.read(b, off, len);
                        if (red < 0) break;
                        off+= red;
                        len-= red;
                    }
                }
            } catch (Exception e) {
                System.err.println("ZipDecode: " + e);
                // TODO: errs and such...
            }
        }

        Set<String> paths() {
            // TODO: make things lazy
            //while (!= null) ..
            return loaded.keySet();
        }

        byte[] bytes(String path) {
            // TODO: make things lazy
            //while (!loaded.containsKey()) ..
            return loaded.get(path).bytes;
        }

        ZipEntry entry(String path) {
            // TODO: make things lazy
            //while (!loaded.containsKey()) ..
            return loaded.get(path).entry;
        }

    } // class Handle

    public static ArrayList<Handle> zips = new ArrayList<Handle>();

    Buf zipbytes;

    public ZipDecode(Buf zipbytes) {
        this.zipbytes = zipbytes;
        dec = false;
        iv = BigInteger.valueOf(zips.size());
        zips.add(new Handle());
        init();
    }

    @Override
    public Obj[] arguments() { return new Obj[] {zipbytes}; }

    @Override
    public void update() {
        zips.get(iv.intValue()).update(zipbytes.raw);
    }

}
