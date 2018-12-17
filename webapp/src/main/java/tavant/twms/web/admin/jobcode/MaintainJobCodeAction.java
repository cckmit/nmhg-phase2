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
package tavant.twms.web.admin.jobcode;

import com.opensymphony.xwork2.Preparable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.*;
import tavant.twms.infra.PageSpecification;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.*;
import tavant.twms.web.i18n.I18nActionSupport;
import java.util.Collection;
import java.util.List;



/**
 * @author janmejay.singh, vikas.sasidharan
 * Date: Nov 23, 2006
 * Time: 6:26:04 PM
 */
public class MaintainJobCodeAction extends I18nActionSupport implements Preparable {

    private CatalogService catalogService;
    private AssemblyTreeJSONifier assemblyTreeJsonifier;
    private FailureStructureService failureStructureService;
    private ItemGroupService itemGroupService;    
	private ConfigParamService configParamService;
    private ItemGroup itemGroupId;   
	private String copyToItemGroupId;
    private String updatedTree;
    private FailureStructure failureStructure;
    private FailureStructure copyTofailureStructure;
   
	private String treadData;

    //ajaxing variables...
    private String jsonString;
    private String startingWith;
    private int depth;
    private String label;

    private static final PageSpecification PAGE = new PageSpecification(0, 250);

    @Required
    public void setAssemblyTreeJsonifier(AssemblyTreeJSONifier assemblyTreeJsonifier) {
        this.assemblyTreeJsonifier = assemblyTreeJsonifier;
    }

    @Required
    public void setCatalogService(final CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Required
    public void setFailureStructureService(FailureStructureService failureStructureService) {
        this.failureStructureService = failureStructureService;
    }

    /////////////////Actions...
    @Override
    public String execute() throws JSONException {
        populateTreadData();
        return SUCCESS;
    }

    public String fetchFailureStructure() throws JSONException {
        jsonString = assemblyTreeJsonifier.getSerializedJSONString(failureStructure);
        return SUCCESS;
    }

    public String fetchAssemblyDefinitions() throws JSONException {
        Collection<AssemblyDefinition> asmDefs =
                failureStructureService.findAssemblyDefinitions(startingWith, depth, PAGE,getLoggedInUser().getLocale().toString()).getResult();
        JSONArray definitions = new JSONArray();
        for(AssemblyDefinition def : asmDefs) {
            definitions.put(new JSONArray().put(getFormattedLabel(def)).put(def.getId()));
        }
        jsonString = definitions.toString();
        return SUCCESS;
    }

    public String fetchActionDefinitions() throws JSONException {
        Collection<ActionDefinition> actionDefs =
                failureStructureService.findActionDefinitions(startingWith, PAGE,getLoggedInUser().getLocale().toString()).getResult();
        JSONArray definitions = new JSONArray();
        for(ActionDefinition def : actionDefs) {
            definitions.put(new JSONArray().put(getFormattedLabel(def)).put(def.getId()));
        }
        jsonString = definitions.toString();
        return SUCCESS;
    }

    public String createAssemblymDefinition() throws JSONException {
        AssemblyDefinition newDef = failureStructureService.createAssemblyDefintion(label, depth);
        jsonString = new JSONObject().put(LABEL, getFormattedLabel(newDef)).put(ID, newDef.getId()).toString();
        return SUCCESS;
    }

    public String createActionDefinition() throws JSONException {
    	ActionDefinition newDef = failureStructureService.createActionDefinition(label);
        jsonString = new JSONObject().put(LABEL, getFormattedLabel(newDef)).put(ID, newDef.getId()).toString();
        return SUCCESS;
    }

    public String saveFailureStructure() throws JSONException {
        jsonString = updatedTree;
        ItemGroup copyToItemGroup;
        try {
        	
			assemblyTreeJsonifier.updateFailureStructure(updatedTree, failureStructure);
			if((copyToItemGroupId != null && !(copyToItemGroupId.trim().equals("")))){
        		copyToItemGroup = new ItemGroup();
        		Long groupId = Long.parseLong(copyToItemGroupId);
        		copyToItemGroup = itemGroupService.findById(groupId);
        		 copyTofailureStructure = failureStructureService.getFailureStructureForItemGroup(copyToItemGroup);
        	        if(copyTofailureStructure == null) {
        	            copyTofailureStructure = new FailureStructure();
       	                copyTofailureStructure.setForItemGroup(copyToItemGroup);
        	        }
        	      
        		assemblyTreeJsonifier.updateFailureStructure(updatedTree, copyTofailureStructure);
        	}
                      
        } catch (AssemblyTreeIllegalDataException e) {
            addActionError(e.getMessage());
        }
        failureStructureService.update(failureStructure);
        if(copyTofailureStructure!=null){
        failureStructureService.update(copyTofailureStructure);
        }
        if(!hasActionErrors()) {
            addActionMessage("message.manageFailureStructure.failureStructurePersistSuccess");
            jsonString = assemblyTreeJsonifier.getSerializedJSONString(failureStructure);
        }
        populateTreadData();
        return SUCCESS;
    }
    ///////////////////..........done

    private void populateTreadData() throws JSONException {
        JSONArray treadData = new JSONArray();
        for(TreadBucket treadBucket : failureStructureService.findTreadBuckets()) {
            treadData.put((new JSONArray()).put(treadBucket.getDescription()).put(treadBucket.getCode()));
        }
        this.treadData = treadData.toString();
    }

    public ItemGroup getItemGroupId() {
        return this.itemGroupId;
    }

	 public String getCopyToItemGroupId() {
        return this.copyToItemGroupId;
    }
    public String getTreadData() {
        return treadData;
    }

    public void setItemGroupId(final ItemGroup itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public void setCopyToItemGroupId(String copyToItemGroupId) {
        this.copyToItemGroupId = copyToItemGroupId;
    }
    
    public List<ItemGroup> getItemGroups() {
        return catalogService.findAllItemModels();
    }

    public List<ItemGroup> listProductsAndModels() {
        return catalogService.findAllItemProductsAndModels(getSearchPrefix().toUpperCase() , 0, 10);
    }

    public FailureStructure getcopyTofailureStructure() {
		return copyTofailureStructure;
	}

	public void setcopyTofailureStructure(FailureStructure copyTofailureStructure) {
		this.copyTofailureStructure = copyTofailureStructure;
	}
    
    public String getJsonString() {
        return jsonString;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setStartingWith(String startingWith) {
        this.startingWith = startingWith;
    }

    public void setUpdatedTree(String updatedTree) {
        this.updatedTree = updatedTree;
    }

    public void prepare() throws Exception {
        //ItemGroup itemGroup = catalogService.findItemGroup(itemGroupId);
        failureStructure = failureStructureService.getFailureStructureForItemGroup(itemGroupId);
        if(failureStructure == null) {
            failureStructure = new FailureStructure();
            failureStructure.setForItemGroup(itemGroupId);
        }
    }
    
    public boolean isCreateModifyFaultCodeAllowed(){
    	 return this.configParamService.getBooleanValue(ConfigName.ALLOW_CREATE_MODIFY_FAULT_CODE.getName());
    }

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
    public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	
}