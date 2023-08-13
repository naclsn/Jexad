package com.jexad.ops;

import com.jexad.base.*;

public class Count extends Num {

    public static final Fun fun = new Fun.ForClass(Count.class, "count elements of a list, with optional predicate");

    Lst lst;
    Fun pred;

    public Count(Lst lst, Fun pred) {
        this.lst = lst;
        this.pred = pred;
    }

    public Count(Lst lst) { this(lst, null); }

    @Override
    public Obj[] arguments() {
        return null == pred
            ? new Obj[] {lst}
            : new Obj[] {lst, pred}
            ;
    }

    @Override
    public void update() {
        if (uptodate) return;
        uptodate = true;

        lst.update();

        if (null == pred) val = lst.length();
        else {
            val = 0;
            pred.update();
            int len = lst.length();
            for (int k = 0; k < len; k++) {
                try {
                    Obj r = pred.call(lst.at(k));
                    if (!(r instanceof Num)) {
                        System.err.println("Count: predicat result is not a number");
                        return; // XXX: errs and such...
                    }
                    if (0 != ((Num)r).val) val++;
                } catch (Fun.InvokeException e) {
                    System.err.println("Count: " + e);
                    return; // XXX: errs and such...
                }
            }
        }
    }

    public static boolean testNoPred() {
        return Util.cmpNum
                ( new Count(new Lst<Num>(new Num[]
                    { new Num(7)
                    , new Num(6)
                    , new Num(5)
                    , new Num(4)
                    , new Num(3)
                    }))
                , new Num(5)
                )
            && Util.cmpNum
                ( new Count(new Lst<Num>(new Num[0]))
                , new Num(0)
                )
            ;
    }

}
