package tavant.twms.web.partsreturn;

import org.apache.struts2.interceptor.ParameterAware;
import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.query.PartReturnClaimSummary;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.search.PreDefinedPartReturnSearchAction;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.partreturn.PartReturnWorkListService;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 21/3/13
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkPartAsScrap extends PartReturnInboxAction {

    private String oemPartReplaced;

    private PartReplacedService partReplacedService;

    private PartReturnService partReturnService;

    private PartReturnWorkListService partReturnWorkListService;

    private WorkListItemService workListItemService;

    public String processPartForScrap(){
       //get oem part replaced and update it with boolean and date
        OEMPartReplaced oemPartReplaced = partReplacedService.findOEMPartReplacedById(Long.parseLong(getOemPartReplaced()));
        oemPartReplaced.setPartScrapped(true);
        oemPartReplaced.setScrapDate(new Date());
        oemPartReplaced.setPartAction1(new PartReturnAction(PartReturnStatus.PART_MARKED_AS_SCRAPPED.getStatus()
                ,oemPartReplaced.getNumberOfUnits()));

        //remove from dealer rejected parts inbox. if already requested we should not allow to scrap
        List<TaskInstance> tasks = partReturnWorkListService.findAllRejectedPartsForDealer(oemPartReplaced);
        //Just wanted to avoid a jbpm call if task is empty
        if(tasks != null && !tasks.isEmpty()){
            workListItemService.endAllTasksWithTransition(tasks, "toEnd");
        }
        //update part status
        partReturnService.updatePartStatus(oemPartReplaced);
        getPartReplacedService().updateOEMPartReplaced(oemPartReplaced);
        addActionMessage("label.scrap.confirmation");
        return SUCCESS;
    }

    public String getOemPartReplaced() {
        return oemPartReplaced;
    }

    public void setOemPartReplaced(String oemPartReplaced) {
        this.oemPartReplaced = oemPartReplaced;
    }

    public PartReplacedService getPartReplacedService() {
        return partReplacedService;
    }

    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setPartReturnWorkListService(PartReturnWorkListService partReturnWorkListService) {
        this.partReturnWorkListService = partReturnWorkListService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }
}
