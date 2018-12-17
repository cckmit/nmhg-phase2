package tavant.twms.domain.partreturn;

import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface WpraNumberPatternRepository extends GenericRepository<WpraNumberPattern,Long> {

	public List<WpraNumberPattern> findAllWpraNumberPatterns();
	
	public WpraNumberPattern findActivePattern();
		
	public void update(WpraNumberPattern wpraPatternToBeUpdated);
	
	public String getNextGeneratedWpraNumber(String sequenceName);

	public void resetSequenceName(String sequenceName);
	
	
}
