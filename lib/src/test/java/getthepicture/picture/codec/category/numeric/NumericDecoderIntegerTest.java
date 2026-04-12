package getthepicture.picture.codec.category.numeric;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.meta.PictureMeta;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class NumericDecoderIntegerTest {

    private static boolean isIntegerValue(Object value) {
        if (value instanceof BigDecimal bd)
            return bd.stripTrailingZeros().scale() <= 0;
        return value instanceof Byte   || value instanceof Short  ||
               value instanceof Integer || value instanceof Long;
    }

    // =========================================================================
    // Byte
    // =========================================================================

    @ParameterizedTest(name = "decode byte: text={0}, pic={1}")
    @CsvSource({
        " 9,  9(1),   9",
        " I, S9(1),   9",
        " R, S9(1),  -9",
        "99,  9(2),  99",
        "9I, S9(2),  99",
        "9R, S9(2), -99",
    })
    void decode_default_byte(String text, String picString, int expectedValue) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.trim().getBytes(StandardCharsets.US_ASCII);

        Object value = PictureCodec.forMeta(pic).decode(buffer);

        assertInstanceOf(Byte.class, value);
        assertEquals((byte) expectedValue, value);
        assertTrue(isIntegerValue(value));
    }

    // =========================================================================
    // Short
    // =========================================================================

    @ParameterizedTest(name = "decode short: text={0}, pic={1}")
    @CsvSource({
        " 998,  9(3),   998",
        " 99H, S9(3),   998",
        " 99Q, S9(3),  -998",
        "9998,  9(4),  9998",
        "999H, S9(4),  9998",
        "999Q, S9(4), -9998",
    })
    void decode_default_short(String text, String picString, int expectedValue) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.trim().getBytes(StandardCharsets.US_ASCII);

        Object value = PictureCodec.forMeta(pic).decode(buffer);

        assertInstanceOf(Short.class, value);
        assertEquals((short) expectedValue, value);
        assertTrue(isIntegerValue(value));
    }

    // =========================================================================
    // Int
    // =========================================================================

    @ParameterizedTest(name = "decode int: text={0}, pic={1}")
    @CsvSource({
        "    99997,  9(5),      99997",
        "    9999G, S9(5),      99997",
        "    9999P, S9(5),     -99997",
        "999999997,  9(9),  999999997",
        "99999999G, S9(9),  999999997",
        "99999999P, S9(9), -999999997",
    })
    void decode_default_int(String text, String picString, long expectedValue) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.trim().getBytes(StandardCharsets.US_ASCII);
        
        Object value = PictureCodec.forMeta(pic).decode(buffer);

        assertInstanceOf(Integer.class, value);
        assertEquals((int) expectedValue, value);
        assertTrue(isIntegerValue(value));
    }

    // =========================================================================
    // Long
    // =========================================================================

    @ParameterizedTest(name = "decode long: text={0}, pic={1}")
    @CsvSource({
        "        9999999996,  9(10),          9999999996",
        "        999999999F, S9(10),          9999999996",
        "        999999999O, S9(10),         -9999999996",
        "999999999999999996,  9(18),  999999999999999996",
        "99999999999999999F, S9(18),  999999999999999996",
        "99999999999999999O, S9(18), -999999999999999996",
    })
    void decode_default_long(String text, String picString, long expectedValue) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.trim().getBytes(StandardCharsets.US_ASCII);

        Object value = PictureCodec.forMeta(pic).decode(buffer);

        assertInstanceOf(Long.class, value);
        assertEquals(expectedValue, value);
        assertTrue(isIntegerValue(value));
    }

    // =========================================================================
    // BigDecimal (scale = 0)
    // =========================================================================

    @ParameterizedTest(name = "decode BigDecimal scale=0: text={0}, pic={1}")
    @CsvSource({
        "         9999999999999999995,  9(19),           9999999999999999995",
        "         999999999999999999E, S9(19),           9999999999999999995",
        "         999999999999999999N, S9(19),          -9999999999999999995",
        "9999999999999999999999999995,  9(28),  9999999999999999999999999995",
        "999999999999999999999999999E, S9(28),  9999999999999999999999999995",
        "999999999999999999999999999N, S9(28), -9999999999999999999999999995",
    })
    void decode_default_decimalWithScaleZero(String text, String picString, String expectedValue) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.trim().getBytes(StandardCharsets.US_ASCII);
        
        Object value = PictureCodec.forMeta(pic).decode(buffer);

        assertInstanceOf(BigDecimal.class, value);
        assertEquals(0, ((BigDecimal) value).compareTo(new BigDecimal(expectedValue)));
        assertTrue(isIntegerValue(value));
    }

    // =========================================================================
    // SIGN IS LEADING
    // =========================================================================

    @Test
    void decode_withLeadingSign() {
        PictureMeta pic = PictureMeta.parse("S9(5)");
        byte[] buffer = "}0123".getBytes(StandardCharsets.US_ASCII);
        Object value = PictureCodec.forMeta(pic).withSignIsLeading().decode(buffer);

        assertInstanceOf(Integer.class, value);
        assertEquals(-123, value);
        assertTrue(isIntegerValue(value));
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @ParameterizedTest(name = "decode overflow: text={0}, pic={1}")
    @CsvSource({
        "99999999999999999999999999994,  9(29)",
        "9999999999999999999999999999D, S9(29)",
        "9999999999999999999999999999M, S9(29)",
    })
    void decode_throwsArithmeticException(String text, String picString) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.trim().getBytes(StandardCharsets.US_ASCII);
        assertThrows(ArithmeticException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_numericWithNonDigit_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("9(5)");
        byte[] buffer = "12A34".getBytes(StandardCharsets.US_ASCII);
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }
}
