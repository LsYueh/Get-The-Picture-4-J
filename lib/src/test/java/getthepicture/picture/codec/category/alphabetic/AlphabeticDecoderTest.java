package getthepicture.picture.codec.category.alphabetic;

import org.junit.jupiter.api.Test;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AlphabeticDecoderTest {

    // =========================================================================
    // Decode - happy path
    // =========================================================================

    @Test
    void decode_alphabetic_trimsRightSpaces() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        byte[] buffer = "AbC  ".getBytes(StandardCharsets.US_ASCII);
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals("AbC", result);
    }

    @Test
    void decode_alphabetic_lesser_extra_trimsRightSpaces() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        byte[] buffer = "AbC  fGh".getBytes(StandardCharsets.US_ASCII);
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals("AbC", result);
    }

    // =========================================================================
    // Decode - invalid format
    // =========================================================================

    @Test
    void decode_alphanumeric_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        byte[] buffer = "AbC@ ".getBytes(StandardCharsets.US_ASCII);
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_numeric_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        byte[] buffer = "12345".getBytes(StandardCharsets.US_ASCII);
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_CP950_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("A(7)");
        byte[] buffer = "中文字 ".getBytes(EncodingFactory.getCP950());
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_wrongUsage_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("A(7)");
        byte[] buffer = "中文字 ".getBytes(EncodingFactory.getCP950());
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).usage(PicClauseUsage.BINARY).decode(buffer));
    }
}