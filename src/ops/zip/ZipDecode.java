package com.jexad.ops.zip;

import com.jexad.base.Buf;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class ZipDecode extends Num {

    public String getHelp() { return "parse bytes as a zip file"; }

    public static class Handle {

        HashMap<String, byte[]> loaded = new HashMap();

        Handle() { }

        void update(byte[] raw) {
            loaded.clear();

            ZipInputStream ztr = new ZipInputStream(new ByteArrayInputStream(raw));
            ZipEntry it;
            try {
                while ((it = ztr.getNextEntry()) != null) {
                    // XXX: idea would to skip directory (they have a size of 0
                    //      and a trailing '/' - test below would also skip
                    //      legitimately empty files); for now keep, could be
                    //      set to eg. linewise of identified direct children...
                    //if (0 == it.getSize()) continue;
                    byte[] b = new byte[(int)it.getSize()];
                    loaded.put(it.getName(), b);

                    int off = 0, len = b.length;
                    while (0 != len) {
                        int red = ztr.read(b, off, len);
                        if (red < 0) break;
                        off+= red;
                        len-= red;
                    }
                }
            } catch (Exception e) {
                System.err.println("ZipDecode$Handle: " + e);
                // TODO: errs and such...
            }
        }

        Set<String> all() {
            // TODO: make things lazy
            //while (!= null) ..
            return loaded.keySet();
        }

        byte[] get(String path) {
            // TODO: make things lazy
            //while (!loaded.containsKey()) ..
            return loaded.get(path);
        }

    }
    public static ArrayList<Handle> zips = new ArrayList();

    Buf zipbytes;

    public ZipDecode(Buf zipbytes) {
        this.zipbytes = zipbytes;
        val = zips.size();
        zips.add(new Handle());
    }

    @Override
    public Obj[] arguments() { return new Obj[] {zipbytes}; }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        zipbytes.update();
        zips.get(val).update(zipbytes.raw);
    }

    public static boolean notest() {
        return false; // TODO: test
    }

}
