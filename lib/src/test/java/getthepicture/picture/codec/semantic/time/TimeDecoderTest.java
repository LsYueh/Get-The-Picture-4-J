package getthepicture.picture.codec.semantic.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeDecoderTest {

    // =========================================================================
    // Decode - happy path
    // =========================================================================

    @ParameterizedTest(name = "decode time: pic={0}, semantic={1}, text={2}")
    @CsvSource({
        "X(6), TIME6, 235959,    23, 59, 59,   0",
        "9(6), TIME6, 235959,    23, 59, 59,   0",
        "X(9), TIME9, 123045678, 12, 30, 45, 678",
        "9(9), TIME9, 123045678, 12, 30, 45, 678",
    })
    void decode_timeOnly(String picString, PicClauseSemantic semantic, String text,
                         int hour, int minute, int second, int millisecond) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(semantic);
        byte[] buffer = text.getBytes(StandardCharsets.US_ASCII);
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals(LocalTime.of(hour, minute, second, millisecond * 1_000_000), result);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void decode_signedNumeric_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("S9(6)");
        pic.setSemantic(PicClauseSemantic.TIME6);
        byte[] buffer = "123456".getBytes(StandardCharsets.US_ASCII);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_invalidTime6_throwsUnsupportedOperationException() {
        // C# 原測試預期 NotSupportedException（因為 S9(6) 違反 constraint）
        // 實際上在 constraint 驗證階段就拋出，不會到 parseTime6
        PictureMeta pic = PictureMeta.parse("S9(6)");
        pic.setSemantic(PicClauseSemantic.TIME6);
        byte[] buffer = "246060".getBytes(StandardCharsets.US_ASCII); // invalid time
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }
}
