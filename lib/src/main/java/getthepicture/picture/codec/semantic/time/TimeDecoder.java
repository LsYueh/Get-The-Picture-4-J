package getthepicture.picture.codec.semantic.time;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import getthepicture.picture.core.meta.PictureMeta;

public class TimeDecoder {

    private static final DateTimeFormatter TIME6_FMT =
        DateTimeFormatter.ofPattern("HHmmss");

    private static final DateTimeFormatter TIME9_FMT =
        DateTimeFormatter.ofPattern("HHmmssSSS");

    public static LocalTime decode(byte[] buffer, PictureMeta pic) {
        return switch (pic.getSemantic()) {
            case TIME6 -> parseTime6(buffer);
            case TIME9 -> parseTime9(buffer);
            default -> throw new UnsupportedOperationException(
                "Unsupported TimeOnly format: " + pic.getSemantic());
        };
    }

    private static LocalTime parseTime6(byte[] buffer) {
        String s = new String(buffer, StandardCharsets.US_ASCII);
        try {
            return LocalTime.parse(s, TIME6_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid Time6 value: '" + s + "'", e);
        }
    }

    private static LocalTime parseTime9(byte[] buffer) {
        String s = new String(buffer, StandardCharsets.US_ASCII);
        try {
            return LocalTime.parse(s, TIME9_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid Time9 value: '" + s + "'", e);
        }
    }
}
