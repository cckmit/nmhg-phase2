package tavant.twms.domain.common;

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.domainlanguage.money.Money;

public class DistanceRateTest extends TestCase {

    public void testCostFor() {
        DistanceRate fixture = new DistanceRate();
        fixture.setRate(Money.dollars(10));
        fixture.setBenchmarkDistance(Distance._10_Miles());
        
        BigDecimal expetedValue = fixture.costFor( Distance.valueOf(100, DistanceUnit.mile) ).breachEncapsulationOfAmount();
        assertEquals( BigDecimal.valueOf(100).doubleValue(), expetedValue.doubleValue() );
        
        expetedValue = fixture.costFor( Distance.valueOf(1, DistanceUnit.mile) ).breachEncapsulationOfAmount();
        assertEquals( BigDecimal.valueOf(1).doubleValue(), expetedValue.doubleValue() );
        
        expetedValue = fixture.costFor( Distance.valueOf(100, DistanceUnit.kilometer) ).breachEncapsulationOfAmount();
        assertEquals( BigDecimal.valueOf(62.2).doubleValue(), expetedValue.doubleValue() );        
    }

}
