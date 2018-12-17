package tavant.twms.domain.orgmodel;

import tavant.twms.domain.common.Purpose;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class UserSchemeRepositoryImplTest extends DomainRepositoryTestCase {
	private UserSchemeRepository userSchemeRepository;
	public void setUserSchemeRepository(UserSchemeRepository userSchemeRepository) {
		this.userSchemeRepository = userSchemeRepository;
	}
	
	public void testFindEmployedPurposes() {
		assertEquals(2, userSchemeRepository.findEmployedPurposes().size());
	}
	
	public void testFindSchemeForPurpose() {
		Purpose purpose = new Purpose();
		purpose.setId(1L);
		UserScheme actual = userSchemeRepository.findSchemeForPurpose(purpose);
		UserScheme expected = userSchemeRepository.findById(1L);
		assertEquals(expected, actual);
	}
	
	public void testCreateScheme() {
		String name = "name";
		UserScheme scheme = new UserScheme(name);
		userSchemeRepository.save(scheme);
		Long id = scheme.getId();
		assertEquals("IR", scheme.getBusinessUnitInfo().getName());
		UserScheme actual = userSchemeRepository.findById(id);
		assertEquals(name, actual.getName());
	}
	
    public void testFindPage_BUFilter()
    {
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(10);
		pageSpecification.setPageNumber(0);
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setPageSpecification(pageSpecification);
    	PageResult<UserScheme> pageResult=userSchemeRepository.findPage("from UserScheme userScheme", listCriteria);
    	assertEquals(2, pageResult.getResult().size());
    }

}
