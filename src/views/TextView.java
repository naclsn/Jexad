package com.jexad.views;

import java.awt.Font;
import java.awt.TextArea;

public class TextView extends View<TextArea> {

    public TextView() {
        super(new TextArea());
    }

    @Override
    protected void update() {
        // TODO: not using decode as it does not handle arbitrary bytes well
        comp.setText(content.decode());
    }

    @Override
    public void updatePrefs() {
        comp.setFont(new Font(
            prefs.get("font.name", "Monospaced"),
            Font.PLAIN,
            prefs.getInt("font.size", 16)
        ));
    }

}
