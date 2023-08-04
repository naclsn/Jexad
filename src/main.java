package com.jexad;

import com.jexad.base.Buf;
import com.jexad.base.Obj;
import com.jexad.base.Util;
import com.jexad.inter.Lang;
import com.jexad.ops.Read;
import com.jexad.views.HexView;
import com.jexad.views.ImgView;
import com.jexad.views.TxtView;
import com.jexad.views.View;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class Jexad extends Frame {

    public Jexad(Buf content) {
        View t = new ImgView();
        t.setContent(content);
        add(t);

        setSize(640, 480);
        setTitle("hi :3");
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent _we) {
                dispose();
            }
        });

        t.requestFocusInWindow();
    }

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
        }

        new Jexad(new Read(Buf.encode(filename)));
    }

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

                new Jexad(new Buf(b));
            }

            f.close();
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    }

    public static void mainLang(String[] args) {
        String prompt = 2 == args.length ? args[1] : "";

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        StringBuilder script = new StringBuilder();
        String prevScript = "";

        HashMap<String, Obj> globalScope = new HashMap();
        Lang.Lookup[] globalNames = new Lang.Lookup[] {
            new Lang.LookupJavaClassesUnder("com.jexad.ops"),
        };

        try {
            System.out.print(prompt);
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                switch (line.charAt(0)) {
                    case '.':
                        try {
                            script.append(line.substring(1));
                            prevScript = script.toString();
                            Lang res = new Lang(prevScript, globalNames, globalScope);
                            Obj obj = res.obj;
                            Util.show(obj);
                        } catch (Lang.LangException e) {
                            System.err.println(e);
                        }

                    case '!':
                        script = new StringBuilder();
                        break;

                    case '&':
                        script.append(prevScript);
                        break;

                    case '%':
                        System.out.print(script);
                        break;

                    case '/':
                        int sep = 1;
                        do sep = line.indexOf('/', sep);
                        while (0 < sep && '\\' == line.charAt(sep-1));
                        script = new StringBuilder(script.toString().replaceFirst(line.substring(1, sep), line.substring(sep)));
                        break;

                    case '=':
                        if (1 == line.length()) {
                            Object[] names = globalScope.keySet().toArray();
                            for (int k = 0; k < names.length; k++)
                                System.out.println(names[k]);
                        } else {
                            Obj obj = globalScope.get(line.substring(1));
                            Util.show(obj);
                        }
                        break;

                    case '?':
                        System.out.println
                            ( "special leader characters:\n"
                            + " '.' - execute the written script, makes it previous\n"
                            + " '!' - abort (erase the written script)\n"
                            + " '&' - insert the previous script\n"
                            + " '%' - print the written script\n"
                            + " '/' - perform `replaceFirst` on the written script\n"
                            + " '=' - show the value for a given name\n"
                            );
                        break;

                    default:
                        script.append(line+"\n");
                }
                System.out.print(prompt);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

}
