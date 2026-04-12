package getthepicture.picture.core.symbols.parser;

import java.util.List;
import java.util.Objects;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;
import getthepicture.picture.core.symbols.lexer.PicSymbolsToken;
import getthepicture.picture.core.symbols.lexer.PicSymbolsTokenType;

public class PicSymbolsParser {

    private List<PicSymbolsToken> tokens;
    private int pos;

    // ----------------------------
    // Pointer
    // ----------------------------

    private PicSymbolsToken previous;

    public PicSymbolsToken getPrevious() { return previous; }

    private PicSymbolsToken current() {
        return pos < tokens.size() ? tokens.get(pos) : null;
    }

    private PicSymbolsToken lookahead(int n) {
        return (pos + n) < tokens.size() ? tokens.get(pos + n) : null;
    }

    private PicSymbolsToken lookahead() { return lookahead(1); }

    // ----------------------------
    // Operations
    // ----------------------------

    private PicSymbolsToken consume() {
        PicSymbolsToken cur = current();
        if (cur == null)
            throw new IllegalStateException("Cannot consume EOF.");
        previous = cur;
        return tokens.get(pos++);
    }

    private PicSymbolsToken expect(PicSymbolsTokenType type) {
        PicSymbolsToken cur = current();
        if (cur == null)
            throw new PicSymbolsParseException("Expected type: '" + type + "' but got end of input");
        if (cur.type() != type)
            throw new PicSymbolsParseException("Expected type: '" + type + "' but got '" + cur.type() + "'");
        return consume();
    }

    // ----------------------------
    // Analysis
    // ----------------------------

    public PicSymbolsMeta analyze(List<PicSymbolsToken> tokens) {
        this.tokens = Objects.requireNonNull(tokens, "tokens must not be null");
        this.pos = 0;

        var symbolMeta = new PicSymbolsMeta();
        parseSymbols(symbolMeta);
        return symbolMeta;
    }

    // ----------------------------
    // Helpers
    // ----------------------------

    private void parseSymbols(PicSymbolsMeta symbolMeta) {
        boolean inDecimal = false;

        while (current() != null) {
            PicSymbolsToken token = current();

            switch (token.type()) {
                // --------------------
                // Sign
                // --------------------
                case SIGN -> {
                    if (symbolMeta.isSigned())
                        throw new PicSymbolsParseException("Duplicate Sign at position " + token.position());
                    symbolMeta.setSigned(true);
                    consume();
                }

                // --------------------
                // Class
                // --------------------
                case ALPHABETIC, ALPHANUMERIC -> {
                    if (symbolMeta.isSigned())
                        throw new PicSymbolsParseException(
                            "Sign cannot be combined with " + token.type() + " at position " + token.position());
                    parseRepeat(symbolMeta, inDecimal);
                }

                case NUMERIC -> parseRepeat(symbolMeta, inDecimal);

                // --------------------
                // ImpliedDecimal (V or .)
                // --------------------
                case IMPLIED_DECIMAL -> {
                    if (symbolMeta.getBaseClass() != PicClauseBaseClass.NUMERIC)
                        throw new PicSymbolsParseException(
                            "Implied decimal can only be used with Numeric class at position " + token.position());
                    if (inDecimal)
                        throw new PicSymbolsParseException("Multiple implied decimals at position " + token.position());
                    inDecimal = true;
                    consume();
                }

                // --------------------
                // 不屬於 Symbol 的 token → 拋出例外
                // --------------------
                default -> throw new UnsupportedOperationException(
                    "Unsupported token '" + token.value() + "' of type '" + token.type() + "' at position " + token.position());
            }
        }
    }

    /**
     * 解析 Numeral / Alpha token，並處理可選的 (Repeat)
     * 累積到 SymbolMeta 的 integerDigits 或 decimalDigits
     */
    private void parseRepeat(PicSymbolsMeta symbolMeta, boolean inDecimal) {
        PicSymbolsToken token = consume();

        PicClauseBaseClass tokenClass = getBaseClass(token.type());

        if (tokenClass == PicClauseBaseClass.UNKNOWN)
            throw new PicSymbolsParseException("Invalid symbol token '" + token.type() + "' at position " + token.position());

        // ===== BaseClass lock =====
        if (symbolMeta.getBaseClass() == PicClauseBaseClass.UNKNOWN) {
            symbolMeta.setBaseClass(tokenClass);
        } else if (symbolMeta.getBaseClass() != tokenClass) {
            throw new PicSymbolsParseException(
                "Cannot mix " + tokenClass + " with " + symbolMeta.getBaseClass() + " at position " + token.position());
        }

        int repeat = 1;

        // ===== Optional repeat clause =====
        if (current() != null && current().type() == PicSymbolsTokenType.L_PAREN) {
            consume(); // consume '('

            var sb = new StringBuilder();
            while (current() != null && current().type() == PicSymbolsTokenType.NUMERIC) {
                sb.append(current().value());
                consume();
            }
            repeat = Integer.parseInt(sb.toString());

            expect(PicSymbolsTokenType.R_PAREN); // consume ')'
        }

        // ===== Semantic accumulation =====
        switch (token.type()) {
            case NUMERIC -> {
                if (inDecimal)
                    symbolMeta.addDecimalDigits(repeat);
                else
                    symbolMeta.addIntegerDigits(repeat);
            }
            case ALPHABETIC, ALPHANUMERIC -> symbolMeta.addIntegerDigits(repeat);
            default -> throw new PicSymbolsParseException(
                "ParseRepeat called on invalid token '" + token.type() + "' at position " + token.position());
        }
    }

    private static PicClauseBaseClass getBaseClass(PicSymbolsTokenType type) {
        return switch (type) {
            case NUMERIC      -> PicClauseBaseClass.NUMERIC;
            case ALPHABETIC   -> PicClauseBaseClass.ALPHABETIC;
            case ALPHANUMERIC -> PicClauseBaseClass.ALPHANUMERIC;
            default           -> PicClauseBaseClass.UNKNOWN;
        };
    }
}
