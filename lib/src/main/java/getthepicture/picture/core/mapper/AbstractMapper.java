package getthepicture.picture.core.mapper;

import java.math.BigDecimal;

import getthepicture.picture.core.CbDecimal;
import getthepicture.picture.core.meta.PictureMeta;

public abstract class AbstractMapper implements Mapper {

    @Override
    public Object map(BigDecimal value, PictureMeta pic) {
        validate(value, pic);
        if (pic.getDecimalDigits() > 0)
            return value;
        return mapInteger(value, pic);
    }

    protected void validate(BigDecimal value, PictureMeta pic) {
        validateSign(value, pic);
        validateIntegerDigits(value, pic);
        validateScale(value, pic);
    }

    protected abstract void validateSign(BigDecimal value, PictureMeta pic);

    protected void validateIntegerDigits(BigDecimal value, PictureMeta pic) {
        if (pic.getIntegerDigits() <= 0)
            return;
        BigDecimal limit = CbDecimal.pow10(pic.getIntegerDigits());
        if (value.compareTo(limit) >= 0 || value.compareTo(limit.negate()) <= 0)
            throw new ArithmeticException(
                "Value exceeds declared PIC 9(" + pic.getIntegerDigits() + ").");
    }

    protected void validateScale(BigDecimal value, PictureMeta pic) {
        if (pic.getDecimalDigits() > 0) {
            BigDecimal scaled = value.multiply(CbDecimal.pow10(pic.getDecimalDigits()));
            if (scaled.compareTo(scaled.setScale(0, java.math.RoundingMode.DOWN)) != 0)
                throw new ArithmeticException(
                    "Value exceeds declared decimal digits V" + pic.getDecimalDigits() + ".");
        }
    }

    protected abstract Object mapInteger(BigDecimal value, PictureMeta pic);
}