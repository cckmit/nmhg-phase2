package tavant.twms.domain.email;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.partreturn.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by deepak.patel on 7/2/14.
 */
public class SendEmailServiceExtended implements OverDueEmail{


    private PartReturnService partReturnService;
    private PartReplacedService partReplacedService;
    private ConfigParamService configParamService;
    private EventService eventService;
    
    public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }

    public void createEmailEventForOverdue(Claim claim, PartReturn partReturn) {
        HashMap<String,Object> eventHashMap = new HashMap<String, Object>();
        eventHashMap.put("claimId",claim.getId().toString());
        eventHashMap.put("partNumberString",partReturn.getOemPartReplaced().getItemReference().getReferredItem().getBrandItemNumber(claim.getBrand()));
        eventHashMap.put("taskInstanceId", partReturn.getId().toString());
        String subject;
        if((configParamService.getStringValueByBU(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName(),claim.getBusinessUnitInfo().getName()).equalsIgnoreCase("true"))){
        	subject = "WPRA Generated - Action Required - Overdue";
        }else{
        	subject = "Action Required - Overdue. Claim - " + claim.getClaimNumber();
        }
        eventHashMap.put("subject", subject);
        getEventService().createEvent("partReturn", EventState.PART_MOVED_TO_OVERDUE, eventHashMap);
        updatePartReturn(claim);
    }

    private void updatePartReturn(Claim claim){
        //Update the part return status
        for(OEMPartReplaced part : claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
           // if(!part.getStatus().equals(PartReturnStatus.PART_MOVED_TO_OVERDUE)) {
                List<PartReturnAudit> audits = part.getPartReturnAudits();
                boolean auditFound =false;
                for(PartReturnAudit audit: audits){
                    if(audit.getPartReturnAction1() != null && audit.getPartReturnAction1().getActionTaken().equals(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus())){
                      audit.setPartReturnAction1(new PartReturnAction(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus(),audit.getPartReturnAction1().getValue().intValue()+1));
                      auditFound =true;
                      partReplacedService.updatePartAudit(audit);
                      break;
                    }
                }
                if(!auditFound){
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus(),1));
                    part.setComments(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus());
                    partReturnService.updatePartStatus(part);
                    partReplacedService.updateOEMPartReplaced(part);
                }
           // }
        }
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
