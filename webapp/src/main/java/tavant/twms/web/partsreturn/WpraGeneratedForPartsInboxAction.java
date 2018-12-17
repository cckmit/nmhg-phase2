package tavant.twms.web.partsreturn;

import com.opensymphony.xwork2.ActionContext;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.partreturn.*;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 30/11/12
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class WpraGeneratedForPartsInboxAction extends PartReturnInboxAction{

    private static final Logger logger = Logger
			.getLogger(WpraGeneratedForPartsInboxAction.class);

    public WpraGeneratedForPartsInboxAction() {
        setActionUrl("wpraGeneratedForParts");
    }

    private PartReturnProcessingService partReturnProcessingService;

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
          this.partReturnProcessingService = partReturnProcessingService;
    }

    @Override
    protected PartReturnWorkList getWorkList() {
         return getPartReturnWorkListService().getPartReturnWorkListByWpra(createCriteria());
    }

    @Override
	protected List<TaskInstance> findAllPartTasksForId(String id) {
		logger.debug("Find Part Tasks for WPRA[" + id + "]");
		WorkListCriteria criteria = createCriteria();
		criteria.setIdentifier(id);
		return getPartReturnWorkListItemService().findAllTasksForWPRA(
				criteria);
	}

    @Override
    protected String resultingView() {
           generateView();
           if (getClaimWithPartBeans().size() == 0) {
               addActionMessage("message.itemStatus.updated");
               return SUCCESS;
           }
           if (!hasActionErrors()) {
               addActionMessage("message.itemStatus.updated");
           }
           return INPUT;
    }

    public String cancelAllReturnProcess(){
        List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans();
        List<OEMPartReplaced> removedParts = new ArrayList<OEMPartReplaced>();
        //List<TaskInstance> partTasksToBeEnded = new ArrayList<TaskInstance>();
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                if(null != part && getTheStatusListForCancelReturn().contains(part.getStatus())) {
                     removedParts.add(part);
                     part.setWpra(null);
                     part.setPartToBeReturned(false);
                     partReplacedBean.partReturnTasks.get(0).getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
                     part.setPartAction1(new PartReturnAction(PartReturnStatus.REMOVED_BY_PROCESSOR.getStatus()
                             ,partReplacedBean.getToBeShipped()+partReplacedBean.getShipmentGenerated()+partReplacedBean.getCevaTracking()));
                     updatePartStatus(part);
                     part.setPartReturns(new ArrayList<PartReturn>());
                     getPartReplacedService().updateOEMPartReplaced(part);
                }else if(!part.getStatus().equals(PartReturnStatus.REMOVED_BY_PROCESSOR)){
                    addActionMessage(getText("message.wpra.cancel.for.shippedParts",part.getItemReference().getReferredItem().getNumber() ));
                }
            }
        }

        if(removedParts.size()>0){
            partReturnProcessingService.endTasksForParts(removedParts);
        }else if(!hasActionMessages()){
            addActionMessage(getText("message.wpra.cancel"));
            return SUCCESS;
        }

        if (!hasActionMessages()) {
            addActionMessage("message.itemStatus.updated");
            return SUCCESS;
        }
        else
        	return INPUT;
       
    }

    public List<PartReturnStatus> getTheStatusListForCancelReturn(){

        List<PartReturnStatus> statusList = new ArrayList<PartReturnStatus>();
        statusList.add(PartReturnStatus.WPRA_GENERATED);
        statusList.add(PartReturnStatus.PART_MOVED_TO_OVERDUE);
        statusList.add(PartReturnStatus.SHIPMENT_GENERATED);
        statusList.add(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO);

        return statusList;
    }

    @Override
    public void validate() {
        super.validate();

        if (hasActionErrors()) {
            generateView();
        }
    }
}
