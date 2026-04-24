package getthepicture.copybook.compiler.core;

import org.junit.jupiter.api.Test;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.lexer.Lexer;
import getthepicture.copybook.compiler.core.lexer.Token;
import getthepicture.copybook.compiler.core.lexer.TokenType;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private static void assertToken(Token token, TokenType type, String text) {
        assertEquals(type, token.getType());
        assertEquals(text, token.getValue());
    }

    private static final Lexer lexer = new Lexer();

    @Test
    void tokenize_Test_01() {
        String line = "05 CUSTOMER-NAME PIC X(10) VALUE 'ABC'.";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(10, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.NUMERIC_LITERAL,       "05");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL,  "CUSTOMER-NAME");
        assertToken(tokens.get(2), TokenType.PICTURE,               "PIC");
        assertToken(tokens.get(3), TokenType.ALPHANUMERIC_LITERAL,  "X");
        assertToken(tokens.get(4), TokenType.L_PAREN,               "(");
        assertToken(tokens.get(5), TokenType.NUMERIC_LITERAL,       "10");
        assertToken(tokens.get(6), TokenType.R_PAREN,               ")");
        assertToken(tokens.get(7), TokenType.VALUE,                 "VALUE");
        assertToken(tokens.get(8), TokenType.ALPHANUMERIC_LITERAL,  "'ABC'");
        assertToken(tokens.get(9), TokenType.DOT,                   ".");
    }

    @Test
    void tokenize_Test_02() {
        String line = "05 BGEN-XXXXX  OCCURS 4.";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(5, tokens.size());

        assertToken(tokens.get(0), TokenType.NUMERIC_LITERAL,      "05");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL, "BGEN-XXXXX");
        assertToken(tokens.get(2), TokenType.OCCURS,               "OCCURS");
        assertToken(tokens.get(3), TokenType.NUMERIC_LITERAL,      "4");
        assertToken(tokens.get(4), TokenType.DOT,                  ".");
    }

    @Test
    void tokenize_Test_03() {
        String line = "07 BGEN-XXXXX-TRANS-NO3     PIC S9(05)V(03) COMP-3.";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(13, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get( 0), TokenType.NUMERIC_LITERAL,      "07");
        assertToken(tokens.get( 1), TokenType.ALPHANUMERIC_LITERAL, "BGEN-XXXXX-TRANS-NO3");
        assertToken(tokens.get( 2), TokenType.PICTURE,              "PIC");
        assertToken(tokens.get( 3), TokenType.ALPHANUMERIC_LITERAL, "S9");
        assertToken(tokens.get( 4), TokenType.L_PAREN,              "(");
        assertToken(tokens.get( 5), TokenType.NUMERIC_LITERAL,      "05");
        assertToken(tokens.get( 6), TokenType.R_PAREN,              ")");
        assertToken(tokens.get( 7), TokenType.ALPHANUMERIC_LITERAL, "V");
        assertToken(tokens.get( 8), TokenType.L_PAREN,              "(");
        assertToken(tokens.get( 9), TokenType.NUMERIC_LITERAL,      "03");
        assertToken(tokens.get(10), TokenType.R_PAREN,              ")");
        assertToken(tokens.get(11), TokenType.COMP_3,               "COMP-3");
        assertToken(tokens.get(12), TokenType.DOT,                  ".");
    }

    @Test
    void tokenize_Test_04() {
        String line = "VALUE 'O''NEIL'";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(2, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.VALUE,                "VALUE");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL, "'O''NEIL'");
    }

    @Test
    void tokenize_Test_05() {
        String line = "VALUE 'ABC.";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(2, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.VALUE,                "VALUE");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL, "'ABC."); // Note: 缺閉合，不會有 DOT
    }

    @Test
    void tokenize_Test_06() {
        String line = "PIC  9(005)";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(5, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.PICTURE,         "PIC");
        assertToken(tokens.get(1), TokenType.NUMERIC_LITERAL, "9");
        assertToken(tokens.get(2), TokenType.L_PAREN,         "(");
        assertToken(tokens.get(3), TokenType.NUMERIC_LITERAL, "005");
        assertToken(tokens.get(4), TokenType.R_PAREN,         ")");
    }

    @Test
    void tokenize_Test_07() {
        String line = "01 STOCK-NO PIC X(6). *> 股票代號     ";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(9, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.NUMERIC_LITERAL,      "01");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL, "STOCK-NO");
        assertToken(tokens.get(2), TokenType.PICTURE,              "PIC");
        assertToken(tokens.get(3), TokenType.ALPHANUMERIC_LITERAL, "X");
        assertToken(tokens.get(4), TokenType.L_PAREN,              "(");
        assertToken(tokens.get(5), TokenType.NUMERIC_LITERAL,      "6");
        assertToken(tokens.get(6), TokenType.R_PAREN,              ")");
        assertToken(tokens.get(7), TokenType.DOT,                  ".");
        assertToken(tokens.get(8), TokenType.COMMENT,              "股票代號");
    }

    @Test
    void tokenize_Test_08() {
        String line = "88 FLAG-ALPHA        VALUES 'AA' 'AB' 'AC'";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(6, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.NUMERIC_LITERAL,      "88");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL, "FLAG-ALPHA");
        assertToken(tokens.get(2), TokenType.VALUES,               "VALUES");
        assertToken(tokens.get(3), TokenType.ALPHANUMERIC_LITERAL, "'AA'");
        assertToken(tokens.get(4), TokenType.ALPHANUMERIC_LITERAL, "'AB'");
        assertToken(tokens.get(5), TokenType.ALPHANUMERIC_LITERAL, "'AC'");
    }

    @Test
    void tokenize_Test_09() {
        String line = "88 FLAG-NUMERIC      VALUE 11 THRU 99";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(6, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.NUMERIC_LITERAL,      "88");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL, "FLAG-NUMERIC");
        assertToken(tokens.get(2), TokenType.VALUE,                "VALUE");
        assertToken(tokens.get(3), TokenType.NUMERIC_LITERAL,      "11");
        assertToken(tokens.get(4), TokenType.THROUGH,              "THRU");
        assertToken(tokens.get(5), TokenType.NUMERIC_LITERAL,      "99");
    }

    @Test
    void tokenize_Test_10() {
        String line = "66  EMP-KEY RENAMES EMP-ID THRU EMP-DEPT";

        List<Token> tokens = lexer.tokenize(line, 0, AreaT.FREE);

        assertEquals(6, tokens.size());

        for (Token token : tokens)
            assertEquals(AreaT.FREE, token.getArea());

        assertToken(tokens.get(0), TokenType.NUMERIC_LITERAL,      "66");
        assertToken(tokens.get(1), TokenType.ALPHANUMERIC_LITERAL, "EMP-KEY");
        assertToken(tokens.get(2), TokenType.RENAMES,              "RENAMES");
        assertToken(tokens.get(3), TokenType.ALPHANUMERIC_LITERAL, "EMP-ID");
        assertToken(tokens.get(4), TokenType.THROUGH,              "THRU");
        assertToken(tokens.get(5), TokenType.ALPHANUMERIC_LITERAL, "EMP-DEPT");
    }
}
