package getthepicture.picture.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BufferSliceTest {

    // =========================================================================
    // slicePadEnd
    // =========================================================================

    static Stream<Arguments> slicePadEndProvider() {
        return Stream.of(
            // 長度剛好
            Arguments.of(new byte[]{ 0x41, 0x42, 0x43 }, 3, (byte) 0x20,
                         new byte[]{ 0x41, 0x42, 0x43 }),
            // buffer 較短，右補空白
            Arguments.of(new byte[]{ 0x41, 0x42 }, 5, (byte) 0x20,
                         new byte[]{ 0x41, 0x42, 0x20, 0x20, 0x20 }),
            // buffer 較長，截斷右側
            Arguments.of(new byte[]{ 0x41, 0x42, 0x43, 0x44, 0x45 }, 3, (byte) 0x20,
                         new byte[]{ 0x41, 0x42, 0x43 }),
            // 空 buffer，全補 pad
            Arguments.of(new byte[]{}, 3, (byte) 0x20,
                         new byte[]{ 0x20, 0x20, 0x20 }),
            // length = 0
            Arguments.of(new byte[]{ 0x41 }, 0, (byte) 0x20,
                         new byte[]{}),
            // 自訂 pad
            Arguments.of(new byte[]{ 0x41 }, 3, (byte) 0x00,
                         new byte[]{ 0x41, 0x00, 0x00 })
        );
    }

    @ParameterizedTest(name = "slicePadEnd: bufLen={0}, length={1}, pad={2}")
    @MethodSource("slicePadEndProvider")
    void slicePadEnd_cases(byte[] buffer, int length, byte pad, byte[] expected) {
        assertArrayEquals(expected, BufferSlice.slicePadEnd(buffer, length, pad));
    }

    @Test
    void slicePadEnd_defaultPad_isSpace() {
        byte[] result = BufferSlice.slicePadEnd(new byte[]{ 0x41 }, 3);
        assertArrayEquals(new byte[]{ 0x41, 0x20, 0x20 }, result);
    }

    @Test
    void slicePadEnd_exactLength_returnsCopy() {
        byte[] input = new byte[]{ 0x41, 0x42 };
        byte[] result = BufferSlice.slicePadEnd(input, 2);
        assertArrayEquals(input, result);
        assertNotSame(input, result); // 確認是複製，不是同一個參考
    }

    @Test
    void slicePadEnd_negativeLength_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> BufferSlice.slicePadEnd(new byte[]{ 0x41 }, -1));
    }

    // =========================================================================
    // slicePadStart
    // =========================================================================

    static Stream<Arguments> slicePadStartProvider() {
        return Stream.of(
            // 長度剛好
            Arguments.of(new byte[]{ 0x31, 0x32, 0x33 }, 3, (byte) 0x30,
                         new byte[]{ 0x31, 0x32, 0x33 }),
            // buffer 較短，左補 '0'
            Arguments.of(new byte[]{ 0x31, 0x32 }, 5, (byte) 0x30,
                         new byte[]{ 0x30, 0x30, 0x30, 0x31, 0x32 }),
            // buffer 較長，截斷左側（保留尾端）
            Arguments.of(new byte[]{ 0x31, 0x32, 0x33, 0x34, 0x35 }, 3, (byte) 0x30,
                         new byte[]{ 0x33, 0x34, 0x35 }),
            // 空 buffer，全補 pad
            Arguments.of(new byte[]{}, 3, (byte) 0x30,
                         new byte[]{ 0x30, 0x30, 0x30 }),
            // length = 0
            Arguments.of(new byte[]{ 0x31 }, 0, (byte) 0x30,
                         new byte[]{}),
            // 自訂 pad
            Arguments.of(new byte[]{ 0x31 }, 3, (byte) 0x20,
                         new byte[]{ 0x20, 0x20, 0x31 })
        );
    }

    @ParameterizedTest(name = "slicePadStart: bufLen={0}, length={1}, pad={2}")
    @MethodSource("slicePadStartProvider")
    void slicePadStart_cases(byte[] buffer, int length, byte pad, byte[] expected) {
        assertArrayEquals(expected, BufferSlice.slicePadStart(buffer, length, pad));
    }

    @Test
    void slicePadStart_defaultPad_isZero() {
        byte[] result = BufferSlice.slicePadStart(new byte[]{ 0x31 }, 3);
        assertArrayEquals(new byte[]{ 0x30, 0x30, 0x31 }, result);
    }

    @Test
    void slicePadStart_exactLength_returnsCopy() {
        byte[] input = new byte[]{ 0x31, 0x32 };
        byte[] result = BufferSlice.slicePadStart(input, 2);
        assertArrayEquals(input, result);
        assertNotSame(input, result); // 確認是複製，不是同一個參考
    }

    @Test
    void slicePadStart_negativeLength_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> BufferSlice.slicePadStart(new byte[]{ 0x31 }, -1));
    }
}
