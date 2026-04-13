package getthepicture.picture.codec.semantic.time;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.EncodingFactory;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeEncoderTest {

    @ParameterizedTest(name = "encode time: pic={0}, semantic={1}, expected={2}")
    @CsvSource({
        "X(6), TIME6, 235959,    23, 59, 59,   0",
        "9(6), TIME6, 235959,    23, 59, 59,   0",
        "X(9), TIME9, 123045678, 12, 30, 45, 678",
        "9(9), TIME9, 123045678, 12, 30, 45, 678",
    })
    void encode_timeOnly(String picString, PicClauseSemantic semantic, String expected,
                         int hour, int minute, int second, int millisecond) {
        PictureMeta pic = PictureMeta.parse(picString);
        pic.setSemantic(semantic);
        LocalTime value = LocalTime.of(hour, minute, second, millisecond * 1_000_000);
        byte[] buffer = PictureCodec.forMeta(pic).encode(value);
        String result = new String(buffer, EncodingFactory.getCP950());
        assertEquals(expected, result);
    }
}
