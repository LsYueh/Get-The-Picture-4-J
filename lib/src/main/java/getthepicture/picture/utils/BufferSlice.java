package getthepicture.picture.utils;

import java.util.Arrays;

public class BufferSlice {

    /**
     * 預設 PIC X 的行為，左對齊，右補空白
     *
     * @param buffer
     * @param length
     * @param pad    預設空白
     */
    public static byte[] slicePadEnd(byte[] buffer, int length, byte pad) {
        if (length < 0)
            throw new IllegalArgumentException("length must not be negative");

        int copyLength = Math.min(buffer.length, length);

        // 長度剛好，直接複製回傳
        if (buffer.length == length)
            return buffer.clone();

        byte[] result = new byte[length];

        // 從 buffer 開頭複製到 result
        System.arraycopy(buffer, 0, result, 0, copyLength);

        // 如果不足，填充尾端 pad
        if (length > copyLength)
            Arrays.fill(result, copyLength, length, pad);

        return result;
    }

    public static byte[] slicePadEnd(byte[] buffer, int length) {
        return slicePadEnd(buffer, length, (byte) 0x20);
    }

    /**
     * 預設 PIC 9/S9 的行為，右對齊，左補 '0'
     *
     * @param buffer
     * @param length
     * @param pad    預設 '0'
     */
    public static byte[] slicePadStart(byte[] buffer, int length, byte pad) {
        if (length < 0)
            throw new IllegalArgumentException("length must not be negative");

        int copyLength = Math.min(buffer.length, length);

        // 長度剛好，直接複製回傳
        if (buffer.length == length)
            return buffer.clone();

        byte[] result = new byte[length];
        int padLength = length - copyLength;

        // 前面補 pad
        if (padLength > 0)
            Arrays.fill(result, 0, padLength, pad);

        // 從 buffer 尾端複製到 result 後面
        System.arraycopy(buffer, buffer.length - copyLength, result, padLength, copyLength);

        return result;
    }

    public static byte[] slicePadStart(byte[] buffer, int length) {
        return slicePadStart(buffer, length, (byte) 0x30);
    }
}
