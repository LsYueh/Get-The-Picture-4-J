package getthepicture.picture.codec.category.alphanumeric;

import java.nio.charset.Charset;

import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.BufferSlice;
import getthepicture.picture.utils.EncodingFactory;

public class AlphanumericDecoder {
    private static final Charset CP950 = EncodingFactory.getCP950();

    /**
     * Decoder for COBOL PIC X.
     *
     * @param buffer ASCII/CP950
     * @param pic
     * @return decoded string
     */
    public static String decode(byte[] buffer, PictureMeta pic) {
        if (pic.getUsage() != PicClauseUsage.DISPLAY)
            throw new UnsupportedOperationException(
                "PIC X does not support usage '" + pic.getUsage() + "'. Only DISPLAY is allowed.");

        // X(n) 通常右補空白
        byte[] fieldBytes = BufferSlice.slicePadEnd(buffer, pic.getDigitCount());

        return new String(fieldBytes, CP950).stripTrailing();
    }
}
