package tavant.twms.web.partsreturn;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 30/11/12
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrePareDuePartsInboxAction extends PartReturnInboxAction{

    private static final Logger logger = Logger
			.getLogger(PrePareDuePartsInboxAction.class);

	private PartReturnProcessingService partReturnProcessingService;

	public PrePareDuePartsInboxAction() {
		// TODO : Check if this is required.
		setActionUrl("dealerPrepareDuePartInbox");
	}

	@Override
	public void validate() {

	}

	@Override
	protected PartReturnWorkList getWorkList() {
		if (showWPRA()) {
			return getPartReturnWorkListService()
					.getPartReturnWorkListForWpraByActorId(createCriteria());
		} else {
			return getPartReturnWorkListService()
					.getPartReturnWorkListByLocation(createCriteria());
		}
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
	protected List<TaskInstance> findAllPartTasksForId(String id) {
		logger.debug("Find Part Tasks for Location[" + id + "]");
		WorkListCriteria criteria = createCriteria();
		criteria.setIdentifier(id);
		if (showWPRA()) {
			return getPartReturnWorkListItemService().findAllTasksForWPRA(
					criteria);
		} else {
			return getPartReturnWorkListItemService().findAllTasksForLocation(
					criteria);
		}
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}
}
