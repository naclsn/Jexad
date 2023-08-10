package com.jexad.base;

public abstract class Fun extends Obj {

    public static class InvokeException extends Exception {
        public InvokeException(String message) { super(message); }
    }

    public String help() { return "no help for this function..."; }
    public abstract Obj call(Obj... args) throws InvokeException;
    public abstract Class ret();

    public static class ForClass extends Fun {

        Class cl;
        String doc;

        public ForClass(Class cl, String doc) {
            //System.out.println("ForClass " + cl + " :" + doc + ":");
            this.cl = cl;
            this.doc = doc;
        }

        @Override
        public String help() { return doc; }

        @Override
        public Obj call(Obj... args) throws InvokeException {
            Class[] clargs = new Class[args.length];
            for (int k = 0; k < args.length; k++)
                clargs[k] = args[k].baseClass();
            try {
                Object r = cl.getConstructor(clargs).newInstance((Object[])args);
                // XXX: that, or disallow non-extends-Obj classes (directly in
                // the maybe-annotation itself)
                return r instanceof Obj ? (Obj)r : new Buf(new byte[0]);
            } catch (Exception e) {
                throw new InvokeException(e.toString()); // TODO: make it the cause
            }
        }

        @Override
        public Class ret() {
            Class r
                = Buf.class.isAssignableFrom(cl) ? Buf.class
                : Num.class.isAssignableFrom(cl) ? Num.class
                : Lst.class.isAssignableFrom(cl) ? Lst.class
                : Fun.class.isAssignableFrom(cl) ? Fun.class
                : Obj.class;
            return r;
        }

    } // class ForClass

}
