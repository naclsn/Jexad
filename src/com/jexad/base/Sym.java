package com.jexad.base;

public class Sym extends Obj {

    public final String str;

    public Sym(String str) { this.str = str; }

    @Override
    public String toString() { return ":" + str; }

    public <T extends Enum<T>> T to(Class<T> enun) {
        T[] l = (T[])enun.getEnumConstants();
        for (int k = 0; k < l.length; k++) {
            if (str.equals(l[k].name()))
                return l[k];
        }
        return null;
    }

}
