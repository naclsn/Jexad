package com.jexad.views;

import com.jexad.base.Buf;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.prefs.Preferences;

public abstract class View extends Canvas implements
    KeyListener,
    MouseWheelListener,
    PropertyChangeListener
{

    protected Buf content;
    protected Preferences prefs;

    protected class ScrollInfo {
        float ve = 0;
        float hz = 0;
        float minVe = 0;
        float minHz = 0;
        float maxVe = 1024;
        float maxHz = 1024;
        float unitVe = 16;
        float unitHz = 16;
        int pageVe = 16;
        int pageHz = 16;

        // returns true if changed
        boolean addVe(int n) {
            float pve = ve;
            ve = ve + n;
            if (ve < minVe) ve = minVe;
            else if (maxVe < ve) ve = maxVe;
            return pve != ve;
        }
        // returns true if changed
        boolean addHz(int n) {
            float phz = hz;
            hz = hz + n;
            if (hz < minHz) hz = minHz;
            else if (maxHz < hz) hz = maxHz;
            return phz != hz;
        }
        // returns true if changed
        boolean cap() {
            boolean did = false;
            if (ve < minVe) { did = true; ve = minVe; }
            else if (maxVe < ve) { did = true; ve = maxVe; }
            if (hz < minHz) { did = true; hz = minHz; }
            else if (maxHz < hz) { did = true; hz = maxHz; }
            return did;
        }
    }
    protected ScrollInfo scroll = new ScrollInfo();

    // (anti-aliasing and such)
    private Map desktophints;

    public View() {
        prefs = Preferences.userNodeForPackage(getClass()); // yes, this is child's class

        Toolkit tk = Toolkit.getDefaultToolkit();
        desktophints = (Map)tk.getDesktopProperty("awt.font.desktophints");
        if (null != desktophints) tk.addPropertyChangeListener("awt.font.desktophints", this);

        setSize(620, 250);
        reloadPrefs();

        addKeyListener(this);
        addMouseWheelListener(this);
    }

    public void reloadPrefs() {
        Font f = new Font(
            prefs.get("font.name", "Monospaced"),
            Font.PLAIN,
            prefs.getInt("font.size", 16)
        );
        setFont(f);
        scroll.unitVe = scroll.unitHz = getFontMetrics(getFont()).getHeight();

        Color bg = parseColor(prefs.get("background", ""));
        if (null != bg) setBackground(bg);

        Color fg = parseColor(prefs.get("foreground", ""));
        if (null != fg) setForeground(fg);
    }

    public void setContent(Buf nullIfKeep) {
        if (null != nullIfKeep) content = nullIfKeep;
        content.update();
        update();
    }

    public Buf getContent() {
        return content;
    }

    @Override
    public void paint(Graphics g0) {
        if (null == getBufferStrategy()) createBufferStrategy(2);

        Graphics2D g = (Graphics2D)g0;
        if (null != desktophints) g.addRenderingHints(desktophints);
        render(g);
    }

    // once, when the content is changed/updated
    protected abstract void update();
    // called from `Canvas#paint`
    protected abstract void render(Graphics2D g);

    @Override
    public void keyPressed(KeyEvent e) {
        boolean shift = 0 != (InputEvent.SHIFT_DOWN_MASK & e.getModifiersEx());
        boolean ctrl = 0 != (InputEvent.CTRL_DOWN_MASK & e.getModifiersEx());

        switch (e.getKeyCode()) {
            case KeyEvent.VK_PAGE_UP:
                if (shift
                    ? scroll.addHz(1-scroll.pageVe)
                    : scroll.addVe(1-scroll.pageVe)
                    ) repaint();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                if (shift
                    ? scroll.addHz(scroll.pageHz-1)
                    : scroll.addVe(scroll.pageHz-1)
                    ) repaint();
                break;

            case KeyEvent.VK_B:
                if (scroll.addVe(1-scroll.pageVe)) repaint();
                break;
            case KeyEvent.VK_F:
                if (scroll.addVe(scroll.pageVe-1)) repaint();
                break;

            case KeyEvent.VK_U:
                if (scroll.addVe(1-scroll.pageVe/2)) repaint();
                break;
            case KeyEvent.VK_D:
                if (scroll.addVe(scroll.pageVe/2-1)) repaint();
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_K:
            case KeyEvent.VK_P:
                if (scroll.addVe(-1)) repaint();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_J:
            case KeyEvent.VK_N:
                if (scroll.addVe(+1)) repaint();
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_H:
                if (scroll.addHz(-1)) repaint();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L:
                if (scroll.addHz(+1)) repaint();
                break;

            case KeyEvent.VK_Q:
                if (ctrl) System.exit(0); //dispose(); // ZZZ: (whatever)
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int u = e.getUnitsToScroll();
        if (0 != (InputEvent.SHIFT_DOWN_MASK & e.getModifiersEx())
            ? scroll.addHz(u)
            : scroll.addVe(u)
            ) repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        desktophints = (Map)e.getNewValue();
        repaint();
    }

    protected static Color parseColor(String s) {
        if (s.startsWith("#")) {
            return new Color(Integer.parseInt(4 == s.length()
                ? new String(new char[] {s.charAt(1), s.charAt(1), s.charAt(2), s.charAt(2), s.charAt(3), s.charAt(3)})
                : s.substring(1)
                , 16));
        }

        switch (s.toLowerCase()) {
            case "black": return Color.black;
            case "blue": return Color.blue;
            case "cyan": return Color.cyan;
            case "darkgray": return Color.darkGray;
            case "gray": return Color.gray;
            case "green": return Color.green;
            case "lightgray": return Color.lightGray;
            case "magenta": return Color.magenta;
            case "orange": return Color.orange;
            case "pink": return Color.pink;
            case "red": return Color.red;
            case "white": return Color.white;
            case "yellow": return Color.yellow;
        }

        return null; // TODO: throw
    }

}
