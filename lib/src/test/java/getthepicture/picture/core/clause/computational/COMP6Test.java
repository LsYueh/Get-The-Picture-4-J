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

class COMP6Test {

    // =========================================================================
    // Encode
    // =========================================================================

    static Stream<Arguments> encodeProvider() {
        return Stream.of(
            Arguments.of("9(1)", (short)           5, new byte[]{ 0x05 }),
            Arguments.of("9(2)", (short)          12, new byte[]{ 0x12 }),
            Arguments.of("9(3)", (int)           123, new byte[]{ 0x01, 0x23 }),
            Arguments.of("9(4)", (int)          1234, new byte[]{ 0x12, 0x34 }),
            Arguments.of("9(5)", (long)        52194, new byte[]{ 0x05, 0x21, (byte)0x94 }),
            Arguments.of("9(6)", (long)       987654, new byte[]{ (byte)0x98, 0x76, 0x54 }),
            Arguments.of("9(7)", (long)      1234567, new byte[]{ 0x01, 0x23, 0x45, 0x67 }),
            Arguments.of("9(8)", (long)     87654321, new byte[]{ (byte)0x87, 0x65, 0x43, 0x21 }),
            Arguments.of("9(9)", (long)    123456789, new byte[]{ 0x01, 0x23, 0x45, 0x67, (byte)0x89 })
        );
    }

    @ParameterizedTest(name = "encode COMP6: pic={0}, value={1}")
    @MethodSource("encodeProvider")
    void encode_combination(String picString, Object value, byte[] expected) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] result = PictureCodec.forMeta(pic)
            .usage(PicClauseUsage.U_PACKED_DECIMAL)
            .withStrict()
            .encode(value);
        assertArrayEquals(expected, result);
    }

    @Test
    void encode_withSignLesser() {
        // 高位截斷：52194 塞入 9(3)，保留後 3 位 194 -> 0x01 0x94
        PictureMeta pic = PictureMeta.parse("9(3)");
        pic.setUsage(PicClauseUsage.U_PACKED_DECIMAL);
        byte[] result = PictureCodec.forMeta(pic).encode(52194);
        assertArrayEquals(new byte[]{ 0x01, (byte)0x94 }, result);
    }

    // =========================================================================
    // Decode
    // =========================================================================

    static Stream<Arguments> decodeProvider() {
        return Stream.of(
            Arguments.of("9(1)", (byte)            5, new byte[]{ 0x05 }),
            Arguments.of("9(2)", (byte)           12, new byte[]{ 0x12 }),
            Arguments.of("9(3)", (short)         123, new byte[]{ 0x01, 0x23 }),
            Arguments.of("9(4)", (short)        1234, new byte[]{ 0x12, 0x34 }),
            Arguments.of("9(5)", (int)         52194, new byte[]{ 0x05, 0x21, (byte)0x94 }),
            Arguments.of("9(6)", (int)        987654, new byte[]{ (byte)0x98, 0x76, 0x54 }),
            Arguments.of("9(7)", (int)       1234567, new byte[]{ 0x01, 0x23, 0x45, 0x67 }),
            Arguments.of("9(8)", (int)      87654321, new byte[]{ (byte)0x87, 0x65, 0x43, 0x21 }),
            Arguments.of("9(9)", (int)     123456789, new byte[]{ 0x01, 0x23, 0x45, 0x67, (byte)0x89 })
        );
    }

    @ParameterizedTest(name = "decode COMP6: pic={0}, expected={1}")
    @MethodSource("decodeProvider")
    void decode_combination(String picString, Object expected, byte[] buffer) {
        PictureMeta pic = PictureMeta.parse(picString);
        Object result = PictureCodec.forMeta(pic)
            .usage(PicClauseUsage.U_PACKED_DECIMAL)
            .withStrict()
            .decode(buffer);
        assertEquals(expected, result);
    }

    @Test
    void decode_withSignLesser() {
        // 9(3) decode [0x05, 0x21, 0x94] -> digits=3，取後 3 nibbles: 1|9|4 -> 194
        // digits<=4 -> int (unsigned short 對應 Java int)
        PictureMeta pic = PictureMeta.parse("9(3)");
        pic.setUsage(PicClauseUsage.U_PACKED_DECIMAL);
        Object result = PictureCodec.forMeta(pic).decode(new byte[]{ 0x05, 0x21, (byte)0x94 });
        assertEquals((short) 194, result);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void decode_withSignNegative_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("S9(5)");
        pic.setUsage(PicClauseUsage.U_PACKED_DECIMAL);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(new byte[]{ 0x05, 0x21, (byte)0x94 }));
    }
}
