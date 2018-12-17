package tavant.twms.web.partsreturn;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;

import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.email.SendEmailService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.*;
import tavant.twms.infra.BeanProvider;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

import java.util.*;
import java.util.Map.Entry;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 30/11/12
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */


public class ClaimBasedRequiredPartsFromDealerInboxAction extends PartReturnInboxAction {

    private static final Logger logger = Logger.getLogger(ClaimBasedPartShipperDuePartForDealerAction.class);

    private PartReturnProcessingService partReturnProcessingService;
    private SendEmailService sendEmailService;

    private StringBuffer wpraNumbersGenerated = new StringBuffer();

    public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

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

		if (hasActionErrors()) {
			generateView();
		}
	}

     private void endTasksFromPrepareDueParts(List<PartTaskBean> selectedPartTasks) {
        List<OEMPartReplaced> removedParts = new ArrayList<OEMPartReplaced>();
       /* for(PartTaskBean partTask: selectedPartTasks){
            removedParts.add(partTask.getPart());
        }*/

        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
             if(partReplacedBean.isSelected()){
                 OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                     removedParts.add(part);
             }
        }
        if(removedParts.size() >0)
            partReturnProcessingService.endPrepareDuePartsTasksForParts(removedParts);
      }

    public String generateWpraForDealerBasedOnReturnLocation() throws Exception {
          List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans();
          Collection<List<PartTaskBean>> partBeansByLocation = getTaskInstanceGroupsByWpra(selectedPartTasks);
          List<Wpra> wpras = partReturnProcessingService.movePartToDuePartInbox(partBeansByLocation, this.transitionTaken);
          endTasksFromPrepareDueParts(selectedPartTasks);
          Map<String,Map<String,List<String> >> mapWPRA = new HashMap<String,Map<String,List<String> >>();
          for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
              if(partReplacedBean.isSelected()){
                  OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                      part.setPartAction1(new PartReturnAction(PartReturnStatus.WPRA_GENERATED.getStatus()
                              ,partReplacedBean.getToBeShipped()));
                      if(part.getWpra() != null){
                          part.getPartAction1().setWpraNumber(part.getWpra().getWpraNumber());
                      }
                  for (Wpra wpra : wpras) {
                        if(wpra.getParts().get(0).getOemPartReplaced().getId().longValue()==part.getId().longValue()){
                            part.getPartAction1().setWpraNumber(wpra.getWpraNumber());
                            part.setWpra(wpra);
                            if(wpraNumbersGenerated.length()!=0)
                                wpraNumbersGenerated.append(",");
                             wpraNumbersGenerated.append(wpra.getWpraNumber());
                             mapWPRA = setEmailDetailsForWPRA(mapWPRA,wpra,partReplacedBean);

                        }
                    }
                   part.setComments(getComments());
                   updatePartStatus(part);
                   //int dueDays=part.getDueDaysForPartReturn();
                   for(BasePartReturn partReturn : part.getActivePartReturns()){
                     // partReturn.setDueDays(dueDays);
                      partReturn.setDueDate(Clock.today().plusDays(partReturn.getActualDueDays()));
                   }

                   getPartReplacedService().updateOEMPartReplaced(part);
                  }

          }
          if(MapUtils.isNotEmpty(mapWPRA)){
        	  StringBuilder emailWPRA = new StringBuilder();
        	  String toEmail = null;
        	  Set<Entry<String, Map<String, List<String>>>> entrySet = mapWPRA.entrySet();
        	  HashMap<String,Object> paramMap = new LinkedHashMap<String, Object>();
        	  for(Map.Entry<String, Map<String,List<String>>> entry : entrySet){
        		  emailWPRA.append("\n").append("WpraNumber=").append(entry.getKey()).append(",");
        		  Map<String, List<String>> claimMap = entry.getValue();
        		  if(MapUtils.isNotEmpty(claimMap)){
	    			  Set<Entry<String, List<String>>> entrySet2 = claimMap.entrySet();
	    			  for(Map.Entry<String,List<String>> entry2 :  entrySet2){
	    				String claimDetails = entry2.getKey();
	    				toEmail = claimDetails.substring(claimDetails.indexOf("#Email=")+7);
	    				claimDetails  = claimDetails.substring(0,claimDetails.indexOf("#Email="));
	    				String userName = claimDetails.substring(claimDetails.indexOf("#filedBy=")+9);
	    				String claimNumber = claimDetails.substring(0,claimDetails.indexOf("#"));
	    				  emailWPRA.append("ClaimNumber=").append(claimNumber).append(","); 
	    				  List<String> partNumbers = entry2.getValue();
	    				  for(String partNumber : partNumbers){
	    				   emailWPRA.append("PartNumber=").append(partNumber).append(","); 
	    				  }
	    				  if(StringUtils.hasText(userName)){
	    	        		  paramMap.put("userName",userName);
	    	        	  }
	    				  paramMap.put("url", applicationSettings.getExternalUrlForEmail());
	    	        	  paramMap.put("emailWPRA",emailWPRA);
	        			 }
        		     }
        	    }
        	  if(StringUtils.hasText(toEmail)){
	        	   sendEmailService.sendEmail(applicationSettings.getFromAddress(),toEmail.trim(),getText("message.wpra.email.subject"),applicationSettings.getEmailWpraTemplate(),paramMap);
	          }
          }
          return resultingView();
      }

    @Override
      protected String resultingView() {
           generateView();
           if(!isBlank(wpraNumbersGenerated.toString()))
               addActionMessage("message.wpra.wpraGenerated",wpraNumbersGenerated);
           if (getClaimWithPartBeans().size() == 0) {
               addActionMessage("message.itemStatus.updated");
               return SUCCESS;
           }
           if (!hasActionErrors()) {
               addActionMessage("message.itemStatus.updated");
           }
           return INPUT;
       }

     public String removeFromWpra(){
         List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans();
         endTasksFromPrepareDueAndRequiredPartsInbox(selectedPartTasks);
         return resultingView();
      }

     private void endTasksFromPrepareDueAndRequiredPartsInbox(List<PartTaskBean> selectedPartTasks) {
        List<OEMPartReplaced> removedParts = new ArrayList<OEMPartReplaced>();
       /* for(PartTaskBean partTask: selectedPartTasks){
            removedParts.add(partTask.getPart());
        }*/
       for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                removedParts.add(part);
            }
       }
       if(removedParts.size() >0 ){
            partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
            for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
                 OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                 if(partReplacedBean.isSelected()){
                 part.setPartAction1(new PartReturnAction(PartReturnStatus.REMOVED_BY_PROCESSOR.getStatus()
                         ,part.getNumberOfUnits()));
                 part.setComments(getComments());
                 part.setPartToBeReturned(false);
                 partReplacedBean.partReturnTasks.get(0).getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
                 updatePartStatus(part);
                 part.setPartReturns(new ArrayList<PartReturn>());
                 getPartReplacedService().updateOEMPartReplaced(part);
               }
            }
       }
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
