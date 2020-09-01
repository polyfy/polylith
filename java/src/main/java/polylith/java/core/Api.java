package polylith.java.core;

import java.util.Map;

import static polylith.java.core.Core.callFn;

public final class Api {

    final static String walkNs = "clojure.walk";
    final static String colorNs = "polylith.clj.core.util.interfc.color";
    final static String workspaceNs = "polylith.clj.core.workspace.interfc";

    static Colorize colorize = new Colorize();

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

    private static Map keywordizeKeys(Map workspace) {
        return (Map)callFn(walkNs, "keywordize-keys", workspace);
    }

    public static Map enrichWorkspace(Map workspace, Map userInput) {
        return (Map)callFn(workspaceNs, "enrich-workspace", keywordizeKeys(workspace), keywordizeKeys(userInput));
    }

    public static void printEnrichedWorkspace(Map workspace, boolean showLoc, boolean showResources) {
        callFn(workspaceNs, "print-table", keywordizeKeys(workspace), showLoc, showResources);
    }

    public static void main(final String... args) {
        System.out.println(colorizeNamespace("my-namespace", "dark"));
        System.out.println(colorizeEnvironment("my-environment", "dark"));
        System.out.println(colorizeInterface("my-interface", "dark"));
        System.out.println(colorizeComponent("my-component", "dark"));
        System.out.println(colorizeBase("my-base", "dark"));
    }
}
