package getthepicture.picture.core.clause.computational;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class COMP5Test {

    // =========================================================================
    // Encode
    // =========================================================================

    static Stream<Arguments> encodeProvider() {
        return Stream.of(
            // 9999 >> 0x270F >> (Little Endian) >> 0F 27
            Arguments.of("9(04)",  (short)  9999, new byte[]{ 0x0F, 0x27 }),
            Arguments.of("S9(04)", (short)  9999, new byte[]{ 0x0F, 0x27 }),
            Arguments.of("S9(04)", (short) -9999, new byte[]{ (byte)0xF1, (byte)0xD8 }),
            Arguments.of("9(09)",  999999999,      new byte[]{ (byte)0xFF, (byte)0xC9, (byte)0x9A, 0x3B }),
            Arguments.of("S9(09)", 999999999,      new byte[]{ (byte)0xFF, (byte)0xC9, (byte)0x9A, 0x3B }),
            Arguments.of("S9(09)",-999999999,      new byte[]{ 0x01, 0x36, 0x65, (byte)0xC4 }),
            Arguments.of("9(18)",  999999999999999999L, new byte[]{ (byte)0xFF, (byte)0xFF, 0x63, (byte)0xA7, (byte)0xB3, (byte)0xB6, (byte)0xE0, 0x0D }),
            Arguments.of("S9(18)", 999999999999999999L, new byte[]{ (byte)0xFF, (byte)0xFF, 0x63, (byte)0xA7, (byte)0xB3, (byte)0xB6, (byte)0xE0, 0x0D }),
            Arguments.of("S9(18)",-999999999999999999L, new byte[]{ 0x01, 0x00, (byte)0x9C, 0x58, 0x4C, 0x49, 0x1F, (byte)0xF2 })
        );
    }

    @ParameterizedTest(name = "encode COMP5: pic={0}, value={1}")
    @MethodSource("encodeProvider")
    void encode_combination(String picString, Object value, byte[] expected) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setUsage(PicClauseUsage.NATIVE_BINARY);
        byte[] result = PictureCodec.forMeta(pic).withStrict().isLittleEndian().encode(value);
        assertArrayEquals(expected, result);
    }

    @Test
    void encode_S9_04_bigEndian() {
        PictureMeta pic = PictureMeta.parse("S9(04)");
        pic.setUsage(PicClauseUsage.NATIVE_BINARY);
        byte[] result = PictureCodec.forMeta(pic).withStrict().encode((short) 9999);
        assertArrayEquals(new byte[]{ 0x27, 0x0F }, result);
    }

    @Test
    void encode_S9_18_bigEndian_negative() {
        PictureMeta pic = PictureMeta.parse("S9(18)");
        pic.setUsage(PicClauseUsage.NATIVE_BINARY);
        byte[] result = PictureCodec.forMeta(pic).withStrict().encode(-999999999999999999L);
        assertArrayEquals(new byte[]{ (byte)0xF2, 0x1F, 0x49, 0x4C, 0x58, (byte)0x9C, 0x00, 0x01 }, result);
    }

    // =========================================================================
    // Decode
    // =========================================================================

    static Stream<Arguments> decodeProvider() {
        return Stream.of(
            Arguments.of("9(04)",  (short)  9999, new byte[]{ 0x0F, 0x27 }),
            Arguments.of("S9(04)", (short)  9999, new byte[]{ 0x0F, 0x27 }),
            Arguments.of("S9(04)", (short) -9999, new byte[]{ (byte)0xF1, (byte)0xD8 }),
            Arguments.of("9(09)",  999999999,      new byte[]{ (byte)0xFF, (byte)0xC9, (byte)0x9A, 0x3B }),
            Arguments.of("S9(09)", 999999999,      new byte[]{ (byte)0xFF, (byte)0xC9, (byte)0x9A, 0x3B }),
            Arguments.of("S9(09)",-999999999,      new byte[]{ 0x01, 0x36, 0x65, (byte)0xC4 }),
            Arguments.of("9(18)",  999999999999999999L, new byte[]{ (byte)0xFF, (byte)0xFF, 0x63, (byte)0xA7, (byte)0xB3, (byte)0xB6, (byte)0xE0, 0x0D }),
            Arguments.of("S9(18)", 999999999999999999L, new byte[]{ (byte)0xFF, (byte)0xFF, 0x63, (byte)0xA7, (byte)0xB3, (byte)0xB6, (byte)0xE0, 0x0D }),
            Arguments.of("S9(18)",-999999999999999999L, new byte[]{ 0x01, 0x00, (byte)0x9C, 0x58, 0x4C, 0x49, 0x1F, (byte)0xF2 })
        );
    }

    @ParameterizedTest(name = "decode COMP5: pic={0}, expected={1}")
    @MethodSource("decodeProvider")
    void decode_combination(String picString, Object expected, byte[] buffer) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setUsage(PicClauseUsage.NATIVE_BINARY);
        Object result = PictureCodec.forMeta(pic).withStrict().isLittleEndian().decode(buffer);
        assertEquals(expected, result);
    }

    @Test
    void decode_S9_04_bigEndian() {
        PictureMeta pic = PictureMeta.parse("S9(04)");
        pic.setUsage(PicClauseUsage.NATIVE_BINARY);
        Object result = PictureCodec.forMeta(pic).withStrict().decode(new byte[]{ 0x27, 0x0F });
        assertEquals((short) 9999, result);
    }

    @Test
    void decode_S9_18_bigEndian_negative() {
        PictureMeta pic = PictureMeta.parse("S9(18)");
        pic.setUsage(PicClauseUsage.NATIVE_BINARY);
        Object result = PictureCodec.forMeta(pic).withStrict().decode(
            new byte[]{ (byte)0xF2, 0x1F, 0x49, 0x4C, 0x58, (byte)0x9C, 0x00, 0x01 });
        assertEquals(-999999999999999999L, result);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void encode_withoutSign_negative_throwsArithmeticException() {
        PictureMeta pic = PictureMeta.parse("9(4)");
        pic.setUsage(PicClauseUsage.BINARY);
        assertThrows(ArithmeticException.class,
            () -> PictureCodec.forMeta(pic).withStrict().encode(-9999));
    }

    @Test
    void encode_9_28_notSupported() {
        PictureMeta pic = PictureMeta.parse("9(28)");
        pic.setUsage(PicClauseUsage.BINARY);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).withStrict().encode(9999));
    }

    @Test
    void encode_S9_28_positive_notSupported() {
        PictureMeta pic = PictureMeta.parse("S9(28)");
        pic.setUsage(PicClauseUsage.BINARY);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).withStrict().encode(9999));
    }

    @Test
    void encode_S9_28_negative_notSupported() {
        PictureMeta pic = PictureMeta.parse("S9(28)");
        pic.setUsage(PicClauseUsage.BINARY);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).withStrict().encode(-9999));
    }
}
