package getthepicture.picture.core.clause.items;

/**
 * Class of elementary items.
 */
public enum PicClauseBaseClass {

    UNKNOWN,

    /**
     * Consists of only digits (0-9), and possibly an operational sign and a decimal point.
     * Defined with PIC 9 or S9.
     *
     * Numeric items can also have different internal USAGE such as PACKED-DECIMAL (COMP-3),
     * BINARY (COMP), COMP-1, COMP-2, etc.
     */
    NUMERIC,

    /**
     * Consists of any character supported by the system's character set
     * (digits, letters, special characters). Defined with PIC X.
     */
    ALPHANUMERIC,

    /**
     * Consists of only letters (A-Z, a-z) and spaces. Defined with PIC A.
     */
    ALPHABETIC
}
