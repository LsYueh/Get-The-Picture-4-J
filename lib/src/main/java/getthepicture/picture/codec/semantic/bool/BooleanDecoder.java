package getthepicture.picture.codec.semantic.bool;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;
import getthepicture.picture.core.meta.PictureMeta;

public class BooleanDecoder {

    public static boolean decode(byte[] buffer, PictureMeta pic) {
        byte raw = buffer[0];

        // 如果是數字型 PIC 9(1)，'0' = false, '1' = true
        if (pic.getBaseClass() == PicClauseBaseClass.NUMERIC) {
            if (raw == (byte) '0') return false;
            if (raw == (byte) '1') return true;
            throw new IllegalArgumentException(
                "Invalid numeric boolean value: " + (char) raw);
        }

        // 如果是字元型 PIC X(1)，通常 'Y'/'N'
        if (pic.getBaseClass() == PicClauseBaseClass.ALPHANUMERIC ||
            pic.getBaseClass() == PicClauseBaseClass.ALPHABETIC) {
            return switch (Character.toUpperCase((char) raw)) {
                case 'Y' -> true;
                case 'N' -> false;
                default  -> throw new IllegalArgumentException(
                    "Invalid alphanumeric boolean value: " + (char) raw);
            };
        }

        throw new UnsupportedOperationException(
            "Unsupported PIC type for Boolean: " + pic.getBaseClass());
    }
}
