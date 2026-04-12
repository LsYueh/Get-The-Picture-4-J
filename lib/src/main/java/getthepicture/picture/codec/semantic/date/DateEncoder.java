package getthepicture.picture.codec.semantic.date;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import getthepicture.picture.core.meta.PictureMeta;

public class DateEncoder {

    private static final DateTimeFormatter GREGORIAN_FMT =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter MMDD_FMT =
        DateTimeFormatter.ofPattern("MMdd");

    public static byte[] encode(Object value, PictureMeta pic) {
        if (!(value instanceof LocalDate date))
            throw new IllegalArgumentException(
                "Invalid value type for LocalDate encoding: " +
                (value != null ? value.getClass().getName() : "null"));

        String s = switch (pic.getSemantic()) {
            case GREGORIAN_DATE -> date.format(GREGORIAN_FMT);
            case MINGUO_DATE    -> toMinguoDateString(date);
            default -> throw new UnsupportedOperationException(
                "Unsupported DateOnly format: " + pic.getSemantic());
        };

        return s.getBytes(StandardCharsets.US_ASCII);
    }

    private static String toMinguoDateString(LocalDate date) {
        int rocYear = date.getYear() - 1911;

        if (rocYear <= 0)
            throw new IllegalArgumentException(
                "Date is before ROC calendar starts (1912-01-01).");

        return String.format("%03d%s", rocYear, date.format(MMDD_FMT));
    }
}
