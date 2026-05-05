package io.github.lsyueh.getthepicture.copybook.wrapper.core;

import io.github.lsyueh.getthepicture.copybook.wrapper.CbField;

/**
 * Copybook wrapper interface
 */
public interface Wrapper {

    /** 底層 byte buffer */
    byte[] getRaw();

    /** 透過欄位名稱讀寫資料 */
    CbField getField(String name);
}
