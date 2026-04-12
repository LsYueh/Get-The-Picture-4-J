package getthepicture.picture.codec.category.alphanumeric;

import org.junit.jupiter.api.Test;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AlphanumericDecoderTest {

    // =========================================================================
    // Decode - happy path
    // =========================================================================

    @Test
    void decode_alphanumeric_trimsRightSpaces() {
        PictureMeta pic = PictureMeta.parse("X(5)");
        byte[] buffer = "ABC  ".getBytes(StandardCharsets.US_ASCII);
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals("ABC", result);
    }

    @Test
    void decode_alphanumeric_lesser_extra_trimsRightSpaces() {
        PictureMeta pic = PictureMeta.parse("X(6)");
        byte[] buffer = "ABC  ".getBytes(StandardCharsets.US_ASCII);
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals("ABC", result);
    }

    @Test
    void decode_alphanumeric_CP950_trimsRightSpaces() {
        PictureMeta pic = PictureMeta.parse("X(7)");
        byte[] buffer = "中文字 ".getBytes(EncodingFactory.getCP950());
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals("中文字", result);
    }

    @Test
    void decode_alphanumeric_CP950_lesser_trimsRightSpaces() {
        // X(5) = 5 bytes，"中文字 " 每個中文字佔 2 bytes = 6 bytes + 1 space = 7 bytes
        // 截取前 5 bytes = "中文" (4 bytes) + 半個"字" (1 byte) -> 最後一個字不完整
        // lenientDecoder 會以替換字元顯示
        PictureMeta pic = PictureMeta.parse("X(5)");
        byte[] buffer = "中文字 ".getBytes(EncodingFactory.getCP950());
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals("中文�", result);
    }

    // =========================================================================
    // Decode - invalid usage
    // =========================================================================

    @Test
    void decode_wrongUsage_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("X(7)");
        byte[] buffer = "中文字 ".getBytes(EncodingFactory.getCP950());
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).usage(PicClauseUsage.BINARY).decode(buffer));
    }
}
