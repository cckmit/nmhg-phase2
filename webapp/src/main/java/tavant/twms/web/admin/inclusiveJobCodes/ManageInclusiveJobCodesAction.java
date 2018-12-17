package tavant.twms.web.admin.inclusiveJobCodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.opensymphony.xwork2.Validateable;

@SuppressWarnings("serial")
public class ManageInclusiveJobCodesAction extends SummaryTableAction implements Validateable {
	
	private FailureStructureService failureStructureService;
	
	private ServiceProcedureDefinition serviceProcedureDefinition;	
	
	private boolean createInclusiveJobCode;
	private boolean parentJobCodeExist;

	@Override
	protected PageResult<?> getBody() {
		return failureStructureService.findParentJobCodes(getCriteria());		
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.dcap.search.hidden", "id", 0, "String", "id",
				false, true, true, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.manageInclusiveJobCodes.parentJobCode", "spd.code", 15,
				"String", "code", true, false, false, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.common.description",
				"getServiceProcDefinitionDesc()", 20, 
				"String",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));		
		return tableHeadData;
	}
    
	public String detail() {
		if (getId() != null) 
			serviceProcedureDefinition = failureStructureService.findServiceProcedureDefinitionById(new Long(id));
		return SUCCESS;		 
	}
	
	public String listJobCodes() {
		List<ServiceProcedureDefinition> serviceProcDef = failureStructureService
				.findServiceProcedureDefinitionWhoseCodeStartsWith(
						getSearchPrefix(), 0, 10);
		return generateAndWriteComboboxJson(serviceProcDef, "id", "code");	
	}
	
	public String getDescriptionForServiceProcedureDefinition()
			throws JSONException {
		JSONArray details = new JSONArray();
		if(serviceProcedureDefinition != null && serviceProcedureDefinition.getId() != null)
		  serviceProcedureDefinition = failureStructureService.findServiceProcedureDefinitionById(serviceProcedureDefinition.getId());
		details.put(serviceProcedureDefinition.getServiceProcDefinitionDesc());
		jsonString = details.toString();
		return SUCCESS;
	}	
	
	public String save(){		
			failureStructureService
					.updateServiceProcedureDefinition(getServiceProcedureDefinition());
			if(createInclusiveJobCode)
			  addActionMessage("message.manageInclusiveJobCodes.createdServProDef",getServiceProcedureDefinition().getCode());
			else
			  addActionMessage("message.manageInclusiveJobCodes.updatedServProDef",getServiceProcedureDefinition().getCode());	
			return SUCCESS;		
	}
	
	@Override
	public void validate() {
		if (serviceProcedureDefinition == null
				|| serviceProcedureDefinition.getCode() == null)
			addActionError("error.manageInclusiveJobCodes.parentJobCodeMandatory");
		else {
			//Check parent job code is already exit			
			List<ServiceProcedureDefinition> parentJobCodes = failureStructureService.findExistParentJobCodes();			
			if(isCreateInclusiveJobCode() && parentJobCodes.contains(serviceProcedureDefinition)){
				addActionError("error.manageInclusiveJobCodes.parentJobCodeExist");
				parentJobCodeExist = true;
			}
			if (serviceProcedureDefinition.getChildJobs().isEmpty())
				addActionError("error.manageInclusiveJobCodes.childJobCodeMandatory");
			else {				
				for (ServiceProcedureDefinition childJobCode : serviceProcedureDefinition.getChildJobs()) {
					if (null != childJobCode && null == childJobCode.getId()) {
						//serviceProcedureDefinition.getChildJobs().clear();
						addActionError("error.manageInclusiveJobCodes.emptyChildJobCodes");
						break;
					}
				 }				
				// If parent and child job codes are same
				if (serviceProcedureDefinition.getChildJobs().contains(serviceProcedureDefinition))
					addActionError("error.manageInclusiveJobCodes.differentParentandchildJobCodes");
				// Check duplicate child job codes
				HashSet<ServiceProcedureDefinition> duplicateJobCodes = new HashSet<ServiceProcedureDefinition>();
				for (int i = 0; i < serviceProcedureDefinition.getChildJobs().size(); i++) {
				  if(serviceProcedureDefinition.getChildJobs().get(i).getId() != null){	
					if (!duplicateJobCodes.add(serviceProcedureDefinition.getChildJobs().get(i))){
						addActionError("error.manageInclusiveJobCodes.duplicateChildJobCodes");
					    break;
					}
				  }
				}
			}
		}
	}

	public FailureStructureService getFailureStructureService() {
		return failureStructureService;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public ServiceProcedureDefinition getServiceProcedureDefinition() {
		return serviceProcedureDefinition;
	}

	public void setServiceProcedureDefinition(
			ServiceProcedureDefinition serviceProcedureDefinition) {
		this.serviceProcedureDefinition = serviceProcedureDefinition;
	}

	public boolean isCreateInclusiveJobCode() {
		return createInclusiveJobCode;
	}

	public void setCreateInclusiveJobCode(boolean createInclusiveJobCode) {
		this.createInclusiveJobCode = createInclusiveJobCode;
	}

	public boolean isParentJobCodeExist() {
		return parentJobCodeExist;
	}

	public void setParentJobCodeExist(boolean parentJobCodeExist) {
		this.parentJobCodeExist = parentJobCodeExist;
	}	
}
