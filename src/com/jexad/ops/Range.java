package com.jexad.ops;

import com.jexad.base.*;

public class Range extends Lst {

    public static final Fun fun = new Fun.ForClass(Range.class, "return a list of integral numbers, start inclusive, end exclusive, default step is 1");

    Num start;
    Num end;
    Num step;

    public Range(Num start, Num end, Num step) {
        this.start = start;
        this.end = end;
        this.step = step;
        init();
    }

    public Range(Num start, Num end) { this(start, end, new Num(1)); }

    @Override
    public Obj[] arguments() { return new Obj[] {start, end, step}; }

    @Override
    public void update() {
        int st = start.asInt();
        int by = step.asInt();
        arr = new Num[(end.asInt()-st) / by];
        for (int k = 0, n = st; k < arr.length; n+= by)
            arr[k++] = new Num(n);
    }

    public static boolean test() {
        return Util.cmpLst
                ( new Range(new Num(2), new Num(7))
                , new Lst(new Obj[]
                    { new Num(2)
                    , new Num(3)
                    , new Num(4)
                    , new Num(5)
                    , new Num(6)
                    })
                )
            && Util.cmpLst
                ( new Range(new Num(6), new Num(-4), new Num(-2))
                , new Lst(new Obj[]
                    { new Num(6)
                    , new Num(4)
                    , new Num(2)
                    , new Num(0)
                    , new Num(-2)
                    })
                )
            ;
    }

}
