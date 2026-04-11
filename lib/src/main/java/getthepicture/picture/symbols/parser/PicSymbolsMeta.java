package getthepicture.picture.symbols.parser;

import getthepicture.picture.clause.Items.PicClauseBaseClass;

public final class PicSymbolsMeta {
    private PicClauseBaseClass baseClass;
    private boolean signed;
    /** 字串長度／整數位數 */
    private int integerDigits;
    /** 小數位數 */
    private int decimalDigits;

    public PicSymbolsMeta() {
        this(PicClauseBaseClass.UNKNOWN, false, 0, 0);
    }

    public PicSymbolsMeta(PicClauseBaseClass baseClass, boolean signed, int integerDigits, int decimalDigits) {
        this.baseClass     = baseClass;
        this.signed        = signed;
        this.integerDigits = integerDigits;
        this.decimalDigits = decimalDigits;
    }

    public PicClauseBaseClass getBaseClass() { return baseClass; }
    public void               setBaseClass(PicClauseBaseClass baseClass) { this.baseClass = baseClass; }

    public boolean isSigned()                { return signed; }
    public void    setSigned(boolean signed) { this.signed = signed; }

    /** 字串長度／整數位數 */
    public int  getIntegerDigits()                 { return integerDigits; }
    public void setIntegerDigits(int integerDigits){ this.integerDigits = integerDigits; }
    public void addIntegerDigits(int n)            { this.integerDigits += n; }

    /** 小數位數 */
    public int  getDecimalDigits()                 { return decimalDigits; }
    public void setDecimalDigits(int decimalDigits){ this.decimalDigits = decimalDigits; }
    public void addDecimalDigits(int n)            { this.decimalDigits += n; }
}