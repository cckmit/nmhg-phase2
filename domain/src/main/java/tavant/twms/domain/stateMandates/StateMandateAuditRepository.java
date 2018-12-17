package tavant.twms.domain.stateMandates;

import tavant.twms.infra.GenericRepository;

public interface StateMandateAuditRepository extends
		GenericRepository<StateMandateAudit, Long> {
	public StateMandateAudit find(Long id);
}
