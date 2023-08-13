package com.jexad;

import com.jexad.base.*;
import com.jexad.inter.*;
import com.jexad.ops.*;
import com.jexad.views.*;
import java.awt.Frame;
import java.io.*;
import java.util.*;

class Jexad extends Frame {

    static void usage() {
        System.out.print
            ( "Usage (temp): <prog> -c <script>\n"
            + "                     -i [<prompt>]\n"
            + "                     -v txt|hex|img <file>\n"
            );
        System.exit(1);
    }

    static Buf input() {
        StringBuilder r = new StringBuilder();
        byte[] b = new byte[1024];
        try {
            int len;
            while (-1 != (len = System.in.read(b)))
                r.append(new String(b, 0, len));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        return Buf.encode(r.toString());
    }

    public static void main(String[] args) {
        if (0 == args.length) usage();

        switch (args[0]) {
            case "-h":
            case "--help":
                usage();
                return;

            case "-c":
                if (2 != args.length) usage();

                command(args[1]);
                return;

            case "-i":
                interactive(1 == args.length ? "" : args[1]);
                return;

            case "-v":
                if (3 != args.length) usage();

                Buf content = "-".equals(args[2])
                    ? input()
                    : new Read(Buf.encode(args[2]))
                    ;

                switch (args[1]) {
                    case "txt": new TxtView(content); break;
                    case "hex": new HexView(content); break;
                    case "img": new ImgView(content); break;
                }
                return;
        }
    } // main

    static HashMap<String, Obj> globalScope = new HashMap();
    static Lang.Lookup[] globalNames = new Lang.Lookup[] {
        new Lang.Lookup.ClassesUnder("com.jexad.ops"),
        new Lang.Lookup.ClassesUnder("com.jexad.views"),
        new Lang.Lookup.ClassesUnder("com.jexad.ops.math"),
        new Lang.Lookup.ClassesUnder("com.jexad.ops.png"),
        new Lang.Lookup.ClassesUnder("com.jexad.ops.zip"),
    };

    static void command(String script) {
        globalScope.put("input", input());
        try {
            Lang res = new Lang(script, globalNames, globalScope);
            if (null != res.obj) {
                res.obj.update();
                if (res.obj instanceof Buf)
                    System.out.print(((Buf)res.obj).decode());
                else
                    System.out.println(res.obj);
            }
        } catch (Lang.LangException e) {
            System.err.println(e);
            e.printLocationInfo(System.err);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    } // command

    static void interactive(String prompt) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";

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
                            else for (int k = 0; k < globalNames.length; k++) {
                                Fun fun = globalNames[k].lookup(name);
                                if (null != fun) Util.show(fun);
                            }
                        }
                        break;

                    case '@':
                        globalScope.clear();
                        System.gc();
                        System.runFinalization();
                        break;

                    case '.':
                        Buf b = new Read(Buf.encode(line.substring(1).trim()));
                        b.update();
                        line = b.decode();
                        System.out.println(line);

                    default:
                        try {
                            new Lang(line, globalNames, globalScope);
                        } catch (Lang.LangException e) {
                            System.err.println(e);
                            e.printLocationInfo(System.err);
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
                }

            } while ((line = br.readLine()) != null);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    } // interactive

}
