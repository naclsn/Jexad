package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Ops;
import com.jexad.base.Util;
import java.lang.reflect.Constructor;

public class Map<Out extends Obj> extends Lst<Out> implements Ops {

    public String getHelp() { return "makes a new list, applying the operation to each item; multiple argument lists are zipped"; }

    Lst args_one;
    Lst[] more_args_zip;
    Constructor ctor;

    public Map(Class op, Lst args_one, Lst... more_args_zip) {
        super(Buf.class.isAssignableFrom(op) ? Buf.class
            : Num.class.isAssignableFrom(op) ? Num.class
            : Lst.class.isAssignableFrom(op) ? Lst.class
            : null);

        this.args_one = args_one;
        this.more_args_zip = new Lst[more_args_zip.length];

        Class[] args_zip_classes = new Class[1 + more_args_zip.length];
        args_zip_classes[0] = args_one.getItemClass();
        for (int j = 1; j < args_zip_classes.length; j++) {
            args_zip_classes[j] = more_args_zip[j-1].getItemClass();

            this.more_args_zip[j-1] = more_args_zip[j-1];
        }

        try {
            ctor = op.getConstructor(args_zip_classes);
        } catch (Exception e) {
            System.err.println("Map: " + e);
            return; // XXX: errs and such...
        }
    }

    // needed for `getConstructor` resolutions...
    public Map(Class op, Lst a) { this(op, a, new Lst[0]); }
    public Map(Class op, Lst a, Lst b) { this(op, a, new Lst[] {b}); }
    public Map(Class op, Lst a, Lst b, Lst c) { this(op, a, new Lst[] {b, c}); }

    @Override
    public Obj[] arguments() {
        Obj[] r = new Obj[1 + more_args_zip.length];
        r[0] = args_one;
        for (int j = 1; j < r.length; j++)
            r[j] = more_args_zip[j-1];
        return r;
    }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        args_one.update();
        for (int j = 0; j < more_args_zip.length; j++)
            more_args_zip[j].update();

        arr = (Out[])new Obj[args_one.arr.length];

        for (int i = 0; i < arr.length; i++) {
            Obj[] args = new Obj[1 + more_args_zip.length];
            args[0] = args_one.arr[i];
            args[0].update();
            for (int j = 1; j < args.length; j++) {
                args[j] = more_args_zip[j-1].arr[i];
                args[j].update();
            }

            try {
                arr[i] = (Out)ctor.newInstance((Object[])args);
            } catch (Exception e) {
                System.err.println("Map: " + e);
                return; // XXX: errs and such...
            }
        }
    }

    public static boolean notest() {
        return false; // TODO: test
    }

}
