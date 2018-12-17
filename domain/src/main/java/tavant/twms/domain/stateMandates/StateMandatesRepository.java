package tavant.twms.domain.stateMandates;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;


public interface StateMandatesRepository extends GenericRepository<StateMandates, Long> {
	public StateMandates findByName(String state);
	public PageResult<StateMandates> findAll(final ListCriteria criteria);
	public StateMandates findActiveByName(String state);
	public PageResult<StateMandates> findByActive(final ListCriteria criteria);

		
}
