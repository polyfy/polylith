package polylith.java.core;

import static polylith.java.core.Core.callFn;

public class Colorize {

    final static String namespace = "polylith.clj.core.util.interface.color";
    
    public Colorize() {
    }

    public static String namespace(String namespace, String colorMode) {
        return (String)callFn(namespace, "namespc", namespace, colorMode);
    }

    public static String environment(String environment, String colorMode) {
        return (String)callFn(namespace, "environment", environment, colorMode);
    }

    public static String interfc(String interfc, String colorMode) {
        return (String)callFn(namespace, "interface", interfc, colorMode);
    }

    public static String base(String base, String colorMode) {
        return (String)callFn(namespace, "base", base, colorMode);
    }

    public static String component(String component, String colorMode) {
        return (String)callFn(namespace, "component", component, colorMode);
    }
}
