package com.jexad.base;

public class Lst extends Obj {

    public Obj[] arr; // readonly
    public Lst(Obj[] arr) { this.arr = arr; }
    public Lst() { this(new Obj[0]); }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder("{\n");
        for (int k = 0; k < arr.length; k++)
            r.append("   " + arr[k].toString().replace("\n", "\n   ") + ",\n");
        return r.toString() + "}";
    }

}
