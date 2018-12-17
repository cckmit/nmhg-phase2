package tavant.twms.domain.stateMandates;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class StateMandatesServiceImpl extends
GenericServiceImpl<StateMandates, Long, Exception> implements StateMandatesService {
	
	private StateMandatesRepository stateMandatesRepository;
	
	@SuppressWarnings("unchecked")
	@Override
	public GenericRepository getRepository() {
		return stateMandatesRepository;
	}
	
	@Required
	public void setStateMandatesRepository(
			StateMandatesRepository stateMandatesRepository) {
		this.stateMandatesRepository = stateMandatesRepository;
	}

	public StateMandates findByName(String state) {
		return stateMandatesRepository.findByName(state);
	}
	
	public StateMandates findActiveByName(String state)
	{
		return stateMandatesRepository.findActiveByName(state);
	}

	public PageResult<StateMandates> findByActive(ListCriteria criteria) {
		return stateMandatesRepository.findByActive(criteria);
	}
	
	public PageResult<StateMandates> findAll(ListCriteria criteria) {
		return stateMandatesRepository.findAll(criteria);
	}


	public void saveStateMandates(StateMandates stateMandates, String comments,User createdBy)
			throws Exception {
		
		StateMandateAudit stateMandateAudit = new StateMandateAudit();
		stateMandateAudit.setCreatedBy(createdBy);
		stateMandateAudit.setState(stateMandates.getState());
		stateMandateAudit.setEffectiveDate(stateMandates.getEffectiveDate());
		stateMandateAudit.setLaborRateType(stateMandates.getLaborRateType());
		stateMandateAudit.setOemPartsPercent(stateMandates.getOemPartsPercent());
		List<StateMndteCostCtgyAudit> costCtgyAuditList = new ArrayList<StateMndteCostCtgyAudit>();
		stateMandateAudit.setComments(comments);
		for(StateMndteCostCtgyMapping costCtgy : stateMandates.getStateMandateCostCatgs()){
			StateMndteCostCtgyAudit costCtgyAudit = new StateMndteCostCtgyAudit();
			costCtgyAudit.setCostCategory(costCtgy.getCostCategory());
			costCtgyAudit.setMandatory(costCtgy.getMandatory());
			costCtgyAudit.setOthers(costCtgy.getOthers());
			costCtgyAudit.setStateMandateAudit(stateMandateAudit);
			costCtgyAuditList.add(costCtgyAudit);
		}
		stateMandateAudit.setStateMandateCostCtgAudit(costCtgyAuditList);
		stateMandates.getStateMandateAudit().add(stateMandateAudit);
		save(stateMandates);
		
	}

	@Transactional(readOnly = false)
	public void updateStateMandates(StateMandates stateMandates,
			String comments,User createdBy) throws Exception {
		StateMandateAudit stateMandateAudit = new StateMandateAudit();
		stateMandateAudit.setCreatedBy(createdBy);
		stateMandateAudit.setState(stateMandates.getState());
		stateMandateAudit.setEffectiveDate(stateMandates.getEffectiveDate());
		stateMandateAudit.setLaborRateType(stateMandates.getLaborRateType());
		stateMandateAudit.setOemPartsPercent(stateMandates.getOemPartsPercent());
		stateMandateAudit.setComments(comments);
		List<StateMndteCostCtgyAudit> costCtgyAuditList = new ArrayList<StateMndteCostCtgyAudit>();
		for(StateMndteCostCtgyMapping costCtgy : stateMandates.getStateMandateCostCatgs()){
			StateMndteCostCtgyAudit costCtgyAudit = new StateMndteCostCtgyAudit();
			costCtgyAudit.setCostCategory(costCtgy.getCostCategory());
			costCtgyAudit.setMandatory(costCtgy.getMandatory());
			costCtgyAudit.setOthers(costCtgy.getOthers());
			costCtgyAudit.setStateMandateAudit(stateMandateAudit);
			costCtgyAuditList.add(costCtgyAudit);
		}
		stateMandateAudit.setStateMandateCostCtgAudit(costCtgyAuditList);
		stateMandates.getStateMandateAudit().add(stateMandateAudit);
		update(stateMandates);
		
	}

}
