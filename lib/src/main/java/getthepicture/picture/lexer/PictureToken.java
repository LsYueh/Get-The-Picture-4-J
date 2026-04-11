package getthepicture.picture.lexer;

/**
 * @param type     token type
 * @param value    raw symbol string
 * @param position position in the input string (1-based)
 */
public record PictureToken(PictureTokenType type, String value, int position) {
    @Override
    public String toString() {
        return type + ": '" + value + "' (Position " + position + ")";
    }
}
