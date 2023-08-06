package com.jexad.base;

public class Num implements Obj {

    public Obj[] arguments() { return new Obj[0]; }
    protected boolean uptodate;
    public void outdated() { uptodate = false; }
    public void update() { }

    // for now everything is `int` (ie. 32 bits signed integer)
    public int val; // readonly
    public Num(int val) { this.val = val; }
    public Num() { this(0); }

    @Override
    public String toString() {
        return "" + val;
    }

}
