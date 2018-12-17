package tavant.twms.domain.claim;

import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface ClaimNumberPatternRepository extends GenericRepository<ClaimNumberPattern,Long> {

	public List<ClaimNumberPattern> findAllClaimNumberPatterns();
	
	public ClaimNumberPattern findActivePattern();
		
	public void update(ClaimNumberPattern claimPatternToBeUpdated);
	
	public String getNextGeneratedClaimNumber(String sequenceName, String prefix);

	public void resetSequenceName(String sequenceName);
	
	
}
