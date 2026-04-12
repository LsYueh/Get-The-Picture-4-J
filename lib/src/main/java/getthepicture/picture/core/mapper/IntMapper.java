package getthepicture.picture.core.mapper;

import java.math.BigDecimal;

import getthepicture.picture.core.meta.PictureMeta;

public final class IntMapper extends AbstractMapper {

    @Override
    protected void validateSign(BigDecimal value, PictureMeta pic) {
        // 不需額外檢查
    }

    @Override
    protected Object mapInteger(BigDecimal value, PictureMeta pic) {
        int digits = pic.getIntegerDigits();
        if (digits <= 2)  return value.byteValueExact();
        if (digits <= 4)  return value.shortValueExact();
        if (digits <= 9)  return value.intValueExact(); 
        if (digits <= 18) return value.longValueExact();
        return value; // fallback BigDecimal
    }
}
