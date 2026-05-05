package io.github.lsyueh.getthepicture.picture.codec.category.alphanumeric;

import io.github.lsyueh.getthepicture.picture.core.clause.items.PicClauseUsage;
import io.github.lsyueh.getthepicture.picture.core.meta.PictureMeta;
import io.github.lsyueh.getthepicture.picture.utils.BufferSlice;
import io.github.lsyueh.getthepicture.picture.utils.EncodingFactory;

public class AlphanumericEncoder {

    /**
     * Encoder for COBOL PIC X.
     *
     * @param text
     * @param pic
     * @return encoded bytes
     */
    public static byte[] encode(String text, PictureMeta pic) {
        if (pic.getUsage() != PicClauseUsage.DISPLAY)
            throw new UnsupportedOperationException(
                "PIC X does not support usage '" + pic.getUsage() + "'. Only DISPLAY is allowed.");

        byte[] buffer = text.getBytes(EncodingFactory.getCP950());
        return BufferSlice.slicePadEnd(buffer, pic.getDigitCount());
    }
}
