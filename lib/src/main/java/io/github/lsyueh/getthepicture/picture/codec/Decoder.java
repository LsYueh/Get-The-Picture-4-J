package io.github.lsyueh.getthepicture.picture.codec;

import io.github.lsyueh.getthepicture.picture.codec.category.alphabetic.AlphabeticDecoder;
import io.github.lsyueh.getthepicture.picture.codec.category.alphanumeric.AlphanumericDecoder;
import io.github.lsyueh.getthepicture.picture.codec.category.numeric.NumericDecoder;
import io.github.lsyueh.getthepicture.picture.codec.semantic.Constraint;
import io.github.lsyueh.getthepicture.picture.codec.semantic.Rules;
import io.github.lsyueh.getthepicture.picture.codec.semantic.bool.BooleanDecoder;
import io.github.lsyueh.getthepicture.picture.codec.semantic.date.DateDecoder;
import io.github.lsyueh.getthepicture.picture.codec.semantic.time.TimeDecoder;
import io.github.lsyueh.getthepicture.picture.codec.semantic.timestamp.TimestampDecoder;
import io.github.lsyueh.getthepicture.picture.core.clause.items.PicClauseSemantic;
import io.github.lsyueh.getthepicture.picture.core.meta.PictureMeta;

/**
 * COBOL Elementary Item (buffer) → Java value
 */
public class Decoder {

    public static Object decode(byte[] buffer, PictureMeta pic, CodecOptions options) {
        if (pic == null)
            throw new IllegalArgumentException("pic must not be null");

        if (options.isStrict() && buffer.length != pic.getStorageOccupied())
            throw new IllegalArgumentException(
                "DISPLAY length mismatch. Expected " + pic.getStorageOccupied() +
                ", actual " + buffer.length + ".");

        if (pic.getSemantic() != PicClauseSemantic.NONE) {
            Constraint rule = Rules.getConstraint(pic.getSemantic());
            rule.validateOrThrow(pic);
        }

        return switch (pic.getSemantic()) {
            case GREGORIAN_DATE,
                 MINGUO_DATE  -> DateDecoder.decode(buffer, pic);
            case TIME6,
                 TIME9        -> TimeDecoder.decode(buffer, pic);
            case TIMESTAMP14  -> TimestampDecoder.decode(buffer, pic);
            case BOOLEAN      -> BooleanDecoder.decode(buffer, pic);
            default           -> decodeBaseType(buffer, pic, options);
        };
    }

    private static Object decodeBaseType(byte[] buffer, PictureMeta pic, CodecOptions options) {
        return switch (pic.getBaseClass()) {
            case NUMERIC      -> NumericDecoder.decode(buffer, pic, options);
            case ALPHANUMERIC -> AlphanumericDecoder.decode(buffer, pic);
            case ALPHABETIC   -> AlphabeticDecoder.decode(buffer, pic);
            default -> throw new UnsupportedOperationException(
                "Unsupported PIC Data Type [Decode] : " + pic.getBaseClass());
        };
    }
}
