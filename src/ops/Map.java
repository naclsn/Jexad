package com.jexad.ops;

import com.jexad.base.Buf;
import com.jexad.base.Lst;
import com.jexad.base.Num;
import com.jexad.base.Obj;
import com.jexad.base.Ops;
import java.lang.reflect.Constructor;

@SuppressWarnings("unchecked")
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
            return; // XXX: errs and such...
        }
    }

    @Override
    public void update() {
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
                return; // XXX: errs and such...
            }
        }
    }

}
