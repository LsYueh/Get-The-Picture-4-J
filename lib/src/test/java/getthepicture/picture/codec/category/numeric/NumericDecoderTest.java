package getthepicture.picture.codec.category.numeric;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import getthepicture.picture.PictureCodec;
import getthepicture.picture.core.meta.PictureMeta;

import static org.junit.jupiter.api.Assertions.*;

class NumericDecoderTest {

    // =========================================================================
    // Exceptions
    // =========================================================================

    @ParameterizedTest(name = "decode digits exceeding 28: pic={0}")
    @ValueSource(strings = { "S9(29)", "9(29)" })
    void decode_withDigitsExceeding28_shouldThrow(String picString) {
        // 總共 29 位數，超過最大精度 28
        PictureMeta pic = PictureMeta.parse(picString);
        assertThrows(ArithmeticException.class,
            () -> PictureCodec.forMeta(pic).decode(new byte[]{ '0' }));
    }
}
