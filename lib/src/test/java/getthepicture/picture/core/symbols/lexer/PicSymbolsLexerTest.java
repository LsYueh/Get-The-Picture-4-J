package getthepicture.picture.core.symbols.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class PicSymbolsLexerTest {
    private static final PicSymbolsLexer lexer = new PicSymbolsLexer();

    private static void assertToken(PicSymbolsToken token, PicSymbolsTokenType type, String value) {
        assertEquals(type,  token.type());
        assertEquals(value, token.value());
    }

    @Test
    void tokenize_XX() {
        List<PicSymbolsToken> tokens = lexer.tokenize("XX");
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), PicSymbolsTokenType.ALPHANUMERIC, "X");
        assertToken(tokens.get(1), PicSymbolsTokenType.ALPHANUMERIC, "X");
    }

    @Test
    void tokenize_X_repeat() {
        List<PicSymbolsToken> tokens = lexer.tokenize("X(10)");
        assertEquals(5, tokens.size());
        assertToken(tokens.get(0), PicSymbolsTokenType.ALPHANUMERIC, "X");
        assertToken(tokens.get(1), PicSymbolsTokenType.L_PAREN,      "(");
        assertToken(tokens.get(2), PicSymbolsTokenType.NUMERIC,      "1");
        assertToken(tokens.get(3), PicSymbolsTokenType.NUMERIC,      "0");
        assertToken(tokens.get(4), PicSymbolsTokenType.R_PAREN,      ")");
    }

    @Test
    void tokenize_99() {
        List<PicSymbolsToken> tokens = lexer.tokenize("99");
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), PicSymbolsTokenType.NUMERIC, "9");
        assertToken(tokens.get(1), PicSymbolsTokenType.NUMERIC, "9");
    }

    @Test
    void tokenize_9_repeat() {
        List<PicSymbolsToken> tokens = lexer.tokenize("9(10)");
        assertEquals(5, tokens.size());
        assertToken(tokens.get(0), PicSymbolsTokenType.NUMERIC, "9");
        assertToken(tokens.get(1), PicSymbolsTokenType.L_PAREN, "(");
        assertToken(tokens.get(2), PicSymbolsTokenType.NUMERIC, "1");
        assertToken(tokens.get(3), PicSymbolsTokenType.NUMERIC, "0");
        assertToken(tokens.get(4), PicSymbolsTokenType.R_PAREN, ")");
    }

    @Test
    void tokenize_null_throws() {
        assertThrows(NullPointerException.class, () -> lexer.tokenize(null));
    }

    @Test
    void tokenize_exceeds_max_length_throws() {
        String input = "9".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize(input));
    }
}
