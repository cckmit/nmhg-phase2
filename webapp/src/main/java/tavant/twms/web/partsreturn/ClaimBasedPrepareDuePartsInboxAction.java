package tavant.twms.web.partsreturn;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.infra.BeanProvider;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 30/11/12
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClaimBasedPrepareDuePartsInboxAction extends PartReturnInboxAction{

    private String claimId;

    private List<Long> selectedTaskInstanceIds = new ArrayList<Long>();

    private PartReturnProcessingService partReturnProcessingService;

   public ClaimBasedPrepareDuePartsInboxAction() {
		// TODO : Check if this is required.
		setActionUrl("claimBased_dealerPrepareDuePartInbox");
	}


    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.partReturnConfiguration.claimNo",
      		  "claim.claimNumber", 16, "string"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.partReturnConfiguration.claimNo", "claim.id", 17,
                "number", "id", true, true, true, false));
        return tableHeadData;
    }

	public BeanProvider getBeanProvider() {
		return new SkipInitialOgnlBeanProvider() {
			@Override
			public Object getProperty(String propertyPath, Object root) {
				if ("populateModelNumber".equals(propertyPath)) {
					boolean isSerialized = (Boolean) super.getProperty("claim.itemReference.serialized", root);
					String modelNumber = "";
			        if(isSerialized)
			        {
			        	modelNumber = (String)super.getProperty("claim.itemReference.unserializedItem.model.name", root);
			        }
			        else
			        {
			        	modelNumber = (String)super.getProperty("claim.itemReference.model.name", root);
			        }
					return modelNumber;
				} else {
					return super.getProperty(propertyPath, root);
				}
			}
		};
	}

    @Override
    protected PartReturnWorkList getWorkList() {
        WorkListCriteria criteria = createCriteria();
        if(criteria.getSortCriteria().keySet()!=null
                && criteria.getSortCriteria().keySet().isEmpty()){
            criteria.addSortCriteria("claim.claimNumber",false);
        }
        return getPartReturnWorkListService().getPartReturnWorkListByClaim(criteria);
    }

    @Override
    protected List<TaskInstance> findAllPartTasksForId(String id) {
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(id);
        return getPartReturnWorkListItemService().findAllTasksForClaim(criteria);
    }

    public boolean isPageReadOnly() {
		return false;
	}

    public boolean isPageReadOnlyAdditional() {
		boolean isReadOnlyDealer = false;
		Set<Role> roles = getLoggedInUser().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
				isReadOnlyDealer = true;
				break;
			}
		}
		return isReadOnlyDealer;
	}

	@Override
	public void validate() {
	}

    @Override
	public void setId(String claimId) {
        this.claimId = claimId;
    }

    @Override
	public String getId() {
        return this.claimId;
    }

    public List getSelectedTaskInstanceIds() {
        return this.selectedTaskInstanceIds;
    }

    public void setSelectedTaskInstanceIds(List<Long> selectedItems) {
        this.selectedTaskInstanceIds = selectedItems;
    }

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }
}
