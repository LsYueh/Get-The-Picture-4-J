package getthepicture.picture.core.mapper;

import java.math.BigDecimal;

import getthepicture.picture.core.meta.PictureMeta;

/**
 * 將數值映射到最合適的 Java 整數型別
 */
public interface Mapper {
    Object map(BigDecimal value, PictureMeta pic);
}
