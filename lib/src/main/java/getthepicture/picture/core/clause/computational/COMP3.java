package getthepicture.picture.core.clause.computational;

import java.math.BigDecimal;

import getthepicture.picture.core.CbDecimal;
import getthepicture.picture.core.clause.options.DataStorageOptions;
import getthepicture.picture.core.mapper.Mapper;
import getthepicture.picture.core.mapper.IntMapper;
import getthepicture.picture.core.meta.NumericMeta;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * COMP-3 (Packed-Decimal)
 */
public class COMP3 {

    private static final int POSITIVE_SIGN = 0x0C;
    private static final int NEGATIVE_SIGN = 0x0D;
    private static final int UNSIGNED      = 0x0F;

    // Packed-Decimal (COMP-3) Bit / Nibble Format
    //
    // Byte n-2           Byte n-1 (last)
    // +--------+--------+--------+--------+
    // |  Digit |  Digit |  Digit |  Sign  |
    // |  4bit  |  4bit  |  4bit  |  4bit  |
    // +--------+--------+--------+--------+
    //    High     Low      High      Low
    // +--------+--------+--------+--------+
    // |  (MSN) |        |  (LSN) |        |
    // +--------+--------+--------+--------+
    // |      (MSB)      |      (LSB)      |
    // +--------+--------+--------+--------+
    //
    // Example:  -12345
    //
    // Digits:  1   2   3   4   5   Sign
    // Nibbles: 1 | 2 | 3 | 4 | 5 |  D
    //
    // Bytes:
    // +--------+--------+--------+
    // |  0x12  |  0x34  |  0x5D  |
    // +--------+--------+--------+
    //
    // Bit layout (one byte):
    //   bit7 bit6 bit5 bit4 | bit3 bit2 bit1 bit0
    //   --------------------+--------------------
    //       High Nibble     |      Low Nibble
    //
    // Rules:
    // - Each digit occupies one nibble (0x0 – 0x9)
    // - Last nibble is the sign
    //     C = positive
    //     D = negative
    //     F = unsigned / positive (vendor dependent)
    // - Total bytes = ceil(nibbles / 2)
    //

    /**
     * Calculates the byte length required for a COMP-3 (Packed Decimal) field.
     * Two digits are stored per byte, and the final half-byte contains the sign.
     */
    public static int getByteLength(int digitCount) {
        if (digitCount <= 0)
            throw new IllegalArgumentException("digitCount must be greater than 0");

        int totalNibbles = digitCount + 1; // include sign

        return (totalNibbles + 1) / 2; // ceil(nibbles / 2)
    }

    private static final Mapper mapper = new IntMapper();

    public static Object decode(byte[] buffer, PictureMeta pic, DataStorageOptions ds) {
        // Decode BCD
        boolean[] negativeRef = new boolean[1];
        byte[] chars = decodePacked(buffer, pic.getDigitCount(), negativeRef);
        boolean isNegative = negativeRef[0];

        if (!pic.isSigned() && isNegative)
            throw new ArithmeticException("Unsigned field contains negative number");

        BigDecimal value = CbDecimal.decode(chars, pic.getDecimalDigits(), isNegative);

        if (pic.getDecimalDigits() > 0)
            return value;

        return mapper.map(value, pic);
    }

    public static Object decode(byte[] buffer, PictureMeta pic) {
        return decode(buffer, pic, DataStorageOptions.CI);
    }

    public static byte[] encode(NumericMeta nMeta, PictureMeta pic, DataStorageOptions ds) {
        if (!pic.isSigned() && nMeta.isNegative())
            throw new IllegalStateException("Unsigned PIC cannot encode negative value");

        int byteLen = getByteLength(pic.getDigitCount());
        byte[] buffer = new byte[byteLen];

        byte[] chars = nMeta.getChars();

        int charIndex = chars.length - 1;
        int byteIndex = byteLen - 1;

        // ---- 處理 sign byte ----

        int signNibble = !pic.isSigned()
            ? UNSIGNED
            : (nMeta.isNegative() ? NEGATIVE_SIGN : POSITIVE_SIGN);

        int low  = signNibble;
        int high = charIndex >= 0 ? chars[charIndex--] - '0' : 0;

        buffer[byteIndex--] = (byte) ((high << 4) | low);

        // ---- 剩餘 digit 每兩個一組 ----

        while (charIndex >= 0) {
            low  = chars[charIndex--] - '0';
            high = charIndex >= 0 ? chars[charIndex--] - '0' : 0;

            buffer[byteIndex--] = (byte) ((high << 4) | low);
        }

        return buffer;
    }

    public static byte[] encode(NumericMeta nMeta, PictureMeta pic) {
        return encode(nMeta, pic, DataStorageOptions.CI);
    }

    private static byte[] decodePacked(byte[] buffer, int digits, boolean[] negativeRef) {
        if (digits < 1)
            throw new IllegalArgumentException("Digits must be greater than 0.");

        byte[] bytes = new byte[digits];

        int outIndex  = digits - 1;
        int byteIndex = buffer.length - 1;

        // ---- 處理最後一個 byte（含 sign） ----

        byte lastByte = buffer[byteIndex--];

        int signNibble = lastByte & 0x0F;
        switch (signNibble) {
            case NEGATIVE_SIGN -> negativeRef[0] = true;
            case POSITIVE_SIGN, UNSIGNED -> negativeRef[0] = false;
            default -> throw new IllegalArgumentException(
                String.format("Invalid sign nibble: %X", signNibble));
        }

        // 先寫最後一個 digit（high nibble）
        bytes[outIndex--] = (byte) ('0' + ((lastByte >> 4) & 0x0F));

        int remaining = digits - 1; // 已寫 1 個 digit

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
