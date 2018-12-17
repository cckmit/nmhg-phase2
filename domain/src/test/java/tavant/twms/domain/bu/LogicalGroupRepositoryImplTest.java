package tavant.twms.domain.bu;

import java.util.List;

import tavant.twms.infra.DomainRepositoryTestCase;

public class LogicalGroupRepositoryImplTest extends DomainRepositoryTestCase {
	
	private LogicalGroupRepository logicalGroupRepository;
	
	public LogicalGroupRepository getLogicalGroupRepository() {
		return logicalGroupRepository;
	}
	
	public void setLogicalGroupRepository(
			LogicalGroupRepository logicalGroupRepository) {
		this.logicalGroupRepository = logicalGroupRepository;
	}

	public void testAllLogicalGroups(){
		List<LogicalGroup> logicalGroups = logicalGroupRepository.findAll();
		assertEquals(true, logicalGroups.size() > 0);
	}

}
