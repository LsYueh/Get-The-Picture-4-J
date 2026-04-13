package getthepicture.picture.codec.semantic.bool;

import getthepicture.picture.core.meta.PictureMeta;

public class BooleanEncoder {

    public static byte[] encode(Object value, PictureMeta pic) {
        if (!(value instanceof Boolean b))
            throw new IllegalArgumentException(
                "Value must be of type Boolean, got " +
                (value != null ? value.getClass().getSimpleName() : "null"));

        byte encoded = switch (pic.getBaseClass()) {
            // 0/1
            case NUMERIC -> b ? (byte) '1' : (byte) '0';
            // Y/N
            case ALPHANUMERIC,
                 ALPHABETIC -> b ? (byte) 'Y' : (byte) 'N';
            default -> throw new UnsupportedOperationException(
                "Unsupported PIC type for Boolean: " + pic.getBaseClass());
        };

        return new byte[]{ encoded };
    }
}
