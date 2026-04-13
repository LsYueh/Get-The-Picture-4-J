package getthepicture.picture.core.clause.computational;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import getthepicture.picture.core.meta.NumericMeta;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * COMP-5 (Native Binary)
 * - Uses platform-native endian.
 * - Little Endian on x86/x64, Big Endian on mainframe.
 * - Cross-platform use: handle endian conversion as needed.
 * - For consistency with COMP-4/COMP-3, codec may optionally normalize to Big Endian.
 */
public class COMP5 {

    public static Object decode(byte[] buffer, PictureMeta pic, boolean isBigEndian) {
        if (pic.getDecimalDigits() > 0)
            throw new UnsupportedOperationException(
                "COMP-5 does not support decimal digits. PIC has " +
                pic.getDecimalDigits() + " decimal digits.");

        int length = getByteLength(pic.getDigitCount());

        if (buffer.length < length)
            throw new IllegalArgumentException("Buffer too short");

        byte[] bytes = new byte[length];
        System.arraycopy(buffer, 0, bytes, 0, length);

        ByteBuffer bb = ByteBuffer.wrap(bytes)
            .order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

        return switch (length) {
            // Binary halfword (2 bytes)
            case 2 -> bb.getShort();
            // Binary fullword (4 bytes)
            case 4 -> bb.getInt();
            // Binary doubleword (8 bytes)
            case 8 -> bb.getLong();
            default -> throw new UnsupportedOperationException("Unsupported COMP length");
        };
    }

    public static Object decode(byte[] buffer, PictureMeta pic) {
        return decode(buffer, pic, true);
    }

    public static byte[] encode(NumericMeta nMeta, PictureMeta pic, boolean isBigEndian) {
        if (pic.getDecimalDigits() > 0)
            throw new UnsupportedOperationException(
                "COMP-5 does not support decimal digits.");

        int length = getByteLength(pic.getDigitCount());
        byte[] bytes = new byte[length];

        ByteBuffer bb = ByteBuffer.wrap(bytes)
            .order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

        if (pic.isSigned()) {
            long value = nMeta.toInt64();
            writeSigned(bb, value, length);
        } else {
            long value = nMeta.toUInt64(); // 以無號語意解讀
            writeUnsigned(bb, value, length);
        }

        return bytes;
    }

    public static byte[] encode(NumericMeta nMeta, PictureMeta pic) {
        return encode(nMeta, pic, true);
    }

    public static int getByteLength(int digitCount) {
        if (digitCount <=  4) return 2;
        if (digitCount <=  9) return 4;
        if (digitCount <= 18) return 8;
        throw new UnsupportedOperationException(
            "Too many digits for COMP-4 (Binary) or COMP-5 (Native-Binary)");
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static void writeSigned(ByteBuffer bb, long value, int length) {
        switch (length) {
            case 2 -> {
                if (value < Short.MIN_VALUE || value > Short.MAX_VALUE)
                    throw new ArithmeticException("Value exceeds 2-byte signed range.");
                bb.putShort((short) value);
            }
            case 4 -> {
                if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)
                    throw new ArithmeticException("Value exceeds 4-byte signed range.");
                bb.putInt((int) value);
            }
            case 8 -> bb.putLong(value);
            default -> throw new UnsupportedOperationException("Unsupported COMP-5 length");
        }
    }

    private static void writeUnsigned(ByteBuffer bb, long value, int length) {
        // value 以無號語意解讀（來自 toUInt64）
        switch (length) {
            case 2 -> {
                if (Long.compareUnsigned(value, 0xFFFFL) > 0)
                    throw new ArithmeticException("Value exceeds 2-byte unsigned range.");
                bb.putShort((short) value);
            }
            case 4 -> {
                if (Long.compareUnsigned(value, 0xFFFFFFFFL) > 0)
                    throw new ArithmeticException("Value exceeds 4-byte unsigned range.");
                bb.putInt((int) value);
            }
            case 8 -> bb.putLong(value);
            default -> throw new UnsupportedOperationException("Unsupported COMP-5 length");
        }
    }
}
