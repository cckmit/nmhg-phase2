package tavant.twms.domain.claim.payment.rates;

import tavant.twms.infra.GenericRepositoryImpl;


public class LaborHistoryRepositoryImpl extends GenericRepositoryImpl<LaborRateAudit, Long> implements LaborHistoryRepository {

	public LaborRateAudit find(Long id) {
		return (LaborRateAudit) getHibernateTemplate().get(LaborRateAudit.class,id);
	 }

}
	
