package com.jexad.views;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class TextView extends View {

    ArrayList<String> lines = new ArrayList();
    int longest_length;

    @Override
    protected void update() {
        String text = content.decode();
        int len = text.length();

        lines.clear();

        int l = 0;
        for (int k = 0; k < len; k++) {
            char ch = text.charAt(k);
            if ('\n' == ch) {
                lines.add(text.substring(l, k));
                if (longest_length < k-l) longest_length = k-l;
                l = k+1;
            }
            // TODO: '\t', unprintable chars, ...
        }

        if (l < len) {
            lines.add(text.substring(l));
            if (longest_length < len-l) longest_length = len-l;
        }

        int total_line_count = lines.size();
        scroll.maxVe = total_line_count - 1; // 1 line is visible when scrolling max down
        //scroll.maxHz = 42;

        int fit_line_count = (int)(getHeight()/scroll.unitVe+.5);
        scroll.pageVe = 19; //fit_line_count;
    }

    @Override
    protected void render(Graphics2D g) {
        int st = (int)scroll.ve;
        int ed = st + scroll.pageVe;

        int total_line_count = lines.size();
        if (total_line_count < ed) ed = total_line_count;
        int nr_max_len = (total_line_count+"").length();

        g.translate(-scroll.hz*scroll.unitHz, -scroll.ve*scroll.unitVe);
        for (int k = st; k < ed; k++) {
            String nr = k + "";
            g.drawString("        ".substring(8-nr_max_len + nr.length()) + k + " " + lines.get(k), 0, scroll.unitVe * (k+1));
        }
    }

}
