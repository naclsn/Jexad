package com.jexad.views;

import com.jexad.base.*;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class TxtView extends View<Buf> {

    public TxtView(Buf content, String title) { super(content, title); }
    public TxtView(Buf content, Buf title) { super(content, title.decode()); }
    public TxtView(Buf content) { super(content, null); }

    ArrayList<String> lines = new ArrayList();
    int longest;
    int char_count;

    @Override
    protected void update() {
        String text = content.decode();
        char_count = text.length();

        lines.clear();
        int longest_length = 0;

        int l = 0;
        for (int k = 0; k < char_count; k++) {
            char ch = text.charAt(k);
            if ('\n' == ch) {
                lines.add(text.substring(l, k));
                if (longest_length < k-l) {
                    longest = lines.size()-1;
                    longest_length = k-l;
                }
                l = k+1;
            }
            // TODO: '\t', unprintable chars, ...
        }

        if (l < char_count) {
            lines.add(text.substring(l));
            if (longest_length < char_count-l) {
                longest = lines.size()-1;
                longest_length = char_count-l;
            }
        }

        scroll.maxVe = lines.size() - 1;
        scroll.maxHz = getFontMetrics(getFont()).stringWidth(lines.get(longest)) / scroll.unitHz;
    }

    @Override
    protected void render(Graphics2D g) {
        scroll.pageVe = (int)(getHeight()/scroll.unitVe+.5) - 1;

        int st = (int)scroll.ve;
        int ed = st + scroll.pageVe;

        int line_count = lines.size();
        if (line_count < ed) ed = line_count;
        int nr_max_len = (line_count+"").length();

        g.translate(-scroll.hz*scroll.unitHz, -scroll.ve*scroll.unitVe);
        g.setColor(getForeground());
        for (int k = st; k < ed; k++) {
            String nr = k+1+"";
            g.drawString(" " + "        ".substring(8-nr_max_len + nr.length()) + nr + " " + lines.get(k), 0, scroll.unitVe * (k+1));
        }

        g.translate(scroll.hz*scroll.unitHz, scroll.ve*scroll.unitVe);
        g.setColor(getForeground());
        g.fillRect(0, getHeight()-(int)scroll.unitVe, getWidth(), (int)scroll.unitVe);
        g.setColor(getBackground());
        g.drawString(" /" + line_count + " l (" + char_count + " c)", 0, getHeight()-(int)scroll.unitVe/3);
    }

}
