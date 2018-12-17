package tavant.twms.infra;

import junit.framework.TestCase;

public class BigDecimalFactoryTest extends TestCase {

    public void testBigDecimalOfDouble() {
        assertEquals("10.23400",BigDecimalFactory.bigDecimalOf(10.234).toString());
        assertEquals("10",BigDecimalFactory.bigDecimalOf(10.0D).toString());
    }
}
