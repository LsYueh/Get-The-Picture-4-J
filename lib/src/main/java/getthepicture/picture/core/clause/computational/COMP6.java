package getthepicture.picture.core.clause.computational;

import java.math.BigDecimal;

import getthepicture.picture.core.CbDecimal;
import getthepicture.picture.core.mapper.IntMapper;
import getthepicture.picture.core.meta.NumericMeta;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * COMP-6 (Unsigned Packed-Decimal)
 *
 * Unsigned Packed Decimal (COMP-6) Bit / Nibble Format
 *
 * Byte n-2           Byte n-1 (last)
 * +--------+--------+--------+--------+
 * |  Digit |  Digit |  Digit |  Digit |
 * |  4bit  |  4bit  |  4bit  |  4bit  |
 * +--------+--------+--------+--------+
 *    High     Low      High     Low
 *
 * Example:  12345
 *
 * Digits:  0   1   2   3   4   5
 * Nibbles: 0 | 1 | 2 | 3 | 4 | 5
 *
 * Bytes:
 * +--------+--------+--------+
 * |  0x01  |  0x23  |  0x45  |
 * +--------+--------+--------+
 *
 * Rules:
 * - Each digit occupies one nibble (0x0 – 0x9)
 * - No sign nibble (unlike COMP-3)
 * - Total bytes = ceil(digits / 2)
 */
public class COMP6 {

    private static final IntMapper INT_MAPPER = new IntMapper();

    public static int getByteLength(int digitCount) {
        if (digitCount <= 0)
            throw new IllegalArgumentException("digitCount must be greater than 0");
        return (digitCount + 1) / 2; // ceil(digits / 2)
    }

    public static Object decode(byte[] buffer, PictureMeta pic) {
        if (pic.getDecimalDigits() > 0)
            throw new UnsupportedOperationException("Decimal digits not supported in COMP-6");
        if (pic.isSigned())
            throw new UnsupportedOperationException("Signed value is not valid for COMP-6");

        // Decode BCD
        byte[] chars = decodeUPacked(buffer, pic.getDigitCount());
        BigDecimal value = CbDecimal.decode(chars, pic.getDecimalDigits(), false);
        return INT_MAPPER.map(value, pic);
    }

    public static byte[] encode(NumericMeta nMeta, PictureMeta pic) {
        if (pic.getDecimalDigits() > 0)
            throw new UnsupportedOperationException("Decimal digits not supported in COMP-6");
        if (pic.isSigned())
            throw new UnsupportedOperationException("Signed value is not valid for COMP-6");
        if (nMeta.isNegative())
            throw new IllegalArgumentException("Negative value is not valid for COMP-6");

        int byteLen = COMP3.getByteLength(pic.getDigitCount()); // 共用計算公式
        byte[] buffer = new byte[byteLen];
        byte[] digits = nMeta.getChars();

        int digitIndex = digits.length - 1;
        int byteIndex  = buffer.length - 1;

        while (byteIndex >= 0) {
            int low  = digitIndex >= 0 ? digits[digitIndex--] - '0' : 0;
            int high = digitIndex >= 0 ? digits[digitIndex--] - '0' : 0;
            buffer[byteIndex--] = (byte) ((high << 4) | low);
        }

        return buffer;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static byte[] decodeUPacked(byte[] buffer, int digits) {
        if (digits < 1)
            throw new IllegalArgumentException("Digits must be greater than 0.");

        byte[] bytes = new byte[digits];
        int outIndex  = digits - 1;
        int byteIndex = buffer.length - 1;
        int remaining = digits;

        while (remaining > 0) {
            byte b = buffer[byteIndex--];

            // low nibble
            bytes[outIndex--] = (byte) ('0' + (b & 0x0F));
            remaining--;

            if (remaining > 0) {
                // high nibble
                bytes[outIndex--] = (byte) ('0' + ((b >> 4) & 0x0F));
                remaining--;
            }
        }

        return bytes;
    }
}
