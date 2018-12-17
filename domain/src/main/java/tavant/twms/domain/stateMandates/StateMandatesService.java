package tavant.twms.domain.stateMandates;


import org.springframework.transaction.annotation.Transactional;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface StateMandatesService extends
GenericService<StateMandates, Long, Exception> {
	public StateMandates findByName(String state);
	public PageResult<StateMandates> findAll(ListCriteria criteria);
	public StateMandates findActiveByName(String state);
	public PageResult<StateMandates> findByActive(ListCriteria criteria);
	
    @Transactional(readOnly=false)
    public void saveStateMandates(StateMandates stateMandates,String comments,User createdBy) throws Exception;
    
    @Transactional(readOnly=false)
	public void updateStateMandates(StateMandates stateMandates,
			String comments, User createdBy) throws Exception;
	
}
