package getthepicture.picture.core.clause.overpunch;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OverpunchTable {

    /**
     * Overpunch Value
     */
    public static String opVal(boolean isNegative, char digit) {
        return (isNegative ? "-" : "+") + digit;
    }

    /**
     * -Dca, -Dcb, -Dcm, -Dcr
     */
    public static final Map<Byte, String> OP_POSITIVE_01;
    public static final Map<String, Byte> OP_POSITIVE_01_REVERSE;

    /**
     * -Dci, -Dcn
     */
    public static final Map<Byte, String> OP_POSITIVE_02;
    public static final Map<String, Byte> OP_POSITIVE_02_REVERSE;

    /**
     * -Dca, -Dci, -Dcn
     */
    public static final Map<Byte, String> OP_NEGATIVE_01;
    public static final Map<String, Byte> OP_NEGATIVE_01_REVERSE;

    /**
     * -Dcb
     */
    public static final Map<Byte, String> OP_NEGATIVE_02;
    public static final Map<String, Byte> OP_NEGATIVE_02_REVERSE;

    /**
     * -Dcm
     */
    public static final Map<Byte, String> OP_NEGATIVE_03;
    public static final Map<String, Byte> OP_NEGATIVE_03_REVERSE;

    /**
     * -Dcr
     */
    public static final Map<Byte, String> OP_NEGATIVE_04;
    public static final Map<String, Byte> OP_NEGATIVE_04_REVERSE;

    static {
        OP_POSITIVE_01 = unmodifiable(new LinkedHashMap<>() {{
            put(b('0'), opVal(false, '0'));
            put(b('1'), opVal(false, '1'));
            put(b('2'), opVal(false, '2'));
            put(b('3'), opVal(false, '3'));
            put(b('4'), opVal(false, '4'));
            put(b('5'), opVal(false, '5'));
            put(b('6'), opVal(false, '6'));
            put(b('7'), opVal(false, '7'));
            put(b('8'), opVal(false, '8'));
            put(b('9'), opVal(false, '9'));
        }});
        OP_POSITIVE_01_REVERSE = reverse(OP_POSITIVE_01);

        OP_POSITIVE_02 = unmodifiable(new LinkedHashMap<>() {{
            put(b('{'), opVal(false, '0'));
            put(b('A'), opVal(false, '1'));
            put(b('B'), opVal(false, '2'));
            put(b('C'), opVal(false, '3'));
            put(b('D'), opVal(false, '4'));
            put(b('E'), opVal(false, '5'));
            put(b('F'), opVal(false, '6'));
            put(b('G'), opVal(false, '7'));
            put(b('H'), opVal(false, '8'));
            put(b('I'), opVal(false, '9'));
        }});
        OP_POSITIVE_02_REVERSE = reverse(OP_POSITIVE_02);

        OP_NEGATIVE_01 = unmodifiable(new LinkedHashMap<>() {{
            put(b('}'), opVal(true, '0'));
            put(b('J'), opVal(true, '1'));
            put(b('K'), opVal(true, '2'));
            put(b('L'), opVal(true, '3'));
            put(b('M'), opVal(true, '4'));
            put(b('N'), opVal(true, '5'));
            put(b('O'), opVal(true, '6'));
            put(b('P'), opVal(true, '7'));
            put(b('Q'), opVal(true, '8'));
            put(b('R'), opVal(true, '9'));
        }});
        OP_NEGATIVE_01_REVERSE = reverse(OP_NEGATIVE_01);

        OP_NEGATIVE_02 = unmodifiable(new LinkedHashMap<>() {{
            put(b('@'), opVal(true, '0'));
            put(b('A'), opVal(true, '1'));
            put(b('B'), opVal(true, '2'));
            put(b('C'), opVal(true, '3'));
            put(b('D'), opVal(true, '4'));
            put(b('E'), opVal(true, '5'));
            put(b('F'), opVal(true, '6'));
            put(b('G'), opVal(true, '7'));
            put(b('H'), opVal(true, '8'));
            put(b('I'), opVal(true, '9'));
        }});
        OP_NEGATIVE_02_REVERSE = reverse(OP_NEGATIVE_02);

        OP_NEGATIVE_03 = unmodifiable(new LinkedHashMap<>() {{
            put(b('p'), opVal(true, '0'));
            put(b('q'), opVal(true, '1'));
            put(b('r'), opVal(true, '2'));
            put(b('s'), opVal(true, '3'));
            put(b('t'), opVal(true, '4'));
            put(b('u'), opVal(true, '5'));
            put(b('v'), opVal(true, '6'));
            put(b('w'), opVal(true, '7'));
            put(b('x'), opVal(true, '8'));
            put(b('y'), opVal(true, '9'));
        }});
        OP_NEGATIVE_03_REVERSE = reverse(OP_NEGATIVE_03);

        OP_NEGATIVE_04 = unmodifiable(new LinkedHashMap<>() {{
            put(b(' '),  opVal(true, '0')); // (space)
            put(b('!'),  opVal(true, '1'));
            put(b('"'),  opVal(true, '2')); // (double-quote)
            put(b('#'),  opVal(true, '3'));
            put(b('$'),  opVal(true, '4'));
            put(b('%'),  opVal(true, '5'));
            put(b('&'),  opVal(true, '6'));
            put(b('\''), opVal(true, '7')); // (single-quote)
            put(b('('),  opVal(true, '8'));
            put(b(')'),  opVal(true, '9'));
        }});
        OP_NEGATIVE_04_REVERSE = reverse(OP_NEGATIVE_04);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static byte b(char c) {
        return (byte) c;
    }

    private static Map<Byte, String> unmodifiable(Map<Byte, String> map) {
        return Collections.unmodifiableMap(map);
    }

    private static Map<String, Byte> reverse(Map<Byte, String> map) {
        return Collections.unmodifiableMap(
            map.entrySet().stream()
               .collect(Collectors.toMap(
                   Map.Entry::getValue,
                   Map.Entry::getKey,
                   (a, b) -> a,
                   LinkedHashMap::new
               ))
        );
    }
}
