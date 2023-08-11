package com.jexad.ops;

import com.jexad.base.*;

public class At extends Fun {

    public static final Fun fun = new At();

    @Override
    public String help() { return "get element from list at index"; }

    @Override
    public Obj call(Obj... args) throws Fun.InvokeException {
        if (2 == args.length && args[0] instanceof Lst) {
            Lst lst = (Lst)args[0];
            lst.update();
            int len = lst.length();

            if (args[1] instanceof Num) {
                Num num = (Num)args[1];
                num.update();
                return lst.at(num.val); // XXX: errs and such...
            }

            else if (args[1] instanceof Sym) {
                Sym sym = (Sym)args[1];
                //sym.update(); // no-op
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
            }
        }

        throw new Fun.InvokeException("wrong argument and such (should be Lst,Num or Lst<Lst>,Sym)");
    }

    @Override
    public Class ret() { return Obj.class; }

}
