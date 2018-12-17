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
package tavant.twms.web.complaints;

import com.opensymphony.xwork2.Preparable;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.complaints.Complaint;
import tavant.twms.domain.complaints.ComplaintsService;
import tavant.twms.domain.complaints.CountryState;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.failurestruct.ActionNode;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ASM_CHILDREN;
import tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.Filter;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewComplaintAction extends I18nActionSupport implements Preparable {

	public static final String SERIAL_INFO_TYPE = "serial";
	
	public static final String ITEM_INFO_TYPE = "item";
	
	public static final String PRODUCT_INFO_TYPE = "product"; 
	
	public static final String US_COUNTRY_KEY = "USA";
	
	private Complaint complaint;
		
	private String vehicleInfoType;
	
	private String vehicleInfoValue;
	
	private String model;
	
	private String year;
	
	private ComplaintsService complaintsService;
	
	private InventoryService inventoryService;
	
	private CatalogService catalogService;

	private ItemReference itemReference;
	
	private String complaintType;
	
	private CountryStateService countryStateService;
	
	private List<CountryState> usStates = new ArrayList<CountryState>();
	
	private List<CountryState> canadaStates = new ArrayList<CountryState>();
	
	private AssemblyTreeJSONifier asmTreeJSONifier;

	private FailureStructureService failureStructureService;
	
	public void prepare() throws Exception {
        fetchAndSetUpStates();
    }
	
	public String showForm() {
        return SUCCESS;
    }
	
	public String showCompleteForm() throws Exception {
		fetchAndUpdateItemReference();
		return SUCCESS;
	}	 
	
	public String saveComplaint() throws Exception{
		fetchAndUpdateItemReference();
		complaint.setComplaintType(complaintType);
		if(itemReference == null) {
			complaint.setProduct(vehicleInfoValue);
			complaint.setModel(model);			
			complaint.setYear(year);			
		}else {
			complaint.setItemReference(itemReference);
		}
		complaintsService.logAComplaint(complaint);
		addActionMessage("message.complaints." + complaintType + ".createSuccess");
		return SUCCESS;
	}

	public String showInitialUpdatePage() {		
		if(complaint.getItemReference() == null) {
			this.vehicleInfoType = PRODUCT_INFO_TYPE;
			this.vehicleInfoValue = complaint.getProduct();
		}	
		else if(complaint.getItemReference().getReferredInventoryItem() == null){
			this.vehicleInfoType = ITEM_INFO_TYPE;
			this.vehicleInfoValue = complaint.getItemNumber();
		}else {			
			this.vehicleInfoType = SERIAL_INFO_TYPE;
			this.vehicleInfoValue = complaint.getSerialNumber();			
		}
		return SUCCESS;
	}
	
	// TODO : This needs optimisations.
	public String showCompleteUpdatePage()throws Exception {
		fixItemReferenceForUpdate();
		return SUCCESS;
	}
	
	public String updateComplaint() throws Exception{
		fixItemReferenceForUpdate();
		complaintsService.updateComplaint(complaint);
		addActionMessage("message.complaints." + complaint.getComplaintType() + ".updateSuccess");
		return SUCCESS;			
	}
	
	private void fixItemReferenceForUpdate() throws Exception{
		if(PRODUCT_INFO_TYPE.equals(vehicleInfoType)) {
			complaint.setItemReference(null);
			complaint.setProduct(vehicleInfoValue);
			complaint.setModel(model);			
			complaint.setYear(year);						
		}else if(ITEM_INFO_TYPE.equals(vehicleInfoType)) {
			complaint.getItemReference().setReferredItem(catalogService.findItemOwnedByManuf(vehicleInfoValue));
		}else {
			complaint.getItemReference().setReferredInventoryItem(inventoryService.findSerializedItem(vehicleInfoValue));
		}		
	}
	
	// TODO : Need to refactor - code duplication ClaimsAction
	public String getJsonFaultCodeTree() throws JSONException {
		Filter filter = new Filter() {

			public boolean preTestNode(Assembly assembly) {
				if (assembly.getComposedOfAssemblies() != null
						&& assembly.getComposedOfAssemblies().size() > 0) {
					return true;
				}
				return assembly.isFaultCode();
			}

			public boolean preTestNode(ActionNode actionNode) {
				return false;
			}

			public boolean postTestNode(JSONObject node, Assembly assembly)
					throws JSONException {
				if (node.getJSONArray(ASM_CHILDREN).length() > 0) {
					return true;
				}
				return assembly.isFaultCode();
			}

			public boolean postTestNode(JSONObject node,
					ActionNode actionNode) {
				return false;
			}

			public boolean includeFaultCodeInfo() {
				return true;
			}
		};
		return asmTreeJSONifier.getSerializedJSONString(getFailureStructure(),
				filter, null);
	}
	
	
	public void setComplaintsService(ComplaintsService complaintsService) {
		this.complaintsService = complaintsService;
	}

	public ItemReference getItemReference() {
		return itemReference;
	}

	public void setItemReference(ItemReference itemReference) {
		this.itemReference = itemReference;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVehicleInfoType() {
		return vehicleInfoType;
	}

	public void setVehicleInfoType(String vehicleInfoType) {
		this.vehicleInfoType = vehicleInfoType;
	}

	public String getVehicleInfoValue() {
		return vehicleInfoValue;
	}

	public void setVehicleInfoValue(String vehicleInfoValue) {
		this.vehicleInfoValue = vehicleInfoValue;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}
	public Complaint getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}
	
	@Required
	public void setAssemblyTreeJsonifier(AssemblyTreeJSONifier jsonifier) {
		this.asmTreeJSONifier = jsonifier;
	}
	
	@Required
	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}
		
	private ItemReference fetchAndUpdateItemReference() throws Exception{
		if(SERIAL_INFO_TYPE.equals(vehicleInfoType)) {
			itemReference = new ItemReference(inventoryService.findSerializedItem(vehicleInfoValue));
		}else if(ITEM_INFO_TYPE.equals(vehicleInfoType)) {
			itemReference = new ItemReference(catalogService.findItemOwnedByManuf(vehicleInfoValue));
		}
		return itemReference;
	}
	
	// assumes only US and Canada states are stored in the DB.
	private void fetchAndSetUpStates() {
		List<CountryState> allStates = countryStateService.fetchAllStates();
		for (Iterator iter = allStates.iterator(); iter.hasNext();) {
			CountryState state = (CountryState) iter.next();
			if(US_COUNTRY_KEY.equals(state.getCountry())) {
				usStates.add(state);			
			}else {
				canadaStates.add(state);
			}			
		}
	}
	
	// TODO : Need to revisit this.
	private FailureStructure getFailureStructure() {
		if(itemReference != null && itemReference.getUnserializedItem() != null ) {
			return failureStructureService.getFailureStructureForItem(itemReference.getUnserializedItem());
		}		
		return null;
	}
	

	public void setCountryStateService(CountryStateService countryStateService) {
		this.countryStateService = countryStateService;
	}

	public List<CountryState> getCanadaStates() {
		return canadaStates;
	}

	public List<CountryState> getUsStates() {
		return usStates;
	}

	public String getComplaintType() {
		return complaintType;
	}

	public void setComplaintType(String complaintType) {
		this.complaintType = complaintType;
	}
}
