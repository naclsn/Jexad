package com.jexad;

import com.jexad.base.*;
import com.jexad.inter.*;
import java.awt.Frame;
import java.io.*;
import java.util.*;

class Jexad extends Frame {

    static void usage(String reason) {
        if (null != reason) System.out.println("Oops: " + reason);
        else System.out.print
            ( "Usage (temp): <prog> -c <script>\n"
            + "                     -i [<prompt>]\n"
            + "\n"
            + "Must be present before the -c/-i/-v:\n"
            + "   -xl <lookup-class-name>  (repeatable)\n"
            + "         specify an additional lookup class (to provide\n"
            + "         more `FunctionName`s); this will requier the\n"
            + "         classpath to contain it somewhere (eg. a jar)\n"
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

    static HashMap<String, Obj> globalScope = new HashMap();
    static Lang.Lookup[] globalNames;

    static void makeLookupsWithUsers(ArrayList<Lang.Lookup> user_lus) {
        user_lus.add(new Lang.Lookup.ClassesUnder("com.jexad.ops"));
        user_lus.add(new Lang.Lookup.ClassesUnder("com.jexad.ops.benc"));
        user_lus.add(new Lang.Lookup.ClassesUnder("com.jexad.ops.math"));
        user_lus.add(new Lang.Lookup.ClassesUnder("com.jexad.ops.png"));
        user_lus.add(new Lang.Lookup.ClassesUnder("com.jexad.ops.zip"));
        user_lus.add(new Lang.Lookup.ClassesUnder("com.jexad.views"));

        globalNames = new Lang.Lookup[user_lus.size()];
        user_lus.toArray(globalNames);
    }

    public static void main(String[] args) {
        ArrayList<Lang.Lookup> user_lus = new ArrayList();

        if (0 == args.length) usage("no argument (try -h)");

        for (int k = 0; k < args.length; k++) switch (args[k]) {
            case "-xl":
                if (k+1 >= args.length) usage("missing class name");

                try {
                    user_lus.add((Lang.Lookup)Class.forName(args[k+1]).getConstructor().newInstance());
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println(" `-> occurred while trying to add user-lookup: " + args[k+1]);
                    System.exit(1);
                }
                k++;
                break;

            case "-c":
                if (k+1 >= args.length) usage("missing command");

                makeLookupsWithUsers(user_lus);
                command(args[k+1]);
                return;

            case "-i":
                makeLookupsWithUsers(user_lus);
                interactive(k+1 < args.length ? args[k+1] : "");
                return;

            case "-h":
            case "--help":
                usage(null);
                return;

            default:
                usage("unknown argument: " + args[k]);
                return;

        }
    } // main

    static void command(String script) {
        globalScope.put("input", input());
        try {
            Lang res = new Lang(script, globalNames, globalScope);
            if (null != res.obj) {
                if (res.obj instanceof Buf)
                    System.out.print(((Buf)res.obj).decode());
                else
                    System.out.println(res.obj);
            }
        } catch (Lang.LangException e) {
            System.err.println(e);
            Throwable c = e;
            while (null != (c = c.getCause())) System.err.println(" `-> " + c);
            e.printLocationInfo(System.err);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    } // command

    static void interactive(String prompt) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";

        try {
            for (System.out.print(prompt); (line = br.readLine()) != null; System.out.print(prompt)) {
                if (line.isEmpty()) continue;

                switch (line.charAt(0)) {
                    case '?':
                        if (1 == line.length()) {
                            Object[] names = globalScope.keySet().toArray();
                            System.out.println("(global scope)");
                            for (int k = 0; k < names.length; k++)
                                System.out.println(names[k]);
                            break;
                        }

                        try {
                            new Lang("_ = " + line.substring(1).trim(), globalNames, globalScope);
                            Util.show(globalScope.get("_"));
                        } catch (Lang.LangException e) {
                            System.err.println(e);
                            Throwable c = e;
                            while (null != (c = c.getCause())) System.err.println(" `-> " + c);
                            e.printLocationInfo(System.err);
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
                        break;

                    case '!':
                        String name = line.substring(1).trim();
                        Obj obj = globalScope.get(name);
                        if (null != obj) {
                            obj.updateAndPropagate();
                            System.out.printf("updated from '%s' (%s)\n", name, obj.getClass().getSimpleName());
                        } else System.out.println("noting to update: no such name");
                        break;

                    case '@':
                        globalScope.clear();
                        System.gc();
                        System.runFinalization();
                        break;

                    case '.':
                        try {
                            FileInputStream f = new FileInputStream(new File(line.substring(1).trim()));
                            byte[] raw = new byte[f.available()];
                            f.read(raw);
                            f.close();
                            line = new String(raw);
                            System.out.println(line);
                        } catch (IOException e) {
                            System.out.println("maybe no such file or something: " + e);
                            break;
                        }

                    default:
                        try {
                            new Lang(line, globalNames, globalScope);
                        } catch (Lang.LangException e) {
                            System.err.println(e);
                            Throwable c = e;
                            while (null != (c = c.getCause())) System.err.println(" `-> " + c);
                            e.printLocationInfo(System.err);
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    } // interactive

}
