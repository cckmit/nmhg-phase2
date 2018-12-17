
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
 * Date: 7/11/12
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartShipperDuePartForDealerAction extends PartReturnInboxAction {

    private static final Logger logger = Logger
			.getLogger(PartShipperDuePartForDealerAction.class);

    public PartShipperDuePartForDealerAction() {
        setActionUrl("dealerRequestedPart");
    }

    private String comments;
    private PartReturnProcessingService partReturnProcessingService;

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
          this.partReturnProcessingService = partReturnProcessingService;
    }

    @Override
    protected PartReturnWorkList getWorkList() {
         return getPartReturnWorkListService().getPartReturnWorkListByDealerLocationForPartShipper(createCriteria());
    }

    public String generateShipmentForDealer() throws Exception {
          List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans();
          Collection<List<PartTaskBean>> partBeansByLocation = getTaskInstanceGroupsByLocation(selectedPartTasks);
          List<Shipment> shipments = this.partReturnProcessingService.createShipmentsByLocation(partBeansByLocation,
                  this.transitionTaken);
          endTasksForCannotShip(selectedPartTasks);
          StringBuffer shipmentIds = new StringBuffer();
          for (Shipment shipment : shipments) {
              shipmentIds.append(shipment.getTransientId().toString() + ",");
          }
          for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
              if(partReplacedBean.isSelected()){
                  OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                      part.setPartAction1(new PartReturnAction(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED.getStatus()
                              ,partReplacedBean.getCountOfShip()));
                      part.setPartAction2(new PartReturnAction(PartReturnStatus.NMHG_TO_DEALER_CANNOT_BE_SHIPPED.getStatus()
                               ,partReplacedBean.getCountOfCannotShip()));
                    for (Shipment shipment : shipments) {
                        if(shipment.getParts().get(0).getOemPartReplaced().getId().longValue()==part.getId().longValue()){
                             part.getPartAction1().setShipmentId(shipment.getTransientId().toString());

                        }
                    }
                   part.setComments(getComments());
                   updatePartStatus(part);
                   getPartReplacedService().updateOEMPartReplaced(part);
                  }

          }
          if(shipmentIds != null)
          {
              String shipmentString = shipmentIds.toString();
              if(shipmentString != null && shipmentString.length()>0){
              setShipmentIdString(shipmentString.substring(0, shipmentString.length() - 1));
              }
          }
          return resultingView();
      }

    private void endTasksForCannotShip(List<PartTaskBean> selectedPartTasks) {
          PartTaskBean partTaskBean = null;
          List<TaskInstance> partTasksToBeEnded = new ArrayList<TaskInstance>();
          for (PartTaskBean instance : selectedPartTasks) {
              if (PartReturnTaskTriggerStatus.TO_BE_ENDED.equals(instance.getPartReturn().getTriggerStatus())) {
                  partTasksToBeEnded.add(instance.getTask());
                  partTaskBean=instance;
                  instance.getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
              }
          }
          getWorkListItemService().endAllTasksWithTransition(partTasksToBeEnded, "toEnd");
      }

      private Collection<List<PartTaskBean>> getTaskInstanceGroupsByLocation(List<PartTaskBean> partTaskBeans) {
          if (partTaskBeans == null) {
              return null;
          }

          Map<Long, List<PartTaskBean>> locationToPartTaskBean = new HashMap<Long, List<PartTaskBean>>();
          for (PartTaskBean instance : partTaskBeans) {
              if (PartReturnTaskTriggerStatus.TO_GENERATE_SHIPMENT_FOR_DEALER.equals(instance.getPartReturn().getTriggerStatus())) {
                  Long keyId = instance.getClaim().getForDealer().getId();
                  List<PartTaskBean> partBeanForLocation = locationToPartTaskBean.get(keyId);
                  if (partBeanForLocation == null) {
                      partBeanForLocation = new ArrayList<PartTaskBean>();
                      locationToPartTaskBean.put(keyId, partBeanForLocation);
                  }
                  partBeanForLocation.add(instance);
              }
          }
          return locationToPartTaskBean.values();
      }

    @Override
	protected List<TaskInstance> findAllPartTasksForId(String id) {
		logger.debug("Find Part Tasks for Dealer Location[" + id + "]");
		WorkListCriteria criteria = createCriteria();
		criteria.setIdentifier(id);
		return getPartReturnWorkListItemService().findAllTasksForDealerLocation(
				criteria);
	}

     public String getComments() {
          return comments;
     }

      public void setComments(String comments) {
          this.comments = comments;
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
               addActionMessage("message.forpartshipper.itemStatus.continue_next_step");
           }
           return INPUT;
       }
}
