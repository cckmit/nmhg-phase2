package tavant.twms.infra;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import junit.framework.TestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class DefaultTraversableTypeIdentifierTest extends TestCase {

	public void testIsTraversable() {
		DefaultTraversableTypeIdentifier fixture = new DefaultTraversableTypeIdentifier();
		fixture.setRichPrimitives(Arrays.asList(new String[]{"com.domainlanguage.time.CalendarDate","com.domainlanguage.money.Money"}));
		assertFalse(fixture.isTraversable( Integer.class ));
		assertFalse(fixture.isTraversable( Long.class ));
		assertFalse(fixture.isTraversable( Float.class ));
		assertFalse(fixture.isTraversable( Double.class ));
		assertFalse(fixture.isTraversable( BigDecimal.class ));
		assertFalse(fixture.isTraversable( BigInteger.class ));
		assertFalse(fixture.isTraversable( CalendarDate.class));
		assertFalse(fixture.isTraversable( Money.class));
		assertFalse(fixture.isTraversable( Object.class));
		assertFalse(fixture.isTraversable( WeakReference.class));
	}

}
