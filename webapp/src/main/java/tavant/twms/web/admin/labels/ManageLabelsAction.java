package tavant.twms.web.admin.labels;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.common.LabelService;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.*;
import tavant.twms.infra.PageResult;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.policy.*;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.failurestruct.FaultCodeDefinition;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAdminService;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemGroup;

@SuppressWarnings("serial")
public class ManageLabelsAction extends SummaryTableAction {

	private LabelService labelService;
	private InventoryService inventoryService;
	private PolicyDefinitionService policyDefinitionService;
	private SupplierService supplierService;
	private FailureStructureService failureStructureService;
    private ItemGroupService itemGroupService;
    private String labelName;
	private String labelType;
	private List<InventoryItem> items;
	private List<PolicyDefinition> policies;
	private List<Supplier> suppliers;
	private List<FaultCodeDefinition> faultCodeDefn;
	private List<ServiceProcedureDefinition> jobCodes;
    private List<ItemGroup> models;
    private List<Campaign> campaigns;
    private List<Warehouse> warehouses;
    private CampaignAdminService campaignAdminService;
    private WarehouseService warehouseService;

    public String showLabels(){
		return SUCCESS;
	}
	public String getLabelDetails(){
		Label lb = new Label();
		lb.setName(this.id);
		if(labelType.equalsIgnoreCase(LABEL_INVENTORY)){
			items = inventoryService.findAllInventoryItemsByLabel(this.id);
			if(items !=null){
				return SUCCESS;
			}
			else{
				addActionError("error.common.noInventoryLabels");
			}
		}
		if(labelType.equalsIgnoreCase(LABEL_POLICY_DEFINITION)){
			
			policies = policyDefinitionService.findPolicyDefinitionsForLabel(lb);
			if(policies !=null){
				return POLICY_LABELS;
			}
			else{
				addActionError("error.common.noPolicyLabels");
			}
		}
		if(labelType.equalsIgnoreCase(LABEL_SUPPLIER)){
			suppliers = supplierService.findSuppliersForLabel(lb);
			if(suppliers !=null){
				return SUPPLIER_LABELS;
			}
			else{
				addActionError("error.common.noSupplierLabels");
			}
		}
		if(labelType.equalsIgnoreCase(LABEL_FAULT_CODE)){
			faultCodeDefn = failureStructureService.findAllFaultCodeDefinitionsForLabel(lb);
			if(faultCodeDefn !=null){
				return FAULT_CODE_DEFN_LABELS;
			}
			else{
				addActionError("error.common.noFaultCodeLabels");
			}
		}
		if(labelType.equalsIgnoreCase(LABEL_JOB_CODE)){
			jobCodes = failureStructureService.findAllJobCodeForLabel(lb);
			if(jobCodes !=null){
				return JOB_CODE_LABELS;
			}
			else{
				addActionError("error.common.noJobCodeLabels");
			}
		}
        if(labelType.equalsIgnoreCase(LABEL_MODEL)){
			models = itemGroupService.findAllModelForLabel(lb.getName());
			if(models !=null){
				return MODEL_LABELS;
			}
			else{
				addActionError("error.common.noModelLabels");
			}
		}
        if(labelType.equalsIgnoreCase(LABEL_CAMPAIGN)){
        	campaigns = campaignAdminService.findAllCampaignsForLabel(lb);
			if(campaigns !=null){
				return CAMPAIGN_LABELS;
			}
			else{
				addActionError("error.common.noCampaignLabels");
			}
		}
        if(labelType.equalsIgnoreCase(LABEL_WAREHOUSE)){
        	warehouses = warehouseService.findAllWarehouseForLabel(lb);
			if(warehouses !=null){
				return WAREHOUSE_LABELS;
			}
			else{
				addActionError("error.common.noWarehouseLabels");
			}
		}
        return INPUT;
		
	}
	
	@Override
	protected PageResult<?> getBody() {
		PageResult<?> labels = null;
		if(labelType.equalsIgnoreCase(LABEL_INVENTORY)){
			labels = this.labelService.findAllInventoryLabels(getCriteria());
		}
		if(labelType.equalsIgnoreCase(LABEL_POLICY_DEFINITION)){
			labels = this.labelService.findAllPolicyDefintionLabels(getCriteria());
		}
		if(labelType.equalsIgnoreCase(LABEL_CAMPAIGN)){
			labels = this.labelService.findAllCampaignLabels(getCriteria());
		}
		if(labelType.equalsIgnoreCase(LABEL_SUPPLIER)){
			labels = this.labelService.findAllSupplierLabels(getCriteria());
		}
		if(labelType.equalsIgnoreCase(LABEL_FAULT_CODE)){
			labels = this.labelService.findAllFaultCodeDefinitionLabels(getCriteria());
		}
		if(labelType.equalsIgnoreCase(LABEL_JOB_CODE)){
			labels = this.labelService.findAllJobCodeDefinitionLabels(getCriteria());
		}
        if(labelType.equalsIgnoreCase(LABEL_MODEL)){
			labels = this.labelService.findAllModelLabels(getCriteria());
		}
        if(labelType.equalsIgnoreCase(LABEL_WAREHOUSE)){
        	labels = this.labelService.findAllWarehouseLabels(getCriteria());
        }
        return labels;
	 }

	@Override
	protected List<SummaryTableColumn> getHeader() {
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		this.tableHeadData.add(new SummaryTableColumn("", "name", 0, "string",
				"id", false, true, true, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"label.common.labelName", "name", 16, "string",
				"name", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"label.dcap.audit.updatedBy",
				"d.lastUpdatedBy.name", 12, "string"));
		this.tableHeadData.add(new SummaryTableColumn("label.common.dateCreated", 
				"d.createdOn", 11, "CalendarDate"));
		return this.tableHeadData;
	
	}

	public LabelService getLabelService() {
		return labelService;
	}

	public void setLabelService(LabelService labelService) {
		this.labelService = labelService;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getLabelType() {
		return labelType;
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public List<InventoryItem> getItems() {
		return items;
	}

	public void setItems(List<InventoryItem> items) {
		this.items = items;
	}

	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}

	public List<PolicyDefinition> getPolicies() {
		return policies;
	}

	public void setPolicies(List<PolicyDefinition> policies) {
		this.policies = policies;
	}
	public List<Supplier> getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}
	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}
	
	public List<FaultCodeDefinition> getFaultCodeDefn() {
		return faultCodeDefn;
	}
	public void setFaultCodeDefn(List<FaultCodeDefinition> faultCodeDefn) {
		this.faultCodeDefn = faultCodeDefn;
	}
	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}
	
	public void setJobCodes(List<ServiceProcedureDefinition> jobCodes) {
		this.jobCodes = jobCodes;
	}

    public List<ServiceProcedureDefinition> getJobCodes() {
        return jobCodes;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public List<ItemGroup> getModels() {
        return models;
    }

    public void setModels(List<ItemGroup> models) {
        this.models = models;
    }
	public List<Campaign> getCampaigns() {
		return campaigns;
	}
	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
	public void setCampaignAdminService(CampaignAdminService campaignAdminService) {
		this.campaignAdminService = campaignAdminService;
	}
	public List<Warehouse> getWarehouses() {
		return warehouses;
	}
	public void setWarehouses(List<Warehouse> warehouses) {
		this.warehouses = warehouses;
	}
	public WarehouseService getWarehouseService() {
		return warehouseService;
	}
	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}
    
}
