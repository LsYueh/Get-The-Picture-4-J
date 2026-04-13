package getthepicture.picture.codec.semantic.timestamp;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimestampEncoderTest {

    @ParameterizedTest(name = "encode timestamp: pic={0}, semantic={1}, expected={2}")
    @CsvSource({
        "X(14), TIMESTAMP14, 20240115123045, 2024,  1, 15, 12, 30, 45",
        "9(14), TIMESTAMP14, 20240115123045, 2024,  1, 15, 12, 30, 45",
        "X(14), TIMESTAMP14, 19991231235959, 1999, 12, 31, 23, 59, 59",
        "9(14), TIMESTAMP14, 19991231235959, 1999, 12, 31, 23, 59, 59",
    })
    void encode_timestamp14(String picString, PicClauseSemantic semantic, String expected,
                            int year, int month, int day,
                            int hour, int minute, int second) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(semantic);
        LocalDateTime value = LocalDateTime.of(year, month, day, hour, minute, second);
        byte[] buffer = PictureCodec.forMeta(pic).encode(value);
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals(expected, result);
    }
}
