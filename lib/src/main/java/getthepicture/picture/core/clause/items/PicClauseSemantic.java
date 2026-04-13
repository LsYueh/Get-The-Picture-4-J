package getthepicture.picture.core.clause.items;

/**
 * Class extensions for specific semantic meaning of the data.
 */
public enum PicClauseSemantic {
    /** No additional semantic meaning. */
    NONE,

    /** Gregorian calendar date (YYYYMMDD) */
    GREGORIAN_DATE,

    /** Minguo calendar date (YYYMMDD) */
    MINGUO_DATE,

    /** Time (HHmmss) */
    TIME6,

    /** Time with milliseconds (HHmmssfff) */
    TIME9,

    /** Timestamp (YYYYMMDDHHmmss) */
    TIMESTAMP14,

    /** PIC X(1) : Y/N or PIC 9(1) : 0/1 */
    BOOLEAN,
}
