package getthepicture.picture.codec.semantic.date;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import getthepicture.picture.core.meta.PictureMeta;

public class DateDecoder {

    private static final DateTimeFormatter GREGORIAN_FMT =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate decode(byte[] buffer, PictureMeta pic) {
        return switch (pic.getSemantic()) {
            case GREGORIAN_DATE -> parseGregorianDate(buffer);
            case MINGUO_DATE    -> parseMinguoDate(buffer);
            default -> throw new UnsupportedOperationException(
                "Unsupported DateOnly format: " + pic.getSemantic());
        };
    }

    private static LocalDate parseGregorianDate(byte[] buffer) {
        String s = new String(buffer, StandardCharsets.US_ASCII);
        try {
            return LocalDate.parse(s, GREGORIAN_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid Gregorian date value: '" + s + "'", e);
        }
    }

    private static LocalDate parseMinguoDate(byte[] buffer) {
        String s = new String(buffer, StandardCharsets.US_ASCII);

        if (s.length() < 7)
            throw new IllegalArgumentException(
                "Invalid Minguo date value: '" + s + "'");

        try {
            // 前 3 碼：民國年
            int minguoYear    = Integer.parseInt(s.substring(0, 3));
            int month         = Integer.parseInt(s.substring(3, 5));
            int day           = Integer.parseInt(s.substring(5, 7));
            int gregorianYear = minguoYear + 1911;

            return LocalDate.of(gregorianYear, month, day);
        } catch (NumberFormatException | java.time.DateTimeException e) {
            throw new IllegalArgumentException(
                "Invalid Minguo date value: '" + s + "'", e);
        }
    }
}
