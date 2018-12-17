package tavant.twms.web.partsreturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

public class WpraSearchForPartsInboxAction extends PartReturnInboxAction{
	
	String wpraNumber;
	
	private String taskName;
	
	private Map partReturnFields;
	
	private String inboxViewType = null;
	
	private static final Logger logger = Logger
	.getLogger(WpraSearchForPartsInboxAction.class);

	public WpraSearchForPartsInboxAction() {
	setActionUrl("viewQuickWpraSearchDetail");
	}
	
	@Override
    protected PartReturnWorkList getWorkList() {
         return getPartReturnWorkListService().getPartReturnWorkListByWpraNumber(createCriteria(), wpraNumber);
    }
	
    @Override
    @SuppressWarnings("unchecked")
    protected PageResult<?> getBody() {
        logger.debug("Fetching task list for folderName [" + getFolderName()
                + "]");
        PartReturnWorkList partReturnWorkList = getWorkList();
        List partReturnTaskList = partReturnWorkList.getPartReturnTaskItem();
        return new PageResult(partReturnTaskList, new PageSpecification(this.page,
                this.pageSize, partReturnWorkList.getTaskItemCount()), getTotalNumberOfPages(partReturnWorkList
                .getTaskItemCount()));
    }

    @Override
	public void validate() {
    	if(wpraNumber == null || !StringUtils.hasText(wpraNumber)){
    		addActionError("error.search.emptyWpraNumber");
    	}
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<SummaryTableColumn> getHeader() {
        Assert
                .state(this.partReturnFields != null,
                        "Column definitions using SummaryTableColumn are not being configured");
        Assert.hasText(this.taskName,
                "Task name hasn't been set for getting column definitions");
        Assert.state(this.partReturnFields.containsKey(this.taskName+(inboxViewType==null ? "" :"_"+inboxViewType)),
                "The configured column definitions ["
                        + this.partReturnFields.keySet()
                        + "] doesnt have a key for [" + this.taskName + "]");
        List<SummaryTableColumn> columnList = (List<SummaryTableColumn>) this.partReturnFields.get(
    			this.taskName + (inboxViewType==null ? "" :"_"+inboxViewType));
        if((columnList!= null && columnList.size() > 0) &&
        		(getLoggedInUser().hasRole(Role.RECEIVER_LIMITED_VIEW) ||
        		getLoggedInUser().hasRole(Role.INSPECTOR_LIMITED_VIEW))){
        	columnList = removeDealerColumnForLimitedView(columnList);
        }
        //addLoadDimensionsForView(columnList);
        return columnList;
    }
    
    private List<SummaryTableColumn> removeDealerColumnForLimitedView(List<SummaryTableColumn> columnList){
		List<SummaryTableColumn> tmpColumnList = new ArrayList<SummaryTableColumn>();

		for(SummaryTableColumn column : columnList){

			if(!"claim.forDealer.name".equalsIgnoreCase(column.getExpression())){
				tmpColumnList.add(column);
			}
		}
		return tmpColumnList;
	}

	public String getWpraNumber() {
		return wpraNumber;
	}

	public void setWpraNumber(String wpraNumber) {
		this.wpraNumber = wpraNumber;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
		super.setTaskName(taskName);
	}

	public Map getPartReturnFields() {
		return partReturnFields;
	}

	public void setPartReturnFields(Map partReturnFields) {
		this.partReturnFields = partReturnFields;
	}

	public String getInboxViewType() {
		return inboxViewType;
	}

	public void setInboxViewType(String inboxViewType) {
		this.inboxViewType = inboxViewType;
	}
	
	String sample(){
		return SUCCESS;
	}
}
