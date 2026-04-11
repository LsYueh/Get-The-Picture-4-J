package getthepicture.picture.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PictureLexer {

    private static final int MAX_LENGTH = 50;

    private String symbols;
    private int pos;

    private boolean isEnd() { return pos >= symbols.length(); }
    private char peek()     { return symbols.charAt(pos); }
    private void advance()  { pos++; }

    public List<PictureToken> tokenize(String symbols) {
        this.symbols = Objects.requireNonNull(symbols, "symbols must not be null");
        this.pos = 0;

        if (symbols.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                "Input exceeds maximum length of " + MAX_LENGTH + " characters.");

        var tokens = new ArrayList<PictureToken>();
        while (!isEnd()) {
            char ch = peek();
            if (Character.isWhitespace(ch)) {
                advance();
                continue;
            }
            int tokenPos = pos + 1; // 1-based
            tokens.add(readToken(ch, tokenPos));
        }
        return tokens;
    }

    // ----------------------------
    // Helpers
    // ----------------------------

    private PictureToken readToken(char ch, int tokenPos) {
        if (Character.isDigit(ch)) {
            advance();
            return new PictureToken(PictureTokenType.NUMERIC, String.valueOf(ch), tokenPos);
        }
        return switch (ch) {
            case 'A' -> { advance(); yield new PictureToken(PictureTokenType.ALPHABETIC,       String.valueOf(ch), tokenPos); }
            case 'X' -> { advance(); yield new PictureToken(PictureTokenType.ALPHANUMERIC,     String.valueOf(ch), tokenPos); }
            case 'S' -> { advance(); yield new PictureToken(PictureTokenType.SIGN,             String.valueOf(ch), tokenPos); }
            case 'V' -> { advance(); yield new PictureToken(PictureTokenType.IMPLIED_DECIMAL,  String.valueOf(ch), tokenPos); }
            case '.' -> { advance(); yield new PictureToken(PictureTokenType.IMPLIED_DECIMAL,  String.valueOf(ch), tokenPos); }
            case 'P' -> { advance(); yield new PictureToken(PictureTokenType.SCALING,          String.valueOf(ch), tokenPos); }
            case '(' -> { advance(); yield new PictureToken(PictureTokenType.L_PAREN,          String.valueOf(ch), tokenPos); }
            case ')' -> { advance(); yield new PictureToken(PictureTokenType.R_PAREN,          String.valueOf(ch), tokenPos); }
            default  -> { advance(); yield new PictureToken(PictureTokenType.UNKNOWN,          String.valueOf(ch), tokenPos); }
        };
    }
}
