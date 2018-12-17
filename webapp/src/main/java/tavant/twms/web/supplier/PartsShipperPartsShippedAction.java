package tavant.twms.web.supplier;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.partreturn.ShipmentRepository;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;

@SuppressWarnings("serial")
public class PartsShipperPartsShippedAction extends AbstractSupplierActionSupport {

    private List<Shipment> shipments = new ArrayList<Shipment>();

    private DomainRepository domainRepository;

    private Boolean detailView;

    private ShipmentRepository shipmentRepository; 
   // private String shipmentIdString;

	@Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        //if it default view then fetch the vpra view else fetch the customized view
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID)) {
            return workListService.getSupplierShipmentBasedView(criteria);
        }
        else {
            return workListService.getPartShipperRecoveryClaimView(criteria);
        }
    }

    public String preview() {
        Shipment shipment = (Shipment) domainRepository.load(Shipment.class, new Long(getId()));
        this.shipments.add(shipment);
        return SUCCESS;
    }

    public String detail() {
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID)){
            Shipment shipment = (Shipment) domainRepository.load(Shipment.class, new Long(getId()));
            this.shipments.add(shipment);
          //  shipmentIdString = shipment.getId().toString();
        } else {
            //Get all the shipments for recovery claim
            this.shipments = getWorkListService().getAllShipmentsForRecoveryClaim(Long.valueOf(getId()), getTaskName());
           /* StringBuffer tempString = new StringBuffer();
            for(int i=0; i<this.shipments.size() ;i++){
                tempString.append(this.shipments.get(i).getId().toString());
                if(i < this.shipments.size()-1){
                    tempString.append(":");
                }
            }
            shipmentIdString = tempString.toString();*/
        }

        detailView = Boolean.TRUE;
        return SUCCESS;
    }
    
    @Override
    protected String getAlias() {
    	// TODO Auto-generated method stub
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID))  {
    	    return "shipment";
        }else{
            return "recoveryClaim";
        }
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public Boolean getDetailView() {
        return detailView;
    }

    public void setDomainRepository(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<OEMPartReplaced>(inboxItems, pageSpecification, noOfPages);
    }
    
    public String updateContactPerson(){
      this.shipmentRepository.updateShipments(shipments);
      detailView = Boolean.TRUE;
      addActionMessage("message.contactPerson.Updated");
      return SUCCESS;
  }
    

   public ShipmentRepository getShipmentRepository() {
		return shipmentRepository;
	}

	public void setShipmentRepository(ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}
 
   /* public String getShipmentIdString() {
        return shipmentIdString;
    }

    public void setShipmentIdString(String shipmentIdString) {
        this.shipmentIdString = shipmentIdString;
    }*/
}
