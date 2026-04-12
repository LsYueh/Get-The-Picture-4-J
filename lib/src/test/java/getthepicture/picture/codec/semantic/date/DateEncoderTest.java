package getthepicture.picture.codec.semantic.date;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateEncoderTest {

    // =========================================================================
    // Encode - happy path
    // =========================================================================

    @ParameterizedTest(name = "encode date: pic={0}, semantic={1}, expected={2}")
    @CsvSource({
        "X(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "9(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "X(7), MINGUO_DATE,     1130115, 2024, 1, 15",
        "9(7), MINGUO_DATE,     1130115, 2024, 1, 15",
    })
    void encode_date(String picString, PicClauseSemantic semantic, String expected,
                     int year, int month, int day) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(semantic);
        LocalDate value = LocalDate.of(year, month, day);
        byte[] buffer = PictureCodec.forMeta(pic).encode(value);
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "encode date asSemantic: pic={0}, semantic={1}, expected={2}")
    @CsvSource({
        "X(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "9(8), GREGORIAN_DATE, 20240115, 2024, 1, 15",
        "X(7), MINGUO_DATE,     1130115, 2024, 1, 15",
        "9(7), MINGUO_DATE,     1130115, 2024, 1, 15",
    })
    void encode_date_asSemantic(String picString, PicClauseSemantic semantic, String expected,
                                int year, int month, int day) {
        PictureMeta pic = PictureMeta.parse(picString);
        LocalDate value = LocalDate.of(year, month, day);
        byte[] buffer = PictureCodec.forMeta(pic).asSemantic(semantic).encode(value);
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals(expected, result);
    }
}
