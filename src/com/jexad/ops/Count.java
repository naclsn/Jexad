package com.jexad.ops;

import com.jexad.base.*;
import com.jexad.util.Util;
import java.math.BigInteger;

public class Count extends Num {

    public static final Fun fun = new Fun.ForClass(Count.class, "count elements of a list, with optional predicate");

    Lst lst;
    Fun pred;

    public Count(Lst lst, Fun pred) {
        this.lst = lst;
        this.pred = pred;
        //if (Num.class != pred.ret()) ..
        init();
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
        int val = 0;
        if (null == pred) val = lst.arr.length;
        else {
            int len = lst.arr.length;
            for (int k = 0; k < len; k++) {
                try {
                    Num r = pred.call(lst.arr[k]).asNum("predicat result %d", k);
                    if (0 != r.asByte()) val++;
                } catch (Fun.InvokeException e) {
                    System.err.println("Count: " + e);
                    return; // XXX: errs and such...
                }
            }
        }

        dec = false;
        iv = BigInteger.valueOf(val);
    }

    public static boolean testNoPred() {
        return Util.cmpNum
                ( new Count(new Lst(new Num[]
                    { new Num(7)
                    , new Num(6)
                    , new Num(5)
                    , new Num(4)
                    , new Num(3)
                    }))
                , new Num(5)
                )
            && Util.cmpNum
                ( new Count(new Lst(new Num[0]))
                , new Num(0)
                )
            ;
    }

    // TODO
    //public static boolean testOddEven() {
    //    Lst l = new Lst<Num>(new Num[]
    //        { new Num(7)
    //        , new Num(6)
    //        , new Num(5)
    //        , new Num(4)
    //        , new Num(3)
    //        });
    //    return Util.cmpNum(new Count(l, Odd.fun), new Num(3))
    //        && Util.cmpNum(new Count(l, Even.fun), new Num(2))
    //        ;
    //}

}
