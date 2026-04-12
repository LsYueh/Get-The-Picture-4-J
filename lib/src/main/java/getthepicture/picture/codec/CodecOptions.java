package getthepicture.picture.codec;

import getthepicture.picture.core.clause.options.DataStorageOptions;
import getthepicture.picture.core.clause.options.SignOptions;

/**
 * COBOL 資料編碼與解碼所需的行為與條件選項。
 */
public final class CodecOptions {

    /**
     * 嚴格長度驗證 (預設 false)
     */
    private boolean strict = false;

    /**
     * {@code -Dci} is the default.
     */
    private DataStorageOptions dataStorage = DataStorageOptions.CI;

    /**
     * {@code SIGN IS TRAILING} is the default.
     */
    private SignOptions sign = SignOptions.IS_TRAILING;

    /**
     * BINARY specifies the representation format of binary data items.
     * <br/>
     * Ref. <a href="https://www.ibm.com/docs/en/cobol-linux-x86/1.2.0?topic=options-binary">BINARY</a>
     */
    private boolean bigEndian = true;

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public boolean isStrict()                { return strict; }
    public void    setStrict(boolean strict) { this.strict = strict; }

    public DataStorageOptions getDataStorage()                      { return dataStorage; }
    public void               setDataStorage(DataStorageOptions ds) { this.dataStorage = ds; }

    public SignOptions getSign()                 { return sign; }
    public void        setSign(SignOptions sign) { this.sign = sign; }

    public boolean isBigEndian()                   { return bigEndian; }
    public void    setBigEndian(boolean bigEndian) { this.bigEndian = bigEndian; }
}
