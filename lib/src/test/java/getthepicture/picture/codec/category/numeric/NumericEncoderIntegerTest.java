package getthepicture.picture.codec.category.numeric;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NumericEncoderIntegerTest {

    // =========================================================================
    // Encode Integer - default
    // =========================================================================

    static Stream<Arguments> encodeIntegerDefaultProvider() {
        return Stream.of(
            Arguments.of((short)                 99, "9(02)",                 "99"),
            Arguments.of((int)                 9999, "9(04)",               "9999"),
            Arguments.of((long)           999999999, "9(09)",          "999999999"),
            Arguments.of(                        1L, "9(18)", "000000000000000001"),
            Arguments.of(999999999999999999L,        "9(18)", "999999999999999999")
        );
    }

    @ParameterizedTest(name = "encode integer default: value={0}, pic={1}")
    @MethodSource("encodeIntegerDefaultProvider")
    void encode_integer_default(Object value, String picString, String expected) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = PictureCodec.forMeta(pic).encode(value);
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals(expected, result);
    }

    // =========================================================================
    // Encode Integer - 28 digits (BigDecimal)
    // =========================================================================

    @ParameterizedTest(name = "encode integer 28: value={0}, pic={1}")
    @CsvSource({
        "9,                            9(28), 0000000000000000000000000009",
        "999999999999999999999999999,  9(27),  999999999999999999999999999",
        "999999999999999999999999999,  9(28), 0999999999999999999999999999",
        "9999999999999999999999999999, 9(28), 9999999999999999999999999999",
    })
    void encode_integer_28(String value, String picString, String expected) {
        BigDecimal v = new BigDecimal(value.trim());
        PictureMeta pic = PictureMeta.parse(picString.trim());
        byte[] buffer = PictureCodec.forMeta(pic).encode(v);
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals(expected.trim(), result);
    }
}
