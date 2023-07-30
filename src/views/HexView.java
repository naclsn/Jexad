package com.jexad.views;

import java.awt.Graphics2D;

public class HexView extends View {

    @Override
    protected void update() {
    }

    @Override
    protected void render(Graphics2D g) {
        g.drawString("coucou (niy: HexView)", 10, 10);
    }

}
