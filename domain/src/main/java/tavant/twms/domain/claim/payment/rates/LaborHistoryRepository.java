package tavant.twms.domain.claim.payment.rates;

import tavant.twms.infra.GenericRepository;

public interface LaborHistoryRepository extends GenericRepository<LaborRateAudit,Long>{
	
	public LaborRateAudit find(Long id);   

}
