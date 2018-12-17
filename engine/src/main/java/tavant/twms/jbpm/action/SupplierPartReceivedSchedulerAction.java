package tavant.twms.jbpm.action;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import org.apache.commons.collections.CollectionUtils;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.scheduler.exe.Timer;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.email.EmailMessageRepository;
import tavant.twms.domain.email.NotificationService;
import tavant.twms.domain.notification.MessageState;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.process.ProcessService;
import tavant.twms.worklist.partreturn.PartReturnWorkListDao;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by deepak.patel on 8/4/14.
 */
public class SupplierPartReceivedSchedulerAction extends Action{

    private BeanLocator beanLocator = new BeanLocator();
    private static final String DATE_FORMAT = "MM-dd-yyyy";

    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        PartReturnWorkListDao partReturnWorkListDao = (PartReturnWorkListDao) beanLocator.lookupBean("partReturnWorkListDao");
        ProcessService processService = (ProcessService) beanLocator.lookupBean("processService");
        RecoveryClaim recClaim = ((RecoveryClaim) executionContext
                .getVariable("recoveryClaim"));
        ConfigParamService configParamService = (ConfigParamService) this.beanLocator
                .lookupBean("configParamService");

        //if part is shipped directly to dealer then recovery claim will be null.
        if(configParamService.getBooleanValue(ConfigName.AUTO_DISPUTE_VRCLAIMS_IF_NO_ACTION_FROM_SUPPLIER.getName())){
            if(recClaim == null){
                Claim claim = (Claim) executionContext.getVariable("claim");
                PartReturn partReturn = (PartReturn)executionContext.getVariable("partReturn");
                if(claim != null && partReturn != null){
                    OEMPartReplaced part = partReturn.getOemPartReplaced();
                    for(RecoveryClaim recoveryClaim :claim.getRecoveryClaims()){
                        List<RecoverablePart> recoverableParts = recoveryClaim.getRecoveryClaimInfo().getRecoverableParts();
                        for(RecoverablePart recoverablePart : recoverableParts){
                            if(recoverablePart.getOemPart().getId() == part.getId()){
                                recClaim = recoveryClaim;
                                break;
                            }
                        }
                    }
                }
            }

            Timer timer = partReturnWorkListDao.findTimerForRecoveryClaim(recClaim);
            if(timer != null){
                int dueDays = recClaim.getContract().getSupplierResponsePeriod();
                processService.updateDueDateForPartReturn(timer, Clock.today().plusDays(dueDays));
                //ok call mail service now
                //Set mail triggering before due day -- this should update the messages created.
                int daysForEmailTriggering = Integer.parseInt(configParamService.getStringValue(ConfigName.DAYS_PENDING_IN_FINAL_RESPONSE_PERIOD_FOR_EMAIL_TRIGGERING.getName()));
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                int daysToaddForEmailTriggering = dueDays - daysForEmailTriggering;
                Date date = sdf.parse(Clock.today().plusDays(daysToaddForEmailTriggering).toString(DATE_FORMAT));

                SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT);
                String partShippeddate = sdf1.format(new Date()).toString();
                //get the list
                cancelScheduledMessages(recClaim);
                //Check duplicates
                EmailMessageRepository emailMessageRepo = (EmailMessageRepository) this.beanLocator
                        .lookupBean("emailMessageRepository");
                List<NotificationMessage> listOfScheduleMessage = emailMessageRepo.getPendingRecoveryEmailMessage(recClaim.getId().toString());
                if(!listOfScheduleMessage.isEmpty()){
                    //update
                    for(NotificationMessage message : listOfScheduleMessage){
                        message.setCreationDate(date);
                        emailMessageRepo.updateEmailMessage(message);
                    }
                }else{
                    createEmailMessage(recClaim, date, partShippeddate, timer.getTaskInstance().getName());
                }
            }

        }
    }

    private void cancelScheduledMessages(RecoveryClaim recoveryClaim){
        EmailMessageRepository emailMessageRepository = (EmailMessageRepository) this.beanLocator
                .lookupBean("emailMessageRepository");
        List<NotificationMessage> emailMessageList = emailMessageRepository.getNewPendingRecoveryEmailMessage(recoveryClaim.getId().toString());

        for(NotificationMessage message : emailMessageList){
            HashMap<String, Object> paramMap = message.getParameterMap();
            String recId = paramMap.get("recClaimId") != null ? paramMap.get("recClaimId").toString() : null;
            if(recId != null && recId.equalsIgnoreCase(recoveryClaim.getId().toString())){
                message.setMessageState(MessageState.CANCELLED);
                emailMessageRepository.updateEmailMessage(message);
            }
        }
    }

    //ok, set a mail reminder, people don't check other's inbox generally
    private void createEmailMessage(RecoveryClaim recoveryClaim, Date dueDate, String partshippedDate,String inboxName){
        OrgService orgService = (OrgService) this.beanLocator
                .lookupBean("orgService");
        List<RecoveryClaimAudit> audits = recoveryClaim.getRecoveryClaimAudits();
        //Send email to all recovery processors see JIRA NMHGSLMS-757
        /*User sendMailTo = null;
        if(!recoveryClaim.getContract().getSraReviewRequired()){
            //fetch the default recovery supplier
            sendMailTo = orgService.findDefaultUserBelongingToRoleForSelectedBU(recoveryClaim.getBusinessUnitInfo().getName(), Role.RECOVERYPROCESSOR);
        }else{

            for(RecoveryClaimAudit recoveryClaimAudit : audits){
                User user = recoveryClaimAudit.getD().getLastUpdatedBy();
                if(user != null && (user.hasRole("recoveryProcessor") || user.hasRole("sra")) ){
                    sendMailTo = user;
                    break;
                }
            }
        }*/

        List<String> roles = new ArrayList<String>();
        //roles.add(Role.RECOVERYPROCESSOR);
        List<User> recoveryProcessors = orgService.findAllAvailableRecoveryProcessors();

        for(User recProssor : recoveryProcessors){
            HashMap<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("userName", recProssor.getCompleteName());
            paramMap.put("subject", "Claims Pending in "+recoveryClaim.getContract().getSupplier().getName() + " "+inboxName+" inbox for review.");
            paramMap.put("supplier", recoveryClaim.getContract().getSupplier().getName());
            paramMap.put("supplierNumber", recoveryClaim.getContract().getSupplier().getSupplierNumber());
            paramMap.put("partShippedDate", partshippedDate);
            paramMap.put("emailDetailsForFinalResponse",recoveryClaim.getRecoveryClaimNumber());
            paramMap.put("recClaimId",recoveryClaim.getId().toString());
            paramMap.put("inboxName",inboxName);

            //Fetched recovery assigned date
            CalendarDate assignedDate = null;
            for(RecoveryClaimAudit recoveryClaimAudit : audits){
                if(recoveryClaimAudit.getRecoveryClaimState().equals(RecoveryClaimState.ON_HOLD_FOR_PART_RETURN)){ //|| recoveryClaimAudit.getRecoveryClaimState().equals(RecoveryClaimState.IN_RECOVERY)){
                    assignedDate = recoveryClaimAudit.getCreatedOn();
                    break;
                }
            }

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT);
            Date onHoldDate = new Date(assignedDate.breachEncapsulationOf_year()-1900,assignedDate.breachEncapsulationOf_month()-1,assignedDate.breachEncapsulationOf_day());

            paramMap.put("assignedDate", sdf1.format(onHoldDate).toString());

            List<NotificationMessage> messageList = new ArrayList<NotificationMessage>();
            NotificationMessage emailMessage = new NotificationMessage();
            emailMessage.setNotifcationMessageParam(recProssor.getEmail(),"email_final_response_exceed.vm", paramMap);
            emailMessage.setCreationDate(dueDate);
            messageList.add(emailMessage);

            NotificationService notificationService = (NotificationService) this.beanLocator.lookupBean("notificationService");
            if(CollectionUtils.isNotEmpty(messageList)){
                notificationService.saveEmailMessageList(messageList);
            }
        }
    }
}
