package com.jexad.base;

import java.lang.ref.WeakReference;

// abstract: arguments, update; impl must super.init as soon as arguments is usable
// but the class isn't marked abstract because it's a pain otherwise
// and because you shouldn't create new child classes >:(
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

    public Buf asBuf(String cx_fmt, Object... cx_va) { return (Buf)this; }
    public Num asNum(String cx_fmt, Object... cx_va) { return (Num)this; }
    public Lst asLst(String cx_fmt, Object... cx_va) { return (Lst)this; }
    public Fun asFun(String cx_fmt, Object... cx_va) { return (Fun)this; }
    public Sym asSym(String cx_fmt, Object... cx_va) { return (Sym)this; }

    protected void init() {
        Obj[] deps = arguments();
        for (int k = 0; k < deps.length; k++)
            deps[k].depends(this);
        update();
    }

    static final Obj[] noarg = new Obj[0];
    // the dependencies for `this` object (ie. that are used in `update`)
    public Obj[] arguments() { return noarg; }
    protected void update() /*throws Throwable*/ { }

    public void updateAndPropagate() {
        updateCycle++;
        update();
        lastUpdateCycle = updateCycle;
        updateDeps();
    }

    private static class DerpList {
        private static class Node {
            WeakReference<Obj> ref;
            Node next;
            Node(WeakReference<Obj> ref) { this.ref = ref; }
        }
        private Node hair = new Node(null);
        private Node last = hair;
        private int count;
    }

    // the dependants on `this` object (ie. that needs `this` in their `update`)
    DerpList dependants = new DerpList();

    void depends(Obj dependant) {
        dependants.last.next = new DerpList.Node(new WeakReference<Obj>(dependant));
        dependants.last = dependants.last.next;
        dependants.count++;
    }

    static int updateCycle = 0;
    int lastUpdateCycle = 0;

    void updateDeps() {
        boolean[] needsUpdateDeps = new boolean[dependants.count];

        int k = -1;
        DerpList.Node it = dependants.hair;
        while (null != (it = it.next)) {
            k++;
            Obj d = it.ref.get();
            if (null == d) continue;
            if (updateCycle != d.lastUpdateCycle) {
                d.update();
                d.lastUpdateCycle = updateCycle;
                needsUpdateDeps[k] = true;
            }
        }

        k = -1;
        it = dependants.hair;
        DerpList.Node pit;
        while (null != (it = (pit = it).next)) {
            k++;
            Obj d = it.ref.get();
            if (null == d) {
                pit.next = it.next;
                continue;
            }
            if (needsUpdateDeps[k]) d.updateDeps();
        }
    }

}
