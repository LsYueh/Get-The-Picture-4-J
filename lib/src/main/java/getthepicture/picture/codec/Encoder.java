package getthepicture.picture.codec;

import java.util.Locale.Category;

import getthepicture.picture.codec.category.alphabetic.AlphabeticEncoder;
import getthepicture.picture.codec.category.alphanumeric.AlphanumericEncoder;
import getthepicture.picture.codec.category.numeric.NumericEncoder;
import getthepicture.picture.codec.semantic.Constraint;
import getthepicture.picture.codec.semantic.Rules;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * Java value → [NumericValue] → COBOL Elementary Item (buffer)
 */
public class Encoder {

    public static byte[] encode(Object value, PictureMeta pic, CodecOptions options) {
        if (value == null)
            throw new IllegalArgumentException("value must not be null");
        if (pic == null)
            throw new IllegalArgumentException("pic must not be null");

        if (pic.getSemantic() != PicClauseSemantic.NONE) {
            Constraint rule = Rules.getConstraint(pic.getSemantic());
            rule.validateOrThrow(pic);
        }

        byte[] normalized = switch (pic.getSemantic()) {
            // case GREGORIAN_DATE,
            //      MINGUO_DATE  -> Semantic.Date.Encoder.encode(value, pic);
            // case TIME6,
            //      TIME9        -> Semantic.Time.Encoder.encode(value, pic);
            // case TIMESTAMP14  -> Semantic.Timestamp.Encoder.encode(value, pic);
            // case BOOLEAN      -> Semantic.Boolean.Encoder.encode(value, pic);
            default           -> encodeBaseType(value, pic, options);
        };

        if (options.isStrict() && normalized.length != pic.getStorageOccupied())
            throw new IllegalArgumentException(
                "DISPLAY length mismatch. Expected " + pic.getStorageOccupied() +
                ", actual " + normalized.length + ".");

        return normalized;
    }

    static byte[] encodeBaseType(Object value, PictureMeta pic, CodecOptions options) {
        return switch (pic.getBaseClass()) {
            case NUMERIC -> {
                if (value instanceof String text)
                    throw new UnsupportedOperationException(
                        "PIC " + pic.getRaw() + " expects Numeric value (number), but got string. Value: \"" + text + "\"");
                yield NumericEncoder.encode(value, pic, options);
            }
            case ALPHANUMERIC -> {
                if (!(value instanceof String text))
                    throw new UnsupportedOperationException(
                        "PIC " + pic.getRaw() + " expects Alphanumeric value (string), but got " +
                        (value != null ? value.getClass().getSimpleName() : "null") + ".");
                yield AlphanumericEncoder.encode(text, pic);
            }
            case ALPHABETIC -> {
                if (!(value instanceof String text))
                    throw new UnsupportedOperationException(
                        "PIC " + pic.getRaw() + " expects Alphabetic value (string), but got " +
                        (value != null ? value.getClass().getSimpleName() : "null") + ".");
                yield AlphabeticEncoder.encode(text, pic);
            }
            default -> throw new UnsupportedOperationException(
                "Unsupported PIC Data Type [Encode] : " + pic.getBaseClass());
        };
    }
}

