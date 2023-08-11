package com.jexad.base;

public class Obj {

    //protected Obj() {
    //    System.out.println("i have been born! " + getClass());
    //}
    //@Override
    //protected void finalize() throws Throwable {
    //    System.out.println("i have been finalized! " + getClass());
    //}

    public Class baseClass() {
        return
            ( this instanceof Buf ? Buf.class
            : this instanceof Num ? Num.class
            : this instanceof Lst ? Lst.class
            : this instanceof Fun ? Fun.class
            : this instanceof Sym ? Sym.class
            : Obj.class
            );
    }

    public Obj[] arguments() { return new Obj[0]; }
    protected boolean uptodate;
    public void outdated() { uptodate = false; }
    public void update() { }

}
