/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.web.admin.additionalAttributes;

import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ASM_CHILDREN;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.SP_CHILDREN;

import java.util.*;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.additionalAttributes.AdditionalAttributesService;
import tavant.twms.domain.additionalAttributes.AttributeAssociation;
import tavant.twms.domain.additionalAttributes.AttributeAssociationService;
import tavant.twms.domain.additionalAttributes.AttributePurpose;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.failurestruct.ActionNode;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.web.admin.campaign.SaveCampaignAction;
import tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier;
import tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.Filter;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author pradipta.a
 */
public class AttributeAssociationAction extends I18nActionSupport {
	
	private static final Logger logger = Logger
			.getLogger(AttributeAssociationAction.class);

    private String attributePurpose;

    private AdditionalAttributesService additionalAttributesService;

    private ItemGroupService itemGroupService;

    private AdditionalAttributes additionalAttributes;
   
    private String userEntry;

    private String productName;
    
    private String number;
    
    private String id;

    private AssemblyTreeJSONifier asmTreeJSONifier;

    private FailureStructureService failureStructureService;
    
    private CatalogService catalogService;

    private String modelId;
    String jsonString;
    
    private List<ItemGroup> models = new ArrayList<ItemGroup>();

    private List<ItemGroup> products = new ArrayList<ItemGroup>();

    private List<AttributeAssociation> itemAtrrList = new ArrayList<AttributeAssociation>();
    
    private List<AttributeAssociation> claimedItemAttrList = new ArrayList<AttributeAssociation>();
    
    private List<AttributeAssociation> claimedInvAttList = new ArrayList<AttributeAssociation>();
    
    private List<AttributeAssociation> claimAttList = new ArrayList<AttributeAssociation>();
    
    private AttributeAssociationService attributeAssociationService;
    
    private Map<String,String> errorMap= new HashMap<String, String>();
    
    private String productId;

    public static class ServiceProcedureTreeFilter implements Filter {

        public boolean preTestNode(Assembly assembly) {
            return !CollectionUtils.isEmpty(assembly.getComposedOfAssemblies()) ||
                    !CollectionUtils.isEmpty(assembly.getActions());
        }

        public boolean preTestNode(ActionNode actionNode) {
            return true;
        }

        public boolean postTestNode(JSONObject node, Assembly assembly) throws JSONException {
            return (node.getJSONArray(ASM_CHILDREN).length() > 0) || (node.getJSONArray(SP_CHILDREN).length() > 0);
        }

        public boolean postTestNode(JSONObject node, ActionNode actionNode) {
            return true;
        }

        public boolean includeFaultCodeInfo() {
            return false;
        }
    }

    public void prepare() {

    }

    public String detail() {
        if (id != null) {
            additionalAttributes = additionalAttributesService.findAdditionalAttributes(Long.parseLong(id));
            changeClaimType();
            prepareAttributeAssociation();
        }
        return SUCCESS;

    }

    public void changeClaimType()
    {
        StringBuffer changedClaimType = new StringBuffer();
        	StringTokenizer token =new StringTokenizer(additionalAttributes.getClaimTypes(),",");
        	while(token.hasMoreTokens()){
        		String str= token.nextToken().trim();
               changedClaimType.append(getText(ClaimType.typeFor(str).getDisplayType()));
                changedClaimType.append(",");
        	}
            additionalAttributes.setClaimTypes(changedClaimType.substring(0, changedClaimType.length()-1));
    }

    public List<AttributeAssociation> getClaimAttList() {
		return claimAttList;
	}

	public void setClaimAttList(List<AttributeAssociation> claimAttList) {
		this.claimAttList = claimAttList;
	}

	private void prepareAttributeAssociation() {
        for (AttributeAssociation attributeAssociation : additionalAttributes.getAttributeAssociations()) {
        	if (AttributePurpose.PART_SOURCING_PURPOSE.equals(additionalAttributes.getAttributePurpose())) {
            if (attributeAssociation.getItem() != null) {
                itemAtrrList.add(attributeAssociation);
            } }else if(AttributePurpose.JOB_CODE_PURPOSE.equals(additionalAttributes.getAttributePurpose())) {
            	if (attributeAssociation.getItemGroup() != null  && ItemGroup.MODEL.equals(attributeAssociation.getItemGroup().getItemGroupType())) {
            	     attributeAssociation.setProduct(getProduct(attributeAssociation.getItemGroup()));                
                     claimedItemAttrList.add(attributeAssociation);
            } else if (attributeAssociation.getItemGroup() != null  && ItemGroup.PRODUCT.equals(attributeAssociation.getItemGroup().getItemGroupType())) {
            	attributeAssociation.setProduct(attributeAssociation.getItemGroup());
                attributeAssociation.setItemGroup(null);
                claimedItemAttrList.add(attributeAssociation);
            }}
            else if(AttributePurpose.CLAIMED_INVENTORY_PURPOSE.equals(additionalAttributes.getAttributePurpose())){
            	if (attributeAssociation.getItemGroup() != null  && ItemGroup.MODEL.equals(attributeAssociation.getItemGroup().getItemGroupType())) {
            	
                attributeAssociation.setProduct(getProduct(attributeAssociation.getItemGroup()));                
                claimedInvAttList.add(attributeAssociation);
            } else if (attributeAssociation.getItemGroup() != null && ItemGroup.PRODUCT.equals(attributeAssociation.getItemGroup().getItemGroupType())) {
            	attributeAssociation.setProduct(attributeAssociation.getItemGroup());
                attributeAssociation.setItemGroup(null);
                claimedInvAttList.add(attributeAssociation);
            } }else if(AttributePurpose.CLAIM_PURPOSE.equals(additionalAttributes.getAttributePurpose())){ 
            	if (attributeAssociation.getSmrreason() != null) {
            
            	claimAttList.add(attributeAssociation);
            }}

        }
    }

    private ItemGroup getProduct(ItemGroup itemGroup) {
    	ItemGroup product = null;	
    	if (!ItemGroup.PRODUCT.equals(itemGroup.getIsPartOf().getItemGroupType())) {
    		product = getProduct(itemGroup.getIsPartOf());
    	} else {
    		product = itemGroup.getIsPartOf();
    	}
    	return product;
    }
    
    public String preview() {
        if (id != null) {
            additionalAttributes = additionalAttributesService.findAdditionalAttributes(Long.parseLong(id));
            changeClaimType();
            prepareAttributeAssociation();
        }
        return SUCCESS;
    }

    public String associate() {
    	if (AttributePurpose.PART_SOURCING_PURPOSE.equals(additionalAttributes.getAttributePurpose())) {
    		List<AttributeAssociation>existingAssociations= additionalAttributes.getAttributeAssociations();
    		additionalAttributes.setAttributeAssociations(itemAtrrList);
        } else if(AttributePurpose.JOB_CODE_PURPOSE.equals(additionalAttributes.getAttributePurpose()))  {
        	for (AttributeAssociation attrAssociation : claimedItemAttrList) {
                if ((attrAssociation.getItemGroup() == null || attrAssociation.getItemGroup().getId() == null) && attrAssociation.getProduct() != null) {
                    attrAssociation.setItemGroup(attrAssociation.getProduct());
                }
            }
        	additionalAttributes.setAttributeAssociations(claimedItemAttrList);
        }else if(AttributePurpose.CLAIMED_INVENTORY_PURPOSE.equals(additionalAttributes.getAttributePurpose())){
        	for (AttributeAssociation attrAssociation : claimedInvAttList) {
                if ((attrAssociation.getItemGroup() == null || attrAssociation.getItemGroup().getId() == null) && attrAssociation.getProduct() != null) {
                    attrAssociation.setItemGroup(attrAssociation.getProduct());
                }
            }
        	additionalAttributes.setAttributeAssociations(claimedInvAttList);
        	
        } else {
        	for (AttributeAssociation attrAssociation : claimAttList) {
                if (attrAssociation.getSmrreason() == null) {
                    attrAssociation.setSmrreason(attrAssociation.getSmrreason());
                }
            }
        	additionalAttributes.setAttributeAssociations(claimAttList);
        	
        }
    	for(AttributeAssociation attributeAssociation : additionalAttributes.getAttributeAssociations()){
    		attributeAssociation.setForAttribute(additionalAttributes);
    	}
        additionalAttributesService.saveAdditionalAttribute(additionalAttributes);
        addActionMessage("message.additionalAttribute.associatesSuccessful");
        return SUCCESS;
    }
    
    public void validate(){
    	
    	Boolean modelRequired = Boolean.FALSE;
    	Boolean productRequired = Boolean.FALSE;
    	Boolean itemRequired = Boolean.FALSE;
    	Boolean partRequired = Boolean.FALSE;
    	Boolean faultCodeRequired = Boolean.FALSE;
    	
    	if(additionalAttributes != null ){
    		if(AttributePurpose.PART_SOURCING_PURPOSE .equals(additionalAttributes.getAttributePurpose())){
    			for (AttributeAssociation attributeAssociation : itemAtrrList){
    				validateForPartDuplicacy(attributeAssociation);
    				if (attributeAssociation.getItem()==null)
    					partRequired= Boolean.TRUE;
    			}
    		}
	    	if(AttributePurpose.JOB_CODE_PURPOSE .equals(additionalAttributes.getAttributePurpose())){
	    		
	    		for (AttributeAssociation attributeAssociation : claimedItemAttrList){
	    			if (attributeAssociation.getProduct()==null || attributeAssociation.getProduct().getId()==null)
		    		{
		    			productRequired = Boolean.TRUE;
		    		}
	    			else if (attributeAssociation.getItemGroup()==null)
		    		{
		    			modelRequired = Boolean.TRUE;		    			
		    		}
		    		else if (attributeAssociation.getFaultCode() == null && attributeAssociation.getServiceProcedure() == null)
		    		{
		    			faultCodeRequired = Boolean.TRUE;	
		    		}else
		    		if(attributeAssociation.getProduct() != null && attributeAssociation.getItemGroup()==null 
		    				&& ItemGroup.PRODUCT
		    				.equals(attributeAssociation.getProduct().getItemGroupType())){
		    			validateForProductDuplicacy(attributeAssociation, claimedItemAttrList );
		    		}else if(attributeAssociation.getItemGroup() != null && ItemGroup.MODEL.equals(attributeAssociation.getItemGroup().getItemGroupType())
		    				&& (attributeAssociation.getProduct() !=null &&! errorMap.containsKey(attributeAssociation.getProduct().getName()))
		    				&& (attributeAssociation.getFaultCode() == null && attributeAssociation.getServiceProcedure() == null)){
		    			validateForModelDuplicacy(attributeAssociation,claimedItemAttrList);
		    		}else if(attributeAssociation.getFaultCode() != null && ! errorMap.containsKey(attributeAssociation.getItemGroup().getName())){
		    			validateForFaultCodeDuplicacy(attributeAssociation);
		    		}else if(attributeAssociation.getServiceProcedure() != null && ! errorMap.containsKey(attributeAssociation.getItemGroup().getName())){
		    			validateForJobCodeDuplicacy(attributeAssociation);
		    		} 
		    		  
		    		}
	    		
	    	}
	    		else if(AttributePurpose.CLAIMED_INVENTORY_PURPOSE .equals(additionalAttributes.getAttributePurpose())){
	    			
		    		for (AttributeAssociation attributeAssociation : claimedInvAttList){
		    			if (attributeAssociation.getProduct()==null || attributeAssociation.getProduct().getId()==null)
			    		{
			    			productRequired = Boolean.TRUE;
			    		} else if (attributeAssociation.getItemGroup()==null)
			    		{
			    			modelRequired = Boolean.TRUE;		    			
			    		}
			    		else if(attributeAssociation.getProduct() != null && attributeAssociation.getItemGroup()==null 
			    				&& ItemGroup.PRODUCT
			    				.equals(attributeAssociation.getProduct().getItemGroupType())){
			    			validateForProductDuplicacy(attributeAssociation,claimedInvAttList);
			    		}else if(attributeAssociation.getItemGroup() != null && ItemGroup.MODEL.equals(attributeAssociation.getItemGroup().getItemGroupType())
			    				&& (attributeAssociation.getProduct() !=null &&! errorMap.containsKey(attributeAssociation.getProduct().getName()))){
			    			validateForModelDuplicacy(attributeAssociation,claimedInvAttList);
			    		}
			    	
		    		}
		    		}}
	    
	    	if(! errorMap.isEmpty()){
	    		for(String error : errorMap.keySet()){
	    			addActionError(errorMap.get(error),new String[]{error});
	    		}
	    	}
	    	if (faultCodeRequired)
	    		addActionError("error.additionalAttribute.faultCodeRequired");
	    	if (itemRequired)
	    		addActionError("error.additionalAttribute.itemRequired");
	    	if (productRequired)
	    		addActionError("error.additionalAttribute.productRequired");
	    	if (modelRequired)
	    		addActionError("error.additionalAttribute.modelRequired");
	    	
	    	if (partRequired)
	    		addActionError("error.additionalAttribute.partRequired");
	    	
   	}
    

	public List<AttributeAssociation> getClaimedInvAttList() {
		return claimedInvAttList;
	}

	public void setClaimedInvAttList(List<AttributeAssociation> claimedInvAttList) {
		this.claimedInvAttList = claimedInvAttList;
	}

	private void validateForPartDuplicacy(AttributeAssociation existingAttributeAssociation) {
		int count=0;
		Map<String,Integer> countMap= new HashMap<String, Integer>();
		for(AttributeAssociation attributeAssociation :itemAtrrList){
			if(attributeAssociation.getItem()!=null && existingAttributeAssociation.getItem()!=null && 
					existingAttributeAssociation.getItem().getId()== attributeAssociation.getItem().getId())
				if(! countMap.containsKey(existingAttributeAssociation.getItem().getNumber())){
					count++;
				}else{
					count++;
				}
		}
		if(count>1)
		{
			errorMap.put(existingAttributeAssociation.getItem().getName(), 
					"error.additionalAttribute.partDuplicacy");
		}
		
	}
	
	private void validateForProductDuplicacy(AttributeAssociation existingAttributeAssociation,List<AttributeAssociation> attributeList) {
		int count=0;
		Map<String,Integer> countMap= new HashMap<String, Integer>();
		for(AttributeAssociation attributeAssociation :attributeList){
			if(existingAttributeAssociation.getProduct() != null && existingAttributeAssociation.getProduct().getId()== attributeAssociation.getProduct().getId())
				if(! countMap.containsKey(existingAttributeAssociation.getProduct().getName())){
					count++;
				}else{
					count++;
				}
		}
		if(count>1)
		{
			errorMap.put(existingAttributeAssociation.getProduct().getName(), 
					"error.additionalAttribute.productDuplicacy");
		}
		
	}
	
	private void validateForModelDuplicacy(AttributeAssociation existingAttributeAssociation, List<AttributeAssociation> attributeList) {
		int count=0;
		Map<String,Integer> countMap= new HashMap<String, Integer>();
		for(AttributeAssociation attributeAssociation :attributeList){
			if(existingAttributeAssociation.getItemGroup()== attributeAssociation.getItemGroup())
				if(! countMap.containsKey(existingAttributeAssociation.getProduct().getName())){
					count++;
				}else{
					count=countMap.get(existingAttributeAssociation.getItemGroup().getName());
					count++;
				}
		}
		if(count>1)
		{
			errorMap.put(existingAttributeAssociation.getItemGroup().getName(), 
					"error.additionalAttribute.modelDuplicacy");
		}
		
	}
	
	private void validateForFaultCodeDuplicacy(
			AttributeAssociation existingAttributeAssociation) {
		int count=0;
		Map<String,Integer> countMap= new HashMap<String, Integer>();
		for(AttributeAssociation attributeAssociation :additionalAttributes.getAttributeAssociations()){
			if(existingAttributeAssociation.getFaultCode()== attributeAssociation.getFaultCode())
				if(! countMap.containsKey(existingAttributeAssociation.getFaultCode().getDefinition().getCode())){
					count++;
					countMap.put(existingAttributeAssociation.getProduct().getName() ,count);
				}else{
					count=countMap.get(existingAttributeAssociation.getFaultCode().getDefinition().getCode());
					count++;
				}
		}
		if(count>1)
		{
			errorMap.put(existingAttributeAssociation.getFaultCode().getDefinition().getCode(), 
					"error.additionalAttribute.faultCodeDuplicacy");
		}
		
	}
	
	private void validateForJobCodeDuplicacy(
			AttributeAssociation existingAttributeAssociation) {
		int count=0;
		Map<String,Integer> countMap= new HashMap<String, Integer>();
		for(AttributeAssociation attributeAssociation :additionalAttributes.getAttributeAssociations()){
			if(attributeAssociation.getServiceProcedure()!=null && 
					existingAttributeAssociation.getServiceProcedure().getId()== attributeAssociation.getServiceProcedure().getId())
				if(! countMap.containsKey(existingAttributeAssociation.getServiceProcedure().getDefinition().getCode())){
					countMap.put(existingAttributeAssociation.getServiceProcedure().getDefinition().getActionDefinition().getCode() ,count);
					count++;
				}else{
					count=countMap.get(existingAttributeAssociation.getServiceProcedure().getDefinition().getActionDefinition().getCode());
					count++;
				}
		}
		if(count>1)
		{
			errorMap.put(existingAttributeAssociation.getServiceProcedure().getDefinition().getCode(), 
					"error.additionalAttribute.jobCodeDuplicacy");
		}
		
	}

	

	private void prepareAssociations(List<AttributeAssociation> attrList) {
		List<AttributeAssociation> duplicateAssociations= new ArrayList<AttributeAssociation>();
		for(AttributeAssociation attributeAssociation : additionalAttributes.getAttributeAssociations()){
			for(AttributeAssociation newAssociation : attrList ){
				if(newAssociation.getId()==attributeAssociation.getId()){
					attributeAssociation.setItem(newAssociation.getItem());
					duplicateAssociations.add(newAssociation);
				}
			}
		}
		if(!duplicateAssociations.isEmpty()){
			attrList.removeAll(duplicateAssociations);
		}
		if(!attrList.isEmpty()){
			additionalAttributes.getAttributeAssociations().addAll(attrList);
		}
	}

    public String findJsonFaultCodeTree() throws JSONException {
        if (modelId != null) {
        	ItemGroup selectedModel = itemGroupService.findItemGroupsById(new Long(modelId));
        	FailureStructure fs=getFailureStructure(selectedModel)!=null?getFailureStructure(selectedModel):getFailureStructure(selectedModel.getProduct(selectedModel));
            Filter filter = new Filter() {
                public boolean preTestNode(Assembly assembly) {
                    return !CollectionUtils.isEmpty(assembly.getComposedOfAssemblies()) || assembly.isFaultCode();

                }

                public boolean preTestNode(ActionNode actionNode) {
                    return false;
                }

                public boolean postTestNode(JSONObject node, Assembly assembly) throws JSONException {
                    return node.getJSONArray(ASM_CHILDREN).length() > 0 || assembly.isFaultCode();
                }

                public boolean postTestNode(JSONObject node, ActionNode actionNode) {
                    return false;
                }

                public boolean includeFaultCodeInfo() {
                    return true;
                }
            };
            jsonString = this.asmTreeJSONifier.getSerializedJSONString(fs, filter, null);
        }
        return SUCCESS;
    }

    public String findJsonServiceProcedureTree() throws JSONException {
        if (modelId != null) {
        	ItemGroup selectedModel = itemGroupService.findItemGroupsById(new Long(modelId));
        	FailureStructure fs=getFailureStructure(selectedModel)!=null?getFailureStructure(selectedModel):getFailureStructure(selectedModel.getProduct(selectedModel));
            jsonString = this.asmTreeJSONifier.getSerializedJSONString(fs,
                    new ServiceProcedureTreeFilter(), null);
        }
        return SUCCESS;
    }

    private FailureStructure getFailureStructure(ItemGroup model) {
        return this.failureStructureService.getFailureStructureForItemGroup(model);
    }
    

    public String findProducts() {
        List<ItemGroup>products = itemGroupService.findProductForNameAndType(getSearchPrefix(),ItemGroup.PRODUCT);
        if(StringUtils.hasText(this.searchPrefix)){
        	return generateAndWriteComboboxJson(products,"id","name");
        }else{
        	return generateAndWriteEmptyComboboxJson();
        }
    }

    public String findModels() {
        models = itemGroupService.findModelsForProduct(itemGroupService.findItemGroupsById(new Long(productId)));
        if(productId != null){
        	List<ItemGroup> selectedModels= new ArrayList<ItemGroup>();
        	for(ItemGroup model : models){
        		if(model.getGroupCode().startsWith(getSearchPrefix(),0)){
        			selectedModels.add(model);
        		}
        	}
        	return generateAndWriteComboboxJson(selectedModels,"id","name");
        }else{
        	return generateAndWriteEmptyComboboxJson();
        }
    }
    
    public String  listParts() {
		return getOemPartItemNumbersStartingWith(getSearchPrefix());

	}

	String getOemPartItemNumbersStartingWith(String prefix) {

            List<Item> items;
            if (StringUtils.hasText(prefix)) {
                items = catalogService.findParts(prefix.toUpperCase());
                return generateAndWriteComboboxJson(items,"number","number");
            }else{
            	return generateAndWriteEmptyComboboxJson();
            }
    }

    
	public String getOEMPartDetails() {
		try {
			Item item = catalogService.findItemOwnedByManuf(number);
			JSONArray oneEntry = new JSONArray();
			oneEntry.put(item.getDescription());
			jsonString = oneEntry.toString();
		} catch (CatalogException e) {
			logger.error("Invalid item number entered", e);
		}
		return SUCCESS;
	}
	
   

    public String getAttributePurpose() {
        return attributePurpose;
    }

    public void setAttributePurpose(String attributePurpose) {
        this.attributePurpose = attributePurpose;
    }

    public void setAdditionalAttributesService(AdditionalAttributesService additionalAttributesService) {
        this.additionalAttributesService = additionalAttributesService;
    }

    public List<ItemGroup> getModels() {
        return models;
    }

    public void setModels(List<ItemGroup> models) {
        this.models = models;
    }

    public List<ItemGroup> getProducts() {
        return products;
    }

    public void setProducts(List<ItemGroup> products) {
        this.products = products;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }


    public List<AttributeAssociation> getItemAtrrList() {
        return itemAtrrList;
    }

    public void setItemAtrrList(List<AttributeAssociation> itemAtrrList) {
        this.itemAtrrList = itemAtrrList;
    }

    public AdditionalAttributes getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(AdditionalAttributes additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

 
 

    public String getUserEntry() {
        return userEntry;
    }

    public void setUserEntry(String userEntry) {
        this.userEntry = userEntry;
    }

    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setFailureStructureService(FailureStructureService failureStructureService) {
        this.failureStructureService = failureStructureService;
    }

    public void setAsmTreeJSONifier(AssemblyTreeJSONifier asmTreeJSONifier) {
        this.asmTreeJSONifier = asmTreeJSONifier;
    }
    
	@Required
	public void setAssemblyTreeJsonifier(AssemblyTreeJSONifier jsonifier) {
		this.asmTreeJSONifier = jsonifier;
	}

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<AttributeAssociation> getClaimedItemAttrList() {
		return claimedItemAttrList;
	}

	public void setClaimedItemAttrList(
			List<AttributeAssociation> claimedItemAttrList) {
		this.claimedItemAttrList = claimedItemAttrList;
	}

	public void setAttributeAssociationService(
			AttributeAssociationService attributeAssociationService) {
		this.attributeAssociationService = attributeAssociationService;
	}

	public Map<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
