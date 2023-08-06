package com.jexad.base;

public interface Fun extends Obj {

    public static class InvokeException extends Exception {
        public InvokeException(String message) { super(message); }
    }

    String help();
    Obj make(Obj... args) throws InvokeException;
    Class ret();

    public static class ForClass implements Fun {

        Class cl;
        String doc;

        public ForClass(Class cl, String doc) {
            this.cl = cl;
            this.doc = doc;
        }

        public Obj[] arguments() { return new Obj[0]; }
        protected boolean uptodate;
        public void outdated() { uptodate = false; }
        public void update() { }

        public String help() { return doc; }

        public Obj make(Obj... args) throws InvokeException {
            Class[] clargs = new Class[args.length];
            for (int k = 0; k < args.length; k++) {
                clargs[k]
                    = args[k] instanceof Buf ? Buf.class
                    : args[k] instanceof Num ? Num.class
                    : args[k] instanceof Lst ? Lst.class
                    : args[k] instanceof Fun ? Fun.class
                    : null; // idealy unreachable
            }
            try {
                Object r = cl.getConstructor(clargs).newInstance((Object[])args);
                return r instanceof Obj ? (Obj)r : new Buf(new byte[0]);
            } catch (Exception e) {
                throw new InvokeException(e.toString()); // TODO: make it the cause
            }
        }

        public Class ret() {
            Class r
                = Buf.class.isAssignableFrom(cl) ? Buf.class
                : Num.class.isAssignableFrom(cl) ? Num.class
                : Lst.class.isAssignableFrom(cl) ? Lst.class
                : Fun.class.isAssignableFrom(cl) ? Fun.class
                : null; // idealy unreachable
            return r;
        }

    } // class ForClass

} // interface Fun
