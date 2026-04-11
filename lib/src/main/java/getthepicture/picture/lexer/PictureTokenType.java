package getthepicture.picture.lexer;

/**
 * PICTURE clause token types.
 */
public enum PictureTokenType {
    UNKNOWN,

    // PICTURE clause symbols
    ALPHABETIC,       // 'A'
    ALPHANUMERIC,     // 'X'
    NUMERIC,          // 0-9

    SIGN,             // 'S'
    IMPLIED_DECIMAL,  // 'V' or '.'
    SCALING,          // 'P'

    // Repeat syntax
    L_PAREN,          // '('
    R_PAREN           // ')'
}
