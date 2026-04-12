package getthepicture.picture.core.clause.overpunch;

import org.junit.jupiter.api.Test;

import getthepicture.picture.core.clause.options.DataStorageOptions;

import static org.junit.jupiter.api.Assertions.*;

class OverpunchCodexTest {

    // =========================================================================
    // MAP lookup
    // =========================================================================

    @Test
    void overpunchCode_positive_CI() {
        var opCode = OverpunchCodex.MAP.get(DataStorageOptions.CI);
        assertNotNull(opCode);
        assertEquals("+3", opCode.get((byte) 'C'));
    }

    @Test
    void overpunchCode_negative_CI() {
        var opCode = OverpunchCodex.MAP.get(DataStorageOptions.CI);
        assertNotNull(opCode);
        assertEquals("-0", opCode.get((byte) '}'));
    }

    @Test
    void overpunchCode_negative_CR() {
        var opCode = OverpunchCodex.MAP.get(DataStorageOptions.CR);
        assertNotNull(opCode);
        assertEquals("-0", opCode.get((byte) ' '));
        assertEquals("-2", opCode.get((byte) '"'));
        assertEquals("-7", opCode.get((byte) 0x27)); // single quote
    }

    // =========================================================================
    // getValue / getKey roundtrip
    // =========================================================================

    @Test
    void getValue_positive_CI() {
        assertEquals("+3", OverpunchCodex.getValue((byte) 'C', DataStorageOptions.CI));
    }

    @Test
    void getValue_negative_CI() {
        assertEquals("-0", OverpunchCodex.getValue((byte) '}', DataStorageOptions.CI));
    }

    @Test
    void getKey_roundtrip_CI() {
        byte original = (byte) 'C';
        String opVal = OverpunchCodex.getValue(original, DataStorageOptions.CI);
        byte restored = OverpunchCodex.getKey(opVal, DataStorageOptions.CI);
        assertEquals(original, restored);
    }

    @Test
    void getValue_unsupportedDataStorage_throwsIllegalArgumentException() {
        // CV は MAP に登録されていない
        assertThrows(IllegalArgumentException.class,
            () -> OverpunchCodex.getValue((byte) 'A', DataStorageOptions.CV));
    }

    @Test
    void getValue_invalidKey_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> OverpunchCodex.getValue((byte) 0x01, DataStorageOptions.CI));
    }

    @Test
    void getKey_invalidOpValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> OverpunchCodex.getKey("+X", DataStorageOptions.CI));
    }
}
