package tavant.twms.web.partsreturn;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.email.SendEmailService;
import tavant.twms.domain.partreturn.*;
import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.partsreturn.WpraGeneratedForPartsInboxAction;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 7/11/12
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequiredPartsFromDealerInboxAction extends PartReturnInboxAction {

    private static final Logger logger = Logger
			.getLogger(PartShipperDuePartForDealerAction.class);

    public RequiredPartsFromDealerInboxAction() {
        setActionUrl("requiredPartsFromDealer");
    }

    private String comments;
    private PartReturnProcessingService partReturnProcessingService;
    private SendEmailService sendEmailService;
    private ApplicationSettingsHolder applicationSettings;
    
	private StringBuffer wpraNumbersGenerated = new StringBuffer();

    
	public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	public void setApplicationSettings(ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}

	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

	public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
          this.partReturnProcessingService = partReturnProcessingService;
    }

    @Override
    protected PartReturnWorkList getWorkList() {
         return getPartReturnWorkListService().getPartReturnWorkListByDealerLocation(createCriteria());
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

        if(removedParts.size() > 0)
            partReturnProcessingService.endPrepareDuePartsTasksForParts(removedParts);
      }

    @Override
	protected List<TaskInstance> findAllPartTasksForId(String id) {
		logger.debug("Find Part Tasks for Dealer Location[" + id + "]");
		WorkListCriteria criteria = createCriteria();
		criteria.setIdentifier(id);
		return getPartReturnWorkListItemService().findAllTasksForDealer(
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
                            setEmailDetailsForWPRA(mapWPRA,wpra,partReplacedBean);
                        }

                    }
                   part.setComments(getComments());
                   //int dueDays=part.getDueDaysForPartReturn();
                   for(BasePartReturn partReturn : part.getActivePartReturns()){
                     // partReturn.setDueDays(dueDays);
                       partReturn.setDueDate(Clock.today().plusDays(partReturn.getActualDueDays()));
                   }
                   updatePartStatus(part);
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
	    				  if(StringUtils.isNotEmpty(userName)){
	    	        		  paramMap.put("userName",userName);
	    	        	  }
	    				  paramMap.put("url", applicationSettings.getExternalUrlForEmail());
	    	        	  paramMap.put("emailWPRA",emailWPRA);
	        			 }
        		     }
        	    }
        	  if(StringUtils.isNotEmpty(toEmail)){
	        	   sendEmailService.sendEmail(applicationSettings.getFromAddress(),toEmail.trim(),getText("message.wpra.email.subject"),applicationSettings.getEmailWpraTemplate(),paramMap);
	          }
          }
          return resultingView();
      }

    
     
     public String removeFromWpra(){
         List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans();
         endTasksFromPrepareDueAndRequiredPartsInbox(selectedPartTasks);
         return resultingView();
      }

     private void endTasksFromPrepareDueAndRequiredPartsInbox(List<PartTaskBean> selectedPartTasks) {
        List<OEMPartReplaced> removedParts = new ArrayList<OEMPartReplaced>();
        /*for(PartTaskBean partTask: selectedPartTasks){
            removedParts.add(partTask.getPart());
        }*/
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                removedParts.add(part);
            }
        }

        if(removedParts.size() > 0){
            partReturnProcessingService.endPrepareDuePartsAndWpraTasksForParts(removedParts);
            for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
              OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                 if(partReplacedBean.isSelected()){
                     part.setPartAction1(new PartReturnAction(PartReturnStatus.REMOVED_BY_PROCESSOR.getStatus()
                             ,part.getNumberOfUnits()));
                     part.setPartToBeReturned(false);
                     part.setComments(getComments());
                     partReplacedBean.partReturnTasks.get(0).getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
                     updatePartStatus(part);
                     part.setPartReturns(new ArrayList<PartReturn>());
                     getPartReplacedService().updateOEMPartReplaced(part);
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


}
