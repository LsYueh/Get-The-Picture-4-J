package getthepicture.picture.core.meta;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;

import static org.junit.jupiter.api.Assertions.*;

class PictureMetaAXTest {

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
    // Alphabetic (A)
    // ─────────────────────────

    @ParameterizedTest
    @CsvSource({
        "A,      1, 0,  1, false",
        "A(1),   1, 0,  1, false",
        "A(20), 20, 0, 20, false",
    })
    void parse_PIC_A(String symbols, int integerDigits, int decimalDigits, int digitCount, boolean signed) {
        PictureMeta pic = PictureMeta.parse(symbols);
        assertPic(pic, symbols, PicClauseBaseClass.ALPHABETIC, integerDigits, decimalDigits, digitCount, signed);
    }

    // ─────────────────────────
    // Alphanumeric (X)
    // ─────────────────────────

    @ParameterizedTest
    @CsvSource({
        "X,         1, 0,  1, false",
        "XX,        2, 0,  2, false",
        "X(1),      1, 0,  1, false",
        "X(20),    20, 0, 20, false",
        "XXX(10)X, 13, 0, 13, false",
    })
    void parse_PIC_X(String symbols, int integerDigits, int decimalDigits, int digitCount, boolean signed) {
        PictureMeta pic = PictureMeta.parse(symbols);
        assertPic(pic, symbols, PicClauseBaseClass.ALPHANUMERIC, integerDigits, decimalDigits, digitCount, signed);
    }
}