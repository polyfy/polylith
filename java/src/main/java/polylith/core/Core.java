package polylith.core;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class Core {

    public static Object callFn(String namespace, String function, Object... args) {
        final IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read(namespace));

        final IFn fn = Clojure.var(namespace, function);

        switch (args.length) {
            case 0:
                return fn.invoke();
            case 1:
                return fn.invoke(args[0]);
            case 2:
                return fn.invoke(args[0], args[1]);
            case 3:
                return fn.invoke(args[0], args[1], args[2]);
            case 4:
                return fn.invoke(args[0], args[1], args[2], args[3]);
            case 5:
                return fn.invoke(args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return fn.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return fn.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return fn.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            default:
                throw new RuntimeException("Max 8 arguments supported at the moment");
        }
    }
}
