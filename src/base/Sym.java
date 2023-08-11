package com.jexad.base;

public class Sym extends Obj {

    public final String str;

    public Sym(String str) { this.str = str; }

    @Override
    public String toString() { return ":" + str; }

}
