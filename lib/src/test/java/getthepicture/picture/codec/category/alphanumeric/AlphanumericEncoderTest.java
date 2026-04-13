package getthepicture.picture.codec.category.alphanumeric;

import org.junit.jupiter.api.Test;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import static org.junit.jupiter.api.Assertions.*;

class AlphanumericEncoderTest {

    // =========================================================================
    // Encode - happy path
    // =========================================================================

    @Test
    void encode_alphanumeric_paddsRightWithSpaces() {
        PictureMeta pic = PictureMeta.parse("X(5)");
        byte[] buffer = PictureCodec.forMeta(pic).encode("AbC");
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals("AbC  ", result);
    }

    @Test
    void encode_alphanumeric_extra_truncatesRight() {
        PictureMeta pic = PictureMeta.parse("X(5)");
        byte[] buffer = PictureCodec.forMeta(pic).encode("AbC  fGh");
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals("AbC  ", result);
    }

    @Test
    void encode_alphanumeric_CP950_paddsRightWithSpaces() {
        PictureMeta pic = PictureMeta.parse("X(7)");
        byte[] buffer = PictureCodec.forMeta(pic).encode("中文字");
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals("中文字 ", result);
    }

    @Test
    void encode_alphanumeric_CP950_lesser_truncatesRight() {
        // X(5) = 5 bytes，"中文字" 每個中文字佔 2 bytes = 6 bytes
        // 截取前 5 bytes = "中文" (4 bytes) + 半個"字" (1 byte) -> 最後一個字不完整
        // lenientDecoder 會以替換字元顯示
        PictureMeta pic = PictureMeta.parse("X(5)");
        byte[] buffer = PictureCodec.forMeta(pic).encode("中文字");
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals("中文�", result);
    }

    // =========================================================================
    // Encode - invalid usage
    // =========================================================================

    @Test
    void encode_wrongUsage_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("X(7)");
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).usage(PicClauseUsage.PACKED_DECIMAL).encode("中文字 "));
    }
}
