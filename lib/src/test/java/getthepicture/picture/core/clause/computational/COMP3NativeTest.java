package getthepicture.picture.core.clause.computational;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.NumericMeta;
import getthepicture.picture.core.meta.PictureMeta;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

class COMP3NativeTest {

    // =========================================================================
    // getByteLength
    // =========================================================================

    @ParameterizedTest(name = "getByteLength({0}) == {1}")
    @CsvSource({
        "1,  1",   // 1 digit + 1 sign = 2 nibbles -> 1 byte
        "2,  2",   // 2 digit + 1 sign = 3 nibbles -> 2 bytes
        "3,  2",   // 3 digit + 1 sign = 4 nibbles -> 2 bytes
        "4,  3",
        "5,  3",
        "6,  4",
        "7,  4",
        "8,  5",
        "9,  5",
        "18, 10",
        "19, 10",
    })
    void getByteLength_knownValues(int digitCount, int expected) {
        assertEquals(expected, COMP3.getByteLength(digitCount));
    }

    @Test
    void getByteLength_zero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> COMP3.getByteLength(0));
    }

    @Test
    void getByteLength_negative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> COMP3.getByteLength(-1));
    }

    // =========================================================================
    // encode / decode roundtrip
    // =========================================================================

    static Stream<Arguments> roundtripProvider() {
        return Stream.of(
            Arguments.of("S9(5)"   ,  12345),
            Arguments.of("S9(5)"   , -12345),
            Arguments.of("S9(5)"   ,      0),
            Arguments.of( "9(5)"   ,      0),
            Arguments.of( "9(5)"   ,  99999),
            Arguments.of("S9(5)V99",   123L),
            Arguments.of("S9(5)V99",  -123L),
            Arguments.of("S9(5)"   ,      1),
            Arguments.of("S9(5)"   ,     -1)
        );
    }

    @ParameterizedTest(name = "roundtrip: PIC {0} - {1}")
    @MethodSource("roundtripProvider")
    void encodeDecode_roundtrip(String symbols, Object value) {
        PictureMeta pic = buildPic(symbols);
        
        NumericMeta nMeta = NumericMeta.parse(value, pic);

        byte[] encoded = COMP3.encode(nMeta, pic);
        Object decoded = COMP3.decode(encoded, pic);

        assertEquals(0, toBigDecimal(decoded).compareTo(toBigDecimal(value)));
    }

    // =========================================================================
    // encode
    // =========================================================================

    @Test
    void encode_positive_12345() {
        // -12345 -> nibbles: 1|2|3|4|5|D -> bytes: 0x12 0x34 0x5D
        PictureMeta pic = buildPic("S9(5)");
        NumericMeta nMeta = NumericMeta.parse(12345L, pic);
        byte[] result = COMP3.encode(nMeta, pic);

        assertArrayEquals(new byte[]{ 0x12, 0x34, 0x5C }, result);
    }

    @Test
    void encode_negative_12345() {
        // -12345 -> nibbles: 1|2|3|4|5|D -> bytes: 0x12 0x34 0x5D
        PictureMeta pic = buildPic("S9(5)");
        NumericMeta nMeta = NumericMeta.parse(-12345L, pic);
        byte[] result = COMP3.encode(nMeta, pic);

        assertArrayEquals(new byte[]{ 0x12, 0x34, 0x5D }, result);
    }

    @Test
    void encode_zero_positive() {
        PictureMeta pic = buildPic("S9(5)");
        NumericMeta nMeta = NumericMeta.parse(0L, pic);
        byte[] result = COMP3.encode(nMeta, pic);

        assertArrayEquals(new byte[]{ 0x00, 0x00, 0x0C }, result);
    }

    @Test
    void encode_unsigned_usesUnsignedNibble() {
        PictureMeta pic = buildPic("9(5)");
        NumericMeta nMeta = NumericMeta.parse(12345L, pic);
        byte[] result = COMP3.encode(nMeta, pic);

        // sign nibble = 0xF
        assertArrayEquals(new byte[]{ 0x12, 0x34, 0x5F }, result);
    }

    @Test
    void encode_unsignedNegative_throwsIllegalStateException() {
        PictureMeta pic = buildPic("9(5)");
        NumericMeta nMeta = new NumericMeta("12345".getBytes(), 0, true);
        assertThrows(IllegalStateException.class, () -> COMP3.encode(nMeta, pic));
    }

    // =========================================================================
    // decode
    // =========================================================================

    @Test
    void decode_positive_12345() {
        // 0x12 0x34 0x5C -> +12345
        byte[] buffer = new byte[]{ 0x12, 0x34, 0x5C };
        PictureMeta pic = buildPic("S9(5)");
        Object result = COMP3.decode(buffer, pic);

        assertEquals(12345, result);
    }

    @Test
    void decode_negative_12345() {
        // 0x12 0x34 0x5D -> -12345
        byte[] buffer = new byte[]{ 0x12, 0x34, 0x5D };
        PictureMeta pic = buildPic("S9(5)");
        Object result = COMP3.decode(buffer, pic);

        assertEquals(-12345, result);
    }

    @Test
    void decode_unsigned_12345() {
        // 0x12 0x34 0x5F -> 12345 (unsigned)
        byte[] buffer = new byte[]{ 0x12, 0x34, 0x5F };
        PictureMeta pic = buildPic("9(5)");
        Object result = COMP3.decode(buffer, pic);

        assertEquals(12345, result);
    }

    @Test
    void decode_invalidSignNibble_throwsIllegalArgumentException() {
        // sign nibble = 0xA (invalid)
        byte[] buffer = new byte[]{ 0x12, 0x34, 0x5A };
        PictureMeta pic = buildPic("S9(5)");
        assertThrows(IllegalArgumentException.class, () -> COMP3.decode(buffer, pic));
    }

    @Test
    void decode_unsignedFieldWithNegativeSign_throwsArithmeticException() {
        // sign nibble = 0xD (negative) but pic is unsigned
        byte[] buffer = new byte[]{ 0x12, 0x34, 0x5D };
        PictureMeta pic = buildPic("9(5)");
        assertThrows(ArithmeticException.class, () -> COMP3.decode(buffer, pic));
    }

    @Test
    void decode_withDecimalDigits() {
        // 0x12 0x34 0x5C -> +12345, decimalDigits=2 -> 123.45
        byte[] buffer = new byte[]{ 0x12, 0x34, 0x5C };
        PictureMeta pic = buildPic("S9(3)V99");
        Object result = COMP3.decode(buffer, pic);

        assertEquals(0, ((BigDecimal) result).compareTo(new BigDecimal("123.45")));
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static PictureMeta buildPic(String symbols) {
        PictureMeta pic = PictureMeta.parse(symbols);

        pic.setSemantic(PicClauseSemantic.NONE);
        pic.setUsage(PicClauseUsage.PACKED_DECIMAL);
        
        return pic;
    }

    private static BigDecimal toBigDecimal(Object obj) {
        if (obj instanceof BigDecimal bd) return bd;
        if (obj instanceof Long l)        return BigDecimal.valueOf(l);
        if (obj instanceof Integer i)     return BigDecimal.valueOf(i);
        if (obj instanceof Short s)       return BigDecimal.valueOf(s);
        if (obj instanceof Byte b)        return BigDecimal.valueOf(b);
        throw new IllegalArgumentException("Unexpected type: " + obj.getClass());
    }
}
