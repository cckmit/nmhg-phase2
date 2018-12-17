/**
 * 
 */
package tavant.twms.web.admin.failuretype;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.admin.failuretype.FailureTypeAssocSerializer.IllegalFailureTypeDataException;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@SuppressWarnings("serial")
public class ManageFailureTypesAction extends I18nActionSupport implements Preparable, FailureTypeAssocConstants {

	private CatalogService catalogService;
	private FailureStructureService failureStructureService;
	private Long itemGroupId;
	private ItemGroup itemGroup;
	private String jsonString;
	private String updatedTree;
	private String startingWith;
	private String label;
	private String description;
    //TODO: This is only temporary. Implement pagination logic in Combo ASAP.
    private static final PageSpecification PAGE = new PageSpecification(0, 1000);
	private FailureTypeAssocSerializer failureTypeAssocSerializer;
	private String failureContext; // Added to identify Failure Cause and Failure Root Cause contexts
	private ConfigParamService configParamService; 
	
	public static final Logger logger = Logger.getLogger(ManageFailureTypesAction.class);

	public void prepare() throws Exception {
		Long idToBeUsed = null;
		if(itemGroupId != null) {
			idToBeUsed = itemGroupId;
		} else if(itemGroup != null && itemGroup.getId() != null) {
			idToBeUsed = itemGroup.getId();
		}
		 
		if(idToBeUsed != null) {
			itemGroup = catalogService.findItemGroup(itemGroupId); 
		}
	}

	public String execute() {
		return SUCCESS;
	}

	public String populateTreeData() throws JSONException {
		jsonString = failureTypeAssocSerializer.serialize(itemGroup, getFailureContext());
		return SUCCESS;
	}	
	
	public String fetchFailureTypes() throws JSONException {
		Collection<FailureTypeDefinition> failureTypeDefinitions =
            failureStructureService.fetchFailureTypesStartingWith(startingWith, PAGE).getResult();
    	JSONArray definitions = new JSONArray();
    	for(FailureTypeDefinition ft : failureTypeDefinitions) {
    		definitions.put(new JSONArray().put(ft.getName()).put(ft.getId()));
    	}
    	jsonString = definitions.toString();
    	return SUCCESS;
	}
	
	public String fetchFailureCauses() throws JSONException {
		Collection<FailureCauseDefinition> failureCauseDefinitions =
            failureStructureService.fetchFailureCausesStartingWith(startingWith, PAGE).getResult();
		JSONArray definitions = new JSONArray();
		for(FailureCauseDefinition cause : failureCauseDefinitions) {
			definitions.put(new JSONArray().put(cause.getName()).put(cause.getId()));
    	}
		jsonString = definitions.toString();
		return SUCCESS;
	}
	
	
	public String fetchFailureRootCauses() throws JSONException {
		Collection<FailureRootCauseDefinition> failureRootCauseDefinitions =
            failureStructureService.fetchFailureRootCausesStartingWith(startingWith, PAGE).getResult();
		JSONArray definitions = new JSONArray();
		for(FailureRootCauseDefinition cause : failureRootCauseDefinitions) {
			definitions.put(new JSONArray().put(cause.getName()).put(cause.getId()));
    	}
		jsonString = definitions.toString();
		return SUCCESS;
	}
	
	public String saveFailureType() throws JSONException {
		FailureTypeDefinition def = new FailureTypeDefinition();
		def.setCode("CODE_"+label);
		def.setDescription(description);
		def.setName(label);
		def = (FailureTypeDefinition) failureStructureService.saveAndReturnObject(def);
		jsonString = new JSONArray().put(def.getName()).put(def.getId()).toString();
		return SUCCESS;
	}

	public String saveFailureRootCause() throws JSONException {
		FailureRootCauseDefinition def = new FailureRootCauseDefinition();
		def.setCode("CODE_"+label);
		def.setDescription(description);
		def.setName(label);
		def = (FailureRootCauseDefinition) failureStructureService.saveAndReturnObject(def);
		jsonString = new JSONObject().put(LABEL, def.getName()).put(ID, def.getId()).toString();
		return SUCCESS;
	}
	
	public String saveFailureCause() throws JSONException {
		FailureCauseDefinition def = new FailureCauseDefinition();
		def.setCode("CODE_"+label);
		def.setDescription(description);
		def.setName(label);
		def = (FailureCauseDefinition) failureStructureService.saveAndReturnObject(def);
		jsonString = new JSONObject().put(LABEL, def.getName()).put(ID, def.getId()).toString();
		return SUCCESS;
	}
	
	public String saveTreeData() throws JSONException {
		try {
			assert itemGroup != null;
			failureTypeAssocSerializer.deserialize(updatedTree, itemGroup, getFailureContext());
		} catch (IllegalFailureTypeDataException e) {
			logger.error(e.getMessage());
			addActionError("message.manageFailureAssoc.failureAssocPersistFailure");
			jsonString = failureTypeAssocSerializer.serialize(itemGroup, getFailureContext());
			return INPUT;
		}
		addActionMessage("message.manageFailureAssoc.failureAssocPersistSuccess");
		jsonString = failureTypeAssocSerializer.serialize(itemGroup, getFailureContext());
		if ( getFailureContext().equalsIgnoreCase(FAILURE_CONTEXT_CAUSE)) {
			return SUCCESS;
		} else {
			return ROOT_CAUSE_RESULT;
		}
	}
	
	public boolean isCreateModifyAllowed(){
		return this.configParamService.getBooleanValue(ConfigName.ALLOW_CREATE_MODIFY_FAULT_FOUND_CAUSED_BY.getName());
	}

	// Only getters and setters follow....
	public Long getItemGroupId() {
		return itemGroupId;
	}

	public void setItemGroupId(Long itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public List<ItemGroup> getItemGroups() {
		return catalogService.findAllItemGroups();
	}

	public ItemGroup getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(ItemGroup itemGroup) {
		this.itemGroup = itemGroup;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getStartingWith() {
		return startingWith;
	}

	public void setStartingWith(String startingWith) {
		this.startingWith = startingWith;
	}

	public String getUpdatedTree() {
		return updatedTree;
	}

	public void setUpdatedTree(String updatedTree) {
		this.updatedTree = updatedTree;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFailureTypeAssocSerializer(
			FailureTypeAssocSerializer failureTypeAssocSerializer) {
		this.failureTypeAssocSerializer = failureTypeAssocSerializer;
	}

	public String getFailureContext() {
		return failureContext;
	}

	public void setFailureContext(String failureContext) {
		this.failureContext = failureContext;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
}
