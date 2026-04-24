package getthepicture.copybook.compiler.core;

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

class ParserForLv88Test {
    private static final Lexer lexer = new Lexer();

    @ParameterizedTest
    @CsvSource({
        "'88 A VALUE ''A''.',              'COPYBOOK-LAYOUT', '88 A >> Value(s) in A'",
        "'88 B VALUES ''A'' ''B'' ''C''.', 'COPYBOOK-LAYOUT', '88 B >> Value(s) in A B C'",
        "'88 DIGIT VALUE 1 THROUGH 9.',    'COPYBOOK-LAYOUT', '88 DIGIT >> Value(s) in 1 through 9'"
        // "'88 FLAG VALUE ZERO.',       'COPYBOOK-LAYOUT', ''",
        // "'88 SPACE-FLAG VALUE SPACE.', 'COPYBOOK-LAYOUT', ''",
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
}
