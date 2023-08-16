package com.jexad.ops;

import com.jexad.base.*;

public class At extends Fun {

    public static final Fun fun = new At();

    @Override
    public String help() { return "get element from list at index (negative counts from the end) or from a list of pairs at key symbol"; }

    @Override
    public Class[][] overloads() {
        return new Class[][]
            { new Class[] {Lst.class, Num.class}
            , new Class[] {Lst.class, Sym.class}
            };
    }

    @Override
    public Class ret() { return Obj.class; }

    @Override
    public Obj call(Obj... args) throws Fun.InvokeException {
        if (2 == args.length && args[0] instanceof Lst) {
            Lst lst = (Lst)args[0];
            int len = lst.length();

            if (args[1] instanceof Num) {
                Num num = (Num)args[1];
                int k = num.asInt();
                return k < 0
                    ? lst.at(lst.length()+k)
                    : lst.at(k)
                    ; // XXX: errs and such...
            }

            else if (args[1] instanceof Sym) {
                Sym sym = (Sym)args[1];
                for (int k = 0; k < len; k++) {
                    Obj it = lst.at(k);
                    if (!(it instanceof Lst)) break;
                    Lst pair = (Lst)it;
                    if (pair.length() < 2) break;
                    Obj key = pair.at(0);
                    if (!(key instanceof Sym)) break;
                    if (sym.str.equals(((Sym)key).str))
                        return pair.at(1);
                }
                // XXX: errs and such...
                //throw new Fun.InvokeException("key not found in dictionary");
            }
        }

        throw new Fun.InvokeException("no such overload");
    }

    public static boolean testList() throws Fun.InvokeException {
        return Util.cmpNum
                ( (Num)fun.call(new Lst(new Obj[]
                    { new Num(5)
                    , new Num(4)
                    , new Num(3)
                    , new Num(2)
                    }), new Num(1))
                , new Num(4)
                )
            && Util.cmpNum
                ( (Num)fun.call(new Lst(new Obj[]
                    { new Num(5)
                    , new Num(4)
                    , new Num(3)
                    , new Num(2)
                    }), new Num(-1))
                , new Num(2)
                )
            ;
    }

    public static boolean testDict() throws Fun.InvokeException {
        return Util.cmpNum
                ( (Num)fun.call(new Lst(new Obj[]
                    { new Lst(new Obj[] {new Sym("how_are_you"), new Num(63)})
                    , new Lst(new Obj[] {new Sym("coucou"), new Num(42)})
                    , new Lst(new Obj[] {new Sym("blabla"), new Num(12)})
                    }), new Sym("coucou"))
                , new Num(42)
                )
            ;
    }

}
