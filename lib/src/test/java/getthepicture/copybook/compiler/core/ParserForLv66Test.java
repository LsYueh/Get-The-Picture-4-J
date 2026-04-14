package getthepicture.copybook.compiler.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.lexer.Lexer;
import getthepicture.copybook.compiler.core.lexer.Token;
import getthepicture.copybook.compiler.core.parser.Parser;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;
import getthepicture.copybook.compiler.core.parser.layout.Item.Renames66Item;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ParserForLv66Test {
    private static final Lexer lexer = new Lexer();

    @ParameterizedTest
    @CsvSource({
        "'05 EMP-ID PIC X(10).66 EMP-KEY RENAMES EMP-ID.',                                     'COPYBOOK-LAYOUT', '66 EMP-KEY >> Renames EMP-ID'",
        "'05 EMP-ID PIC X(10).05 EMP-DEPT PIC X(04).66 EMP-KEY RENAMES EMP-ID THRU EMP-DEPT.', 'COPYBOOK-LAYOUT', '  66 EMP-KEY >> Renames EMP-ID through EMP-DEPT'"
    })
    void test_Set(String line, String expected_01, String expected_02) {
        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        Parser parser = new Parser(tokens);
        CbLayout layout = parser.analyze();
        layout.seal();

        assertNotNull(layout);

        List<Renames66Item> renames66 = layout.getRenames66();
        assertEquals(1, renames66.size());

        StringWriter sw = new StringWriter();
        try (PrintWriter writer = new PrintWriter(sw)) {
            layout.dump(writer);
        }
        String result = sw.toString();

        assertTrue(result.contains(expected_01));
        assertTrue(result.contains(expected_02));
    }
}
