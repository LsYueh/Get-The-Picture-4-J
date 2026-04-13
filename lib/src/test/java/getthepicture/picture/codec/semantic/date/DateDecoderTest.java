package getthepicture.picture.codec.semantic.date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateDecoderTest {

    // =========================================================================
    // Decode - happy path
    // =========================================================================

    @ParameterizedTest(name = "decode date: pic={0}, semantic={1}, text={2}")
    @CsvSource({
        "X(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "9(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "X(7), MINGUO_DATE,     1130115, 2024, 1, 15",
        "9(7), MINGUO_DATE,     1130115, 2024, 1, 15",
    })
    void decode_date(String picString, PicClauseSemantic semantic, String text,
                     int year, int month, int day) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(semantic);
        byte[] buffer = text.getBytes(StandardCharsets.US_ASCII);
        Object value = PictureCodec.forMeta(pic).decode(buffer);
        assertEquals(LocalDate.of(year, month, day), value);
    }

    @ParameterizedTest(name = "decode date asSemantic: pic={0}, semantic={1}, text={2}")
    @CsvSource({
        "X(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "9(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "X(7), MINGUO_DATE,     1130115, 2024, 1, 15",
        "9(7), MINGUO_DATE,     1130115, 2024, 1, 15",
    })
    void decode_date_asSemantic(String picString, PicClauseSemantic semantic, String text,
                                int year, int month, int day) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.getBytes(StandardCharsets.US_ASCII);
        Object result = PictureCodec.forMeta(pic).asSemantic(semantic).decode(buffer);
        assertEquals(LocalDate.of(year, month, day), result);
    }

    // =========================================================================
    // Exceptions
    // =========================================================================

    @Test
    void decode_invalidBaseType_throwsUnsupportedOperationException() {
        PictureMeta pic = PictureMeta.parse("S9(8)");
        pic.setSemantic(PicClauseSemantic.GREGORIAN_DATE);
        byte[] buffer = "20241301".getBytes(StandardCharsets.US_ASCII);
        assertThrows(UnsupportedOperationException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @Test
    void decode_invalid_gregorianDate_throwsIllegalArgumentException() {
        PictureMeta pic = PictureMeta.parse("X(8)");
        pic.setSemantic(PicClauseSemantic.GREGORIAN_DATE);
        byte[] buffer = "20241301".getBytes(StandardCharsets.US_ASCII);
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }

    @ParameterizedTest(name = "decode invalid minguo: pic={0}, text={1}")
    @CsvSource({
        "X(7), 11301CC",
        "9(7), 11301CC",
        "X(7), 113BBCC",
        "9(7), 113BBCC",
        "X(7), AAABBCC",
        "9(7), AAABBCC",
    })
    void decode_invalid_minguoDate_throwsIllegalArgumentException(String picString, String text) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(PicClauseSemantic.MINGUO_DATE);
        byte[] buffer = text.getBytes(StandardCharsets.US_ASCII);
        assertThrows(IllegalArgumentException.class,
            () -> PictureCodec.forMeta(pic).decode(buffer));
    }
}
