package getthepicture.picture.core;

import java.math.BigDecimal;

public class CbDecimal {

    /**
     * Decode from byte array (e.g., COMP-3 / overpunch digits in ASCII).
     *
     * @param chars         The integer digits (PIC 9(n)) as chars.
     * @param decimalDigits Number of decimal digits (V9(m)).
     * @param isNegative    Sign flag.
     * @return Decoded decimal value.
     * @throws NumberFormatException    If any char is not 0–9.
     * @throws ArithmeticException      If total digits exceed supported precision.
     */
    public static BigDecimal decode(byte[] chars, int decimalDigits, boolean isNegative) {
        BigDecimal result;

        if (chars.length <= 18) {
            // long fast-path（安全 18 位）
            long value = 0;
            for (byte c : chars) {
                int digit = c - (byte) '0';
                if (Integer.compareUnsigned(digit, 9) > 0)
                    throw new NumberFormatException(
                        String.format("Invalid digit '%c' in numeric field.", (char) c));
                value = Math.addExact(Math.multiplyExact(value, 10L), digit);
            }
            result = BigDecimal.valueOf(value);
        } else {
            // BigDecimal fallback
            BigDecimal value = BigDecimal.ZERO;
            for (byte c : chars) {
                int digit = c - (byte) '0';
                if (Integer.compareUnsigned(digit, 9) > 0)
                    throw new NumberFormatException(
                        String.format("Invalid digit '%c' in numeric field.", (char) c));
                value = value.multiply(BigDecimal.TEN).add(BigDecimal.valueOf(digit));
            }
            result = value;
        }

        // scale adjustment
        if (decimalDigits > 0)
            result = result.divide(pow10(decimalDigits));

        if (isNegative)
            result = result.negate();

        return result;
    }

    /**
     * Pow10 lookup table for 0..28 decimal digits.
     */
    public static BigDecimal pow10(int n) {
        if (n >= POW10_TABLE.length)
            throw new ArithmeticException("Decimal scale too large.");
        return POW10_TABLE[n];
    }

    private static final BigDecimal[] POW10_TABLE = {
        new BigDecimal("1"),                              // 10^0
        new BigDecimal("10"),                             // 10^1
        new BigDecimal("100"),
        new BigDecimal("1000"),
        new BigDecimal("10000"),
        new BigDecimal("100000"),
        new BigDecimal("1000000"),
        new BigDecimal("10000000"),
        new BigDecimal("100000000"),
        new BigDecimal("1000000000"),
        new BigDecimal("10000000000"),
        new BigDecimal("100000000000"),
        new BigDecimal("1000000000000"),
        new BigDecimal("10000000000000"),
        new BigDecimal("100000000000000"),
        new BigDecimal("1000000000000000"),
        new BigDecimal("10000000000000000"),
        new BigDecimal("100000000000000000"),
        new BigDecimal("1000000000000000000"),
        new BigDecimal("10000000000000000000"),
        new BigDecimal("100000000000000000000"),
        new BigDecimal("1000000000000000000000"),
        new BigDecimal("10000000000000000000000"),
        new BigDecimal("100000000000000000000000"),
        new BigDecimal("1000000000000000000000000"),
        new BigDecimal("10000000000000000000000000"),
        new BigDecimal("100000000000000000000000000"),
        new BigDecimal("1000000000000000000000000000"),
        new BigDecimal("10000000000000000000000000000"),  // 10^28
    };
}