package getthepicture.copybook.wrapper;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CbWrapperTest {

    private static final Charset CP950 = Charset.forName("MS950");

    @Test
    void wrapper_T30_Test() {
        final String before = "11011 00106600000096950000087300020251219000000  0台泥一永        000000000000000000000 0           ";
        final String after  = "2330  00106600000096950000087300020251114000000  0台積電          000000000000000000000X0           ";

        byte[] raw = before.getBytes(CP950);

        T30 t30 = new T30(raw);

        assertEquals("11011",                    t30.getStockNo());
        assertEquals(new BigDecimal("106.6"),  t30.getBullPrice());
        assertEquals(new BigDecimal("96.95"),   t30.getLdcPrice());
        assertEquals(new BigDecimal("87.3"),   t30.getBearPrice());
        assertEquals(LocalDate.of(2025, 12, 19),  t30.getLastMthDate());
        assertEquals("0",  t30.getField("SETTYPE").get());
        assertEquals("0",  t30.getField("MARK-W").get());
        assertEquals("0",  t30.getField("MARK-P").get());
        assertEquals("0",  t30.getField("MARK-L").get());
        assertEquals("00", t30.getField("IND-CODE").get());
        assertEquals("",   t30.getField("IND-SUB-CODE").get());
        assertEquals("0",  t30.getField("MARK-M").get());
        assertEquals("台泥一永", t30.getStockName());
        assertEquals((short) 0,    t30.getField("MATCH-INTERVAL").<Short>get());
        assertEquals(0,    t30.getField("ORDER-LIMIT").<Integer>get());
        assertEquals(0,    t30.getField("ORDERS-LIMIT").<Integer>get());
        assertEquals((short) 0,    t30.getField("PREPAY-RATE").<Short>get());
        assertEquals("0",  t30.getField("MARK-S").get());
        assertEquals("0",  t30.getField("STK-MARK").get());
        assertEquals("0",  t30.getField("MARK-F").get());
        assertEquals("",   t30.getField("MARK-DAY-TRADE").get());
        assertEquals("0",  t30.getField("STK-CTGCD").get());
        assertEquals("",   t30.getField("FILLER").get());

        t30.setStockNo("2330");
        t30.setLastMthDate(LocalDate.of(2025, 11, 14));
        t30.setStockName("台積電");
        t30.getField("MARK-DAY-TRADE").set("X");

        String result = new String(t30.getRaw(), CP950);
        assertEquals(after, result);
    }

    @Test
    void wrapper_Default_Value() {
        T30 t30 = new T30();

        String result = new String(t30.getRaw(), CP950);

        final String expected = "      00000000000000000000000000000000000                         000000000000000000                ";
        assertEquals(expected, result);
    }

    @Test
    void wrapper_Get_Field_Throw_NoSuchElementException() {
        final String s = "11011 00106600000096950000087300020251219000000  0台泥一永        000000000000000000000 0           ";

        byte[] raw = s.getBytes(CP950);
        T30 t30 = new T30(raw);

        assertThrows(NoSuchElementException.class,
            () -> t30.getField("SHOW-ME-THE-MONEY").<Long>get());
    }

    @Test
    void wrapper_Set_Field_Throw_NoSuchElementException() {
        final String s = "11011 00106600000096950000087300020251219000000  0台泥一永        000000000000000000000 0           ";

        byte[] raw = s.getBytes(CP950);
        T30 t30 = new T30(raw);

        assertThrows(NoSuchElementException.class,
            () -> t30.getField("SHOW-ME-THE-MONEY").set(new BigDecimal("100000000")));
    }
}
