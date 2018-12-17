package tavant.twms.domain.rules;

import java.util.List;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class DomainPredicateRepositoryImplTest extends DomainRepositoryTestCase {
    private DomainPredicateRepository domainPredicateRepository;
    
    /**
     * @param domainPredicateRepository the domainPredicateRepository to set
     */
    public void setDomainPredicateRepository(DomainPredicateRepository domainPredicateRepository) {
        this.domainPredicateRepository = domainPredicateRepository;
    }


    public void testFindByNameForNonExistentPredicateName() throws Exception {
        List<DomainPredicate> list = domainPredicateRepository.findByNameInContext("Foo Bar","PolicyRules");
        assertEquals(0, list.size());
    }

    public void testFindByNameForExistingPredicateName() throws Exception {
        DomainPredicate domainPredicate = new DomainPredicate();
        domainPredicate.setContext("ClaimProcessingRules");
        domainPredicate.setName("Travel details not set.");
        
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable(Claim.class,"claim.serviceInformation.serviceDetail.travelDetails",BusinessObjectModelFactory.CLAIM_RULES);
        IsNotSet isNotSet = new IsNotSet(domainSpecificVariable);
        domainPredicate.setPredicate(isNotSet);
        
        domainPredicateRepository.save(domainPredicate);
        assertNotNull(domainPredicate.getId());
        
        List<DomainPredicate> findByNameInContext = domainPredicateRepository.findByNameInContext("details","ClaimProcessingRules");
        assertEquals(1,findByNameInContext.size());
        assertEquals("Travel details not set.",findByNameInContext.get(0).getName());
    }
    
    public void testSave() {
    	DomainPredicate domainPredicate = new DomainPredicate();
    	domainPredicate.setContext("someContext");
    	domainPredicate.setName("someName");
    	domainPredicate.setPredicate(new Equals(new Constant("Java",Type.STRING),new Constant("Java 1.5",Type.STRING)));
    	domainPredicateRepository.save(domainPredicate);
    	assertNotNull(domainPredicate.getId());
    	String predicateAsXML = domainPredicate.getPredicateAsXML();
		assertNotNull(predicateAsXML);
		
		flushAndClear();
    }    
    
    public void testUpdate() {
    	DomainPredicate domainPredicate = new DomainPredicate();
    	domainPredicate.setContext("someContext");
    	domainPredicate.setName("someName");
    	domainPredicate.setPredicate(new Equals(new Constant("Java",Type.STRING),new Constant("Java 1.5",Type.STRING)));
    	domainPredicateRepository.save(domainPredicate);
    	assertNotNull(domainPredicate.getId());
    	String predicateAsXML = domainPredicate.getPredicateAsXML();
		assertNotNull(predicateAsXML);
		
		domainPredicate.setPredicate(new Equals(new Constant("Java 1.5",Type.STRING),new Constant("Java 1.5",Type.STRING)));
		domainPredicateRepository.update(domainPredicate);
		assertNotSame(predicateAsXML,domainPredicate.getPredicateAsXML());
    }
    
    public void testFindAllNonSearchPredicates_BUFilter()
    {
    	PageSpecification pageSpecification = new PageSpecification();
    	pageSpecification.setPageNumber(0);
    	pageSpecification.setPageSize(10);
//    	PageResult<DomainPredicate> pageResult = domainPredicateRepository.findAllNonSearchPredicates(pageSpecification);
//    	assertEquals(1, pageResult.getResult().size());
    }
}
