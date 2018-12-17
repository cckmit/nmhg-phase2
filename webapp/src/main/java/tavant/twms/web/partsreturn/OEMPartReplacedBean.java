/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.web.partsreturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.process.PartTaskBean;
import tavant.twms.security.SelectedBusinessUnitsHolder;

/**
 * @author pradipta.a This bean has been created for display of part return as a
 *         single unit
 */
public class OEMPartReplacedBean {
    List<PartTaskBean> partReturnTasks = new ArrayList<PartTaskBean>(100);
    List<Long> partTaskIds = new ArrayList<Long>();
    private boolean selected = true;
    long partReplacedId;
    int toBeShipped = 0;
    int shipped = 0;
    int cannotBeShipped = 0;
    int ship = 0;
    int cannotShip = 0;
    int shipmentGenerated = 0;
    int receive = 0;
    int didNotReceive = 0;
    int received = 0;
    int notReceived = 0;
    int inspected = 0;
    int totalNoOfParts = 0;
    private List<String> failureCauses;
    private List<String> acceptanceCauses;
    OEMPartReplaced oemPartReplaced;
    PartTaskBean partTaskBean;
    private List<String> actionsTaken;
	private boolean toBeInspected = false;
	private boolean toBeScrapped = false;
    private boolean multipleFailureCauses = false;
    private boolean multipleAcceptanceCauses = false;
    private String warehouseLocation;
    private int accepted = 0;
    private int rejected = 0;
    private int qtyForShipment;
    private  Map<Contract, Integer> applicableContracts = new HashMap<Contract, Integer>();
    private int cevaTracking;

    Claim claim;
    
	private BeanLocator beanLocator = new BeanLocator();
    
    public OEMPartReplacedBean(PartTaskBean partTaskBean) {
        this.partTaskBean = partTaskBean;
        this.partTaskIds.add(partTaskBean.getId());
        this.partReturnTasks.add(this.partTaskBean);
        this.oemPartReplaced = partTaskBean.getPart();
        this.claim = partTaskBean.getClaim();
    }

    public OEMPartReplacedBean() {
        // default constructor
    }

    public List<PartTaskBean> getPartReturnTasks() {
        return partReturnTasks;
    }

    public void setPartReturnTasks(List<PartTaskBean> partReturnTasks) {
        this.partReturnTasks = partReturnTasks;
    }

    public List<Long> getPartTaskIds() {
        return partTaskIds;
    }

    public void setPartTaskIds(List<Long> partTaskIds) {
        this.partTaskIds = partTaskIds;
    }

    public int getToBeShipped() {
        return toBeShipped;
    }

    public void setToBeShipped(int toBeShipped) {
        this.toBeShipped = toBeShipped;
    }

    public int getShipped() {
        return shipped;
    }

    public void setShipped(int shipped) {
        this.shipped = shipped;
    }

    public int getTotalNoOfParts() {
        return totalNoOfParts;
    }

    public void setTotalNoOfParts(int totalNoOfParts) {
        this.totalNoOfParts = totalNoOfParts;
    }

    public OEMPartReplaced getOemPartReplaced() {
        return oemPartReplaced;
    }

    public void setOemPartReplaced(OEMPartReplaced oemPartReplaced) {
        this.oemPartReplaced = oemPartReplaced;
    }

    public PartTaskBean getPartTaskBean() {
        return partTaskBean;
    }

    public void setPartTaskBean(PartTaskBean partTaskBean) {
        this.partTaskBean = partTaskBean;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

	public int getCannotShip() {
		return this.cannotShip;
	}
	
	public int getCountOfCannotShip(){
		
			return this.cannotShip;
	}

    public void setCannotShip(int cannotShip) {
        this.cannotShip = cannotShip;
    }

	public int getShipmentGenerated() {
		return this.shipmentGenerated;
	}
	
	
	public int getCountOfShipmentGenerated() {
		
			return this.shipmentGenerated;

	}

    public void setShipmentGenerated(int shipmentGenerated) {
        this.shipmentGenerated = shipmentGenerated;
    }
    

	public int getCannotBeShipped() {
	  return this.cannotBeShipped;
	}
	
	public int getCountOfCannotBeShipped(){
		
			return this.cannotBeShipped;
	}
	

	public void setCannotBeShipped(int cannotBeShipped) {
		this.cannotBeShipped = cannotBeShipped;
	}

	public int getShip() {
		return this.ship;
	}
	
	public List<Contract> getListOfApplicableContracts(){
		List<Contract> contracts = new ArrayList<Contract>();
		contracts.addAll(this.applicableContracts.keySet());
		return contracts;
	}
	
	public int getCountOfShip(){
		
			return this.ship;
	}

	
    

    public void setShip(int ship) {
        this.ship = ship;
    }

    public long getPartReplacedId() {
    	if(oemPartReplaced != null && oemPartReplaced.getId() != null)
    		return this.oemPartReplaced.getId(); 
    	return partReplacedId;
    }

    public void setPartReplacedId(long partReplacedId) {
        this.partReplacedId = partReplacedId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getReceive() {
        return receive;
    }

    public void setReceive(int receive) {
        this.receive = receive;
    }

    public int getDidNotReceive() {
        return didNotReceive;
    }

    public void setDidNotReceive(int didNotReceive) {
        this.didNotReceive = didNotReceive;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getNotReceived() {
        return notReceived;
    }

    public void setNotReceived(int notReceived) {
        this.notReceived = notReceived;
    }

    public List<String> getActionsTaken() {
        return actionsTaken;
    }

    public void setActionsTaken(List<String> actionsTaken) {
        this.actionsTaken = actionsTaken;
    }

    public boolean isToBeInspected() {
        return toBeInspected;
    }

    public void setToBeInspected(boolean toBeInspected) {
        this.toBeInspected = toBeInspected;
    }

    public boolean isToBeScrapped() {
		return toBeScrapped;
	}

	public void setToBeScrapped(boolean toBeScrapped) {
		this.toBeScrapped = toBeScrapped;
	}

	public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public int getInspected() {
        return inspected;
    }

    public void setInspected(int inspected) {
        this.inspected = inspected;
    }

	public int getAccepted() {
		return this.accepted;
	}
	
	public int getCountOfAccepted(){
		
			return this.accepted;
	}

       public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

	public int getRejected() {
		return this.rejected;
	}
	
	public int getCountOfRejected(){
		
			return this.rejected;
	}
	
	public int getCountOfToBeShipped() {
		this.toBeShipped = 0;
		for (PartTaskBean partTaskBean : this.getPartReturnTasks()) {
			if (partTaskBean != null && partTaskBean.getPartReturn() != null) {
				if (partTaskBean.isSelected()
						&& PartReturnStatus.PART_TO_BE_SHIPPED.equals(partTaskBean.getPartReturn().getStatus())) {
					this.toBeShipped++;
				}
			}
		}
		/*for (PartReturn partReturn : oemPartReplaced.getPartReturns()) {
			if (PartReturnStatus.PART_TO_BE_SHIPPED.equals(partReturn
					.getStatus())) {
				this.toBeShipped++;
			}
		}*/
		return this.toBeShipped;
	}
	 
    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    public List<String> getFailureCauses() {
        return failureCauses;
    }

    public void setFailureCauses(List<String> failureCauses) {
        this.failureCauses = failureCauses;
    }

    public List<String> getAcceptanceCauses() {
        return acceptanceCauses;
    }

    public void setAcceptanceCauses(List<String> acceptanceCauses) {
        this.acceptanceCauses = acceptanceCauses;
    }

    public boolean isMultipleFailureCauses() {
        return multipleFailureCauses;
    }

    public void setMultipleFailureCauses(boolean multipleFailureCauses) {
        this.multipleFailureCauses = multipleFailureCauses;
    }

    public boolean isMultipleAcceptanceCauses() {
        return multipleAcceptanceCauses;
    }

    public void setMultipleAcceptanceCauses(boolean multipleAcceptanceCauses) {
        this.multipleAcceptanceCauses = multipleAcceptanceCauses;
    }

    public int getQtyForShipment() {
        return qtyForShipment;
    }

    public void setQtyForShipment(int qtyForShipment) {
        this.qtyForShipment = qtyForShipment;
    }

	public Map<Contract, Integer> getApplicableContracts() {
		return applicableContracts;
	}

	public void setApplicableContracts(Map<Contract, Integer> applicableContracts) {
		this.applicableContracts = applicableContracts;
	}
	
    public int getCevaTracking() {
        return cevaTracking;
    }

    public void setCevaTracking(int cevaTracking) {
        this.cevaTracking = cevaTracking;
    }

}
