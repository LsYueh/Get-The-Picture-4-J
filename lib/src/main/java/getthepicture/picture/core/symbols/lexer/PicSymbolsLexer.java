package getthepicture.picture.core.symbols.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PicSymbolsLexer {

    private static final int MAX_LENGTH = 50;

    private String symbols;
    private int pos;

    private boolean isEnd() { return pos >= symbols.length(); }
    private char peek()     { return symbols.charAt(pos); }
    private void advance()  { pos++; }

    public List<PicSymbolsToken> tokenize(String symbols) {
        this.symbols = Objects.requireNonNull(symbols, "symbols must not be null");
        this.pos = 0;

        if (symbols.length() > MAX_LENGTH)
            throw new IllegalArgumentException(
                "Input exceeds maximum length of " + MAX_LENGTH + " characters.");

        var tokens = new ArrayList<PicSymbolsToken>();
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

    private PicSymbolsToken readToken(char ch, int tokenPos) {
        if (Character.isDigit(ch)) {
            advance();
            return new PicSymbolsToken(PicSymbolsTokenType.NUMERIC, String.valueOf(ch), tokenPos);
        }
        return switch (ch) {
            case 'A' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.ALPHABETIC,       String.valueOf(ch), tokenPos); }
            case 'X' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.ALPHANUMERIC,     String.valueOf(ch), tokenPos); }
            case 'S' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.SIGN,             String.valueOf(ch), tokenPos); }
            case 'V' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.IMPLIED_DECIMAL,  String.valueOf(ch), tokenPos); }
            case '.' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.IMPLIED_DECIMAL,  String.valueOf(ch), tokenPos); }
            case 'P' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.SCALING,          String.valueOf(ch), tokenPos); }
            case '(' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.L_PAREN,          String.valueOf(ch), tokenPos); }
            case ')' -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.R_PAREN,          String.valueOf(ch), tokenPos); }
            default  -> { advance(); yield new PicSymbolsToken(PicSymbolsTokenType.UNKNOWN,          String.valueOf(ch), tokenPos); }
        };
    }
}
