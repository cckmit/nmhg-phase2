package tavant.twms.domain.partreturn;


import java.util.List;

import tavant.twms.infra.GenericService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;


public interface WpraNumberPatternService extends GenericService<WpraNumberPattern,Long,Exception> {
	
	public List<WpraNumberPattern> findAllPatterns();
	
	public WpraNumberPattern findActivePattern();
	
	public String generateNextWpraNumber(Claim claim,Wpra wpra);
	
}

