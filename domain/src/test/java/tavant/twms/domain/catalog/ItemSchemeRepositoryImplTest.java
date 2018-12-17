package tavant.twms.domain.catalog;

import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeRepository;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class ItemSchemeRepositoryImplTest extends DomainRepositoryTestCase {
    ItemSchemeRepository itemSchemeRepository;
    ItemGroupRepository itemGroupRepository;
    PurposeRepository purposeRepository;
    
    public void setPurposeRepository(PurposeRepository purposeRepository) {
        this.purposeRepository = purposeRepository;
    }

    public void setItemSchemeRepository(ItemSchemeRepository itemSchemeRepository) {
        this.itemSchemeRepository = itemSchemeRepository;
    }
    
    public void testSchemeForProductStructure() {
    	assertNotNull(itemSchemeRepository.findSchemeForPurpose("PRODUCT STRUCTURE"));
    }
    
    public void testCreate() {
        ItemScheme scheme = new ItemScheme("name");
        itemSchemeRepository.save(scheme);
        assertNotNull(scheme.getId());
    }
    
    public void testFindById() {
        ItemScheme scheme = itemSchemeRepository.findById(Long.parseLong("1"));
        assertNotNull(scheme);
    }

    
    public void testFindByPurpose() {
        ItemScheme scheme = itemSchemeRepository.findById(Long.parseLong("1"));
        Purpose aPurpose = purposeRepository.findById(new Long(1));
        scheme.availableFor(aPurpose);
        itemSchemeRepository.update(scheme);
        assertNotNull(scheme.getId());
        flushAndClear();
        
        ItemScheme resultFound = itemSchemeRepository.findSchemeForPurpose(aPurpose);
        assertNotNull(resultFound);
        assertEquals(scheme.getId(),resultFound.getId());        
    }
    
    public void testSaveWithBusinessUnit() {
        ItemScheme scheme = new ItemScheme("name");
        itemSchemeRepository.save(scheme);
        assertNotNull(scheme.getId());
        assertEquals("IR", scheme.getBusinessUnitInfo().getName());
    }
    public void testFindPage_BUFilter()
    {
    	
    	PageResult<ItemScheme> pageResult=itemSchemeRepository.findPage("from ItemScheme itemScheme", getCriteria());
    	assertEquals(3, pageResult.getResult().size());
    }
    
	private ListCriteria getCriteria() {
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(10);
		pageSpecification.setPageNumber(0);
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}

    
    public void testFindEmployedPurposes() {
        assertEquals(4, itemSchemeRepository.findEmployedPurposes().size());
    }
    
    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }
  
}
