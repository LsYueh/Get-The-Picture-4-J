package getthepicture.cobol;

import org.junit.jupiter.api.Test;

import getthepicture.cobol.core.Area;
import getthepicture.picture.utils.EncodingFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CobolLineTest {
    private static final Charset CP950 = EncodingFactory.getCP950();

    @Test
    void readerTest() throws IOException {
        var stream = getClass().getResourceAsStream("/sample-cobol-copybook.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var reader = new InputStreamReader(stream, CP950)) {
            List<CobolLine> lines = CobolLine.fromReader(reader);

            assertEquals(12, lines.size());

            assertEquals("01 CUSTOMER-RECORD.", lines.get(0).getText());
            assertEquals(Area.A, lines.get(0).getArea());

            assertEquals("        'NEEDS TO BE CONTINUED ACROSS MULTIPLE LINES'.", lines.get(lines.size() - 1).getText());
            assertEquals(Area.B, lines.get(lines.size() - 1).getArea());
        }
    }
}
