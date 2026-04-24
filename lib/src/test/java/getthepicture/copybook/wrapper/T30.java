package getthepicture.copybook.wrapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import getthepicture.copybook.wrapper.core.CbAddress;
import getthepicture.picture.core.clause.items.PicClauseSemantic;

public class T30 extends CbWrapper {

    // ----------------------------
    // Copybook Address Map
    // ----------------------------

    private static final Map<String, CbAddress> ADDRESS_MAP = new LinkedHashMap<>();

    static {
        ADDRESS_MAP.put("STOCK-NO",       new CbAddress( 1,  6, "X(6)"));
        ADDRESS_MAP.put("BULL-PRICE",     new CbAddress( 7,  9, "9(5)V9(4)"));
        ADDRESS_MAP.put("LDC-PRICE",      new CbAddress(16,  9, "9(5)V9(4)"));
        ADDRESS_MAP.put("BEAR-PRICE",     new CbAddress(25,  9, "9(5)V9(4)"));
        ADDRESS_MAP.put("LAST-MTH-DATE",  new CbAddress(34,  8, "9(8)", PicClauseSemantic.GREGORIAN_DATE));
        ADDRESS_MAP.put("SETTYPE",        new CbAddress(42,  1, "X(01)"));
        ADDRESS_MAP.put("MARK-W",         new CbAddress(43,  1, "X(01)"));
        ADDRESS_MAP.put("MARK-P",         new CbAddress(44,  1, "X(01)"));
        ADDRESS_MAP.put("MARK-L",         new CbAddress(45,  1, "X(01)"));
        ADDRESS_MAP.put("IND-CODE",       new CbAddress(46,  2, "X(02)"));
        ADDRESS_MAP.put("IND-SUB-CODE",   new CbAddress(48,  2, "X(02)"));
        ADDRESS_MAP.put("MARK-M",         new CbAddress(50,  1, "X(01)"));
        ADDRESS_MAP.put("STOCK-NAME",     new CbAddress(51, 16, "X(16)"));
        // MARK-W
        ADDRESS_MAP.put("MATCH-INTERVAL", new CbAddress(67,  3, "9(03)"));
        ADDRESS_MAP.put("ORDER-LIMIT",    new CbAddress(70,  6, "9(06)"));
        ADDRESS_MAP.put("ORDERS-LIMIT",   new CbAddress(76,  6, "9(06)"));
        ADDRESS_MAP.put("PREPAY-RATE",    new CbAddress(82,  3, "9(03)"));
        ADDRESS_MAP.put("MARK-S",         new CbAddress(85,  1, "X(01)"));
        ADDRESS_MAP.put("STK-MARK",       new CbAddress(86,  1, "X(01)"));
        ADDRESS_MAP.put("MARK-F",         new CbAddress(87,  1, "X(01)"));
        ADDRESS_MAP.put("MARK-DAY-TRADE", new CbAddress(88,  1, "X(01)"));
        ADDRESS_MAP.put("STK-CTGCD",      new CbAddress(89,  1, "X(01)"));
        ADDRESS_MAP.put("FILLER",         new CbAddress(90, 11, "X(11)"));
    }

    @Override
    protected Map<String, CbAddress> getAddressMap() {
        return ADDRESS_MAP;
    }

    // ----------------------------
    // Constructors
    // ----------------------------

    public T30() {
        super();
    }

    public T30(byte[] raw) {
        super(raw);
    }

    // ----------------------------
    // 強型別屬性
    // ----------------------------

    public String getStockNo()               { return field("STOCK-NO").get(); }
    public void   setStockNo(String v)       { field("STOCK-NO").set(v); }

    public BigDecimal getBullPrice()             { return field("BULL-PRICE").get(); }
    public void       setBullPrice(BigDecimal v) { field("BULL-PRICE").set(v); }

    public BigDecimal getLdcPrice()              { return field("LDC-PRICE").get(); }
    public void       setLdcPrice(BigDecimal v)  { field("LDC-PRICE").set(v); }

    public BigDecimal getBearPrice()             { return field("BEAR-PRICE").get(); }
    public void       setBearPrice(BigDecimal v) { field("BEAR-PRICE").set(v); }

    public LocalDate getLastMthDate()            { return field("LAST-MTH-DATE").get(); }
    public void      setLastMthDate(LocalDate v) { field("LAST-MTH-DATE").set(v); }

    // ...

    public String getStockName()             { return field("STOCK-NAME").get(); }
    public void   setStockName(String v)     { field("STOCK-NAME").set(v); }

    // ...
}
