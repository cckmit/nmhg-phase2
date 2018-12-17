package tavant.twms.domain.bu;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface LogicalGroupService {
	
	@Transactional
	public List<LogicalGroup> findAll();

}
