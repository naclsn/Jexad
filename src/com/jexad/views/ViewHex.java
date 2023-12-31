package com.jexad.views;

import com.jexad.base.*;
import java.awt.Graphics2D;

public class ViewHex extends Buf {

    public static final Fun fun = new Fun.ForClass(ViewHex.class, "view buffer as hex bytes");

    Buf content;
    View v;
    boolean edited;

    int line_count;

    public ViewHex(Buf content, Buf title) {
        this.content = content;
        v = new View(null != title ? title.decode() : null) {

            @Override
            protected void update() {
                double f = content.raw.length / 16.;
                line_count = (int)f;
                if (line_count < f) line_count++;

                scroll.maxVe = line_count - 1;
                //scroll.maxHz = getFontMetrics(getFont()).stringWidth() / scroll.unitHz;

                repaint();
            }

            @Override
            protected void render(Graphics2D g) {
                scroll.pageVe = (int)(getHeight()/scroll.unitVe+.5) - 1;

                int st = (int)scroll.ve;
                int ed = st + scroll.pageVe;

                if (line_count < ed) ed = line_count;

                int bw = g.getFontMetrics().stringWidth("00 ");
                int nroff = bw * 4;

                g.translate(-scroll.hz*scroll.unitHz, -(scroll.ve-1)*scroll.unitVe);
                g.setColor(getForeground());
                for (int i = st; i < ed; i++) {
                    String nr = Integer.toHexString(i*16).toUpperCase();
                    g.drawString(" " + "00000000".substring(nr.length()) + nr, 0, scroll.unitVe * (i+1));

                    for (int j = 0; j < 16; j++) {
                        int k = i*16 + j;
                        if (content.raw.length == k) break;

                        byte hl = content.raw[k];
                        int h = (hl&0xf0) >> 4, l = hl&0x0f;
                        g.drawString(new String(new byte[] {
                            (byte)((9 < h ? 'A'-10 : '0') + h),
                            (byte)((9 < l ? 'A'-10 : '0') + l),
                        }), nroff + bw*j, scroll.unitVe * (i+1));
                        g.drawString(new String(new byte[] {
                            (byte)(20 <= hl && hl < 127 ? hl : '.'),
                        }), nroff + bw*17 + (bw*j)/2, scroll.unitVe * (i+1));
                    }
                }

                g.translate(scroll.hz*scroll.unitHz, (scroll.ve-1)*scroll.unitVe);
                g.setColor(getForeground());
                g.fillRect(0, 0, getWidth(), (int)scroll.unitVe);
                g.translate(-scroll.hz*scroll.unitHz, 0);
                g.setColor(getBackground());
                for (int k = 0; k < 16; k++) {
                    byte n = (byte)((9 < k ? 'A'-10 : '0') + k);
                    g.drawString(new String(new byte[] {' ', n}), nroff + bw * k, scroll.unitVe*2/3);
                    g.drawString(new String(new byte[] {n}), nroff + bw*17 + (bw*k)/2, scroll.unitVe*2/3);
                }

                g.translate(scroll.hz*scroll.unitHz, 0);
                g.setColor(getForeground());
                g.fillRect(0, getHeight()-(int)scroll.unitVe, getWidth(), (int)scroll.unitVe);
                g.setColor(getBackground());
                g.drawString(" /" + Integer.toHexString(line_count*16-16).toUpperCase() + " (" + content.raw.length + " B)", 0, getHeight()-(int)scroll.unitVe/3);
            }

        }; // v = ..
        init();
    }
    public ViewHex(Buf content) { this(content, null); }

    @Override
    public Obj[] arguments() { return new Obj[] {content}; }

    @Override
    public void update() {
        raw = content.raw;
        v.update();
    }

}
