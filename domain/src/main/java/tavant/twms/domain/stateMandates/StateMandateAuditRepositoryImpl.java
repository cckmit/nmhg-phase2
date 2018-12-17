package tavant.twms.domain.stateMandates;

import tavant.twms.infra.GenericRepositoryImpl;

public class StateMandateAuditRepositoryImpl extends
		GenericRepositoryImpl<StateMandateAudit, Long> implements
		StateMandateAuditRepository {

	public StateMandateAudit find(Long id) {
		return (StateMandateAudit) getHibernateTemplate().get(
				StateMandateAudit.class, id);
	}

}
