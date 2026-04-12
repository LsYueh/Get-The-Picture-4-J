package getthepicture.picture.core.meta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.utils.EncodingFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NumericMetaTest01 {

    // =========================================================================
    // Integer
    // =========================================================================

    @Test
    void integer_preservesSign() {
        PictureMeta pic = PictureMeta.parse("9(5)");
        NumericMeta v = NumericMeta.parse(-123, pic);
        String actual = new String(v.getChars(), EncodingFactory.getCP950());
        assertEquals("00123", actual);
        assertEquals(0, v.getDecimalDigits());
        assertTrue(v.isNegative());
    }

    // =========================================================================
    // Decimal
    // =========================================================================

    @ParameterizedTest
    @CsvSource({
        "9(5)V9(2), 12.3, 0001230, 2",
    })
    void decimal_positive(String picString, String value, String expected, int expectedScale) {
        BigDecimal _value = new BigDecimal(value);
        PictureMeta pic = PictureMeta.parse(picString);
        NumericMeta v = NumericMeta.parse(_value, pic);
        String actual = new String(v.getChars(), EncodingFactory.getCP950());
        assertEquals(expected, actual);
        assertEquals(expectedScale, v.getDecimalDigits());
        assertFalse(v.isNegative());
    }

    @ParameterizedTest
    @CsvSource({
        "9(5)V9(2), -12.3, 0001230, 2",
        "9(2)V9(3), -12.3,   12300, 3",
    })
    void decimal_isNegative(String picString, String value, String expected, int expectedScale) {
        BigDecimal _value = new BigDecimal(value);
        PictureMeta pic = PictureMeta.parse(picString);
        NumericMeta v = NumericMeta.parse(_value, pic);
        String actual = new String(v.getChars(), EncodingFactory.getCP950());
        assertEquals(expected.trim(), actual);
        assertEquals(expectedScale, v.getDecimalDigits());
        assertTrue(v.isNegative());
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void dateTime_reject_time6() {
        PictureMeta pic = PictureMeta.parse("9(6)");
        pic.setSemantic(PicClauseSemantic.TIME6);
        assertThrows(UnsupportedOperationException.class,
            () -> NumericMeta.parse(LocalDateTime.now(), pic));
    }

    @Test
    void dateTime_reject_dateGregorian8() {
        PictureMeta pic = PictureMeta.parse("9(8)");
        pic.setSemantic(PicClauseSemantic.GREGORIAN_DATE);
        assertThrows(UnsupportedOperationException.class,
            () -> NumericMeta.parse(LocalDateTime.now(), pic));
    }

    @Test
    void unsupportedType_throws() {
        assertThrows(UnsupportedOperationException.class,
            () -> NumericMeta.parse(new Object(), PictureMeta.parse("X(5)")));
    }
}
