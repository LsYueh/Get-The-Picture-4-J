package getthepicture.copybook.resolver;

import org.junit.jupiter.api.Test;

import getthepicture.copybook.compiler.CbCompiler;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;
import getthepicture.copybook.resolver.storage.CbStorage;
import getthepicture.picture.utils.EncodingFactory;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class CbResolverTest {

    private static final Charset cp950 = EncodingFactory.getCP950();

    @Test
    public void copybook_Resolver_Test_01() throws Exception {
        var stream = getClass().getResourceAsStream("/sample-cobol-copybook.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var sr = new InputStreamReader(stream, cp950)) {

            CbLayout layout = CbCompiler.fromReader(sr);
            assertNotNull(layout);

            CbStorage storage = CbResolver.fromLayout(layout);
            assertNotNull(storage);

            var sw = new StringWriter();
            storage.dump(new PrintWriter(sw));

            String result = sw.toString();

            assertTrue(result.contains("LONG-DESCRIPTION start=60"));
            assertTrue(result.contains("DESC-LINE start=60 len=50"));
        }
    }

    @Test
    public void copybook_Resolver_Test_02() throws Exception {
        var stream = getClass().getResourceAsStream("/copybook-with-redefines.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var sr = new InputStreamReader(stream, cp950)) {

            CbLayout layout = CbCompiler.fromReader(sr);
            assertNotNull(layout);

            CbStorage storage = CbResolver.fromLayout(layout);
            assertNotNull(storage);

            var sw = new StringWriter();
            storage.dump(new PrintWriter(sw));

            String result = sw.toString();

            assertTrue(result.contains("B start=5"));
            assertTrue(result.contains("B-1 start=5 len=2 end=7"));
            assertTrue(result.contains("B-2 start=7 len=4 end=11"));
            assertTrue(result.contains("C start=11 len=4 end=15"));
        }
    }

    @Test
    public void copybook_Resolver_Test_03() throws Exception {
        var stream = getClass().getResourceAsStream("/copybook-with-redefines-group-in-middle.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var sr = new InputStreamReader(stream, cp950)) {

            CbLayout layout = CbCompiler.fromReader(sr);
            assertNotNull(layout);

            CbStorage storage = CbResolver.fromLayout(layout);
            assertNotNull(storage);

            var sw = new StringWriter();
            storage.dump(new PrintWriter(sw));

            String result = sw.toString();

            assertTrue(result.contains("A start=5"));
            assertTrue(result.contains("A-1 start=5 len=2 end=7"));
            assertTrue(result.contains("A-2 start=7 len=4 end=11"));
            assertTrue(result.contains("B start=5"));
            assertTrue(result.contains("B-1 start=5"));
            assertTrue(result.contains("B-1-1 start=5 len=1 end=6"));
            assertTrue(result.contains("B-1-2 start=6 len=1 end=7"));
            assertTrue(result.contains("B-2 start=7 len=4 end=11"));
            assertTrue(result.contains("C start=11"));
        }
    }

    @Test
    public void copybook_Resolver_Test_04() throws Exception {
        var stream = getClass().getResourceAsStream("/nested-occurs-record.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var sr = new InputStreamReader(stream, cp950)) {

            CbLayout layout = CbCompiler.fromReader(sr);
            assertNotNull(layout);

            CbStorage storage = CbResolver.fromLayout(layout);
            assertNotNull(storage);

            var sw = new StringWriter();
            storage.dump(new PrintWriter(sw));

            String result = sw.toString();

            assertTrue(result.contains("ORDER-ID start=1 len=10 end=11"));
            assertTrue(result.contains("ORDER-LINES(1) start=31"));
            assertTrue(result.contains("ORDER-LINES(2) start=56"));
            assertTrue(result.contains("LINE-AMOUNTS(1) start=67"));
            assertTrue(result.contains("AMOUNT start=67 len=7 end=74"));
            assertTrue(result.contains("LINE-AMOUNTS(2) start=74"));
            assertTrue(result.contains("AMOUNT start=74 len=7 end=81"));
            assertTrue(result.contains("ORDER-LINES(3) start=81"));
            assertTrue(result.contains("AMOUNT start=99 len=7 end=106"));
            assertTrue(result.contains("TOTAL-AMOUNT start=106 len=9 end=115"));
        }
    }

    @Test
    public void copybook_Resolver_Test_05_Redefines() throws Exception {
        var stream = getClass().getResourceAsStream("/twse/m05.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var sr = new InputStreamReader(stream, cp950)) {

            CbLayout layout = CbCompiler.fromReader(sr);
            assertNotNull(layout);

            CbStorage storage = CbResolver.fromLayout(layout);
            assertNotNull(storage);

            var sw = new StringWriter();
            storage.dump(new PrintWriter(sw));

            String result = sw.toString();

            assertTrue(result.contains("PD-ID start=1 len=4 end=5"));
            assertTrue(result.contains("FIELD-DATA start=29 len=126 end=155"));
            assertTrue(result.contains("COMT-DATA start=29"));
            assertTrue(result.contains("COMT-VALUE start=29 len=126 end=155"));
            assertTrue(result.contains("CMEN-DATA start=29"));
            assertTrue(result.contains("CMEN-VALUE start=29 len=126 end=155"));
            assertTrue(result.contains("ANCE-DATA start=29"));
            assertTrue(result.contains("ANNOUNCE-YMD start=29 len=8 end=37"));
            assertTrue(result.contains("FILLER start=152 len=3 end=155"));
            assertTrue(result.contains("OBJ-DATA start=29"));
            assertTrue(result.contains("OBJ-ID start=29 len=6 end=35"));
            assertTrue(result.contains("FILLER start=62 len=93 end=155"));
            assertTrue(result.contains("CTRL-DATA start=29"));
            assertTrue(result.contains("CREATION-S start=29 len=1 end=30"));
            assertTrue(result.contains("FILLER start=96 len=59 end=155"));
        }
    }

    @Test
    public void copybook_Resolver_Test_06_Lv66_Renames() throws Exception {
        var stream = getClass().getResourceAsStream("/twse/m02.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var sr = new InputStreamReader(stream, cp950)) {

            CbLayout layout = CbCompiler.fromReader(sr);
            assertNotNull(layout);

            CbStorage storage = CbResolver.fromLayout(layout);
            assertNotNull(storage);

            var sw = new StringWriter();
            storage.dump(new PrintWriter(sw));

            String result = sw.toString();

            assertTrue(result.contains("05 ACNT-BROKER start=23 len=4 end=27"));
            assertTrue(result.contains("05 ACNT-NO start=27 len=7 end=34"));
            assertTrue(result.contains("66 *ACNT start=23 len=11 end=34"));
            assertTrue(result.contains("05 BROKER-ID start=8 len=4 end=12"));
            assertTrue(result.contains("05 TX-DATE start=12 len=8 end=20"));
            assertTrue(result.contains("05 SEQNO start=20 len=3 end=23"));
            assertTrue(result.contains("66 *M02-KEY start=8 len=15 end=23"));
        }
    }
}
