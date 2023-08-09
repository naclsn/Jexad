package com.jexad.base;

public class Lst<T extends Obj> extends Obj {

    public T[] arr; // readonly
    public Lst(Class item_class, T[] arr) {
        this.item_class = item_class;
        this.arr = arr;
    }
    public Lst(Class item_class) { this(item_class, null); }

    Class item_class;
    public Class getItemClass() { return item_class; }

    public int length() { return arr.length; }
    public T at(int k) { return arr[k]; }

    @Override
    public String toString() {
        String cln = item_class.getName();
        String r = "<" + cln.substring(cln.lastIndexOf('.')+1) + ">{\n";
        for (int k = 0; k < arr.length; k++) {
            r+= "   " + arr[k].toString().replace("\n", "\n   ") + ",\n";
        }
        return r + "}";
    }

}
