package com.jexad.ops;

import com.jexad.base.*;

public class Zip extends Lst {

    public static final Fun fun = new Fun.ForClass(Zip.class, "not to be mistaken with zip files, this is the zip function in fp!");

    Lst[] args;

    public Zip(Lst[] args) {
        this.args = args;
        // (when number of arg is 0)
        // XXX: errs and such...
        init();
    }
    public Zip(Lst l, Lst r) { this(new Lst[] {l, r}); }

    @Override
    public Obj[] arguments() { return args; }

    @Override
    public void update() {
        // (when list sizes differ)
        // XXX: errs and such...

        int len = args[0].arr.length;
        arr = new Obj[len];
        for (int j = 0; j < len; j++) {
            Obj[] ll = new Obj[args.length];
            for (int i = 0; i < args.length; i++)
                ll[i] = args[i].arr[j];
            arr[j] = new Lst(ll);
        }
    }

    public static boolean test() {
        return Util.cmpLst
                ( new Zip
                    ( new Lst(new Obj[]
                        { new Num(1)
                        , new Num(2)
                        , new Num(3)
                        })
                    , new Lst(new Obj[]
                        { new Num(6)
                        , new Num(5)
                        , new Num(4)
                        })
                    )
                , new Lst(new Obj[]
                    { new Lst(new Obj[] {new Num(1), new Num(6)})
                    , new Lst(new Obj[] {new Num(2), new Num(5)})
                    , new Lst(new Obj[] {new Num(3), new Num(4)})
                    })
                )
            ;
    }

}
