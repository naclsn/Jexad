package com.jexad;

import com.jexad.base.*;
import com.jexad.inter.*;
import com.jexad.ops.*;
import com.jexad.views.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

class Jexad extends Frame {

    public static void main(String[] args) {
        String filename = "build.xml";
        if (0 < args.length) {
            switch (args[0]) {
                case "-h":
                case "--help":
                    System.out.println("Usage (temp): <prog> --help|--font-list|--props-list|--zip|--lang|<filename>");
                    return;

                case "--font-list":
                    System.out.println("Font list: " + Arrays.toString(Toolkit.getDefaultToolkit().getFontList()));
                    return;

                case "--props-list":
                    System.getProperties().list(System.out);
                    return;

                case "--zip":
                    mainZip(args);
                    return;

                case "--lang":
                    mainLang(args);
                    return;

                default:
                    filename = args[0];
            }
        } // else no arguments

        //new Jexad(new Read(Buf.encode(filename)));
        Buf filebuf = new Read(Buf.encode(filename));
        new HexView(filebuf);
        new ImgView(filebuf);
        new TxtView(filebuf);
    } // main

    public static void mainZip(String[] args) {
        if (args.length <3) {
            System.out.println("Usage (temp): <prog> --zip <zipfilename> --entry-list|<entryname>");
            return;
        }

        try {
            ZipFile f = new ZipFile(args[1]);

            if ("--entry-list".equals(args[2])) {
                System.out.println("Entry list:");
                for (Enumeration e = f.entries(); e.hasMoreElements(); ) {
                    ZipEntry it = (ZipEntry)e.nextElement();
                    long size = it.getSize();
                    String ssize
                        = 1000000 < size ? size/1000000 + " MB"
                        : 1000 < size ? size/1000 + " KB"
                        : size + " B";
                    System.out.println(" - " + it.getName() + " (" + ssize + ")");
                }

            } else {
                ZipEntry it = f.getEntry(args[2]);
                byte[] b = new byte[(int)it.getSize()];
                f.getInputStream(it).read(b);

                Buf contentbuf = new Buf(b);
                new HexView(contentbuf);
                new ImgView(contentbuf);
                new TxtView(contentbuf);
            }

            f.close();
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    } // mainZip

    public static void mainLang(String[] args) {
        String prompt = 2 == args.length ? args[1] : "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";

        HashMap<String, Obj> globalScope = new HashMap();
        Lang.Lookup[] globalNames = new Lang.Lookup[] {
            new Lang.Lookup.ClassesUnder("com.jexad.ops"),
            new Lang.Lookup.ClassesUnder("com.jexad.views"),
            new Lang.Lookup.ClassesUnder("com.jexad.ops.math"),
            new Lang.Lookup.ClassesUnder("com.jexad.ops.png"),
            new Lang.Lookup.ClassesUnder("com.jexad.ops.zip"),
        };

        try {
            do {
                System.out.print(prompt);
                if (line.isEmpty()) continue;

                switch (line.charAt(0)) {
                    case '?':
                        if (1 == line.length()) {
                            Object[] names = globalScope.keySet().toArray();
                            System.out.println("(global scope)");
                            for (int k = 0; k < names.length; k++)
                                System.out.println(names[k]);
                        } else {
                            String name = line.substring(1).trim();
                            Obj obj = globalScope.get(name);
                            if (null != obj) Util.show(obj);
                            else {
                                for (int k = 0; k < globalNames.length; k++) {
                                    Fun fun = globalNames[k].lookup(name);
                                    if (null != fun) Util.show(fun);
                                }
                            }
                        }
                        break;

                    //case '@':
                    //    globalScope.clear();
                    //    System.gc();
                    //    System.runFinalization();
                    //    break;

                    case '.':
                        Buf b = new Read(Buf.encode(line.substring(1).trim()));
                        b.update();
                        line = b.decode();
                        System.out.println(line);

                    default:
                        try {
                            Lang res = new Lang(line, globalNames, globalScope);
                            //if (null != res.obj) Util.show(res.obj);
                        } catch (Lang.LangException e) {
                            System.err.println(e);
                            e.printLocationInfo(System.err);
                            //e.printStackTrace(System.err);
                        }
                }

            } while ((line = br.readLine()) != null);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    } // mainLang

}
