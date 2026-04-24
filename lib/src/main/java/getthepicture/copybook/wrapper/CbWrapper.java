package getthepicture.copybook.wrapper;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

import getthepicture.copybook.wrapper.core.CbAddress;
import getthepicture.copybook.wrapper.core.Wrapper;
import getthepicture.picture.PictureCodec;

/**
 * 抽象 Copybook wrapper base class
 * 提供欄位存取、Read/Write、零複製操作
 */
public abstract class CbWrapper implements Wrapper {

    // ----------------------------
    // Interface
    // ----------------------------

    /** 底層 buffer */
    @Override
    public byte[] getRaw() {
        return raw;
    }

    /**
     * 透過欄位名稱存取資料
     *
     * <pre>{@code
     * CbWrapper wrapper = new SomeCbWrapper(null);
     *
     * wrapper.getField("FIELD1").set(1234);
     * wrapper.getField("FIELD2").set("Hello!");
     *
     * int    field1 = wrapper.getField("FIELD1").get();
     * String field2 = wrapper.getField("FIELD2").get();
     * }</pre>
     *
     * @param name 欄位名稱
     * @return 對應的 {@link CbField}
     * @throws java.util.NoSuchElementException 當指定欄位不存在時拋出
     */
    @Override
    public CbField getField(String name) {
        CbAddress addr = getAddressMap().get(name);
        if (addr == null)
            throw new NoSuchElementException("Field '" + name + "' does not exist.");
        return new CbField(this, addr);
    }

    // ----------------------------
    // Wrapper
    // ----------------------------

    /**
     * 以空白 buffer 初始化，所有欄位將依 PIC 類型寫入預設值。
     */
    protected CbWrapper() {
        this.raw = new byte[requiredBufferLength()];
        for (CbAddress addr : getAddressMap().values()) {
            new CbField(this, addr).clear();
        }
    }

    /**
     * 以既有 buffer 初始化，將複製傳入的位元組陣列。
     *
     * @param raw 來源 buffer
     * @throws IllegalArgumentException 當 buffer 長度不符時拋出
     */
    protected CbWrapper(byte[] raw) {
        int required = requiredBufferLength();
        if (raw.length != required)
            throw new IllegalArgumentException("Buffer length must be " + required + ".");
        this.raw = raw.clone();
    }

    // (真身)
    private final byte[] raw;

    /**
     * 子類別必須提供欄位映射
     */
    protected abstract Map<String, CbAddress> getAddressMap();

    /**
     * 計算 Copybook 佈局所需的最小 raw buffer 長度。
     * 以最大欄位的 (Start + Length) 為基準。
     */
    private int requiredBufferLength() {
        return getAddressMap().values().stream()
                .mapToInt(a -> a.getStart() + a.getLength())
                .max()
                .orElse(0);
    }

    /**
     * 依欄位名稱取得對應的 {@link CbField} 存取代理。
     *
     * <p>
     * 此方法提供更具語意的欄位存取方式，
     * 適用於需要明確表達「操作欄位」語意的情境。
     * </p>
     * <p>與 {@link #getField(String)} 等價：</p>
     * <pre>{@code
     * wrapper.field("NAME").set("HELLO");
     * String value = wrapper.field("NAME").get();
     * }</pre>
     *
     * @param name 欄位名稱
     * @return 對應的 {@link CbField}
     * @throws java.util.NoSuchElementException 當指定欄位不存在時拋出
     */
    public CbField field(String name) {
        return getField(name);
    }

    /**
     * 讀取欄位值
     *
     * @param addr 欄位位址
     * @return 解碼後的欄位值
     */
    Object read(CbAddress addr) {
        byte[] buffer = Arrays.copyOfRange(raw, addr.getStart(), addr.getStart() + addr.getLength());
        return PictureCodec.forMeta(addr.getMeta()).withStrict().decode(buffer);
    }

    /**
     * 寫入欄位值
     *
     * @param value 欲寫入的值
     * @param addr  欄位位址
     * @return 寫入的位元組數
     * @throws IllegalArgumentException 當編碼後長度不符時拋出
     */
    int write(Object value, CbAddress addr) {
        byte[] bytes = PictureCodec.forMeta(addr.getMeta()).withStrict().encode(value);
        if (bytes.length != addr.getLength())
            throw new IllegalArgumentException(
                "Encoded length " + bytes.length + " does not match expected length " + addr.getLength());
        System.arraycopy(bytes, 0, raw, addr.getStart(), bytes.length);
        return bytes.length;
    }

    /**
     * 將指定欄位重設為其 COBOL 預設值。
     *
     * <ul>
     *   <li>Alphabetic / Alphanumeric → SPACES</li>
     *   <li>Numeric → ZEROS</li>
     * </ul>
     *
     * @param addr 欄位位址
     * @return 寫入的位元組數
     * @throws IllegalStateException 當初始化長度不符時拋出
     */
    int writeDefault(CbAddress addr) {
        byte[] bytes = PictureCodec.forMeta(addr.getMeta()).withStrict().createDefaultRepresentation();
        if (bytes.length != addr.getLength())
            throw new IllegalStateException(
                "Initialized length " + bytes.length + " does not match expected length " + addr.getLength());
        System.arraycopy(bytes, 0, raw, addr.getStart(), bytes.length);
        return bytes.length;
    }
}
