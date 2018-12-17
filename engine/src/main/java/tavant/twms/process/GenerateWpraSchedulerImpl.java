package tavant.twms.process;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.domainlanguage.timeutil.Clock;
import com.domainlanguage.time.CalendarDate;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.email.SendEmailService;
import tavant.twms.domain.partreturn.*;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.partreturn.PartReturnWorkListDao;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 3/12/12
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateWpraSchedulerImpl implements GenerateWpraScheduler {

    private ConfigParamService configParamService;
	private WorkListItemService workListItemService;
    private PartReturnWorkListDao partReturnWorkListDao;
    private PartReturnProcessingService partReturnProcessingService;
    private PartReturnService partReturnService;
    private PartReplacedService partReplacedService;
    private SendEmailService sendEmailService;
    private String externalUrlForEmail;
	private String fromAddress;
	private String emailWpraTemplate;

	public String getEmailWpraTemplate() {
		return emailWpraTemplate;
	}

	public void setEmailWpraTemplate(String emailWpraTemplate) {
		this.emailWpraTemplate = emailWpraTemplate;
	}

	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}

	private static final Logger logger = Logger.getLogger(GenerateWpraSchedulerImpl.class);

    public void executeTasks(){
    	logger.info("Enter : Start GenerateWRPA task : executeTasks() for GenerateWpraSchedulerImpl");
        generateWpraForAllTasks(null,null);
    }
    public void executeTasksBetweenDate(CalendarDate startDate,CalendarDate endDate)
    {
    	logger.info("Enter : Start GenerateWRPA task : executeTasksBetweenDate() for GenerateWpraSchedulerImpl");
        generateWpraForAllTasks(startDate,endDate);
    }

    private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}
    private void generateWpraForAllTasks(CalendarDate startDate,CalendarDate endDate){
    	logger.info("Enter : generateWpraForAllTasks() for GenerateWpraSchedulerImpl");
        populateDummyAuthentication();
        int configuredDayOfTheMonthForWparaGenerate = 0;
        List<ConfigValue> configValues = configParamService.getValuesForConfigParam(ConfigName.WPRA_GENERATE_DATE.getName());
        List<ConfigValue> configValuesForWpraEnable = configParamService.getValuesForConfigParam(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName());
        for(ConfigValue isWpraEnable : configValuesForWpraEnable){
            if(isWpraEnable.getConfigParamOption().getValue().equalsIgnoreCase("true")){
                String buInfo = isWpraEnable.getBusinessUnitInfo().getName();
                 for(ConfigValue configValue :configValues){
                     String buForWpraDate = configValue.getBusinessUnitInfo().getName();
                     if(buInfo.equals(buForWpraDate)) {
                    	 configuredDayOfTheMonthForWparaGenerate = Integer.parseInt(configValue.getValue());
                         generateWpra(configuredDayOfTheMonthForWparaGenerate,startDate,endDate);
                     }
                }
            }
        }
    }

    private void generateWpra(int configuredDayOfTheMonthForWparaGenerate,CalendarDate startDate,CalendarDate endDate){
    	if(logger.isDebugEnabled()){
    		logger.debug("Enter : generateWpra() for GenerateWpraSchedulerImpl with configDayofthemonth :"+ configuredDayOfTheMonthForWparaGenerate);
    	}
    	 if(startDate!=null && endDate!=null)
    	 {
    		 configuredDayOfTheMonthForWparaGenerate=new Date().getDate();
    	 }

        if(configuredDayOfTheMonthForWparaGenerate != 0 && new Date().getDate() == configuredDayOfTheMonthForWparaGenerate){
        	
        	 if(logger.isDebugEnabled()){
         		logger.debug("Config match : trigger wrpa");
         	}
        	 List<TaskInstance> taskinstances=null;
        	 if(startDate!=null && endDate!=null)
        	 {
        		 //Generate wpra for all parts between given dates
        		 taskinstances = partReturnWorkListDao.findAllWpraTasksBetweenGivenDate(startDate,endDate);
        	 }
        	 else
        	 {
        		//Generate wpra for all parts
        		 taskinstances= partReturnWorkListDao.findAllWpraTasks();
        	 }
        	 
            //generate Wpra
            List<PartTaskBean> taskBeans = new ArrayList<PartTaskBean>();
            List<Claim> claimList = new ArrayList<Claim>();
            for(TaskInstance instance : taskinstances){
                PartTaskBean partTaskBean = new PartTaskBean(instance);
                taskBeans.add(partTaskBean);
                Claim claim = (Claim) instance.getVariable("claim");
                if(!isClaimListContainsClaim(claimList,claim)){
                  claimList.add(claim);
                }
            }

            Collection<List<PartTaskBean>> partBeansByLocation = getTaskInstanceGroupsByWpra(taskBeans);
            List<Wpra> wpras = partReturnProcessingService.movePartToDuePartInbox(partBeansByLocation, WorkflowConstants.MOVE_TO_DUE_PARTS_FROM_WPRA);
            
            //End All prepare due parts tasks
            List<TaskInstance> taskinstancesForPrepareDueParts=null;
            if(startDate!=null && endDate!=null)
            {
       		 
            	taskinstancesForPrepareDueParts = partReturnWorkListDao.findAllPrepareDuePartsTasksBetweenGivenDate(startDate,endDate);
            }
            else
            {
            	taskinstancesForPrepareDueParts = partReturnWorkListDao.findAllPrepareDuePartsTasks();
            }
            workListItemService.endAllTasksWithTransition(taskinstancesForPrepareDueParts, "toEnd");

         
            Map<String,String> mapWpraGroupByDealer = new HashMap<String, String>();
            // need to update the part return history
            for(Claim clm : claimList){
                try{
                    if(logger.isDebugEnabled()) {
                        logger.debug("WPRA for Claim number : "+clm.getClaimNumber());
                    }
                    List<OEMPartReplaced> parts = clm.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced();
                    StringBuilder emailWPRA = new StringBuilder();
                    if(StringUtils.isNotEmpty(clm.getClaimNumber())){
                       emailWPRA.append("\n").append("ClaimNumber=").append(clm.getClaimNumber()).append(",");
                    }
                    for(OEMPartReplaced part: parts){
                        part.setPartAction1(new PartReturnAction(PartReturnStatus.WPRA_GENERATED.getStatus()
                                      ,part.getNumberOfUnits()));
                        if(part.getWpra() != null){
                            part.getPartAction1().setWpraNumber(part.getWpra().getWpraNumber());
                        }
                        for (Wpra wpra : wpras) {
                                if(isWpraForPart(wpra.getParts(),part)){
                                     part.getPartAction1().setWpraNumber(wpra.getWpraNumber());
                                     part.setWpra(wpra);
                                     emailWPRA.append("WpraNumber=").append(wpra.getWpraNumber()).append(",");
                                     emailWPRA.append("PartNumber=").append(part.getItemReference().getReferredItem().getBrandItemNumber(clm.getBrand())).append(",");

                                }
                            }
                           partReturnService.updatePartStatus(part);
                           partReplacedService.updateOEMPartReplaced(part);
                           //int dueDays=part.getDueDaysForPartReturn();
                            for(BasePartReturn partReturn : part.getActivePartReturns()){
                                //partReturn.setDueDays(dueDays);
                                partReturn.setDueDate(Clock.today().plusDays(partReturn.getActualDueDays()));
                            }
                      }
                    StringBuilder dealerDetail = new StringBuilder();
                    if (MapUtils.isEmpty(mapWpraGroupByDealer)) {
                        if(clm.getFiledBy()!=null && StringUtils.isNotEmpty(clm.getFiledBy().getName())){
                         dealerDetail.append(clm.getFiledBy().getName()).append("#");
                         if(StringUtils.isNotEmpty(clm.getFiledBy().getEmail())){
                          dealerDetail.append("Email=").append(clm.getFiledBy().getEmail());
                         }
                         mapWpraGroupByDealer.put(dealerDetail.toString(),emailWPRA.toString());
                        }
                    }
                    else {
                        if(clm.getFiledBy()!=null && StringUtils.isNotEmpty(clm.getFiledBy().getName())){
                         dealerDetail.append(clm.getFiledBy().getName()).append("#");
                         if(StringUtils.isNotEmpty(clm.getFiledBy().getEmail())){
                          dealerDetail.append("Email=").append(clm.getFiledBy().getEmail());
                         }
                        if (!StringUtils.isNotEmpty(mapWpraGroupByDealer.get(dealerDetail.toString()))) {
                         mapWpraGroupByDealer.put(dealerDetail.toString(),emailWPRA.toString());
                        }
                        else {
                            emailWPRA.append(mapWpraGroupByDealer.get(dealerDetail.toString()));
                            mapWpraGroupByDealer.put(dealerDetail.toString(),emailWPRA.toString());
                          }
                        }
                    }

                }catch(Exception exe){
                    logger.error("WPRA Scheduler: Exception in fetching claim data" + exe.getStackTrace());
                }
            }

            Map<String,Object> paramMap = new HashMap<String, Object>();
            try{
                if(MapUtils.isNotEmpty(mapWpraGroupByDealer)){
                    Set<Entry<String, String>> entrySet = mapWpraGroupByDealer.entrySet();
                    for(Map.Entry<String, String> entry : entrySet){
                          String toEmail = null;
                          if(StringUtils.isNotEmpty(entry.getKey())){
                              String userName = entry.getKey().substring(0,entry.getKey().indexOf("#"));
                              paramMap.put("userName",userName);
                              if(entry.getKey().contains("#Email=")){
                                  toEmail = entry.getKey().substring(entry.getKey().indexOf("#Email")+7);
                              }
                          }
                          paramMap.put("url", externalUrlForEmail);
                          paramMap.put("emailWPRA",entry.getValue());
                          if(StringUtils.isNotEmpty(toEmail)){
                           sendEmailService.sendEmail(fromAddress,  toEmail.trim(),  "WPRA Generated - Action Required", emailWpraTemplate,paramMap);
                          }
                    }
                }
            }catch(Exception exe){
                logger.error("WPRA Scheduler: Exception in sending emails" + exe.getStackTrace());
            }

        }
        if(logger.isDebugEnabled()){
    		logger.debug("Exit : generateWpra()");
    	}

    }
    
    private boolean isWpraForPart(List<PartReturn> parts,OEMPartReplaced oemPartReplaced){
   	 for(PartReturn partReturn :  parts){
   		 if(partReturn.getOemPartReplaced().getId().longValue()==oemPartReplaced.getId().longValue()){
   			 return true;
   		 }
   	 }
   	 return false;
   	 
    }
    
    private boolean isClaimListContainsClaim( List<Claim> claimList,Claim claim){
    	 for(Claim clm :  claimList){
       		 if(clm.getClaimNumber().equalsIgnoreCase(claim.getClaimNumber())){
       			 return true;
       		 }
       	 }
       	 return false;
    }

    protected Collection<List<PartTaskBean>> getTaskInstanceGroupsByWpra(List<PartTaskBean> partTaskBeans) {
          if (partTaskBeans == null) {
              return null;
          }

          Map<String, List<PartTaskBean>> locationToPartTaskBean = new HashMap<String, List<PartTaskBean>>();
          for (PartTaskBean instance : partTaskBeans) {
              StringBuilder keyId = new StringBuilder(String.valueOf(instance.getClaim().getForDealer().getId())).append(String.valueOf(instance.getPartReturn().getReturnLocation().getId()));
              List<PartTaskBean> partBeanForLocation = locationToPartTaskBean.get(keyId.toString());
              if (partBeanForLocation == null) {
                  partBeanForLocation = new ArrayList<PartTaskBean>();
                  locationToPartTaskBean.put(keyId.toString(), partBeanForLocation);
              }
              partBeanForLocation.add(instance);
              }
          return locationToPartTaskBean.values();
      }

    public void setPartReturnWorkListDao(PartReturnWorkListDao partReturnWorkListDao) {
        this.partReturnWorkListDao = partReturnWorkListDao;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }
    
    public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getExternalUrlForEmail() {
		return externalUrlForEmail;
	}

	public void setExternalUrlForEmail(String externalUrlForEmail) {
		this.externalUrlForEmail = externalUrlForEmail;
	}	
}
