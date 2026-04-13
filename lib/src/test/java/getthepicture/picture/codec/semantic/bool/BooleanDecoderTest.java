package getthepicture.picture.codec.semantic.bool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class BooleanDecoderTest {

    // =========================================================================
    // Decode - happy path
    // =========================================================================

    @ParameterizedTest(name = "decode boolean: pic={0}, text={1}, expected={2}")
    @CsvSource({
        "X(1), Y, true",
        "X(1), N, false",
        "A(1), Y, true",
        "A(1), N, false",
        "9(1), 1, true",
        "9(1), 0, false",
        "X(1), y, true",   // 測大小寫
        "X(1), n, false",
    })
    void decode_alpha(String picString, String text, boolean expected) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        byte[] buffer = text.getBytes(StandardCharsets.US_ASCII);
        Object value = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals(expected, value);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void decode_shouldThrow_whenUsageNotDisplay() {
        PictureMeta pic = PictureMeta.parse("9(1)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        pic.setUsage(PicClauseUsage.BINARY);
        byte[] buffer = "1".getBytes(StandardCharsets.US_ASCII);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_shouldThrow_whenStorageOccupiedNot1() {
        PictureMeta pic = PictureMeta.parse("9(2)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        byte[] buffer = "12".getBytes(StandardCharsets.US_ASCII);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_shouldThrow_whenInvalidNumericValue() {
        PictureMeta pic = PictureMeta.parse("9(1)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        byte[] buffer = "2".getBytes(StandardCharsets.US_ASCII); // 不合法值
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_shouldThrow_whenInvalidAlphanumericValue() {
        PictureMeta pic = PictureMeta.parse("X(1)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        byte[] buffer = "Z".getBytes(StandardCharsets.US_ASCII); // 不合法值
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_shouldThrow_whenSigned() {
        PictureMeta pic = PictureMeta.parse("S9(1)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        byte[] buffer = "1".getBytes(StandardCharsets.US_ASCII);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }
}
