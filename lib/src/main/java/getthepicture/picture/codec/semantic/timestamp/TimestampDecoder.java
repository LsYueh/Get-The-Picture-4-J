package getthepicture.picture.codec.semantic.timestamp;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import getthepicture.picture.core.meta.PictureMeta;

public class TimestampDecoder {

    private static final DateTimeFormatter TIMESTAMP14_FMT =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static LocalDateTime decode(byte[] buffer, PictureMeta pic) {
        return switch (pic.getSemantic()) {
            case TIMESTAMP14 -> parseTimestamp14(buffer);
            default -> throw new UnsupportedOperationException(
                "Unsupported DateTime format: " + pic.getSemantic());
        };
    }

    private static LocalDateTime parseTimestamp14(byte[] buffer) {
        String s = new String(buffer, StandardCharsets.US_ASCII);
        try {
            return LocalDateTime.parse(s, TIMESTAMP14_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid Timestamp14 value: '" + s + "'", e);
        }
    }
}
