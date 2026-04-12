package getthepicture.picture.core.clause.overpunch;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import getthepicture.picture.core.clause.options.DataStorageOptions;

/**
 * Overpunch Codex
 */
public class OverpunchCodex {
    public static final Map<DataStorageOptions, Map<Byte, String>> MAP;
    public static final Map<DataStorageOptions, Map<String, Byte>> REVERSED_MAP;

    static {
        MAP          = Collections.unmodifiableMap(buildMap());
        REVERSED_MAP = Collections.unmodifiableMap(buildReversedMap());
    }

    /**
     * Get Overpunch Value
     *
     * @param key byte key
     * @param ds  DataStorage Options
     * @return overpunch value
     * @throws IllegalArgumentException if key or ds is invalid
     */
    public static String getValue(byte key, DataStorageOptions ds) {
        Map<Byte, String> codex = MAP.get(ds);
        if (codex == null)
            throw new IllegalArgumentException("Unsupported DataStorage: " + ds);

        String opValue = codex.get(key);
        if (opValue == null)
            throw new IllegalArgumentException(
                String.format("Invalid overpunch search key: '%c' (0x%02X)", (char)(key & 0xFF), key & 0xFF));

        return opValue;
    }

    /**
     * Get Overpunch Key
     *
     * @param opValue overpunch value
     * @param ds      DataStorage Options
     * @return byte key
     * @throws IllegalArgumentException if opValue or ds is invalid
     */
    public static byte getKey(String opValue, DataStorageOptions ds) {
        Map<String, Byte> codex = REVERSED_MAP.get(ds);
        if (codex == null)
            throw new IllegalArgumentException("Unsupported DataStorage: " + ds);

        Byte key = codex.get(opValue);
        if (key == null)
            throw new IllegalArgumentException("Invalid overpunch search value: '" + opValue + "'");

        return key;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Map<DataStorageOptions, Map<Byte, String>> buildMap() {
        Map<DataStorageOptions, Map<Byte, String>> map = new EnumMap<>(DataStorageOptions.class);
        map.put(DataStorageOptions.CA, merge(OverpunchTable.OP_POSITIVE_01, OverpunchTable.OP_NEGATIVE_01));
        map.put(DataStorageOptions.CB, merge(OverpunchTable.OP_POSITIVE_01, OverpunchTable.OP_NEGATIVE_02));
        map.put(DataStorageOptions.CI, merge(OverpunchTable.OP_POSITIVE_02, OverpunchTable.OP_NEGATIVE_01));
        map.put(DataStorageOptions.CM, merge(OverpunchTable.OP_POSITIVE_01, OverpunchTable.OP_NEGATIVE_03));
        map.put(DataStorageOptions.CN, merge(OverpunchTable.OP_POSITIVE_02, OverpunchTable.OP_NEGATIVE_01));
        map.put(DataStorageOptions.CR, merge(OverpunchTable.OP_POSITIVE_01, OverpunchTable.OP_NEGATIVE_04));
        return map;
    }

    private static Map<DataStorageOptions, Map<String, Byte>> buildReversedMap() {
        Map<DataStorageOptions, Map<String, Byte>> map = new EnumMap<>(DataStorageOptions.class);
        map.put(DataStorageOptions.CA, mergeRev(OverpunchTable.OP_POSITIVE_01_REVERSE, OverpunchTable.OP_NEGATIVE_01_REVERSE));
        map.put(DataStorageOptions.CB, mergeRev(OverpunchTable.OP_POSITIVE_01_REVERSE, OverpunchTable.OP_NEGATIVE_02_REVERSE));
        map.put(DataStorageOptions.CI, mergeRev(OverpunchTable.OP_POSITIVE_02_REVERSE, OverpunchTable.OP_NEGATIVE_01_REVERSE));
        map.put(DataStorageOptions.CM, mergeRev(OverpunchTable.OP_POSITIVE_01_REVERSE, OverpunchTable.OP_NEGATIVE_03_REVERSE));
        map.put(DataStorageOptions.CN, mergeRev(OverpunchTable.OP_POSITIVE_02_REVERSE, OverpunchTable.OP_NEGATIVE_01_REVERSE));
        map.put(DataStorageOptions.CR, mergeRev(OverpunchTable.OP_POSITIVE_01_REVERSE, OverpunchTable.OP_NEGATIVE_04_REVERSE));
        return map;
    }

    private static Map<Byte, String> merge(Map<Byte, String> a, Map<Byte, String> b) {
        Map<Byte, String> result = new HashMap<>(a.size() + b.size());
        result.putAll(a);
        result.putAll(b);
        return result;
    }

    private static Map<String, Byte> mergeRev(Map<String, Byte> a, Map<String, Byte> b) {
        Map<String, Byte> result = new HashMap<>(a.size() + b.size());
        result.putAll(a);
        result.putAll(b);
        return result;
    }
}
