package getthepicture.picture.codec.category.alphabetic;

import java.nio.charset.Charset;

import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.BufferSlice;
import getthepicture.picture.utils.EncodingFactory;

public class AlphabeticDecoder {

    private static final Charset CP950 = EncodingFactory.getCP950();

    /**
     * Decoder for COBOL PIC A.
     *
     * @param buffer ASCII/CP950
     * @param pic
     * @return
     */
    public static String decode(byte[] buffer, PictureMeta pic) {
        if (pic.getUsage() != PicClauseUsage.DISPLAY)
            throw new UnsupportedOperationException(
                "PIC A does not support usage '" + pic.getUsage() + "'. Only DISPLAY is allowed.");

        // X(n) 通常右補空白
        byte[] fieldBytes = BufferSlice.slicePadEnd(buffer, pic.getDigitCount());

        // PIC A 檢查
        for (int i = 0; i < fieldBytes.length; i++) {
            byte b = fieldBytes[i];
            // space
            if (b == 0x20) continue;
            // A-Z
            if (b >= 0x41 && b <= 0x5A) continue;
            // a-z
            if (b >= 0x61 && b <= 0x7A) continue;

            throw new IllegalArgumentException(
                String.format("PIC A : Invalid byte 0x%02X at position %d", b & 0xFF, i + 1)); // Note: 轉成 1-based
        }

        String value = new String(fieldBytes, CP950);
        return value.stripTrailing();
    }
}
