package tavant.twms.domain.claim.payment.rates;

import tavant.twms.infra.GenericRepository;

public interface PartPriceHistoryRepository extends	GenericRepository<PartPriceAudit, Long> {
	public PartPriceAudit find(Long id); 
}
