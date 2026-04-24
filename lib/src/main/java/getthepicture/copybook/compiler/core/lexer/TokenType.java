package getthepicture.copybook.compiler.core.lexer;

/**
 * Copybook 專用 Token 類型
 */
public enum TokenType {
    UNKNOWN,

    CHARACTER,
    ALPHANUMERIC_LITERAL,
    NUMERIC_LITERAL,
    COMMENT,
    PICTURE, USAGE,
    DISPLAY, DISPLAY_1,
    BINARY, PACKED_DECIMAL, COMP, COMP_1, COMP_2, COMP_3, COMP_4, COMP_5, COMP_6,
    DATE, TIME,
    NATIONAL,
    FILLER,
    OCCURS, TIMES,
    REDEFINES, REFERENCE, RENAMES,
    VALUE, VALUES,
    SPACE, ZERO,
    THROUGH,
    L_PAREN, R_PAREN,
    DOT, HYPHEN
}
