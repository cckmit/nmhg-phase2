package tavant.twms.web.admin.faultcode;


import java.util.HashSet;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.opensymphony.xwork2.Preparable;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FaultCodeDefinition;
import tavant.twms.web.i18n.I18nActionSupport;

@SuppressWarnings("serial")
public class PartClassAndJobCodeAssociationAction extends I18nActionSupport implements ServletResponseAware,Preparable {
	private static Logger logger = LogManager.getLogger(PartClassAndJobCodeAssociationAction.class);
    private FaultCodeDefinition faultCodeDefinition;
    private FaultCodeDefinition codeDefinitionItem;
    private FaultCodeDefinition codeDefinitionItemGroup;
	private FailureStructureService failureStructureService;
	
	
	
	


	   @Override
	    public void validate(){
		   if(null!=codeDefinitionItem){
			     if(null!=codeDefinitionItem.getPartClasses()){ 
			    	//validation for valid Item
					 for(ItemCriterion itemCriterion: codeDefinitionItem.getPartClasses()){
						 if(null != itemCriterion){
							 if(null == itemCriterion.getItem()){
								 addActionError(getText("error.mantainPartAndJob.validItem"));
								 break;
							 }
						 }
					 }	
					//validation for duplicates Item
		 			 if(isDuplicateItem(codeDefinitionItem.getPartClasses())){
		 				addActionError(getText("label.mantainPartAndJob.duplicateItem")); 
					 } 
			     }	 
		   }   
		   if(null!=codeDefinitionItemGroup){
			   if(null!=codeDefinitionItemGroup.getPartClasses()){ 
				   //validation for valid Item Group
				   for(ItemCriterion itemCriterion: codeDefinitionItemGroup.getPartClasses()){
					   if(null != itemCriterion){
						   if(null == itemCriterion.getItemGroup().getId()){
							   addActionError(getText("error.mantainPartAndJob.validItemGroup"));
							   break;
						   }
					   }
				   }	
				   //validation for duplicates Item Group
				   if(isDuplicateItemGroup(codeDefinitionItemGroup.getPartClasses())){
					   addActionError(getText("label.mantainPartAndJob.duplicateItemGroup")); 
				   } 
			   }	 
		   }   
		}
	

//Save	
	public String savePartClassAndJobCodeAssociation(){
	 faultCodeDefinition.getPartClasses().clear();	
	 if(null!=codeDefinitionItem && null!=codeDefinitionItem.getPartClasses()){
		 for(ItemCriterion itemCriterion: codeDefinitionItem.getPartClasses()){
			 if(null!=itemCriterion){
				 if(null!=itemCriterion.getItem()){
					 faultCodeDefinition.getPartClasses().add(itemCriterion);
				 }
			 }	 
		 }
	  }
	 if(null!= codeDefinitionItemGroup && null!=codeDefinitionItemGroup.getPartClasses()){
		 for(ItemCriterion itemCriterion: codeDefinitionItemGroup.getPartClasses()){
			 if(null!=itemCriterion){
				 if(null!=itemCriterion.getItemGroup()){
					 faultCodeDefinition.getPartClasses().add(itemCriterion);
				 }
			 } 	 
	    }
	 }
	 failureStructureService.updateFaultCodeDefinition(faultCodeDefinition);
	 addActionMessage(getText("message.mantainPartAndJob.update"));
	 return SUCCESS;
	}

//Load 	
	public String loadPartClassAndJobCodeAssociation(){
		if(null!= faultCodeDefinition.getPartClasses()){
			   codeDefinitionItem=new FaultCodeDefinition();
			   codeDefinitionItemGroup=new FaultCodeDefinition();
		    for(ItemCriterion itemCriterion: faultCodeDefinition.getPartClasses()){
		    	if(null!=itemCriterion.getItem()){
		    		codeDefinitionItem.getPartClasses().add(itemCriterion);
		    	}else{
		    		codeDefinitionItemGroup.getPartClasses().add(itemCriterion);
		    	}
		    }
		}   
		return  SUCCESS;
	}

//Prepare 	
	public void prepare() throws Exception {
        if (id != null) {
			faultCodeDefinition = failureStructureService.findFaultCodeDefinitionById(Long.parseLong(id));
		}
	}
	
// Checks  duplicate Part
	public static boolean isDuplicateItem(List<ItemCriterion> itemCriterions) {
		HashSet<Item> duplicateitemCriterions = new HashSet<Item>();
		 for (int i = 0; i < itemCriterions.size(); i++) {
			if(null!=itemCriterions.get(i)) {
				boolean val = duplicateitemCriterions.add(itemCriterions.get(i).getItem());
				if (val == false) {
					return true;
				}
			}
		}	
		 return false;
    }

// Checks  duplicate Part Class
	public static boolean isDuplicateItemGroup(List<ItemCriterion> itemCriterions) {
		HashSet<ItemGroup> duplicateitemCriterions = new HashSet<ItemGroup>();
		for (int i = 0; i < itemCriterions.size(); i++) {
			if(null!=itemCriterions.get(i)) {
				boolean val = duplicateitemCriterions.add(itemCriterions.get(i).getItemGroup());
				if (val == false) {
					return true;
				}
			}	
		}
		return false;
	}
  
//Setters And Getters
	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    
	public FaultCodeDefinition getCodeDefinitionItem() {
		return codeDefinitionItem;
	}

	public void setCodeDefinitionItem(FaultCodeDefinition codeDefinitionItem) {
		this.codeDefinitionItem = codeDefinitionItem;
	}

	public FaultCodeDefinition getCodeDefinitionItemGroup() {
		return codeDefinitionItemGroup;
	}

	public void setCodeDefinitionItemGroup(
			FaultCodeDefinition codeDefinitionItemGroup) {
		this.codeDefinitionItemGroup = codeDefinitionItemGroup;
	}

	public FaultCodeDefinition getFaultCodeDefinition() {
			return faultCodeDefinition;
	}
	public void setFaultCodeDefinition(FaultCodeDefinition faultCodeDefinition) {
			this.faultCodeDefinition = faultCodeDefinition;
	}
	public FailureStructureService getFailureStructureService() {
			return failureStructureService;
	}

	public void setFailureStructureService(
				FailureStructureService failureStructureService) {
			this.failureStructureService = failureStructureService;
	}	

}
