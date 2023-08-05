package com.jexad.base;

public class Obj {

    public Obj[] arguments() { return new Obj[0]; }

    protected boolean uptodate;
    public void outdated() { uptodate = false; }

    public void update() { }

}
