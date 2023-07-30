package com.jexad;

import com.jexad.base.Buf;
import com.jexad.ops.File;
import com.jexad.views.HexView;
import com.jexad.views.TextView;
import com.jexad.views.View;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class Jexad extends Frame {

    public Jexad(Buf content) {
        View t = new TextView();
        t.setContent(content);
        add(t);

        //View h = new HexView();
        //h.setContent(content);
        //add(h);

        //setLayout(new FlowLayout());
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
                    System.out.println("Usage (temp): <prog> --help|--font-list|--props-list|--zip|<filename>");
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

                default:
                    filename = args[0];
            }
        }

        new Jexad(new File(Buf.encode(filename)));
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

}
