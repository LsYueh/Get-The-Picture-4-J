package getthepicture.picture.codec;

import getthepicture.picture.core.meta.PictureMeta;

public class Initializer {

    public static byte[] initialize(PictureMeta pic, CodecOptions options) {
        if (pic == null)
            throw new IllegalArgumentException("pic must not be null");

        return switch (pic.getBaseClass()) {
            case NUMERIC      -> Encoder.encodeBaseType(0, pic, options);
            case ALPHANUMERIC,
                 ALPHABETIC   -> Encoder.encodeBaseType("", pic, options);
            default -> throw new UnsupportedOperationException(
                "Unsupported PIC Data Type [Encode] : " + pic.getBaseClass());
        };
    }
}
