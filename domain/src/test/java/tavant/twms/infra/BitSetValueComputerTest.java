package tavant.twms.infra;

import junit.framework.TestCase;

public class BitSetValueComputerTest extends TestCase {

	public void testValue() {
		BitSetValueComputer fixture = new BitSetValueComputer();
		assertEquals(0L,fixture.compute(new boolean[]{}));
		
				
		fixture = new BitSetValueComputer();
		assertEquals(0L,fixture.compute(new boolean[]{false}));
		
		assertEquals(1L,fixture.compute(new boolean[]{true}));
		
		assertEquals(2L,fixture.compute(new boolean[]{true,false}));
		
		assertEquals(3L,fixture.compute(new boolean[]{true,true}));
		
		assertEquals(4L,fixture.compute(new boolean[]{true,false,false}));
		
		assertEquals(5L,fixture.compute(new boolean[]{true,false,true}));
		
		assertEquals(6L,fixture.compute(new boolean[]{true,true,false}));
		
		assertEquals(7L,fixture.compute(new boolean[]{true,true,true}));
	}
}
