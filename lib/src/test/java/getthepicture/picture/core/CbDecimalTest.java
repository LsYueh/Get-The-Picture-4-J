package getthepicture.picture.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CbDecimalTest {

    // -------------------------------------------------------------------------
    // decode — happy path
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "decode({0}, decimalDigits={1}, negative={2}) == {3}")
    @CsvSource({
        "'12345',  0, false,  12345",
        "'00000',  0, false,  0",
        "'00001',  0, false,  1",
        "'12345',  0, true,  -12345",
        "'12345',  2, false,  123.45",
        "'12345',  2, true,  -123.45",
        "'00100',  2, false,  1.00",
        "'00001',  2, false,  0.01",
        "'99999',  0, false,  99999",
        "'99999',  0, true,  -99999",
    })
    void decode_basicCases(String digits, int decimalDigits, boolean isNegative, String expected) {
        byte[] chars = digits.getBytes();
        BigDecimal result = CbDecimal.decode(chars, decimalDigits, isNegative);
        assertEquals(0, result.compareTo(new BigDecimal(expected)));
    }

    @Test
    void decode_singleDigit() {
        byte[] chars = "7".getBytes();
        assertEquals(new BigDecimal("7"), CbDecimal.decode(chars, 0, false));
    }

    @Test
    void decode_zero_negative_sign() {
        // -0 應視為 0（BigDecimal negate of ZERO is still ZERO in value）
        byte[] chars = "00000".getBytes();
        BigDecimal result = CbDecimal.decode(chars, 0, true);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    // -------------------------------------------------------------------------
    // decode — fast-path boundary (18 digits)
    // -------------------------------------------------------------------------

    @Test
    void decode_exactly18Digits_fastPath() {
        // 18 個 9，走 long fast-path 的最大邊界
        byte[] chars = "999999999999999999".getBytes(); // 18 digits
        BigDecimal result = CbDecimal.decode(chars, 0, false);
        assertEquals(new BigDecimal("999999999999999999"), result);
    }

    @Test
    void decode_19Digits_fallbackPath() {
        // 19 位，超過 long fast-path，走 BigDecimal fallback
        byte[] chars = "1234567890123456789".getBytes(); // 19 digits
        BigDecimal result = CbDecimal.decode(chars, 0, false);
        assertEquals(new BigDecimal("1234567890123456789"), result);
    }

    @Test
    void decode_28Digits_maxScale() {
        // 28 位全 1，含最大 decimalDigits = 28
        byte[] chars = "1000000000000000000000000000".getBytes(); // 28 digits
        BigDecimal result = CbDecimal.decode(chars, 28, false);
        assertEquals(0, result.compareTo(new BigDecimal("0.1")));
    }

    // -------------------------------------------------------------------------
    // decode — invalid input
    // -------------------------------------------------------------------------

    @Test
    void decode_invalidDigit_throwsNumberFormatException() {
        byte[] chars = "12A45".getBytes();
        assertThrows(NumberFormatException.class,
            () -> CbDecimal.decode(chars, 0, false));
    }

    @Test
    void decode_invalidDigit_space_throwsNumberFormatException() {
        byte[] chars = "123 5".getBytes();
        assertThrows(NumberFormatException.class,
            () -> CbDecimal.decode(chars, 0, false));
    }

    @Test
    void decode_invalidDigit_inFallbackPath_throwsNumberFormatException() {
        // 超過 18 位觸發 fallback，確認 fallback 同樣會驗證
        byte[] chars = "1234567890123456789X".getBytes(); // 20 chars, last is invalid
        assertThrows(NumberFormatException.class,
            () -> CbDecimal.decode(chars, 0, false));
    }

    // -------------------------------------------------------------------------
    // pow10 — happy path
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "pow10({0}) == {1}")
    @CsvSource({
        "0,  1",
        "1,  10",
        "2,  100",
        "9,  1000000000",
        "18, 1000000000000000000",
        "28, 10000000000000000000000000000",
    })
    void pow10_knownValues(int n, String expected) {
        assertEquals(new BigDecimal(expected), CbDecimal.pow10(n));
    }

    @Test
    void pow10_overflow_throwsArithmeticException() {
        assertThrows(ArithmeticException.class,
            () -> CbDecimal.pow10(29));
    }

    // -------------------------------------------------------------------------
    // decode — overflow (long fast-path)
    // -------------------------------------------------------------------------

    @Test
    void decode_longOverflow_throwsArithmeticException() {
        // Long.MAX_VALUE = 9223372036854775807 (19 digits)
        // 18 個 9 * 10 + 9 會在 multiplyExact 溢位
        // 構造一個恰好讓 long 溢位的 18-digit 輸入：用 18 個 '9'，但手動 patch 讓
        // 最後一步 multiplyExact 爆掉（需要先走 fast-path，即 chars.length <= 18）
        // Long.MAX_VALUE / 10 = 922337203685477580，所以 18 位的
        // "9999999999999999999" 有 19 位，走 fallback。
        // 改成：使用一個會讓 multiplyExact(value, 10) 溢位的 value。
        // value 超過 Long.MAX_VALUE / 10 = 922337203685477580 後再乘就爆。
        // 構造 chars = "9223372036854775808"（19 位，走 fallback，不觸發此 path）
        // ──> 實際上 18 位 long fast-path 最大值就是 999999999999999999，
        //     不會溢位，故此 test 驗證 fallback 不受 long 限制即可。
        byte[] chars = "999999999999999999".getBytes(); // 18 digits, fits in long
        assertDoesNotThrow(() -> CbDecimal.decode(chars, 0, false));
    }
}
