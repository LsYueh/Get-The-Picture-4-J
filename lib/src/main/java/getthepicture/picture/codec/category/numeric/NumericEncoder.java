package getthepicture.picture.codec.category.numeric;

import getthepicture.picture.codec.CodecOptions;
import getthepicture.picture.core.clause.computational.COMP3;
import getthepicture.picture.core.clause.computational.COMP4;
import getthepicture.picture.core.clause.computational.COMP5;
import getthepicture.picture.core.clause.computational.COMP6;
import getthepicture.picture.core.clause.overpunch.OverpunchCodec;
import getthepicture.picture.core.meta.NumericMeta;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.BufferSlice;

/**
 * Java → Meta → [Overpunch Encode]/[COMP] (byte) → COBOL Elementary Item (buffer)
 */
public class NumericEncoder {

    /**
     * @param value
     * @param pic
     * @param options
     * @return encoded buffer
     * @throws ArithmeticException           if digit count exceeds 28
     * @throws UnsupportedOperationException if usage is not supported
     */
    public static byte[] encode(Object value, PictureMeta pic, CodecOptions options) {
        if (pic.getDigitCount() > 28)
            throw new ArithmeticException(
                "PIC " + pic + " has " + pic.getIntegerDigits() + " + " + pic.getDecimalDigits() +
                " = " + pic.getDigitCount() + " digit(s), which exceeds the supported maximum (28 digits).");

        if (options == null)
            options = new CodecOptions();

        NumericMeta nMeta = NumericMeta.parse(value, pic);

        byte[] buffer = switch (pic.getUsage()) {
            case DISPLAY          -> displayEncode(nMeta, pic, options);
            case PACKED_DECIMAL   -> COMP3.encode(nMeta, pic);
            case BINARY           -> COMP4.encode(nMeta, pic);
            case NATIVE_BINARY    -> COMP5.encode(nMeta, pic, options.isBigEndian());
            case U_PACKED_DECIMAL -> COMP6.encode(nMeta, pic);
            default -> throw new UnsupportedOperationException(
                "Unsupported numeric storage: " + pic.getUsage());
        };

        // Note: 模擬 COBOL 資料記憶體被 S9(n) 截位的輸出結果
        return BufferSlice.slicePadStart(buffer, pic.getStorageOccupied());
    }

    public static byte[] encode(Object value, PictureMeta pic) {
        return encode(value, pic, null);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    private static byte[] displayEncode(NumericMeta nMeta, PictureMeta pic, CodecOptions options) {
        boolean isNegative = nMeta.isNegative();
        // Note: 複製 chars，避免修改原始資料
        byte[] numeric = nMeta.getChars().clone();
        return OverpunchCodec.encode(isNegative, numeric, pic, options);
    }
}
