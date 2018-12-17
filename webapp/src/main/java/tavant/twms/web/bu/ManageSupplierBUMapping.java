package tavant.twms.web.bu;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.security.SecurityHelper;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ManageSupplierBUMapping  extends ActionSupport {
	
	private String supplierNumber;	
	private String supplierName;
	private Supplier supplier;
    private SortedSet<BusinessUnit> allBusinessUnits = new TreeSet<BusinessUnit>();
    private List<BusinessUnit> buListSelected = new ArrayList<BusinessUnit>();
	private SecurityHelper securityHelper;
	private SupplierService supplierService;
	
	public String search() {
        if (supplierNumber != null && !supplierNumber.equals("") && supplierName !=null && !supplierName.equals("")) {
            allBusinessUnits = securityHelper.getLoggedInUser().getBusinessUnits(); 
            supplier = supplierService.findSupplierByNameAndNumber(supplierName, supplierNumber);
            if (supplier == null) {
                addActionError(getText("error.resultsNotFound"));
                return INPUT;
            }
        } else {
            addActionError(getText("error.bu.supplier.mapping"));
            return INPUT;
        }        
        return SUCCESS;
    }
	
	public String update() {
    	TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
    	if (supplier.getBusinessUnits() != null){
    		businessUnitMapping.addAll(supplier.getBusinessUnits());
    	}
    	for (BusinessUnit buToBeMapped : buListSelected) {
    		if (buToBeMapped !=null) {
    			businessUnitMapping.add(buToBeMapped);                		
    		}
    	}
    	if (!businessUnitMapping.isEmpty()){
    		supplier.setBusinessUnits(businessUnitMapping);
    		supplierService.update(supplier);
    		addActionMessage(getText("message.update.supplierBuMapping"));
    		return SUCCESS;
    	}
    	return INPUT;
    }

	public boolean isBUAlreadyMapped(BusinessUnit businessUnit) {
		SortedSet<BusinessUnit> mappedBUs = supplier.getBusinessUnits();
		for (BusinessUnit mappedBU : mappedBUs) {
			if (mappedBU.getName().equals(businessUnit.getName())) {
				return true;
			}
		}
		return false;
	}


	public String getSupplierNumber() {
		return supplierNumber;
	}


	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}


	public Supplier getSupplier() {
		return supplier;
	}


	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}


	public SortedSet<BusinessUnit> getAllBusinessUnits() {
		return allBusinessUnits;
	}


	public void setAllBusinessUnits(SortedSet<BusinessUnit> allBusinessUnits) {
		this.allBusinessUnits = allBusinessUnits;
	}


	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}


	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}


	public SupplierService getSupplierService() {
		return supplierService;
	}


	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	public List<BusinessUnit> getBuListSelected() {
		return buListSelected;
	}

	public void setBuListSelected(List<BusinessUnit> buListSelected) {
		this.buListSelected = buListSelected;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}	

}
