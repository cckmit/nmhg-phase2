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
 * Date: 8/11/12
 * Time: 8:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClaimBasedPartShipperDuePartForDealerAction extends PartReturnInboxAction {

    private static final Logger logger = Logger.getLogger(ClaimBasedPartShipperDuePartForDealerAction.class);

    private PartReturnProcessingService partReturnProcessingService;

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }

    private String comments;
    private String claimId;
    private List<Long> selectedTaskInstanceIds = new ArrayList<Long>();

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
    protected void setUserSpecifiedQuantity() {
		for (ClaimWithPartBeans claimWithPartBeans : getClaimWithPartBeans()) {
			for (OEMPartReplacedBean partReplacedBean : claimWithPartBeans.getPartReplacedBeans()) {
				for (OEMPartReplacedBean uiPartReplacedBean : this.getPartReplacedBeans()) {
					if (partReplacedBean.getPartReplacedId() == uiPartReplacedBean.getPartReplacedId()) {
						partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
						partReplacedBean.setShip(uiPartReplacedBean.getShip());
						partReplacedBean.setCannotShip(uiPartReplacedBean.getCannotShip());


					}
				}
			}
		}
	}

    @Override
	public void validate() {
		super.validate();
		validateData();

		if (hasActionErrors()) {
			generateView();
			setUserSpecifiedQuantity();
		}

	}

	public void validateData() {
		if (!hasActionErrors()) {
			for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
				if (partReplacedBean.isSelected()) {
					if (partReplacedBean.getCannotShip() == 0 && partReplacedBean.getShip() == 0) {
						addActionError("error.partReturnConfiguration.noPartSelected");
					} else if (partReplacedBean.getCannotShip() + partReplacedBean.getShip() > partReplacedBean
							.getToBeShipped()) {
						addActionError("error.partReturnConfiguration.excessPartsShipmentGenerate");
					}
				}
			}
			for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
				if (partReplacedBean.getCannotShip() > 0 && !StringUtils.hasText(getComments())) {
					addActionError("error.manageFleetCoverage.commentsMandatory");
					break;
				}
			}
		}
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<Long> getSelectedTaskInstanceIds() {
        return selectedTaskInstanceIds;
    }

    public void setSelectedTaskInstanceIds(List<Long> selectedTaskInstanceIds) {
        this.selectedTaskInstanceIds = selectedTaskInstanceIds;
    }

    @Override
    public void setId(String claimId) {
       this.claimId = claimId;
    }

    @Override
    public String getId() {
       return this.claimId;
    }
}
