package getthepicture.picture.core.clause.items;

/** USAGE Clause Options */
public enum PicClauseUsage {
    /**
     * DISPLAY (default)
     */
    DISPLAY,

    /**
     * COMP-3 (Packed-Decimal)
     */
    PACKED_DECIMAL,

    /**
     * COMP-4 (Binary)
     */
    BINARY,

    /**
     * COMP-5 (Native Binary)
     */
    NATIVE_BINARY,

    /**
     * COMP-6 (Unsigned Packed-Decimal)
     */
    U_PACKED_DECIMAL;

    // Aliases

    public static final PicClauseUsage COMP3  = PACKED_DECIMAL;
    public static final PicClauseUsage COMP4  = BINARY;
    public static final PicClauseUsage COMP5  = NATIVE_BINARY;
    public static final PicClauseUsage COMP6  = U_PACKED_DECIMAL;

    public static final PicClauseUsage COMP   = BINARY;
}
