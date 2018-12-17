package tavant.twms.domain.orgmodel;

import tavant.twms.domain.catalog.ItemScheme;
import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeRepository;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class DealerSchemeRepositoryImplTest extends DomainRepositoryTestCase {
	DealerSchemeRepository dealerSchemeRepository;
	DealerGroupRepository dealerGroupRepository;
	PurposeRepository purposeRepository;

	public void setPurposeRepository(PurposeRepository purposeRepository) {
		this.purposeRepository = purposeRepository;
	}

	public void testCreate() {
		DealerScheme scheme = new DealerScheme("something");
		dealerSchemeRepository.save(scheme);
		assertNotNull(scheme.getId());
	}

	public void testFindByPurpose() {
		DealerScheme scheme = dealerSchemeRepository.findById(Long
				.parseLong("1"));
		Purpose aPurpose = purposeRepository.findById(new Long(1));
		DealerScheme resultFound = dealerSchemeRepository
				.findSchemeForPurpose(aPurpose);
		assertNotNull(resultFound);
		assertEquals(scheme.getId(), resultFound.getId());
	}
	
	public void testSave_BU()
	{
		DealerScheme scheme = new DealerScheme("something");
		dealerSchemeRepository.save(scheme);
		assertNotNull(scheme.getId());
		assertEquals("IR", scheme.getBusinessUnitInfo().getName());
	}
    public void testFindPage_BUFilter()
    {
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(10);
		pageSpecification.setPageNumber(0);
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setPageSpecification(pageSpecification);
    	PageResult<DealerScheme> pageResult=dealerSchemeRepository.findPage("from DealerScheme dealerScheme", listCriteria);
    	assertEquals(2, pageResult.getResult().size());
    }

	public void testFindEmployedPurposes() {
		assertEquals(4, dealerSchemeRepository.findEmployedPurposes().size());
	}

	public void testFindSchemeForGroup() {
		DealerGroup group = dealerGroupRepository.findById(2L);
		assertNotNull(group);
		assertTrue(3L == group.getScheme().getId());
	}

	public void setDealerGroupRepository(
			DealerGroupRepository dealerGroupRepository) {
		this.dealerGroupRepository = dealerGroupRepository;
	}

	public void setDealerSchemeRepository(
			DealerSchemeRepository dealerSchemeRepository) {
		this.dealerSchemeRepository = dealerSchemeRepository;
	}
}
