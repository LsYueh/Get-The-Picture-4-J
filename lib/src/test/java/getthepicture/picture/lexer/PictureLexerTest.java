package getthepicture.picture.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class PictureLexerTest {
    private static final PictureLexer lexer = new PictureLexer();

    private static void assertToken(PictureToken token, PictureTokenType type, String value) {
        assertEquals(type,  token.type());
        assertEquals(value, token.value());
    }

    @Test
    void tokenize_XX() {
        List<PictureToken> tokens = lexer.tokenize("XX");
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), PictureTokenType.ALPHANUMERIC, "X");
        assertToken(tokens.get(1), PictureTokenType.ALPHANUMERIC, "X");
    }

    @Test
    void tokenize_X_repeat() {
        List<PictureToken> tokens = lexer.tokenize("X(10)");
        assertEquals(5, tokens.size());
        assertToken(tokens.get(0), PictureTokenType.ALPHANUMERIC, "X");
        assertToken(tokens.get(1), PictureTokenType.L_PAREN,      "(");
        assertToken(tokens.get(2), PictureTokenType.NUMERIC,      "1");
        assertToken(tokens.get(3), PictureTokenType.NUMERIC,      "0");
        assertToken(tokens.get(4), PictureTokenType.R_PAREN,      ")");
    }

    @Test
    void tokenize_99() {
        List<PictureToken> tokens = lexer.tokenize("99");
        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), PictureTokenType.NUMERIC, "9");
        assertToken(tokens.get(1), PictureTokenType.NUMERIC, "9");
    }

    @Test
    void tokenize_9_repeat() {
        List<PictureToken> tokens = lexer.tokenize("9(10)");
        assertEquals(5, tokens.size());
        assertToken(tokens.get(0), PictureTokenType.NUMERIC, "9");
        assertToken(tokens.get(1), PictureTokenType.L_PAREN, "(");
        assertToken(tokens.get(2), PictureTokenType.NUMERIC, "1");
        assertToken(tokens.get(3), PictureTokenType.NUMERIC, "0");
        assertToken(tokens.get(4), PictureTokenType.R_PAREN, ")");
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
