package getthepicture.copybook.wrapper;

import getthepicture.copybook.wrapper.core.CbAddress;

/**
 * 表示 {@link CbWrapper} 中的單一欄位存取代理。
 *
 * <p>
 * {@code CbField} 封裝指定欄位的位址資訊與讀寫行為，
 * 提供型別安全的 {@code get()}、{@code set()}
 * 以及 {@code clear()} 操作。
 * </p>
 * <p>
 * 注意：欄位不支援 {@code null} 語意，
 * 清除欄位時將依 PIC 類型寫入對應的預設值（例如 SPACES 或 ZEROS）。
 * </p>
 */
public final class CbField {

    private final CbWrapper wrapper;
    private final CbAddress addr;

    CbField(CbWrapper wrapper, CbAddress addr) {
        this.wrapper = wrapper;
        this.addr = addr;
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) wrapper.read(addr);
    }

    public <T> void set(T value) {
        wrapper.write(value, addr);
    }

    public void clear() {
        wrapper.writeDefault(addr);
    }
}
