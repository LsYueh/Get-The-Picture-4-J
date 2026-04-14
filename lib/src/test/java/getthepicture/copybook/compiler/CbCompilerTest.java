package getthepicture.copybook.compiler;

import org.junit.jupiter.api.Test;

import getthepicture.copybook.compiler.core.parser.layout.CbLayout;
import getthepicture.copybook.compiler.core.parser.layout.Item.Condition88Item;
import getthepicture.copybook.compiler.core.parser.layout.Item.ElementaryDataItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.GroupItem;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;
import getthepicture.picture.utils.EncodingFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import static org.junit.jupiter.api.Assertions.*;

class CbCompilerTest {
    private static final Charset CP950 = EncodingFactory.getCP950();

    @Test
    void copybook_Compiler_Test_01() throws IOException {
        var stream = getClass().getResourceAsStream("/sample-cobol-copybook.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var reader = new InputStreamReader(stream, CP950)) {
            CbLayout layout = CbCompiler.fromReader(reader);

            assertNotNull(layout);
            assertEquals(0, layout.getLevel());
            assertNotNull(layout.getChildren());
            assertEquals(3, layout.getChildren().size());

            GroupItem groupItem = (GroupItem) layout.getChildren().get(2);
            assertNotNull(groupItem);

            ElementaryDataItem elementaryDataItem_05 = (ElementaryDataItem) groupItem.getChildren().get(0);
            assertNotNull(elementaryDataItem_05);
            assertEquals(5, elementaryDataItem_05.getLevel());
            assertNotNull(elementaryDataItem_05.getPic());
            assertFalse(elementaryDataItem_05.isFiller());

            final String expected = "THIS IS A VERY LONG DESCRIPTION THAT NEEDS TO BE CONTINUED ACROSS MULTIPLE LINES";
            assertEquals(expected, elementaryDataItem_05.getValue());

            // layout.dump(new PrintWriter(System.out, true));
        }
    }

    @Test
    void copybook_Compiler_Test_02() throws Exception {
        var stream = getClass().getResourceAsStream("/employee-record-with-levle-88.cpy");
        assertNotNull(stream, "Test resource not found");

        try (var reader = new InputStreamReader(stream, CP950)) {
            CbLayout layout = CbCompiler.fromReader(reader);

            // layout.dump(new PrintWriter(System.out, true));

            assertNotNull(layout);
            assertEquals(0, layout.getLevel());
            assertNotNull(layout.getChildren());
            assertEquals(1, layout.getChildren().size());
            assertEquals(3, layout.getStorageOccupied());

            GroupItem GROUP_ITEM_01 = (GroupItem) layout.getChildren().get(0);
            assertNotNull(GROUP_ITEM_01);
            assertEquals(1, GROUP_ITEM_01.getLevel());
            assertNotNull(GROUP_ITEM_01.getChildren());
            assertEquals(3, GROUP_ITEM_01.getChildren().size());

            for (DataItem child : GROUP_ITEM_01.getChildren()) {
                ElementaryDataItem ITEM_05 = (ElementaryDataItem) child;
                assertEquals(5, ITEM_05.getLevel());
                assertNotNull(ITEM_05.getChildren());

                for (DataItem condChild : ITEM_05.getChildren()) {
                    Condition88Item cond = (Condition88Item) condChild;
                    assertEquals(88, cond.getLevel());
                    assertTrue(cond.getValues().size() > 0);
                }
            }
        }
    }
}
