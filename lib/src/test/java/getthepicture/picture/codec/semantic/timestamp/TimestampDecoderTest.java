package getthepicture.picture.codec.semantic.timestamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimestampDecoderTest {

    // =========================================================================
    // Decode - happy path
    // =========================================================================

    @ParameterizedTest(name = "decode timestamp: pic={0}, semantic={1}, text={2}")
    @CsvSource({
        "X(14), TIMESTAMP14, 20240115123045, 2024,  1, 15, 12, 30, 45",
        "9(14), TIMESTAMP14, 20240115123045, 2024,  1, 15, 12, 30, 45",
        "X(14), TIMESTAMP14, 19991231235959, 1999, 12, 31, 23, 59, 59",
        "9(14), TIMESTAMP14, 19991231235959, 1999, 12, 31, 23, 59, 59",
    })
    void decode_timestamp14(String picString, PicClauseSemantic semantic, String text,
                            int year, int month, int day,
                            int hour, int minute, int second) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(semantic);
        byte[] buffer = text.getBytes(StandardCharsets.US_ASCII);
        Object result = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals(LocalDateTime.of(year, month, day, hour, minute, second), result);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void decode_signedNumeric_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("S9(14)");
        pic.setSemantic(PicClauseSemantic.TIMESTAMP14);
        byte[] buffer = "20240115123045".getBytes(StandardCharsets.US_ASCII);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_invalidTimestamp14_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("9(14)");
        pic.setSemantic(PicClauseSemantic.TIMESTAMP14);
        byte[] buffer = "20241301120000".getBytes(StandardCharsets.US_ASCII); // invalid month
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }
}
