package getthepicture.picture.core.clause.overpunch;

import getthepicture.picture.codec.CodecOptions;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * Overpunch Codec
 */
public class OverpunchCodec {

    /**
     * PIC 9/S9 → 符號(sign)與數字文(numeric)
     *
     * @param bytes      input bytes (modified in place)
     * @param pic
     * @param options
     * @param negativeRef out parameter: negativeRef[0] = isNegative
     * @return decoded digit bytes
     */
    public static byte[] decode(byte[] bytes, PictureMeta pic, CodecOptions options, boolean[] negativeRef) {
        negativeRef[0] = false;

        if (pic.isSigned()) {
            int index = signIndex(bytes.length, options);

            byte key = (byte) (bytes[index] & 0x7F); // ASCII overpunch
            String opVal = OverpunchCodex.getValue(key, options.getDataStorage());
            char sign = opVal.charAt(0);

            negativeRef[0] = switch (sign) {
                case '+' -> false;
                case '-' -> true;
                default  -> throw new IllegalArgumentException("Invalid overpunch sign: '" + sign + "'");
            };

            bytes[index] = (byte) opVal.charAt(1);
        }

        ensureAllAsciiDigits(bytes);
        return bytes; // 數字文 (char[])
    }

    /**
     * 符號(sign)與數字文(numeric) → PIC 9/S9
     *
     * @param isNegative
     * @param numeric    數字文 (char[])
     * @param pic
     * @param options
     * @return encoded bytes
     */
    public static byte[] encode(boolean isNegative, byte[] numeric, PictureMeta pic, CodecOptions options) {
        ensureAllAsciiDigits(numeric);

        if (pic.isSigned()) {
            int index = signIndex(numeric.length, options);

            char digit = (char) (numeric[index] & 0x7F); // ASCII overpunch
            String opValue = OverpunchTable.opVal(isNegative, digit);
            byte value = OverpunchCodex.getKey(opValue, options.getDataStorage());
            numeric[index] = value;
        }

        return numeric;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static int signIndex(int length, CodecOptions options) {
        return switch (options.getSign()) {
            case IS_TRAILING -> length - 1;
            case IS_LEADING  -> 0;
            default -> throw new IllegalArgumentException(
                "Unsupported Sign option: " + options.getSign());
        };
    }

    private static void ensureAllAsciiDigits(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (Integer.compareUnsigned(bytes[i] - (byte) '0', 9) > 0)
                throw new IllegalArgumentException(
                    "Invalid digit at index " + (i + 1)); // Note: 轉成 1-based
        }
    }
}
