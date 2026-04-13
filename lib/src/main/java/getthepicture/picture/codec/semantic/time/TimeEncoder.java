package getthepicture.picture.codec.semantic.time;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import getthepicture.picture.core.meta.PictureMeta;

public class TimeEncoder {

    private static final DateTimeFormatter TIME6_FMT =
        DateTimeFormatter.ofPattern("HHmmss");

    private static final DateTimeFormatter TIME9_FMT =
        DateTimeFormatter.ofPattern("HHmmssSSS");

    public static byte[] encode(Object value, PictureMeta pic) {
        if (!(value instanceof LocalTime time))
            throw new IllegalArgumentException(
                "Invalid value type for LocalTime encoding: " +
                (value != null ? value.getClass().getName() : "null"));

        String s = switch (pic.getSemantic()) {
            case TIME6 -> time.format(TIME6_FMT);
            case TIME9 -> time.format(TIME9_FMT);
            default -> throw new UnsupportedOperationException(
                "Unsupported TIME format: " + pic.getSemantic());
        };

        return s.getBytes(StandardCharsets.US_ASCII);
    }
}
