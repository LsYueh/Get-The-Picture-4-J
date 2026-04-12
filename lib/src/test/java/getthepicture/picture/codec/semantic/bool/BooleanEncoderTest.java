package getthepicture.picture.codec.semantic.bool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import static org.junit.jupiter.api.Assertions.*;

class BooleanEncoderTest {

    // =========================================================================
    // Encode - happy path
    // =========================================================================

    @ParameterizedTest(name = "encode boolean: pic={0}, expected={1}, value={2}")
    @CsvSource({
        "X(1), Y, true",
        "X(1), N, false",
        "A(1), Y, true",
        "A(1), N, false",
        "9(1), 1, true",
        "9(1), 0, false",
    })
    void encode_alpha(String picString, String expected, boolean value) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        byte[] buffer = PictureCodec.forMeta(pic).encode(value);
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals(expected, result);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void encode_shouldThrow_whenValueIsNotBool() {
        PictureMeta pic = PictureMeta.parse("X(1)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).encode("NotABool"));
    }

    @Test
    void encode_shouldThrow_whenUsageNotDisplay() {
        PictureMeta pic = PictureMeta.parse("X(1)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        pic.setUsage(PicClauseUsage.PACKED_DECIMAL);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).encode(true));
    }

    @Test
    void encode_shouldThrow_whenStorageOccupiedNot1() {
        PictureMeta pic = PictureMeta.parse("X(2)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).encode(true));
    }

    @Test
    void encode_shouldThrow_whenSigned() {
        PictureMeta pic = PictureMeta.parse("S9(1)");
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).encode(true));
    }

    @Test
    void encode_shouldThrow_whenUnsupportedPicType() {
        PictureMeta pic = PictureMeta.parse("9(2)"); // Numeric 但長度 >1
        pic.setSemantic(PicClauseSemantic.BOOLEAN);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).encode(true));
    }
}
