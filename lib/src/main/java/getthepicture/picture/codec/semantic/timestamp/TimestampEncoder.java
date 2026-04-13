package getthepicture.picture.codec.semantic.timestamp;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;

public class TimestampEncoder {

    private static final DateTimeFormatter TIMESTAMP14_FMT =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static byte[] encode(Object value, PictureMeta pic) {
        if (!(value instanceof LocalDateTime dt))
            throw new IllegalArgumentException(
                "Invalid value type for Timestamp (LocalDateTime) encoding: " +
                (value != null ? value.getClass().getName() : "null"));

        if (pic.getSemantic() != PicClauseSemantic.TIMESTAMP14)
            throw new UnsupportedOperationException(
                "LocalDateTime can only be encoded as Timestamp14, but was " + pic.getSemantic());

        return dt.format(TIMESTAMP14_FMT).getBytes(StandardCharsets.US_ASCII);
    }
}
