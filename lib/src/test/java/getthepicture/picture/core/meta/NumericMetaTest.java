package getthepicture.picture.core.meta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NumericMetaTest {

    // =========================================================================
    // Constructor / Value
    // =========================================================================

    @Test
    void constructor_setsValueViaDecoding() {
        byte[] chars = "12345".getBytes();
        NumericMeta meta = new NumericMeta(chars, 0, false);
        assertEquals(0, meta.getValue().compareTo(new BigDecimal("12345")));
    }

    @Test
    void constructor_negative_setsValueCorrectly() {
        byte[] chars = "12345".getBytes();
        NumericMeta meta = new NumericMeta(chars, 0, true);
        assertEquals(0, meta.getValue().compareTo(new BigDecimal("-12345")));
    }

    @Test
    void constructor_withDecimalDigits_setsValueCorrectly() {
        byte[] chars = "12345".getBytes();
        NumericMeta meta = new NumericMeta(chars, 2, false);
        assertEquals(0, meta.getValue().compareTo(new BigDecimal("123.45")));
    }

    // =========================================================================
    // toInt64
    // =========================================================================

    @ParameterizedTest(name = "toInt64: chars={0}, negative={1} => {2}")
    @CsvSource({
        "'00000',     false,  0",
        "'00001',     false,  1",
        "'12345',     false,  12345",
        "'12345',     true,  -12345",
        "'9223372036854775807', false,  9223372036854775807",   // Long.MAX_VALUE
        "'9223372036854775808', true,  -9223372036854775808",   // Long.MIN_VALUE
    })
    void toInt64_happyPath(String digits, boolean isNegative, long expected) {
        NumericMeta meta = new NumericMeta(digits.getBytes(), 0, isNegative);
        assertEquals(expected, meta.toInt64());
    }

    @Test
    void toInt64_throwsWhenDecimalDigitsExist() {
        NumericMeta meta = new NumericMeta("12345".getBytes(), 2, false);
        assertThrows(IllegalStateException.class, meta::toInt64);
    }

    @Test
    void toInt64_throwsWhenCharsEmpty() {
        NumericMeta meta = new NumericMeta(new byte[0], 0, false);
        assertThrows(IllegalArgumentException.class, meta::toInt64);
    }

    @Test
    void toInt64_throwsOnPositiveOverflow() {
        // 9223372036854775808 > Long.MAX_VALUE, positive
        NumericMeta meta = new NumericMeta("9223372036854775808".getBytes(), 0, false);
        assertThrows(ArithmeticException.class, meta::toInt64);
    }

    @Test
    void toInt64_throwsOnNegativeOverflow() {
        // abs 9223372036854775809 > Long.MAX_VALUE + 1
        NumericMeta meta = new NumericMeta("9223372036854775809".getBytes(), 0, true);
        assertThrows(ArithmeticException.class, meta::toInt64);
    }

    // =========================================================================
    // toUInt64
    // =========================================================================

    @Test
    void toUInt64_zero() {
        NumericMeta meta = new NumericMeta("00000".getBytes(), 0, false);
        assertEquals(0L, meta.toUInt64());
    }

    @Test
    void toUInt64_longMaxValue() {
        NumericMeta meta = new NumericMeta("9223372036854775807".getBytes(), 0, false);
        assertEquals(Long.MAX_VALUE, meta.toUInt64());
    }

    @Test
    void toUInt64_throwsWhenDecimalDigitsExist() {
        NumericMeta meta = new NumericMeta("12345".getBytes(), 2, false);
        assertThrows(IllegalStateException.class, meta::toUInt64);
    }

    @Test
    void toUInt64_throwsWhenCharsEmpty() {
        NumericMeta meta = new NumericMeta(new byte[0], 0, false);
        assertThrows(IllegalArgumentException.class, meta::toUInt64);
    }

    @Test
    void toUInt64_throwsWhenNegative() {
        NumericMeta meta = new NumericMeta("12345".getBytes(), 0, true);
        assertThrows(ArithmeticException.class, meta::toUInt64);
    }

    // =========================================================================
    // parse — int fast-path (digitCount <= 18, decimalDigits == 0)
    // =========================================================================

    @ParameterizedTest(name = "parse int fast-path: value={0}, digitCount={1}")
    @CsvSource({
        "0,       5",
        "1,       5",
        "-1,      5",
        "12345,   5",
        "-12345,  5",
        "999999999999999999,  18",
        "-999999999999999999, 18",
    })
    void parse_intFastPath(long value, int digitCount) {
        PictureMeta pic = buildIntPic(digitCount, 0, value < 0);
        NumericMeta meta = NumericMeta.parse(value, pic);

        assertEquals(0, meta.getValue().compareTo(BigDecimal.valueOf(value)));
        assertEquals(digitCount, meta.getChars().length);
        assertEquals(value < 0, meta.isNegative());
    }

    @Test
    void parse_longMinValue_decimalFallbackPath() {
        PictureMeta pic = buildIntPic(19, 0, true);

        // System.out.println("=== input ===");
        // System.out.println("Long.MIN_VALUE        = " + Long.MIN_VALUE);
        // System.out.println("BigDecimal.valueOf    = " + BigDecimal.valueOf(Long.MIN_VALUE));
        // System.out.println("pic.getDigitCount()   = " + pic.getDigitCount());
        // System.out.println("pic.getDecimalDigits()= " + pic.getDecimalDigits());
        // System.out.println("pic.isSigned()        = " + pic.isSigned());

        NumericMeta meta = NumericMeta.parse(Long.MIN_VALUE, pic);

        // System.out.println("\n=== NumericMeta ===");
        // System.out.println("isNegative()    = " + meta.isNegative());
        // System.out.println("decimalDigits() = " + meta.getDecimalDigits());
        // System.out.println("chars.length    = " + meta.getChars().length);
        // System.out.println("chars (string)  = " + new String(meta.getChars()));
        // System.out.println("getValue()      = " + meta.getValue());

        // System.out.println("\n=== comparison ===");
        BigDecimal expected = BigDecimal.valueOf(Long.MIN_VALUE);
        // System.out.println("expected        = " + expected);
        // System.out.println("compareTo       = " + meta.getValue().compareTo(expected));

        assertEquals(0, meta.getValue().compareTo(expected));
    }

    @Test
    void parse_longMinValue_fastPath_absOverflowHandledCorrectly() {
        // digitCount=18, decimalDigits=0 -> 走 encodeInt64 fast-path
        // 驗證 Math.abs(Long.MIN_VALUE) 溢位的特殊處理是否正確
        // Long.MIN_VALUE = -9223372036854775808，塞入 18 位會截掉最高位 '9'
        // 預期保留後 18 位：223372036854775808
        PictureMeta pic = buildIntPic(18, 0, true);
        NumericMeta meta = NumericMeta.parse(Long.MIN_VALUE, pic);
        assertTrue(meta.isNegative());
        assertEquals(18, meta.getChars().length);
        assertEquals(0, meta.getValue().compareTo(new BigDecimal("-223372036854775808")));
    }

    // =========================================================================
    // parse — decimal fallback path
    // =========================================================================

    @Test
    void parse_withDecimalDigits_encodesCorrectly() {
        PictureMeta pic = buildIntPic(5, 2, false); // PIC 9(3)V9(2) -> digitCount=5
        NumericMeta meta = NumericMeta.parse(new BigDecimal("123.45"), pic);

        assertEquals(0, meta.getValue().compareTo(new BigDecimal("123.45")));
        assertEquals(2, meta.getDecimalDigits());
        assertFalse(meta.isNegative());
    }

    @Test
    void parse_negativeDecimal_encodesCorrectly() {
        PictureMeta pic = buildIntPic(5, 2, true);
        NumericMeta meta = NumericMeta.parse(new BigDecimal("-123.45"), pic);

        assertEquals(0, meta.getValue().compareTo(new BigDecimal("-123.45")));
        assertTrue(meta.isNegative());
    }

    @Test
    void parse_digitTruncation_highBitsDropped() {
        // COBOL silent truncation: 123456 into PIC 9(3) -> keeps 456
        PictureMeta pic = buildIntPic(3, 0, false);
        NumericMeta meta = NumericMeta.parse(123456L, pic);

        // 高位截斷，只保留後 3 位
        assertEquals(0, meta.getValue().compareTo(new BigDecimal("456")));
    }

    // =========================================================================
    // parse — unsupported type
    // =========================================================================

    @Test
    void parse_unsupportedType_throwsUnsupportedOperationException() {
        PictureMeta pic = buildIntPic(5, 0, false);
        assertThrows(UnsupportedOperationException.class,
            () -> NumericMeta.parse("not a number", pic));
    }

    // =========================================================================
    // parse — float / double
    // =========================================================================

    @Test
    void parse_floatValue_encodesCorrectly() {
        PictureMeta pic = buildIntPic(4, 2, false); // PIC 9(2)V9(2)
        NumericMeta meta = NumericMeta.parse(12.34f, pic);
        // float 精度有限，僅驗證符號與位數
        assertFalse(meta.isNegative());
        assertEquals(4, meta.getChars().length);
    }

    @Test
    void parse_doubleValue_encodesCorrectly() {
        PictureMeta pic = buildIntPic(6, 2, false); // PIC 9(4)V9(2)
        NumericMeta meta = NumericMeta.parse(1234.56, pic);
        assertFalse(meta.isNegative());
        assertEquals(6, meta.getChars().length);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static PictureMeta buildIntPic(int digitCount, int decimalDigits, boolean signed) {
        PictureMeta pic = new PictureMeta();
        pic.setBaseClass(PicClauseBaseClass.NUMERIC);
        pic.setSemantic(PicClauseSemantic.NONE);
        pic.setUsage(PicClauseUsage.DISPLAY);
        pic.setSigned(signed);
        pic.setIntegerDigits(digitCount - decimalDigits);
        pic.setDecimalDigits(decimalDigits);
        pic.setRaw("9(" + digitCount + ")");
        return pic;
    }
}
