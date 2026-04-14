package getthepicture.copybook.compiler.core;

import org.junit.jupiter.api.Test;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.lexer.Lexer;
import getthepicture.copybook.compiler.core.lexer.Token;
import getthepicture.copybook.compiler.core.parser.Parser;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;
import getthepicture.copybook.compiler.core.parser.layout.Item.ElementaryDataItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.GroupItem;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private static final Lexer lexer = new Lexer();

    @Test
    void semantic_Analysis_Test_01() {
        String line = "05 CUSTOMER-NAME PIC X(10).";

        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        Parser parser = new Parser(tokens);
        CbLayout model = parser.analyze();

        assertNotNull(model);
    }

    @Test
    void semantic_Analysis_Test_02() {
        String line = "05 CUSTOMER-NAME PIC X(10) VALUE 'ABC'.";

        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        Parser parser = new Parser(tokens);
        CbLayout model = parser.analyze();

        assertNotNull(model);
    }

    @Test
    void semantic_Analysis_Test_03() {
        String line = "05 MONTH-NAME PIC X(3) OCCURS 12 TIMES VALUE \"---\".";

        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        Parser parser = new Parser(tokens);
        CbLayout model = parser.analyze();

        assertNotNull(model);
    }

    @Test
    void semantic_Analysis_Test_04() {
        String line = """
 01  CLIRTVO-REC.
           03 MESSAGE-HEADER.
               05 MSGIDA                       PIC  X(030).
""";

        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        Parser parser = new Parser(tokens);
        CbLayout layout = parser.analyze();

        assertNotNull(layout);
        assertEquals(0, layout.getLevel());
        assertNotNull(layout.getChildren());

        GroupItem groupItem_01 = (GroupItem) layout.getChildren().get(0);
        assertNotNull(groupItem_01);
        assertEquals(1, groupItem_01.getLevel());
        assertNotNull(groupItem_01.getChildren());

        GroupItem subordinate_03 = (GroupItem) groupItem_01.getChildren().get(0);
        assertNotNull(subordinate_03);
        assertEquals(3, subordinate_03.getLevel());
        assertNotNull(subordinate_03.getChildren());

        ElementaryDataItem subordinate_05 = (ElementaryDataItem) subordinate_03.getChildren().get(0);
        assertNotNull(subordinate_05);
        assertEquals(5, subordinate_05.getLevel());
        assertNotNull(subordinate_05.getPic());
    }

    @Test
    void semantic_Analysis_Test_05() {
        String line = """
 01  CLIRTVO-REC.
           03 MESSAGE-HEADER.
               05 MSGIDA                       PIC  X(030).
               05 MSGLNG                       PIC  9(005).
               05 MSGCNT                       PIC  S9(004)V9(4).
               05 FILLER                       PIC  X(010).
               05 MSGID                        PIC  X(010).
""";

        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        Parser parser = new Parser(tokens);
        CbLayout layout = parser.analyze();

        assertNotNull(layout);
        assertEquals(0, layout.getLevel());
        assertNotNull(layout.getChildren());

        GroupItem groupItem_01 = (GroupItem) layout.getChildren().get(0);
        assertNotNull(groupItem_01);
        assertEquals(1, groupItem_01.getLevel());
        assertNotNull(groupItem_01.getChildren());

        GroupItem subordinate_03 = (GroupItem) groupItem_01.getChildren().get(0);
        assertNotNull(subordinate_03);
        assertEquals(3, subordinate_03.getLevel());
        assertEquals(5, subordinate_03.getChildren().size());

        ElementaryDataItem subordinate_05 = (ElementaryDataItem) subordinate_03.getChildren().get(3);
        assertNotNull(subordinate_05);
        assertEquals(5, subordinate_05.getLevel());
        assertNotNull(subordinate_05.getPic());
        assertTrue(subordinate_05.isFiller());
    }

    @Test
    void semantic_Analysis_Test_06() {
        String line = """
  01  MAILING-RECORD.
           05  COMPANY-NAME            PIC X(30).
           05  CONTACTS.
               10  PRESIDENT.
                   15  LAST-NAME       PIC X(15).
                   15  FIRST-NAME      PIC X(8).
               10  VP-MARKETING.
                   15  LAST-NAME       PIC X(15).
                   15  FIRST-NAME      PIC X(8).
               10  ALTERNATE-CONTACT.
                   15  TITLE           PIC X(10).
                   15  LAST-NAME       PIC X(15).
                   15  FIRST-NAME      PIC X(8).
           05  ADDRESS                 PIC X(15).
           05  CITY                    PIC X(15).
           05  STATE                   PIC X(2).
           05  ZIP                     PIC 9(5).
""";

        List<Token> tokens = lexer.tokenize(line, 1, AreaT.FREE);
        Parser parser = new Parser(tokens);
        CbLayout layout = parser.analyze();

        assertNotNull(layout);
        assertEquals(0, layout.getLevel());
        assertNotNull(layout.getChildren());

        GroupItem groupItem_01 = (GroupItem) layout.getChildren().get(0);
        assertNotNull(groupItem_01);
        assertEquals(1, groupItem_01.getLevel());
        assertNotNull(groupItem_01.getChildren());

        GroupItem subordinate_05 = (GroupItem) groupItem_01.getChildren().get(1);
        assertNotNull(subordinate_05);
        assertEquals(5, subordinate_05.getLevel());
        assertEquals(3, subordinate_05.getChildren().size());

        GroupItem subordinate_10 = (GroupItem) subordinate_05.getChildren().get(2);
        assertNotNull(subordinate_10);
        assertEquals(10, subordinate_10.getLevel());
        assertEquals("ALTERNATE-CONTACT", subordinate_10.getName());
        assertEquals(3, subordinate_10.getChildren().size());

        ElementaryDataItem subordinate_15 = (ElementaryDataItem) subordinate_10.getChildren().get(2);
        assertNotNull(subordinate_15);
        assertEquals(15, subordinate_15.getLevel());
        assertNotNull(subordinate_15.getPic());
        assertFalse(subordinate_15.isFiller());
    }

    @Test
    void semantic_Analysis_Test_07() {
        String line = "05 FILLER                PIC 9(10) VALUE ZEROS.";
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
        System.out.println("=== dump result ===");
        System.out.println(result);
        System.out.println("===================");

        assertTrue(result.contains(
            "05 FILLER >> PIC: [9(10)] Class='NUMERIC' (Semantic='NONE'), Signed=false, Int=10, Dec=0, Len=10, Usage='DISPLAY' VALUE: \"0\""));
    }
}
