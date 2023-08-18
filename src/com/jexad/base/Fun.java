package com.jexad.base;

import java.lang.reflect.Constructor;

// abstract: overloads, ret, call
public abstract class Fun extends Obj {

    public static class InvokeException extends Exception {
        public InvokeException(String message) { super(message); }
        public InvokeException(String message, Throwable cause) { super(message, cause); }
    }

    public String help() { return "no help for this function..."; }
    public abstract Class[][] overloads();
    public abstract Class ret();
    public abstract Obj call(Obj... args) throws InvokeException;

    public static int findOvl(Class[][] overloads, Class[] args) {
        for (int i = 0; i < overloads.length; i++) {
            if (overloads[i].length == args.length) {
                boolean matches = true;
                for (int j = 0; j < args.length; j++) {
                    if (args[j] != overloads[i][j] && Obj.class != overloads[i][j]) {
                        matches = false;
                        break;
                    }
                } // for params
                if (matches) return i;
            } // if same count
        } // for overloads
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder(help());

        String n = getClass().getSimpleName();
        String b = ret().getSimpleName();

        Class[][] ovl = overloads();
        for (int i = 0; i < ovl.length; i++) {
            r.append('\n');
            r.append(n);
            r.append(" (");

            for (int j = 0; j < ovl[i].length; j++) {
                if (0 != j) r.append(", ");
                r.append(ovl[i][j].getSimpleName());
            }

            r.append(") -> ");
            r.append(b);
        }

        return r.toString();
    }

    // your usual factory factory
    public static class ForClass extends Fun {

        Class cl;
        String doc;
        Constructor[] ctors;

        public ForClass(Class cl, String doc) {
            this.cl = cl;
            this.doc = doc;
            ctors = cl.getConstructors();
        }

        @Override
        public String help() { return doc; }

        @Override
        public Class[][] overloads() {
            Class[][] r = new Class[ctors.length][];
            for (int k = 0; k < r.length; k++)
                r[k] = ctors[k].getParameterTypes();
            return r;
        }

        @Override
        public Class ret() {
            return
                ( Buf.class.isAssignableFrom(cl) ? Buf.class
                : Num.class.isAssignableFrom(cl) ? Num.class
                : Lst.class.isAssignableFrom(cl) ? Lst.class
                : Fun.class.isAssignableFrom(cl) ? Fun.class
                : Obj.class
                );
        }

        @Override
        public Obj call(Obj... args) throws InvokeException {
            Class[] clargs = new Class[args.length];
            for (int k = 0; k < args.length; k++)
                clargs[k] = args[k].baseClass();

            int k = findOvl(overloads(), clargs);
            if (k < 0) throw new InvokeException("no such overload");

            try {
                return (Obj)ctors[k].newInstance((Object[])args);
            } catch (Exception e) {
                throw new InvokeException("an other exception occured", e);
            }
        }

    } // class ForClass

}
