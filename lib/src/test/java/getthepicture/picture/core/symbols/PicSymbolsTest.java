package getthepicture.picture.core.symbols;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;
import getthepicture.picture.core.symbols.parser.PicSymbolsMeta;
import getthepicture.picture.core.symbols.parser.PicSymbolsParseException;

import static org.junit.jupiter.api.Assertions.*;

class PicSymbolsTest {

    @ParameterizedTest
    @CsvSource({
        "X,        ALPHANUMERIC, false,  1,  0",
        "XX,       ALPHANUMERIC, false,  2,  0",
        "X(3),     ALPHANUMERIC, false,  3,  0",
        "X(1),     ALPHANUMERIC, false,  1,  0",
        "XX(2),    ALPHANUMERIC, false,  3,  0",
        "X(2)X(3), ALPHANUMERIC, false,  5,  0",
        "XXX(1),   ALPHANUMERIC, false,  3,  0",
        "X(93),    ALPHANUMERIC, false, 93,  0",
        "9,        NUMERIC     , false,  1,  0",
        "99,       NUMERIC     , false,  2,  0",
        "9(3),     NUMERIC     , false,  3,  0",
        "9(9),     NUMERIC     , false,  9,  0",
        "9(10),    NUMERIC     , false, 10,  0",
        "99(2),    NUMERIC     , false,  3,  0",
        "9(2)9(3), NUMERIC     , false,  5,  0",
        "9V9,      NUMERIC     , false,  1,  1",
        "99V9,     NUMERIC     , false,  2,  1",
        "99V9(10)9,NUMERIC     , false,  2, 11",
        "9V99,     NUMERIC     , false,  1,  2",
        "9(2)V9(3),NUMERIC     , false,  2,  3",
        "99V9(2),  NUMERIC     , false,  2,  2",
        "S9,       NUMERIC     ,  true,  1,  0",
        "S99,      NUMERIC     ,  true,  2,  0",
        "S9V9,     NUMERIC     ,  true,  1,  1",
        "S9(2)V9,  NUMERIC     ,  true,  2,  1",
    })
    void parser_tests_01(String symbols, PicClauseBaseClass baseClass, boolean signed, int integerDigits, int decimalDigits) {
        PicSymbolsMeta meta = PicSymbols.read(symbols.strip());
        assertEquals(baseClass,     meta.getBaseClass());
        assertEquals(signed,        meta.isSigned());
        assertEquals(integerDigits, meta.getIntegerDigits());
        assertEquals(decimalDigits, meta.getDecimalDigits());
    }

    @ParameterizedTest
    @CsvSource({
        "9P",
        "9P9",
        "9(2)P9",
        "SP9",
    })
    void parser_tests_02_scaling_not_supported(String symbols) {
        assertThrows(UnsupportedOperationException.class, () -> PicSymbols.read(symbols.strip()));
    }

    @Test
    void parser_should_throw_when_mixing_classes() {
        assertThrows(PicSymbolsParseException.class, () -> PicSymbols.read("X9"));
    }

    @Test
    void parser_should_throw_when_sign_with_alpha() {
        assertThrows(PicSymbolsParseException.class, () -> PicSymbols.read("SX"));
    }

    @Test
    void parser_should_throw_on_v_without_numeric() {
        assertThrows(PicSymbolsParseException.class, () -> PicSymbols.read("V9"));
    }

    @Test
    void parser_should_throw_on_multiple_v() {
        assertThrows(PicSymbolsParseException.class, () -> PicSymbols.read("9V9V9"));
    }
}
