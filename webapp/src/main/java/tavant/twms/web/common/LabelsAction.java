/**
 * 
 */
package tavant.twms.web.common;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.classic.Validatable;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAdminService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.LabelService;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FaultCodeDefinition;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.RetailItemsService;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.web.i18n.I18nActionSupport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@SuppressWarnings("serial")
public class LabelsAction extends I18nActionSupport implements Validatable, ServletResponseAware {

	private List<Long> dataIds;
	private List<String> chosenLabels = new ArrayList<String>();
	private LabelService labelService;
	private String jsonString;
	private String searchPrefix;
	private PolicyDefinitionService policyDefinitionService;
	private RetailItemsService retailItemsService;
	private FailureStructureService failureStructureService;
	private String createLabel;
    private ItemGroupService itemGroupService;
    private ItemGroupRepository itemGroupRepository;
    private String labelType;
    private CampaignAdminService campaignAdminService;
    private WarehouseService warehouseService;

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public void setLabelService(LabelService labelService) {
		this.labelService = labelService;
	}

	public String listLabels() {
		
		List<String> names = labelService.findLabelsWithNameAndTypeLike(searchPrefix, getLabelType(), 0, 10);
        return generateAndWriteComboboxJson(names);
	}
	
	public String applyLabelsOnCampaign() {
		List<Label> labels;
		try {
			labels = initLabels(Label.CAMPAIGN);
			List<Campaign> campaigns = campaignAdminService
					.findByIds(dataIds);
			for (Campaign campaign : campaigns) {
				for (Label theLabel : labels) {
					if (!campaign.getLabels().contains(theLabel)) {
						campaign.getLabels().add(theLabel);
						campaignAdminService.update(campaign);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.campaignLabelAdded");
		return SUCCESS;
	}

	public String removeLabelsOnCampaign() {
		List<Label> labels;
		try {
			labels = initLabels(Label.CAMPAIGN);
			List<Campaign> campaigns = campaignAdminService.findByIds(dataIds);
			for (Campaign campaign : campaigns) {
				for (Label theLabel : labels) {
					if (campaign.getLabels().contains(theLabel)) {
						campaign.getLabels().remove(theLabel);
						campaignAdminService.update(campaign);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.campaignLabelRemoved");
		return SUCCESS;
	}

	public String applyLabelsOnPolicy() {
		List<Label> labels;
		try {
			labels = initLabels(Label.POLICY);
			List<PolicyDefinition> definitions = policyDefinitionService
					.findByIds(dataIds);
			for (PolicyDefinition definition : definitions) {
				for (Label theLabel : labels) {
					if (!definition.getLabels().contains(theLabel)) {
						definition.getLabels().add(theLabel);
						policyDefinitionService.update(definition);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("Labels have been added on the selected policies.");
		return SUCCESS;
	}

	public String removeLabelsOnPolicy() {
		List<Label> labels;
		try {
			labels = initLabels(Label.POLICY);
			List<PolicyDefinition> definitions = policyDefinitionService.findByIds(dataIds);
			for (PolicyDefinition definition : definitions) {
				for (Label theLabel : labels) {
					if (definition.getLabels().contains(theLabel)) {
						definition.getLabels().remove(theLabel);
						policyDefinitionService.update(definition);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.policyLabelRemoved");
		return SUCCESS;
	}

	public String applyLabelsOnRetail() {
		List<Label> labels;
		try {
			labels = initLabels(Label.INVENTORY);
			List<InventoryItem> invList = retailItemsService.findByIds(dataIds);
			for (InventoryItem inventoryItem : invList) {
				for (Label theLabel : labels) {
					if (!inventoryItem.getLabels().contains(theLabel)) {
						inventoryItem.getLabels().add(theLabel);
						retailItemsService.update(inventoryItem);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.itemLabelAdded");
		return SUCCESS;
	}

	public String removeLabelsOnRetail() {
		List<Label> labels;
		try {
			labels = initLabels(Label.INVENTORY);
			List<InventoryItem> invList = retailItemsService.findByIds(dataIds);
			for (InventoryItem inventoryItem : invList) {
				for (Label theLabel : labels) {
					if (inventoryItem.getLabels().contains(theLabel)) {
						inventoryItem.getLabels().remove(theLabel);
						retailItemsService.update(inventoryItem);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.itemLabelRemoved");
		return SUCCESS;
	}

	public String applyLabelsOnFaultCode() {
		List<Label> labels;
		try {
			labels = initLabels(Label.FAULT_CODE_DEFINITION);
			List<FaultCodeDefinition> faultCodeDefs = failureStructureService
					.findFaultCodeDefinitionsByIds(dataIds);
			for (FaultCodeDefinition definition : faultCodeDefs) {
				for (Label theLabel : labels) {
					if (!definition.getLabels().contains(theLabel)) {
						definition.getLabels().add(theLabel);
						failureStructureService
								.updateFaultCodeDefinition(definition);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.faultCodeLabelAdded");
		return SUCCESS;
	}
	
	public String removeLabelsOnFaultCode() {
		List<Label> labels;
		try {
			labels = initLabels(Label.FAULT_CODE_DEFINITION);
			List<FaultCodeDefinition> faultCodeDefs = failureStructureService
					.findFaultCodeDefinitionsByIds(dataIds);
			for (FaultCodeDefinition definition : faultCodeDefs) {
				for (Label theLabel : labels) {
					if (definition.getLabels().contains(theLabel)) {
						definition.getLabels().remove(theLabel);
						failureStructureService
								.updateFaultCodeDefinition(definition);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.faultCodeLabelRemoved");
		return SUCCESS;
	}

	public String applyLabelsOnJobCode() {
		List<Label> labels;
		try {
			labels = initLabels(Label.SERVICE_PROCEDURE_DEFINITION);
			List<ServiceProcedureDefinition> spdz = failureStructureService
					.findServiceProcedureDefinitionsByIds(dataIds);
			for (ServiceProcedureDefinition definition : spdz) {
				for (Label theLabel : labels) {
					if (!definition.getLabels().contains(theLabel)) {
						definition.getLabels().add(theLabel);
						failureStructureService
								.updateServiceProcedureDefinition(definition);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.jobCodeLabelAdded");
		return SUCCESS;
	}

	public String removeLabelsOnJobCode() {
		List<Label> labels;
		try {
			labels = initLabels(Label.SERVICE_PROCEDURE_DEFINITION);
			List<ServiceProcedureDefinition> spdz = failureStructureService
					.findServiceProcedureDefinitionsByIds(dataIds);
			for (ServiceProcedureDefinition definition : spdz) {
				for (Label theLabel : labels) {
					if (definition.getLabels().contains(theLabel)) {
						definition.getLabels().remove(theLabel);
						failureStructureService
								.updateServiceProcedureDefinition(definition);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.jobCodeLabelRemoved");
		return SUCCESS;
	}
	
	public String applyLabelsOnWarehouse() {
		List<Label> labels;
		try {
			labels = initLabels(Label.WAREHOUSE);
			List<Warehouse> warehouses = warehouseService.findByIds(dataIds);
			for (Warehouse warehouse : warehouses) {
				for (Label theLabel : labels) {
					if (!warehouse.getLabels().contains(theLabel)) {
						warehouse.getLabels().add(theLabel);
						warehouseService.update(warehouse);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.warehouseAdded");
		return SUCCESS;
	}
	
	public String removeLabelsOnWarehouse() {
		List<Label> labels;
		try {
			labels = initLabels(Label.WAREHOUSE);
			List<Warehouse> warehouses = warehouseService.findByIds(dataIds);
			for (Warehouse warehouse : warehouses) {
				for (Label theLabel : labels) {
					if (warehouse.getLabels().contains(theLabel)) {
						warehouse.getLabels().remove(theLabel);
						warehouseService.update(warehouse);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.warehouseLabelRemoved");
		return SUCCESS;
	}

	/**
	 * This api actually fetches the labels based on their names. If a label by
	 * the name does not exist then it is created and added to the list of
	 * labels that gets returned.
	 * 
	 * @return
	 * @throws Exception
	 * @throws Exception
	 * @throws Exception
	 */
	private List<Label> initLabels(String type) throws Exception {
		List<Label> labels = new ArrayList<Label>();
		for (String alabel : chosenLabels) {
			String labelName = alabel.trim();
			Label labelById = labelService.findById(labelName);
			if (labelById == null) {
				labelService.save(new Label(labelName,type));
				labelById = labelService.findById(labelName);
			}
			labels.add(labelById);
		}
		return labels;
	}

	@Override
	public void validate() {
		if (chosenLabels == null || chosenLabels.isEmpty()) {
			addActionError("error.common.pleaseSelectLabel");
		}
	}

	public List<Long> getDataIds() {
		return dataIds;
	}

	public void setDataIds(List<Long> dataIds) {
		this.dataIds = dataIds;
	}

	public List<String> getChosenLabels() {
		return chosenLabels;
	}

	public void setChosenLabels(List<String> chosenLabels) {
		this.chosenLabels = chosenLabels;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getSearchPrefix() {
		return searchPrefix;
	}

	public void setSearchPrefix(String searchPrefix) {
		this.searchPrefix = searchPrefix;
	}

	public void setPolicyDefinitionService(
			PolicyDefinitionService policyDefinitionService) {
		this.policyDefinitionService = policyDefinitionService;
	}

	public void setCampaignAdminService(CampaignAdminService campaignAdminService) {
		this.campaignAdminService = campaignAdminService;
	}

	public void setRetailItemsService(RetailItemsService retailItemsService) {
		this.retailItemsService = retailItemsService;
	}

	public String applyLabelsOnSupplier() {
		List<Label> labels;
		try {
			labels = initLabels(Label.SUPPLIER);
			List<Party> parties = orgService.findByIds(dataIds);
			for (Party party : parties) {
				Supplier supplier = new HibernateCast<Supplier>().cast(party);
				for (Label theLabel : labels) {
					if (!supplier.getLabels().contains(theLabel)) {
						supplier.getLabels().add(theLabel);
						orgService.updateOrganization(supplier);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.supplierLabelAdded");
		return SUCCESS;
	}

	public String removeLabelsOnSupplier() {
		List<Label> labels;
		try {
			labels = initLabels(Label.SUPPLIER);
			List<Party> parties = orgService.findByIds(dataIds);
			for (Party party : parties) {
				Supplier supplier = new HibernateCast<Supplier>().cast(party);
				for (Label theLabel : labels) {
					if (supplier.getLabels().contains(theLabel)) {
						supplier.getLabels().remove(theLabel);
						orgService.updateOrganization(supplier);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.supplierLabelRemoved");
		return SUCCESS;
	}

    public String applyLabelsOnModel() {
		List<Label> labels;
		try {
			labels = initLabels(Label.MODEL);
			List<ItemGroup> models = itemGroupRepository.findByIds(dataIds);
			for (ItemGroup model : models) {
				for (Label theLabel : labels) {
					if (!model.getLabels().contains(theLabel)) {
						model.getLabels().add(theLabel);
						itemGroupService.update(model);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.success.labelAddedOnModel");
		return SUCCESS;
	}

	public String removeLabelsOnModel() {
		List<Label> labels;
		try {
			labels = initLabels(Label.MODEL);
			List<ItemGroup> models = itemGroupRepository.findByIds(dataIds);
			for (ItemGroup model : models) {
				for (Label theLabel : labels) {
					if (model.getLabels().contains(theLabel)) {
						model.getLabels().remove(theLabel);
						itemGroupService.update(model);
					}
				}
			}
		} catch (Exception e) {
			addActionError("error.common.internalError");
			return INPUT;
		}
		addActionMessage("message.common.modelLabelRemoved");
		return SUCCESS;
	}

    public String checkIfLabelExists() throws IOException{
			Label label = labelService.findLabelWithName(createLabel); 
			if(label!=null){
				return sendValidationResponse();
			}
		return SUCCESS;
	}

	public String getCreateLabel() {
		return createLabel;
	}

	public void setCreateLabel(String createLabel) {
		this.createLabel = createLabel;
	}
	
	private String sendValidationResponse() throws IOException {
        this.response.setHeader("Pragma", "no-cache");
        this.response.addHeader("Cache-Control", "must-revalidate");
        this.response.addHeader("Cache-Control", "no-cache");
        this.response.addHeader("Cache-Control", "no-store");
        this.response.setDateHeader("Expires", 0);
        this.response.setContentType("text/html");
        this.response.getWriter().write("<true>");
        this.response.flushBuffer();
        return null;
    }
	
	 public void setServletResponse(HttpServletResponse httpServletResponse) {
	        this.response = httpServletResponse;
	    }

    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }
    public String getLabelType() {
		return labelType;
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}
	
	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

}
