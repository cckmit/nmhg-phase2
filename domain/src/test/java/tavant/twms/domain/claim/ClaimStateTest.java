package tavant.twms.domain.claim;

import junit.framework.TestCase;

public class ClaimStateTest extends TestCase {

    public void testState() {
        assertEquals(ClaimState.DRAFT, ClaimState.valueOf("DRAFT"));
    }
    
}
