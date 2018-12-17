package tavant.twms.domain.claim;

import java.util.List;

import tavant.twms.infra.GenericService;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;


public interface ClaimNumberPatternService extends GenericService<ClaimNumberPattern,Long,Exception> {
	
	public List<ClaimNumberPattern> findAllPatterns();
	
	public ClaimNumberPattern findActivePattern();
	
	public String generateNextClaimNumber(Claim claim);
	
    public String generateNextRecoveryClaimNumber(RecoveryInfo recoveryInfo);
	
}
