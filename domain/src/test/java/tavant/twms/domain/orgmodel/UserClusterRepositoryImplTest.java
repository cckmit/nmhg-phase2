package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeRepository;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class UserClusterRepositoryImplTest extends DomainRepositoryTestCase {
	private UserClusterRepository userClusterRepository;
	private UserSchemeRepository userSchemeRepository;
	private PurposeRepository purposeRepository;
	private UserScheme scheme;
	private Purpose purpose;
	
	@Override
	protected void setUpInTxnRollbackOnFailure() throws Exception {
		super.setUpInTxnRollbackOnFailure();
		scheme = userSchemeRepository.findById(1L);
		purpose = purposeRepository.findById(1L);
	}
	
	public void testFindByNameAndPurpose() {
		UserCluster cluster = userClusterRepository.findByNameAndPurpose("Test Cluster1", purpose.getName());
		assertNotNull(cluster);
		assertEquals(new Long("2"), cluster.getId());
		cluster = userClusterRepository.findByNameAndPurpose("Non Existing", purpose.getName());
		assertNull(cluster);
	}
	
	public void testFindClusterContainingUser() {
		User user = new User();
		user.setId(1L);
		UserCluster cluster = userClusterRepository.findClusterContainingUser(user, scheme);
		assertNotNull(cluster);
		assertEquals(new Long("3"), cluster.getId());
		user = new User();
		user.setId(4L);
		cluster = userClusterRepository.findClusterContainingUser(user, scheme);
		assertNull(cluster);
	}
	
	public void testFindClustersByNameAndDescription() {
		assertEquals(3, userClusterRepository.findClustersByNameAndDescription(scheme, "", "").size());
		assertEquals(3, userClusterRepository.findClustersByNameAndDescription(scheme, "T", "").size());
		assertEquals(0, userClusterRepository.findClustersByNameAndDescription(scheme, "sdffdd", "").size());
	}
	
	public void testGetUserClusterByName() {
		UserCluster cluster = userClusterRepository.findUserClusterByName("Test Cluster1", scheme);
		assertNotNull(cluster);
		assertEquals(new Long("2"), cluster.getId());
		cluster = userClusterRepository.findUserClusterByName("Test Cluster231", scheme);
		assertNull(cluster);
	}
	
	public void testGetClustersForScheme() {
		List<UserCluster> clusters = userClusterRepository.findUserClustersFromScheme(scheme);
		assertEquals(3, clusters.size());
	}

	public void testFindUserClustersByPurpose() {
		List<UserCluster> clusters = userClusterRepository.findUserClustersByPurpose(purpose.getName());
		assertNotNull(clusters);
		assertEquals(3, clusters.size());
	}
		
	public void testGetPage() {
		ListCriteria listCriteria = new ListCriteria();
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageNumber(-1);
        pageSpecification.setPageSize(7);
        listCriteria.setPageSpecification(pageSpecification);
        PageResult<UserCluster> page = userClusterRepository.findPage(listCriteria, scheme);
        List<UserCluster> clusters = page.getResult();
        assertEquals(3, clusters.size());
	}

	public void setUserSchemeRepository(UserSchemeRepository userSchemeRepository) {
		this.userSchemeRepository = userSchemeRepository;
	}

	public void setUserClusterRepository(UserClusterRepository userClusterRepository) {
		this.userClusterRepository = userClusterRepository;
	}
	
	public void setPurposeRepository(PurposeRepository purposeRepository) {
		this.purposeRepository = purposeRepository;
	}
	
	
}
