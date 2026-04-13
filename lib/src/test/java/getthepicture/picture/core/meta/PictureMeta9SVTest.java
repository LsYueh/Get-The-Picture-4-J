package getthepicture.picture.core.meta;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;

import static org.junit.jupiter.api.Assertions.*;

class PictureMeta9SVTest {

    private static void assertPic(PictureMeta pic, String symbols, PicClauseBaseClass baseClass,
                                  int integerDigits, int decimalDigits, int digitCount, boolean signed) {
        assertEquals(symbols,       pic.getRaw());
        assertEquals(baseClass,     pic.getBaseClass());
        assertEquals(integerDigits, pic.getIntegerDigits());
        assertEquals(decimalDigits, pic.getDecimalDigits());
        assertEquals(digitCount,    pic.getDigitCount());
        assertEquals(signed,        pic.isSigned());
    }

    // ─────────────────────────
    // Numeric - Integer only
    // ─────────────────────────

    @ParameterizedTest
    @CsvSource({
        "9,    1, 0, 1, false",
        "9(4), 4, 0, 4, false",
        "9(1), 1, 0, 1, false",
        "999,  3, 0, 3, false",
    })
    void parse_PIC_9(String symbols, int integerDigits, int decimalDigits, int digitCount, boolean signed) {
        PictureMeta pic = PictureMeta.parse(symbols);
        assertPic(pic, symbols, PicClauseBaseClass.NUMERIC, integerDigits, decimalDigits, digitCount, signed);
    }

    // ─────────────────────────
    // Numeric - Signed
    // ─────────────────────────

    @ParameterizedTest
    @CsvSource({
        "S9,    1, 0, 1, true",
        "S9(5), 5, 0, 5, true",
    })
    void parse_PIC_S9(String symbols, int integerDigits, int decimalDigits, int digitCount, boolean signed) {
        PictureMeta pic = PictureMeta.parse(symbols);
        assertPic(pic, symbols, PicClauseBaseClass.NUMERIC, integerDigits, decimalDigits, digitCount, signed);
    }

    // ─────────────────────────
    // Numeric - Decimal (V)
    // ─────────────────────────

    @ParameterizedTest
    @CsvSource({
        "9V9,      1, 1, 2, false",
        "9(3)V9(2),3, 2, 5, false",
        "999V99,   3, 2, 5, false",
    })
    void parse_PIC_9V9(String symbols, int integerDigits, int decimalDigits, int digitCount, boolean signed) {
        PictureMeta pic = PictureMeta.parse(symbols);
        assertPic(pic, symbols, PicClauseBaseClass.NUMERIC, integerDigits, decimalDigits, digitCount, signed);
    }

    // ─────────────────────────
    // Numeric - Signed + Decimal
    // ─────────────────────────

    @ParameterizedTest
    @CsvSource({
        "S9V9,     1, 1, 2, true",
        "S9(5)V99, 5, 2, 7, true",
    })
    void parse_PIC_S9V9(String symbols, int integerDigits, int decimalDigits, int digitCount, boolean signed) {
        PictureMeta pic = PictureMeta.parse(symbols);
        assertPic(pic, symbols, PicClauseBaseClass.NUMERIC, integerDigits, decimalDigits, digitCount, signed);
    }
}
