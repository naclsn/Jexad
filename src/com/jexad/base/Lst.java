package com.jexad.base;

public class Lst<T extends Obj> extends Obj {

    public T[] arr; // readonly
    public Lst(T[] arr) { this.arr = arr; }
    public Lst() { this(null); }

    public int length() { return arr.length; }
    public T at(int k) { return arr[k]; }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder("{\n");
        for (int k = 0; k < arr.length; k++)
            r.append("   " + arr[k].toString().replace("\n", "\n   ") + ",\n");
        return r.toString() + "}";
    }

}
