package getthepicture.picture.core.meta;

import org.junit.jupiter.api.Test;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;

import static org.junit.jupiter.api.Assertions.*;

class PictureMetaTest {

    // ─────────────────────────
    // Whitespace / Case
    // ─────────────────────────

    @Test
    void parse_lowerCase_withSpaces() {
        PictureMeta pic = PictureMeta.parse("    s9(2) v9 ");
        assertEquals("S9(2)V9", pic.getRaw());
        assertEquals(PicClauseBaseClass.NUMERIC, pic.getBaseClass());
        assertEquals(2, pic.getIntegerDigits());
        assertEquals(1, pic.getDecimalDigits());
        assertEquals(3, pic.getDigitCount());
        assertTrue(pic.isSigned());
    }

    // ─────────────────────────
    // Error handling
    // ─────────────────────────

    @Test
    void parse_empty_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> PictureMeta.parse(""));
    }

    @Test
    void parse_withPicKeyword_shouldThrow() {
        assertThrows(UnsupportedOperationException.class,
            () -> PictureMeta.parse("PIC 9(3)"));
    }
}
