package com.jexad.base;

public class Obj {

    //protected Obj() {
    //    System.out.println("i have been born! " + getClass());
    //}
    //@Override
    //protected void finalize() throws Throwable {
    //    System.out.println("i have been finalized! " + getClass());
    //}

    public Obj[] arguments() { return new Obj[0]; }
    protected boolean uptodate;
    public void outdated() { uptodate = false; }
    public void update() { }

}
