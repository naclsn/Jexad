package com.jexad.views;

import java.awt.Component;
import java.util.prefs.Preferences;

import com.jexad.base.Buf;

public abstract class View<Comp extends Component> {

    public Comp comp; // readonly

    protected Buf content;
    protected Preferences prefs;

    public View(Comp comp) {
        this.comp = comp;
        this.prefs = Preferences.userNodeForPackage(this.getClass());
        updatePrefs();
    }

    public void updateContent(Buf nullIfKeep) {
        if (null != nullIfKeep) content = nullIfKeep;
        content.update();
        update();
    }

    protected abstract void update();
    public abstract void updatePrefs();

}
