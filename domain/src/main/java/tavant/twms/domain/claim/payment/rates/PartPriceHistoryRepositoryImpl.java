package tavant.twms.domain.claim.payment.rates;

import tavant.twms.infra.GenericRepositoryImpl;


public class PartPriceHistoryRepositoryImpl extends GenericRepositoryImpl<PartPriceAudit, Long>implements PartPriceHistoryRepository {
	public PartPriceAudit find(Long id) {
		return (PartPriceAudit) getHibernateTemplate().get(PartPriceAudit.class,id);
	 }
}
