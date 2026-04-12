package getthepicture.picture.core.meta;

import getthepicture.picture.core.clause.computational.COMP3;
import getthepicture.picture.core.clause.items.PicClauseBaseClass;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;


/**
 * COBOL PICTURE Metadata Class
 */
public class PictureMeta {

    private String raw;
    private PicClauseBaseClass baseClass;
    private PicClauseSemantic  semantic;
    private PicClauseUsage     usage = PicClauseUsage.DISPLAY;

    private boolean signed = false;
    /** 字串長度/整數位數 */
    private int integerDigits = 0;
    /** 小數位數 */
    private int decimalDigits = 0;

    /** COBOL-PIC 宣告資料的長度 */
    public int getDigitCount() {
        return integerDigits + decimalDigits;
    }

    /** COBOL-PIC 記憶體佔用資料的長度 */
    public int getStorageOccupied() {
        return switch (usage) {
            case DISPLAY          -> getDigitCount();
            case PACKED_DECIMAL   -> COMP3.getByteLength(getDigitCount());
            // case BINARY,
            //      NATIVE_BINARY    -> COMP5.getByteLength(getDigitCount());
            // case U_PACKED_DECIMAL -> COMP6.getByteLength(getDigitCount());
            default -> throw new UnsupportedOperationException(
                "Unsupported usage: " + usage);
        };
    }

    @Override
    public String toString() {
        return String.format(
            "[%s] Class='%s' (Semantic='%s'), Signed=%b, Int=%d, Dec=%d, Len=%d, Usage='%s'",
            raw, baseClass, semantic, signed, integerDigits, decimalDigits, getDigitCount(), usage);
    }

    public static PictureMeta parse(String input) {
        return PictureMetaBuilder.parse(input, PicClauseSemantic.NONE, PicClauseUsage.DISPLAY);
    }

    public static PictureMeta parse(String input, PicClauseSemantic semantic) {
        return PictureMetaBuilder.parse(input, semantic, PicClauseUsage.DISPLAY);
    }

    public static PictureMeta parse(String input, PicClauseSemantic semantic, PicClauseUsage usage) {
        return PictureMetaBuilder.parse(input, semantic, usage);
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public String getRaw()           { return raw; }
    public void   setRaw(String raw) { this.raw = raw; }

    public PicClauseBaseClass getBaseClass() { return baseClass; }
    public void               setBaseClass(PicClauseBaseClass baseClass) { this.baseClass = baseClass; }

    public PicClauseSemantic getSemantic() { return semantic; }
    public void              setSemantic(PicClauseSemantic semantic){ this.semantic = semantic; }

    public PicClauseUsage getUsage()                     { return usage; }
    public void           setUsage(PicClauseUsage usage) { this.usage = usage; }

    public boolean isSigned()                { return signed; }
    public void    setSigned(boolean signed) { this.signed = signed; }

    public int  getIntegerDigits()      { return integerDigits; }
    public void setIntegerDigits(int v) { this.integerDigits = v; }

    public int  getDecimalDigits()      { return decimalDigits; }
    public void setDecimalDigits(int v) { this.decimalDigits = v; }
}
