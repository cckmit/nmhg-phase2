package tavant.twms.domain.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class BasicCriterionResolverTest extends TestCase {

    public List<DummyClassWithCriteria> objects;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.objects = new ArrayList<DummyClassWithCriteria>();
    }

    @SuppressWarnings("unchecked")
    public void testWithObjectHavingClaimAndWarrantyTypesSet() {
        addObjectWithClaimTypeAndWarrantyType("Machine", "WT");
        addObjectWithClaimTypeAndWarrantyType("Machine", null);
        addObjectWithClaimTypeAndWarrantyType(null, "WT");
        addObjectWithClaimTypeAndWarrantyType(null, null);
        List matches = BasicCriterionResolver.findMostRelevantMatch(this.objects, "forCriteria");
        assertEquals(1, matches.size());
        assertNotNull(((DummyClassWithCriteria) matches.get(0)).getForCriteria().getClaimType());
        assertNotNull(((DummyClassWithCriteria) matches.get(0)).getForCriteria().getWarrantyType());
    }

    @SuppressWarnings("unchecked")
    public void testWithObjectEachHavingClaimTypeAndWarrantyTypeSet() {
        addObjectWithClaimTypeAndWarrantyType("Machine", null);
        addObjectWithClaimTypeAndWarrantyType(null, "WT");
        addObjectWithClaimTypeAndWarrantyType(null, null);
        List matches = BasicCriterionResolver.findMostRelevantMatch(this.objects, "forCriteria");
        assertEquals(1, matches.size());
        assertNotNull(((DummyClassWithCriteria) matches.get(0)).getForCriteria().getClaimType());
        assertNull(((DummyClassWithCriteria) matches.get(0)).getForCriteria().getWarrantyType());
    }

    @SuppressWarnings("unchecked")
    public void testWithObjectHavingNothingSet() {
        addObjectWithClaimTypeAndWarrantyType(null, null);
        addObjectWithClaimTypeAndWarrantyType(null, null);
        List matches = BasicCriterionResolver.findMostRelevantMatch(this.objects, "forCriteria");
        assertEquals(2, matches.size());
        for (Object match : matches) {
            assertNull(((DummyClassWithCriteria) match).getForCriteria().getClaimType());
            assertNull(((DummyClassWithCriteria) match).getForCriteria().getWarrantyType());
        }

    }

    private void addObjectWithClaimTypeAndWarrantyType(String claimType, String warrantyType) {
        DummyClassWithCriteria object = new DummyClassWithCriteria();
        object.getForCriteria().setClaimType(claimType);
        object.getForCriteria().setWarrantyType(warrantyType);
        this.objects.add(object);
    }

}
