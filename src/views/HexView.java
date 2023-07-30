package com.jexad.views;

import java.awt.Graphics2D;

public class HexView extends View {

    int line_count;

    @Override
    protected void update() {
        double f = content.raw.length / 16.;
        line_count = (int)f;
        if (line_count < f) line_count++;

        scroll.maxVe = line_count - 1;
        //scroll.maxHz = getFontMetrics(getFont()).stringWidth() / scroll.unitHz;
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
            g.drawString(new String(new byte[] {
                ' ',
                (byte)((9 < k ? 'A'-10 : '0') + k),
            }), nroff + bw * k, scroll.unitVe*2/3);
            g.drawString(new String(new byte[] {
                (byte)((9 < k ? 'A'-10 : '0') + k),
            }), nroff + bw*17 + (bw*k)/2, scroll.unitVe*2/3);
        }

        g.translate(scroll.hz*scroll.unitHz, 0);
        g.setColor(getForeground());
        g.fillRect(0, getHeight()-(int)scroll.unitVe, getWidth(), (int)scroll.unitVe);
        g.setColor(getBackground());
        g.drawString(" /" + line_count + " (" + content.raw.length + " B)", 0, getHeight()-(int)scroll.unitVe/3);
    }

}
