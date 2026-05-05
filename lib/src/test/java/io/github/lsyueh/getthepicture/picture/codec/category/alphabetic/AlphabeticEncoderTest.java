package io.github.lsyueh.getthepicture.picture.codec.category.alphabetic;

import org.junit.jupiter.api.Test;

import io.github.lsyueh.getthepicture.picture.PictureCodec;
import io.github.lsyueh.getthepicture.picture.core.clause.items.PicClauseUsage;
import io.github.lsyueh.getthepicture.picture.core.meta.PictureMeta;
import io.github.lsyueh.getthepicture.picture.utils.EncodingFactory;

import static org.junit.jupiter.api.Assertions.*;

class AlphabeticEncoderTest {

    // =========================================================================
    // Encode - happy path
    // =========================================================================

    @Test
    void encode_alphabetic_paddsRightWithSpaces() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        byte[] buffer = PictureCodec.forMeta(pic).encode("AbC");
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals("AbC  ", result);
    }

    @Test
    void encode_alphabetic_extra_truncatesRight() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        byte[] buffer = PictureCodec.forMeta(pic).encode("AbC  fGh");
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals("AbC  ", result);
    }

    // =========================================================================
    // Encode - invalid format
    // =========================================================================

    @Test
    void encode_alphanumeric_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).encode("AbC@ "));
    }

    @Test
    void encode_numeric_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("A(5)");
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).encode("12345"));
    }

    @Test
    void encode_CP950_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("A(7)");
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).encode("中文字 "));
    }

    @Test
    void encode_wrongUsage_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("A(7)");
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).usage(PicClauseUsage.PACKED_DECIMAL).encode("中文字 "));
    }
}
