package com.jexad.base;

public class Sym extends Obj {

    public final String str;

    public Sym(String str) { this.str = str; }

    @Override
    public String toString() { return ":" + str; }

    // note: assumes the enum uses upper case (ie. it will toUpperCase the symbol
    // for the comparisons)
    public <T extends Enum> T to(Class enun) {
        String up = str.toUpperCase();
        T[] l = (T[])enun.getEnumConstants();
        for (int k = 0; k < l.length; k++) {
            if (up.equals(l[k].name()))
                return l[k];
        }
        return null;
    }


}
