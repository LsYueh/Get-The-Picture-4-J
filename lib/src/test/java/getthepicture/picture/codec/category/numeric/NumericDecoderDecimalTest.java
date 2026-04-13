package getthepicture.picture.codec.category.numeric;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.meta.PictureMeta;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class NumericDecoderDecimalTest {

    @ParameterizedTest(name = "decode decimal: text={0}, pic={1}, expected={3}")
    @CsvSource({
        "01234,                           9(3)V9(2),    12.34",
        "12345,                           9(3)V9(2),   123.45",   // Note: 原 "01234" -> 12.34，這邊用 "12345" -> 123.45
        "1234E,                          S9(3)V9(2),   123.45",
        "1234N,                          S9(3)V9(2),  -123.45",
        "9999999999999999999999999995,     9(27)V9,     999999999999999999999999999.5",
        "999999999999999999999999999E,    S9(27)V9,     999999999999999999999999999.5",
        "999999999999999999999999999N,    S9(27)V9,    -999999999999999999999999999.5",
    })
    void decode_default_decimal(String text, String picString, String expectedValue) {
        PictureMeta pic = PictureMeta.parse(picString);
        byte[] buffer = text.getBytes(StandardCharsets.US_ASCII);
        Object value = PictureCodec.forMeta(pic).decode(buffer);

        assertInstanceOf(BigDecimal.class, value);
        assertEquals(0, ((BigDecimal) value).compareTo(new BigDecimal(expectedValue)));
    }
}
