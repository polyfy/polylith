package polylith.core;

import static polylith.core.Core.callFn;

public class Api {

    final static String colorNs = "polylith.clj.core.util.interfc.color";

    public static String colorizeNamespace(String namespace, String colorMode) {
        return (String)callFn(colorNs, "namespc", namespace, colorMode);
    }

    public static String colorizeEnvironment(String environment, String colorMode) {
        return (String)callFn(colorNs, "environment", environment, colorMode);
    }

    public static String colorizeInterface(String interfc, String colorMode) {
        return (String)callFn(colorNs, "interface", interfc, colorMode);
    }

    public static String colorizeBase(String base, String colorMode) {
        return (String)callFn(colorNs, "base", base, colorMode);
    }

    public static String colorizeComponent(String component, String colorMode) {
        return (String)callFn(colorNs, "component", component, colorMode);
    }

    public static void main(final String... args) {
        System.out.println(colorizeNamespace("my-namespace", "dark"));
        System.out.println(colorizeEnvironment("my-environment", "dark"));
        System.out.println(colorizeInterface("my-interface", "dark"));
        System.out.println(colorizeComponent("my-component", "dark"));
        System.out.println(colorizeBase("my-base", "dark"));
    }
}
