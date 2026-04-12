package getthepicture.picture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PictureCodecCOMP3Test {

    // =========================================================================
    // Encode
    // =========================================================================

    static Stream<Arguments> encodeProvider() {
        return Stream.of(
            Arguments.of("9(5)",   52194,  new byte[]{ 0x52, 0x19, 0x4F }),
            Arguments.of("S9(5)",  52194,  new byte[]{ 0x52, 0x19, 0x4C }),
            Arguments.of("S9(5)", -52194,  new byte[]{ 0x52, 0x19, 0x4D }),
            Arguments.of("9(18)",      1L, new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1F }),
            Arguments.of("S9(18)",     1L, new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1C })
        );
    }

    @ParameterizedTest(name = "encode: pic={0}, value={1}")
    @MethodSource("encodeProvider")
    void encode_combination(String picString, Object value, byte[] expected) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] result = PictureCodec.forMeta(pic)
            .usage(PicClauseUsage.PACKED_DECIMAL)
            .withStrict()
            .encode(value);
        assertArrayEquals(expected, result);
    }

    @Test
    void encode_withSignLesser() {
        // 高位截斷：-52194 塞入 S9(3)，保留後 3 位 194，sign=D -> 0x19 0x4D
        PictureMeta pic = PictureMeta.parse("S9(3)");
        pic.setUsage(PicClauseUsage.PACKED_DECIMAL);
        byte[] result = PictureCodec.forMeta(pic).encode(-52194);
        assertArrayEquals(new byte[]{ 0x19, 0x4D }, result);
    }

    // =========================================================================
    // Decode
    // =========================================================================

    static Stream<Arguments> decodeProvider() {
        return Stream.of(
            // 9(5) unsigned -> sign C or F 都視為正，digits<=9 -> int
            Arguments.of("9(5)",   52194,  new byte[]{ 0x52, 0x19, 0x4F }),
            Arguments.of("9(5)",   52194,  new byte[]{ 0x52, 0x19, 0x4C }),
            // 9(10) unsigned, digits<=18 -> long
            Arguments.of("9(10)",      1L, new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x1F }),
            // S9(05) signed, digits<=9 -> int
            Arguments.of("S9(05)",  52194, new byte[]{ 0x52, 0x19, 0x4C }),
            Arguments.of("S9(05)", -52194, new byte[]{ 0x52, 0x19, 0x4D }),
            // 9(18) / S9(18), digits<=18 -> long
            Arguments.of("9(18)",      1L, new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1F }),
            Arguments.of("S9(18)",     1L, new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1C })
        );
    }

    @ParameterizedTest(name = "decode: pic={0}, expected={1}")
    @MethodSource("decodeProvider")
    void decode_combination(String picString, Object expected, byte[] buffer) {
        PictureMeta pic = PictureMeta.parse(picString);
        Object result = PictureCodec.forMeta(pic)
            .usage(PicClauseUsage.PACKED_DECIMAL)
            .withStrict()
            .decode(buffer);
        assertEquals(expected, result);
    }

    @Test
    void decode_withSignLesser() {
        // S9(3) decode [0x52, 0x19, 0x4D]
        // digits=3，取後 3 nibbles: 1|9|4 + sign D -> -194
        // digits<=4 -> short
        PictureMeta pic = PictureMeta.parse("S9(3)");
        pic.setUsage(PicClauseUsage.PACKED_DECIMAL);
        Object result = PictureCodec.forMeta(pic).decode(new byte[]{ 0x52, 0x19, 0x4D });
        assertEquals((short) -194, result);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void decode_withoutSign_negative_throwsArithmeticException() {
        PictureMeta pic = PictureMeta.parse("9(5)");
        pic.setUsage(PicClauseUsage.PACKED_DECIMAL);
        assertThrows(ArithmeticException.class,
            () -> PictureCodec.forMeta(pic).decode(new byte[]{ 0x52, 0x19, 0x4D }));
    }
}
