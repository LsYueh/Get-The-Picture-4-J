package getthepicture;

public final class Version {

    private static final Package PACKAGE = Version.class.getPackage();

    private Version() {}

    /**
     * Semantic version (MinVer)
     * e.g. 1.2.3 / 1.2.3-alpha.0+5.gabc123
     */
    public static String getInformational() {
        String v = PACKAGE != null ? PACKAGE.getImplementationVersion() : null;
        return v != null ? v : "unknown";
    }

    /**
     * File version (x.y.z.0)
     */
    public static String getFile() {
        String v = PACKAGE != null ? PACKAGE.getSpecificationVersion() : null;
        return v != null ? v : "0.0.0.0";
    }

    /**
     * Assembly version (usually fixed)
     */
    public static String getAssembly() {
        String v = PACKAGE != null ? PACKAGE.getImplementationVersion() : null;
        return v != null ? v : "0.0.0.0";
    }
}
