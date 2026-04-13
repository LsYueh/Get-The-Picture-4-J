package getthepicture.picture.codec.category.numeric;

import java.math.BigDecimal;

import getthepicture.picture.codec.CodecOptions;
import getthepicture.picture.core.CbDecimal;
import getthepicture.picture.core.clause.computational.COMP3;
import getthepicture.picture.core.clause.computational.COMP4;
import getthepicture.picture.core.clause.computational.COMP5;
import getthepicture.picture.core.clause.computational.COMP6;
import getthepicture.picture.core.clause.overpunch.OverpunchCodec;
import getthepicture.picture.core.mapper.IntMapper;
import getthepicture.picture.core.mapper.Mapper;
import getthepicture.picture.core.meta.PictureMeta;
import getthepicture.picture.utils.BufferSlice;

/**
 * CP950 → [Overpunch Decode]/[COMP] (object) → Mapper → Java
 */
public class NumericDecoder {

    private static final Mapper mapper = new IntMapper();

    /**
     * @param buffer ASCII/CP950
     * @param pic
     * @param options
     * @return decoded value
     * @throws ArithmeticException      if digit count exceeds 28
     * @throws UnsupportedOperationException if usage is not supported
     */
    public static Object decode(byte[] buffer, PictureMeta pic, CodecOptions options) {
        if (pic.getDigitCount() > 28)
            throw new ArithmeticException(
                "PIC " + pic + " has " + pic.getIntegerDigits() + " + " + pic.getDecimalDigits() +
                " = " + pic.getDigitCount() + " digit(s), which exceeds the supported maximum (28 digits).");

        if (options == null)
            options = new CodecOptions();

        // Note: COBOL 資料記憶體先被 S9(n) 截位再轉處理，一般 COBOL 應該也是這樣的狀況
        // 截位或補字處理
        byte[] bytes = BufferSlice.slicePadStart(buffer, pic.getStorageOccupied());

        return switch (pic.getUsage()) {
            case DISPLAY          -> displayDecode(bytes, pic, options);
            case PACKED_DECIMAL   -> COMP3.decode(bytes, pic);
            case BINARY           -> COMP4.decode(bytes, pic);
            case NATIVE_BINARY    -> COMP5.decode(bytes, pic, options.isBigEndian());
            case U_PACKED_DECIMAL -> COMP6.decode(bytes, pic);
            default -> throw new UnsupportedOperationException(
                "Unsupported numeric storage: " + pic.getUsage());
        };
    }

    public static Object decode(byte[] buffer, PictureMeta pic) {
        return decode(buffer, pic, null);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    private static Object displayDecode(byte[] bytes, PictureMeta pic, CodecOptions options) {
        boolean[] negativeRef = new boolean[1];
        byte[] chars = OverpunchCodec.decode(bytes, pic, options, negativeRef);
        boolean isNegative = negativeRef[0];

        if (chars.length != pic.getDigitCount())
            throw new IllegalArgumentException(
                "Numeric length mismatch for PIC. Expected " + pic.getDigitCount() +
                ", actual " + chars.length + ".");

        BigDecimal value = CbDecimal.decode(chars, pic.getDecimalDigits(), isNegative);
        return (pic.getDecimalDigits() == 0) ? mapper.map(value, pic) : value;
    }
}

