package tavant.twms.web.supplier;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class PartShipperShipPartsAction extends AbstractSupplierActionSupport {

    private static Logger logger = Logger.getLogger(PartShipperShipPartsAction.class);

    private Shipment shipment;

    private List<String> transitions = new ArrayList<String>();

    private WorkListItemService workListItemService;

    private DomainRepository domainRepository;

    // These two fields are only used for the popup window which shows RecoveryAmountDetails
    private Long partId;

    private OEMPartReplaced oemPartReplaced;

	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
		return this.workListService.getSupplierShipmentBasedView(criteria);
	}

	private void fetchShipmentView() {
        Assert.hasText(getId(), "Id should not be empty for fetch");
        if(logger.isInfoEnabled())
        {
            logger.info("Fetching Shipment for " + getId());
        }
        // Get the shipment from the repository
        this.shipment = (Shipment) this.domainRepository.load(Shipment.class, new Long(getId()));
    }

    public String preview() {
        fetchShipmentView();
        return SUCCESS;
    }

    public String detail() {
        fetchShipmentView();
        return SUCCESS;
    }

    public String getRecoveryAmount() {
        this.oemPartReplaced = (OEMPartReplaced) this.domainRepository.load(OEMPartReplaced.class, this.partId);
        return SUCCESS;
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public List<String> getTransitions() {
        return this.transitions;
    }

    public void setTransitions(List<String> transitions) {
        this.transitions = transitions;
    }

    public WorkListItemService getWorkListItemService() {
        return this.workListItemService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public DomainRepository getDomainRepository() {
        return this.domainRepository;
    }

    public void setDomainRepository(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public Long getPartId() {
        return this.partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    @Override
    public OEMPartReplaced getOemPartReplaced() {
        return this.oemPartReplaced;
    }

    @Override
    public void setOemPartReplaced(OEMPartReplaced oemPartReplaced) {
        this.oemPartReplaced = oemPartReplaced;
    }

	@SuppressWarnings("unchecked")
	@Override
	protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
		return new PageResult<Shipment>(inboxItems, pageSpecification, noOfPages);
	}

}
