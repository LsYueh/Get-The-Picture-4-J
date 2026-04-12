package getthepicture.picture;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PictureCodecTest {

    static Stream<Arguments> defaultRepresentationProvider() {
        return Stream.of(
            // DISPLAY - Alphabetic / Alphanumeric
            Arguments.of("A(5)", PicClauseUsage.DISPLAY,
                new byte[]{ ' ', ' ', ' ', ' ', ' ' }),
            Arguments.of("X(5)", PicClauseUsage.DISPLAY,
                new byte[]{ ' ', ' ', ' ', ' ', ' ' }),

            // DISPLAY - Numeric
            Arguments.of("9(5)", PicClauseUsage.DISPLAY,
                new byte[]{ '0', '0', '0', '0', '0' }),
            Arguments.of("S9(5)", PicClauseUsage.DISPLAY,
                new byte[]{ '0', '0', '0', '0', '{' }),

            // COMP-3
            Arguments.of("9(5)",  PicClauseUsage.PACKED_DECIMAL,
                new byte[]{ 0x00, 0x00, 0x0F }),
            Arguments.of("S9(5)", PicClauseUsage.PACKED_DECIMAL,
                new byte[]{ 0x00, 0x00, 0x0C }),
            Arguments.of("9(6)",  PicClauseUsage.PACKED_DECIMAL,
                new byte[]{ 0x00, 0x00, 0x00, 0x0F }),
            Arguments.of("S9(6)", PicClauseUsage.PACKED_DECIMAL,
                new byte[]{ 0x00, 0x00, 0x00, 0x0C }),

            // COMP-4
            Arguments.of("9(5)",  PicClauseUsage.BINARY,
                new byte[]{ 0x00, 0x00, 0x00, 0x00 }),
            Arguments.of("S9(5)", PicClauseUsage.BINARY,
                new byte[]{ 0x00, 0x00, 0x00, 0x00 }),

            // COMP-5
            Arguments.of("9(5)",  PicClauseUsage.NATIVE_BINARY,
                new byte[]{ 0x00, 0x00, 0x00, 0x00 }),
            Arguments.of("S9(5)", PicClauseUsage.NATIVE_BINARY,
                new byte[]{ 0x00, 0x00, 0x00, 0x00 }),

            // COMP-6
            Arguments.of("9(5)", PicClauseUsage.U_PACKED_DECIMAL,
                new byte[]{ 0x00, 0x00, 0x00 }),
            Arguments.of("9(6)", PicClauseUsage.U_PACKED_DECIMAL,
                new byte[]{ 0x00, 0x00, 0x00 })
        );
    }

    @ParameterizedTest(name = "createDefaultRepresentation: pic={0}, usage={1}")
    @MethodSource("defaultRepresentationProvider")
    void createDefaultRepresentation(String picString, PicClauseUsage usage, byte[] expected) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = PictureCodec.forMeta(pic)
            .usage(usage)
            .withStrict()
            .createDefaultRepresentation();
        assertArrayEquals(expected, buffer);
    }
}
