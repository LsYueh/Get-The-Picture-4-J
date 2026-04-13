package getthepicture.picture.core.meta;

import java.math.BigDecimal;
import java.math.BigInteger;

import getthepicture.picture.core.CbDecimal;

public class NumericMeta {

    /** 純數字，不含符號 */
    private final byte[] chars;
    private final int decimalDigits;
    private final boolean negative;
    /** 原始數值，方便計算 */
    private final BigDecimal value;

    public NumericMeta(byte[] chars, int decimalDigits, boolean negative) {
        this.chars         = chars;
        this.decimalDigits = decimalDigits;
        this.negative      = negative;
        this.value         = CbDecimal.decode(chars, decimalDigits, negative);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public byte[]     getChars()         { return chars; }
    public int        getDecimalDigits() { return decimalDigits; }
    public boolean    isNegative()       { return negative; }
    public BigDecimal getValue()         { return value; }

    // -------------------------------------------------------------------------
    // Conversions
    // -------------------------------------------------------------------------

    public long toInt64() {
        if (decimalDigits != 0)
            throw new IllegalStateException("Cannot convert to Int64 when decimal digits exist.");

        if (chars.length == 0)
            throw new IllegalArgumentException("Empty numeric value.");

        BigInteger unsigned;
        try {
            unsigned = new BigInteger(new String(chars));
        } catch (NumberFormatException e) {
            throw new ArithmeticException("Overflow parsing numeric chars.");
        }

        if (negative) {
            // Long.MIN_VALUE = -9223372036854775808
            BigInteger minAbs = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
            if (unsigned.equals(minAbs))
                return Long.MIN_VALUE;
            if (unsigned.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0)
                throw new ArithmeticException("Overflow: value exceeds Int64 range.");
            return -unsigned.longValueExact();
        } else {
            if (unsigned.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0)
                throw new ArithmeticException("Overflow: value exceeds Int64 range.");
            return unsigned.longValueExact();
        }
    }

    public long toUInt64() {
        if (decimalDigits != 0)
            throw new IllegalStateException("Cannot convert to UInt64 when decimal digits exist.");

        if (chars.length == 0)
            throw new IllegalArgumentException("Empty numeric value.");

        if (negative)
            throw new ArithmeticException("Negative value cannot convert to UInt64.");

        BigInteger unsigned;
        try {
            unsigned = new BigInteger(new String(chars));
        } catch (NumberFormatException e) {
            throw new ArithmeticException("Value exceeds UInt64 range.");
        }

        // Java 沒有 ulong，用 BigInteger 檢查上界 2^64 - 1
        BigInteger ULONG_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
        if (unsigned.compareTo(ULONG_MAX) > 0)
            throw new ArithmeticException("Value exceeds UInt64 range.");

        return unsigned.longValue(); // caller 應以 unsigned 語意解讀
    }

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    public static NumericMeta parse(Object value, PictureMeta pic) {
        if (!isNumericType(value))
            throw new UnsupportedOperationException(
                "Type " + value.getClass().getSimpleName() + " is not supported.");

        byte[] digits = new byte[pic.getDigitCount()];
        boolean isNegative;

        // 特定格式分開處裡
        if (pic.getDecimalDigits() == 0 && pic.getDigitCount() <= 18) {
            isNegative = encodeInt64(value, digits);
        } else {
            isNegative = encodeDecimal(value, pic, digits);
        }

        return new NumericMeta(digits, pic.getDecimalDigits(), isNegative);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static boolean encodeInt64(Object value, byte[] buffer) {
        long v = toLong(value);
        boolean isNegative = v < 0;

        BigInteger absValue;
        if (isNegative) {
            absValue = (v == Long.MIN_VALUE)
                ? BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)
                : BigInteger.valueOf(-v);
        } else {
            absValue = BigInteger.valueOf(v);
        }

        fillDigitsBigInteger(absValue, buffer);

        return isNegative;
    }

    private static boolean encodeDecimal(Object value, PictureMeta pic, byte[] buffer) {
        // 先轉 decimal
        BigDecimal d = toBigDecimal(value);

        // 符號處理
        boolean isNegative = d.signum() < 0;
        if (isNegative)
            d = d.negate();

        // scale 小數點
        BigDecimal magnitude = (pic.getDecimalDigits() > 0)
            ? d.multiply(CbDecimal.pow10(pic.getDecimalDigits()))
            : d;

        // 拆位填 buffer
        BigInteger intPart = magnitude.toBigInteger();

        if (intPart.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0) {
            fillDigitsLong(intPart.longValue(), buffer);
        } else {
            fillDigitsBigInteger(intPart, buffer);
        }

        return isNegative;
    }

    private static void fillDigitsLong(long value, byte[] buffer) {
        long absVal = Math.abs(value);
        int i = buffer.length - 1;

        // 從尾到頭寫入數字
        // COBOL 特性：超過 buffer 位數的高位會被截斷 (silent truncation)
        while (i >= 0 && absVal > 0) {
            buffer[i] = (byte) ('0' + (absVal % 10));
            absVal /= 10;
            i--;
        }
        while (i >= 0) {
            buffer[i] = (byte) '0';
            i--;
        }
    }

    private static void fillDigitsBigInteger(BigInteger value, byte[] buffer) {
        int i = buffer.length - 1;

        // 從尾到頭寫入數字
        // COBOL 特性：超過 buffer 位數的高位會被截斷 (silent truncation)
        while (i >= 0 && value.signum() > 0) {
            BigInteger[] divRem = value.divideAndRemainder(BigInteger.TEN);
            buffer[i] = (byte) ('0' + divRem[1].intValue());
            value = divRem[0];
            i--;
        }
        while (i >= 0) {
            buffer[i] = (byte) '0';
            i--;
        }
    }

    private static long toLong(Object value) {
        if (value instanceof Byte b)       return b;
        if (value instanceof Short s)      return s;
        if (value instanceof Integer i)    return i;
        if (value instanceof Long l)       return l;
        if (value instanceof Float f)      return f.longValue();
        if (value instanceof Double d)     return d.longValue();
        if (value instanceof BigDecimal bd) return bd.longValue();
        throw new UnsupportedOperationException(
            "Unsupported numeric type: " + value.getClass().getSimpleName());
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value instanceof Byte b)       return BigDecimal.valueOf(b);
        if (value instanceof Short s)      return BigDecimal.valueOf(s);
        if (value instanceof Integer i)    return BigDecimal.valueOf(i);
        if (value instanceof Long l)       return BigDecimal.valueOf(l);
        if (value instanceof Float f)      return new BigDecimal(f.toString());
        if (value instanceof Double d)     return new BigDecimal(d.toString());
        if (value instanceof BigDecimal bd) return bd;
        throw new UnsupportedOperationException(
            "Unsupported numeric type: " + value.getClass().getSimpleName());
    }

    private static boolean isNumericType(Object value) {
        return value instanceof Byte    || value instanceof Short  ||
               value instanceof Integer || value instanceof Long   ||
               value instanceof Float   || value instanceof Double ||
               value instanceof BigDecimal;
    }
}
