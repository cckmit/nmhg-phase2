/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.jbpm.action;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.scheduler.exe.Timer;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.email.EmailMessageRepository;
import tavant.twms.domain.email.NotificationService;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.notification.MessageState;
import tavant.twms.domain.notification.NotificationEvent;
import tavant.twms.domain.notification.NotificationMessage;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.jbpm.infra.BeanLocator;

import com.domainlanguage.timeutil.Clock;
/**
 * @author pradipta.a
 * @date Dec 5, 2007
 */

public class SupplierDisputeAction extends Action {

	private static final String DATE_FORMAT = "MM-dd-yyyy";
	private final BeanLocator beanLocator = new BeanLocator();


	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		Timer timer = executionContext.getTimer();
		RecoveryClaim recoveryClaim = null;
		recoveryClaim = (RecoveryClaim) executionContext
				.getVariable("recoveryClaim");
        ConfigParamService configParamService = (ConfigParamService) this.beanLocator
                .lookupBean("configParamService");
        Date shipmentDate = new Date();
		if (recoveryClaim != null) {
            List<RecoverablePart> recoverableParts = recoveryClaim.getRecoveryClaimInfo().getRecoverableParts();
            boolean autoDisputeAllowed = true;
            for(RecoverablePart recoverablePart : recoverableParts){
                if(recoverablePart.getOemPart().isPartToBeReturned() && recoverablePart.getOemPart().isReturnDirectlyToSupplier() && recoverablePart.getOemPart().getStatus() != null && recoverablePart.getOemPart().getStatus().ordinal() >= PartReturnStatus.PART_SHIPPED.ordinal()){
                    autoDisputeAllowed = false;
                    Shipment shipment = recoverablePart.getOemPart().getShipment();
                    if(shipment == null && !recoverablePart.getOemPart().getPartReturns().isEmpty()){
                        //try to fetch from part return
                        shipment = recoverablePart.getOemPart().getPartReturns().get(0).getShipment();
                    }
                    if(shipment != null){
                        shipmentDate = shipment.getShipmentDate();
                    }
                }else if(recoverablePart.isSupplierReturnNeeded() && !recoverablePart.getOemPart().isReturnDirectlyToSupplier() && recoverablePart.getStatus() != null && recoverablePart.getStatus().ordinal() >= PartReturnStatus.PART_SHIPPED.ordinal()){
                    autoDisputeAllowed = false;
                    if(!recoverablePart.getSupplierPartReturns().isEmpty()){
                      Shipment shipment = recoverablePart.getSupplierPartReturns().get(0).getSupplierShipment();
                      if(shipment != null){
                        shipmentDate = shipment.getShipmentDate();
                      }
                    }
                }
            }
            if(configParamService.getBooleanValue(ConfigName.AUTO_DISPUTE_VRCLAIMS_IF_NO_ACTION_FROM_SUPPLIER.getName())){
                int dueDays = 0;
                if(autoDisputeAllowed){
                   dueDays = recoveryClaim.getContract()
                        .getSupplierDisputePeriod();
                }else{
                   dueDays =  recoveryClaim.getContract()
                            .getSupplierResponsePeriod();
                }
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                Date date = sdf.parse(Clock.today().plusDays(dueDays).toString(
                        DATE_FORMAT));
                timer.setDueDate(date);
                //Set mail triggering before due day
                int daysForEmailTriggering = 0;
                if(autoDisputeAllowed)
                    daysForEmailTriggering = Integer.parseInt(configParamService.getStringValue(ConfigName.DAYS_PENDING_IN_INITIAL_RESPONSE_PERIOD_FOR_EMAIL_TRIGGERING.getName()));
                else
                    daysForEmailTriggering = Integer.parseInt(configParamService.getStringValue(ConfigName.DAYS_PENDING_IN_FINAL_RESPONSE_PERIOD_FOR_EMAIL_TRIGGERING.getName()));
                int daysToAddForEmail = dueDays - daysForEmailTriggering;
                Date emailDate = sdf.parse(Clock.today().plusDays(daysToAddForEmail).toString(DATE_FORMAT));
                String assignedDate = sdf.format(new Date());
                createEmailMessage(recoveryClaim, emailDate,assignedDate,executionContext.getTask().getName(), autoDisputeAllowed, shipmentDate);

            }else{
                timer.setDueDate(null);
            }
		}
	}

    private void createEmailMessage(RecoveryClaim recoveryClaim, Date dueDate, String assignedDate, String inboxName, boolean initialOrFinal, Date shipmentDate){
        OrgService orgService = (OrgService) this.beanLocator
                .lookupBean("orgService");

        //Send email to all recovery processors see JIRA NMHGSLMS-757
       /* User sendMailTo = null;
        if(!recoveryClaim.getContract().getSraReviewRequired()){
            //fetch the default recovery supplier
            sendMailTo = orgService.findDefaultUserBelongingToRoleForSelectedBU(recoveryClaim.getBusinessUnitInfo().getName(), Role.RECOVERYPROCESSOR);
        }
       else{
            List<RecoveryClaimAudit> audits = recoveryClaim.getRecoveryClaimAudits();
            for(RecoveryClaimAudit recoveryClaimAudit : audits){
                User user = recoveryClaimAudit.getCreatedBy();
                if(user != null && (user.hasRole("recoveryProcessor") || user.hasRole("sra")) ){
                    sendMailTo = user;
                    break;
                }
            }
        }*/

        List<String> roles = new ArrayList<String>();
       // roles.add(Role.RECOVERYPROCESSOR);
        List<User> recoveryProcessors = orgService.findAllAvailableRecoveryProcessors();

        for(User recProssor : recoveryProcessors){
            HashMap<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("userName", recProssor.getCompleteName());
            paramMap.put("subject", "Claims Pending in "+recoveryClaim.getContract().getSupplier().getName() + " " + inboxName
                    +" inbox for review.");
            paramMap.put("supplier", recoveryClaim.getContract().getSupplier().getName());
            paramMap.put("supplierNumber", recoveryClaim.getContract().getSupplier().getSupplierNumber());
            paramMap.put("assignedDate",assignedDate );
            paramMap.put("emailDetailsForInitialResponse",recoveryClaim.getRecoveryClaimNumber());
            paramMap.put("emailDetailsForFinalResponse",recoveryClaim.getRecoveryClaimNumber());
            paramMap.put("recClaimId",recoveryClaim.getId().toString());
            paramMap.put("inboxName",inboxName);
            paramMap.put("partShippedDate", shipmentDate.toString());

            List<NotificationMessage> messageList = new ArrayList<NotificationMessage>();
            NotificationMessage emailMessage = new NotificationMessage();
            if(initialOrFinal)
                emailMessage.setNotifcationMessageParam(recProssor.getEmail(),"email_initial_response_exceed.vm", paramMap);
            else
                emailMessage.setNotifcationMessageParam(recProssor.getEmail(), "email_final_response_exceed.vm", paramMap);
            emailMessage.setCreationDate(dueDate);
            messageList.add(emailMessage);

            NotificationService notificationService = (NotificationService) this.beanLocator.lookupBean("notificationService");
            if(CollectionUtils.isNotEmpty(messageList)){
                notificationService.saveEmailMessageList(messageList);
            }
        }
    }
}
