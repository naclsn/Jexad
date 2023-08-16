package com.hi;

import com.jexad.base.*;
import com.jexad.inter.Lang.Lookup;

public class HiLookup implements Lookup {

    public static class Hi extends Buf {

        public static final Fun fun = new Fun.ForClass(Hi.class, "sais 'hi <name>'");

        Buf name;
        public Hi(Buf name) {
            this.name = name;
            init();
        }

        @Override
        public Obj[] arguments() { return new Obj[] {name}; }

        @Override
        public void update() {
            raw = new byte[3+name.raw.length];
            raw[0] = 'h';
            raw[1] = 'i';
            raw[2] = ' ';
            System.arraycopy(name.raw, 0, raw, 3, name.raw.length);
        }

    }

    public Fun lookup(String name) {
        return "Hi".equals(name) ? Hi.fun : null;
    }

    //public String[] known() { return new String[] {"Hi"}; }

}
