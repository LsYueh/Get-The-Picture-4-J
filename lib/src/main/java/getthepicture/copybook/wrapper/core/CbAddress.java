package getthepicture.copybook.wrapper.core;

import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * Copybook 欄位位址資訊
 */
public final class CbAddress {

    /** 欄位起始位置 */
    private final int start;

    /** 欄位長度 (byte) */
    private final int length;

    /** 欄位型別描述 */
    private final PictureMeta meta;

    /**
     * Copybook 欄位位址資訊
     *
     * @param start    欄位在 buffer 的起始位置 (1-based)
     * @param length   欄位長度 (byte)
     * @param symbols  欄位的 PIC/型別描述
     * @param semantic 欄位的二次語意資料描述
     * @param usage    底層記憶體的儲存方式
     */
    public CbAddress(int start, int length, String symbols, PicClauseSemantic semantic, PicClauseUsage usage) {
        // Note: 內部使用 0-based
        this.start = start - 1;
        this.length = length;
        this.meta = PictureMeta.parse(symbols, semantic, usage);
        if (this.meta.getStorageOccupied() != length)
            throw new IllegalArgumentException(
                "Address length " + length + " does not match PIC storage occupied " + this.meta.getStorageOccupied()
            );
    }

    /**
     * Copybook 欄位位址資訊
     *
     * @param start    欄位在 buffer 的起始位置 (1-based)
     * @param length   欄位長度 (byte)
     * @param symbols  欄位的 PIC/型別描述
     * @param semantic 欄位的二次語意資料描述
     */
    public CbAddress(int start, int length, String symbols, PicClauseSemantic semantic) {
        this(start, length, symbols, semantic, PicClauseUsage.DISPLAY);
    }

    /** 使用預設 semantic/usage 的便利建構子 */
    public CbAddress(int start, int length, String symbols) {
        this(start, length, symbols, PicClauseSemantic.NONE, PicClauseUsage.DISPLAY);
    }

    /** 欄位起始位置 */
    public int getStart()   { return start; }

    /** 欄位長度 (byte) */
    public int getLength()  { return length; }

    /** 欄位型別描述 */
    public PictureMeta getMeta() { return meta; }
}
