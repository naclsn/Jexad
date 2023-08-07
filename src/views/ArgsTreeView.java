package com.jexad.views;

import com.jexad.base.Buf;
import com.jexad.base.Fun;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class ArgsTreeView extends View {

    public ArgsTreeView(Buf content, String title) { super(content, title); }
    public ArgsTreeView(Lst content, String title) { super(content, title); }
    public ArgsTreeView(Num content, String title) { super(content, title); }
    public ArgsTreeView(Buf content, Buf title) { super(content, title.decode()); }
    public ArgsTreeView(Lst content, Buf title) { super(content, title.decode()); }
    public ArgsTreeView(Num content, Buf title) { super(content, title.decode()); }
    public ArgsTreeView(Buf content) { super(content, null); }
    public ArgsTreeView(Lst content) { super(content, null); }
    public ArgsTreeView(Num content) { super(content, null); }

    static enum NodeKind { BUF, NUM, LST, FUN }

    class Node {

        NodeKind kind;
        boolean raw; // does not apply to FUN
        String text;
        Node[] chld;
        Obj obj;
        boolean folded;
        Rectangle2D area;
        boolean hover;

        Node(Obj o) {
            kind= o instanceof Buf ? NodeKind.BUF
                : o instanceof Num ? NodeKind.NUM
                : o instanceof Lst ? NodeKind.LST
                : o instanceof Fun ? NodeKind.FUN
                : null; // idealy unreachable
            Class cl = o.getClass();
            raw = cl == Buf.class || cl == Num.class || cl == Lst.class;

            text = cl.getName();
            text = text.substring(text.lastIndexOf('.')+1);

            chld = null;
            obj = o;

            folded = true;
            area = null;
            hover = false;
        }

        void load() {
            Obj[] args = obj.arguments();
            obj = null;

            chld = new Node[args.length];
            for (int k = 0; k < args.length; k++)
                chld[k] = new Node(args[k]);
        }

        void toggle() {
            if (folded && null != obj) load();
            folded = !folded;
        }

        void render(Graphics2D g) {
            g.setColor(getForeground());
            g.drawString(raw ? "=" : folded ? "+" : "-", 0, 0);

            switch (kind) {
                case BUF: g.drawString("B", scroll.unitVe/2, 0); break;
                case NUM: g.drawString("N", scroll.unitVe/2, 0); break;
                case LST: g.drawString("L", scroll.unitVe/2, 0); break;
            }

            g.setColor(hover ? parseColor("red") : getForeground());
            g.drawString(text, scroll.unitVe, 0);

            AffineTransform t = g.getTransform();
            area = g.getFontMetrics().getStringBounds(text, g);
            area.setRect(
                    t.getTranslateX()+scroll.unitVe, t.getTranslateY()-scroll.unitHz,
                    area.getWidth(), area.getHeight());

            if (!folded && null != chld && 0 < chld.length) {
                g.translate(scroll.unitVe, 0);
                for (int k = 0; k < chld.length; k++) {
                    g.translate(0, scroll.unitHz);
                    chld[k].render(g);
                }
                g.translate(-scroll.unitVe, 0);
            }
        }

        boolean clicked() {
            if (hover) {
                toggle();
                return true;
            }
            if (!folded && null != chld && 0 < chld.length) {
                for (int k = 0; k < chld.length; k++)
                    if (chld[k].clicked()) return true;
            }
            return false;
        }

        boolean moved(int x, int y) {
            boolean pho = hover;
            hover = area.contains(x, y);
            boolean r = pho != hover;
            if (!folded && null != chld && 0 < chld.length) {
                for (int k = 0; k < chld.length; k++)
                    r = r || chld[k].moved(x, y);
            }
            return r;
        }

    } // class Node

    Node root;
    Node target;

    @Override
    protected void update() {
        root = new Node(content);
        target = null;
    }

    @Override
    protected void render(Graphics2D g) {
        g.translate(0, scroll.unitHz);
        root.render(g);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (root.clicked()) repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (root.moved(e.getX(), e.getY())) repaint();
    }

}
