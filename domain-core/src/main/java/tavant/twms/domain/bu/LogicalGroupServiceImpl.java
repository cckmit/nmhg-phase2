package tavant.twms.domain.bu;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


public class LogicalGroupServiceImpl implements LogicalGroupService {
	
	private LogicalGroupRepository logicalGroupRepository;

	public void setLogicalGroupRepository(LogicalGroupRepository logicalGroupRepository) {
		this.logicalGroupRepository = logicalGroupRepository;
	}
	
	public LogicalGroupRepository getRepository() {
		return logicalGroupRepository;
	}
	
	@Transactional
	public List<LogicalGroup> findAll(){
		return logicalGroupRepository.findAll();
	}

}
