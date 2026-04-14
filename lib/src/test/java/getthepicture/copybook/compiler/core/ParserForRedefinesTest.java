package getthepicture.copybook.compiler.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.lexer.Lexer;
import getthepicture.copybook.compiler.core.lexer.Token;
import getthepicture.copybook.compiler.core.parser.Parser;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ParserForRedefinesTest {
    private static final Lexer lexer = new Lexer();

    @ParameterizedTest
    @CsvSource({
        "'05 A PIC X.05 B REDEFINES A.', 'COPYBOOK-LAYOUT', '05 B REDEFINES A.'",
        "'05 FIELD-DATA PIC X(126).05 COMT-DATA REDEFINES FIELD-DATA.', 'COPYBOOK-LAYOUT', '05 COMT-DATA REDEFINES FIELD-DATA.'"
    })
    void test_Set(String line, String expected_01, String expected_02) {
        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);

        Parser parser = new Parser(tokens);

        CbLayout layout = parser.analyze();
        layout.seal();

        assertNotNull(layout);

        StringWriter sw = new StringWriter();
        try (PrintWriter writer = new PrintWriter(sw)) {
            layout.dump(writer);
        }

        String result = sw.toString();

        assertTrue(result.contains(expected_01));
        assertTrue(result.contains(expected_02));
    }

    @ParameterizedTest
    @CsvSource({
        "'05 B REDEFINES A.'",
        "'05 COMT-DATA REDEFINES FIELD-DATA.'"
    })
    void test_Throw_CompileException(String line) {
        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        
        Parser parser = new Parser(tokens);

        assertThrows(CompileException.class, parser::analyze);
    }
}
