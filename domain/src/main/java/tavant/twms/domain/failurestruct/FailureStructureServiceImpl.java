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

package tavant.twms.domain.failurestruct;

import java.io.Serializable;
import java.util.*;

import tavant.twms.common.TWMSException;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Job;
import tavant.twms.domain.claim.JobCodeRepository;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class FailureStructureServiceImpl implements FailureStructureService {

	private FailureStructureRepository failureStructureRepository;

	private JobCodeRepository jobCodeRepository;

	public FailureStructure getFailureStructureForItem(Item item) {
		return failureStructureRepository.findFailureStructureForItem(item);
	}

	public FailureStructure getFailureStructureForItemGroup(ItemGroup itemGroup) {
		return failureStructureRepository.getFailureStructureForItemGroup(itemGroup);
	}

    public List<FaultCodeDefinition> findFaultCodesForPart(Item part) {
        return failureStructureRepository.findFaultCodesForPart(part);
    }
    public List<FailureCauseDefinition> findCausedByOptions(String serialNumber, String faultFound) {
		return failureStructureRepository.findCausedByOptions(serialNumber, faultFound);
	}
	
	public List<FailureCauseDefinition> findCausedByOptionsForModel(String modelNumber, String faultFound,
			String partialNameOrcode) {
		return failureStructureRepository.findCausedByOptionsForModel(modelNumber, faultFound, partialNameOrcode);
	}

	public List<FailureCauseDefinition> findCausedByOptionsForModel(String modelNumber, String faultFound) {
		return failureStructureRepository.findCausedByOptionsForModel(modelNumber, faultFound);
	}

    /**
     * If fault codes are associated with the causal part or the causal part's part class then returns the intersection
     * of the model's fault codes and the causal part's fault codes. If the intersection is empty or if no fault codes
     * are associated with the causal part then returns the model's complete failure structure
     */
    public FailureStructure getFailureStructure(Claim claim, Item causalPart) {
        FailureStructure failureStructure = getFailureStructureForInventoryItem(claim);
        if (failureStructure != null && causalPart != null) {
            List<FaultCodeDefinition> faultCodeDefinitions = findFaultCodesForPart(causalPart);
            if (faultCodeDefinitions.size() > 0) {
                findIntersectingFaultCodes(failureStructure.getAssemblies(), faultCodeDefinitions);
                if (foundIntersectingFaultCodes(failureStructure)) {
                    FailureStructure filteredFailureStructure = new FailureStructure();
                    filteredFailureStructure.setId(failureStructure.getId());
                    filteredFailureStructure.setName(failureStructure.getName());
                    filteredFailureStructure.setForItemGroup(failureStructure.getForItemGroup());
                    filteredFailureStructure.setD(failureStructure.getD());
                    copyIntersectingFaultCodes(failureStructure.getAssemblies(), filteredFailureStructure.getAssemblies());
                    return filteredFailureStructure;
                }
            }
        }
        return failureStructure;
    }

    private FailureStructure getFailureStructureForInventoryItem(Claim claim) {
        ItemReference itemReference = claim.getItemReference();
        if (itemReference.getModel() != null) {
            FailureStructure modelFailureStructure =this.getFailureStructureForItemGroup(itemReference.getModel());
            if(modelFailureStructure!=null){
                return modelFailureStructure;
            } else{
            return this.getFailureStructureForItemGroup(itemReference.getModel().getProduct());
            }
        } else if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
            PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
            if (partsClaim.getPartInstalled() && (partsClaim.getCompetitorModelBrand()!=null && partsClaim.getCompetitorModelBrand().isEmpty()
					&& partsClaim.getCompetitorModelDescription().isEmpty() && partsClaim
					.getCompetitorModelTruckSerialnumber().isEmpty())) {
                if (partsClaim.getItemReference().getReferredInventoryItem() != null ) {
                    return this.getFailureStructureForItem(partsClaim.getItemReference().getUnserializedItem());
                } else {
                    return getFailureStructureForItemGroup(partsClaim.getItemReference().getModel());
                }
            }
        } else if(itemReference.getUnserializedItem()!=null){
            return getFailureStructureForItem(itemReference.getUnserializedItem());
        }
        return null;
    }
    
    private void findIntersectingFaultCodes(Set<Assembly> assemblies, List<FaultCodeDefinition> faultCodeDefinitions) {
        for (Assembly asm : assemblies) {
            if (asm.isFaultCode() && faultCodeDefinitions.contains(asm.getFaultCode().getDefinition())) {
                asm.setIntersectingFaultCode(true);
                markIntersectingAssembly(asm);
            }
            findIntersectingFaultCodes(asm.getComposedOfAssemblies(), faultCodeDefinitions);
        }
    }

    private void markIntersectingAssembly(Assembly asm) {
        if (asm == null || asm.isIntersectingAssembly()) return;
        asm.setIntersectingAssembly(true);
        markIntersectingAssembly(asm.getIsPartOfAssembly());
    }

    private boolean foundIntersectingFaultCodes(FailureStructure failureStructure) {
        for (Assembly assembly : failureStructure.getAssemblies()) {
            if (assembly.isIntersectingAssembly()) {
                return true;
            }
        }
        return false;
    }

    private void copyIntersectingFaultCodes(Set<Assembly> assemblies, Set<Assembly> newAssemblies) {
        for (Assembly assembly : assemblies) {
            if (assembly.isIntersectingAssembly()) {
                Assembly newAssembly = new Assembly();
                newAssembly.setId(assembly.getId());
                newAssembly.setDefinition(assembly.getDefinition());
                newAssembly.setTreadAble(assembly.getTreadAble());
                if (assembly.isIntersectingFaultCode()) {
                    newAssembly.setActions(assembly.getActions());
                    newAssembly.setFaultCode(assembly.getFaultCode());
                }
                newAssembly.setActive(assembly.getActive());
                newAssembly.setD(assembly.getD());
                newAssemblies.add(newAssembly);
                copyIntersectingFaultCodes(assembly.getComposedOfAssemblies(), newAssembly.getComposedOfAssemblies());
                for (Assembly subAssembly : newAssembly.getComposedOfAssemblies()) {
                    subAssembly.setIsPartOfAssembly(newAssembly);
                }
            }
        }
    }

    public void update(FailureStructure failureStructure) {
		failureStructureRepository.update(failureStructure);
	}

	
	
	public List<FailureTypeDefinition> findFaultFoundOptions(String inventoryItemId) {
		return failureStructureRepository.findFaultFoundOptions(inventoryItemId);
	}
	
	public List<FailureTypeDefinition> findFaultFoundOptionsForModels(String modelId) {
		return failureStructureRepository.findFaultFoundOptionsForModels(modelId);
	}
	
	public FailureTypeDefinition findFaultFoundOptionsForModelsByFaultName(final String modelId, final String faultName) {
		return failureStructureRepository.findFaultFoundOptionsForModelsByFaultName(modelId, faultName);
	}
	public AssemblyDefinition createAssemblyDefintion(String name, int level) {
		return failureStructureRepository.createAssemblyDefinition(name, level);
	}

	public Collection<Job> findJobsStartingWith(final String jobCodePrefix) {
		return jobCodeRepository.findJobsStartingWith(jobCodePrefix);
	}

	public void setJobCodeRepository(JobCodeRepository jobCodeRepository) {
		this.jobCodeRepository = jobCodeRepository;
	}

	public ActionDefinition createActionDefinition(String name) {
		return failureStructureRepository.createActionDefintion(name);
	}

	public AssemblyDefinition findAssemblyDefinition(Long id) {
		return failureStructureRepository.findAssemblyDefiniton(id);
	}

	public PageResult<AssemblyDefinition> findAssemblyDefinitions(String nameStartsWith, int level,
			PageSpecification page,String locale) {
		return failureStructureRepository.findAssemblyDefinitions(nameStartsWith, level, page,locale);
	}

	public ActionDefinition findActionDefinition(Long id) {
		return failureStructureRepository.findActionDefinition(id);
	}

	public PageResult<ActionDefinition> findActionDefinitions(String nameStartsWith,
			PageSpecification page,String locale) {
		return failureStructureRepository.findActionDefinition(nameStartsWith, page,locale);
	}

	public TreadBucket findTreadBucket(String code) {
		return failureStructureRepository.findTreadBucket(code);
	}

	public Collection<TreadBucket> findTreadBuckets() {
		return failureStructureRepository.findTreadBuckets();
	}

	public FaultCodeDefinition findOrCreateFaultCodeDefinition(String faultCode) {
		FaultCodeDefinition faultCodeDefinition = failureStructureRepository.findFaultCodeDefintiion(faultCode);
		if (faultCodeDefinition == null) {
			faultCodeDefinition = new FaultCodeDefinition(faultCode);
			String[] components = faultCode.split(Assembly.FAULT_CODE_SEPARATOR);
			for (int i = 0; i < components.length; i++) {
				AssemblyDefinition assemblyDefinition = failureStructureRepository.findAssemblyDefinition(
                        components[i], i + 1);
				if (assemblyDefinition == null) {
					throw new TWMSException("AssemblyDefinition with code [" + components[i] + "] for level " + i
							+ "not found");
				}
				faultCodeDefinition.addComponent(assemblyDefinition);
			}
			failureStructureRepository.createFaultCodeDefintion(faultCodeDefinition);
		}
		return faultCodeDefinition;
	}

    public FaultCodeDefinition findOrCreateFaultCodeDefinition(Assembly childAssembly) {
        String faultCode = childAssembly.getFullCode();
        Map faultCodeMap = childAssembly.getCodeAndName();
        FaultCodeDefinition faultCodeDefinition = failureStructureRepository.findFaultCodeDefintiion(faultCode);
		if (faultCodeDefinition == null) {
			faultCodeDefinition = new FaultCodeDefinition(faultCode);
			String[] components = faultCode.split(Assembly.FAULT_CODE_SEPARATOR);
            String assemblyDefName =  "";
			for (int i = 0; i < components.length; i++) {
                assemblyDefName = (String) faultCodeMap.get(components[i]);
				AssemblyDefinition assemblyDefinition = failureStructureRepository.findAssemblyDefinitionByName(
						 i+1 , components[i], assemblyDefName);
				if (assemblyDefinition == null) {
					throw new TWMSException("AssemblyDefinition with code [" + components[i] + "] for level " + i
							+ "not found");
				}
				faultCodeDefinition.addComponent(assemblyDefinition);
			}
			failureStructureRepository.createFaultCodeDefintion(faultCodeDefinition);
		}
		return faultCodeDefinition;
	}

	public void setFailureStructureRepository(FailureStructureRepository failureStructureRepository) {
		this.failureStructureRepository = failureStructureRepository;
	}

	public FaultCode findFaultCode(Item item, String fullCode) {
		FailureStructure failureStructure = getFailureStructureForItem(item);
		Assembly assembly = failureStructure.getAssembly(fullCode);
		return assembly == null ? null : assembly.getFaultCode();
	}
	
	public FaultCode findFaultCodeByItemGroup(ItemGroup itemGroup, String fullCode) {
		FailureStructure failureStructure = getFailureStructureForItemGroup(itemGroup);
		Assembly assembly = failureStructure.getAssembly(fullCode);
		return assembly == null ? null : assembly.getFaultCode();
	}

	public ServiceProcedure findServiceProcedure(Item item, String code) {
		FailureStructure failureStructure = getFailureStructureForItem(item);
		ServiceProcedure serviceProceture = failureStructure.findSeriveProcedure(code);
		return serviceProceture;
	}

	public ServiceProcedure findServiceProcedureForRoundUp(String code)
	{
		return this.failureStructureRepository.findServiceProcedureForRoundUp(code);
	}
	
	public PageResult<FaultCodeDefinition> findAllFaultCodeDefinitions(ListCriteria listCriteria) {
		return failureStructureRepository.findAllFualtCodeDefinitions(listCriteria);
	}

	public List<AssemblyLevel> findAllAssemblyLevels() {
		return failureStructureRepository.findAllAssemblyLevels();
	}

	public FaultCodeDefinition findFaultCodeDefinitionById(Long id) {
		return failureStructureRepository.findFaultCodeDefinitionById(id);
	}

	public List<FaultCodeDefinition> findFaultCodeDefinitionsByIds(Collection<Long> ids) {
		return failureStructureRepository.findFaultCodeDefinitionsByIds(ids);
	}

	public void updateFaultCodeDefinition(FaultCodeDefinition definition) {
		failureStructureRepository.updateFaultCodeDefinition(definition);
	}

	
	public ServiceProcedureDefinition findOrCreateServiceProcedureDefinition(String fullCode) {
		ServiceProcedureDefinition serviceProcedureDefinition = failureStructureRepository.findServiceProcedureDefintiion(fullCode);
		if (serviceProcedureDefinition == null) {
			serviceProcedureDefinition = new ServiceProcedureDefinition(fullCode);
			String[] components = fullCode.split(Assembly.FAULT_CODE_SEPARATOR);
			for (int i = 0; i < components.length - 1; i++) {
				AssemblyDefinition assemblyDefinition = failureStructureRepository.findAssemblyDefinition(
						components[i], i+1);
				if (assemblyDefinition == null) {
					throw new TWMSException("AssemblyDefinition with code [" + components[i] + "] for level " + i
							+ "not found");
				}
				serviceProcedureDefinition.addComponent(assemblyDefinition);
				serviceProcedureDefinition.setActionDefinition(
						failureStructureRepository.findActionDefinition(components[components.length-1]));
			}
			failureStructureRepository.createServiceProcedureDefinition(serviceProcedureDefinition);
		}
		return serviceProcedureDefinition;
	}
	
	public PageResult<ServiceProcedureDefinition> findAllServiceProcedureDefinitions(ListCriteria listCriteria) {
		return failureStructureRepository.findAllServiceProcedureDefinitions(listCriteria);
	}

	public ServiceProcedureDefinition findServiceProcedureDefinitionById(Long id) {
		return failureStructureRepository.findServiceProcedureDefinitionById(id);
	}

	public List<ServiceProcedureDefinition> findServiceProcedureDefinitionsByIds(Collection<Long> ids) {
		return failureStructureRepository.findServiceProcedureDefinitionsByIds(ids);
	}

	public void updateServiceProcedureDefinition(ServiceProcedureDefinition definition) {
		failureStructureRepository.updateServiceProcedureDefinition(definition);
	}
	

	public PageResult<FailureTypeDefinition> fetchFailureTypesStartingWith(String nameStartsWith, PageSpecification page) {
		return failureStructureRepository.fetchFailureTypesStartingWith(nameStartsWith, page);
	}

	public Object saveAndReturnObject(Object obj) {
		return failureStructureRepository.saveAndReturnObject(obj);
	}

	public Object updateAndReturnObject(Object obj) {
		return failureStructureRepository.updateAndReturnObject(obj);
	}	
	
	public FailureTypeDefinition findFailureTypeDefinitionByName(String name) {
		return failureStructureRepository.findFailureTypeDefinitionByName(name);
	}

	public Object findObjectByPrimaryKey(Class clazz, Serializable id) {
		return failureStructureRepository.findObjectByPrimaryKey(clazz, id);
    }

	public List<FailureCause> findFailureCausesForFailureType(FailureType failureType) {
		return failureStructureRepository.findFailureCausesForFailureType(failureType);
	}

	public List<FailureType> findFailureTypesForItemGroup(ItemGroup itemGroup) {
		return failureStructureRepository.findFailureTypesForItemGroup(itemGroup);
	}

	public void deleteObject(Object obj) {
		failureStructureRepository.deleteObject(obj);
	}

	
	public FailureStructure getMergedFailureStructure(Collection<ItemGroup> itemGroups) {
		List<FailureStructure> failureStructures =  failureStructureRepository
			.findFailureStructuresForItemGroups(itemGroups);
		
		return mergeFailureStructures(failureStructures);
	}

	private FailureStructure mergeFailureStructures(List<FailureStructure> failureStructures) {
		if (failureStructures.size() == 0) {
			return new FailureStructure();
		}
		// in case when size=1, there is not need to find the intersection
		if(failureStructures.size() == 1){
			return failureStructures.get(0);
		}
		
		Set<ServiceProcedureDefinition> serviceProcedureDefinitions  = 
			failureStructures.get(0).getAllServiceProcedureDefinitions();
		
		// Find intersection
		// Find intersection
        Set<ServiceProcedureDefinition> serviceProcedureDefinitionSet;
		for(int i = 1;i<failureStructures.size();i++) {
            serviceProcedureDefinitionSet = failureStructures.get(i).getAllServiceProcedureDefinitions();
            if(serviceProcedureDefinitionSet==null || serviceProcedureDefinitionSet.size()==0){
                List productList = new ArrayList();
                productList.add(failureStructures.get(i).getForItemGroup().getProduct());
                List<FailureStructure> failureStructuresForProducts = failureStructureRepository.findFailureStructuresForItemGroups(productList);
                if(failureStructuresForProducts.size()>0)
                    serviceProcedureDefinitionSet =  failureStructuresForProducts.get(0).getAllServiceProcedureDefinitions();
            }
			serviceProcedureDefinitions.retainAll(serviceProcedureDefinitionSet);
		}
		return createFailureStructure(serviceProcedureDefinitions);
	}

	private FailureStructure createFailureStructure(Set<ServiceProcedureDefinition> serviceProcedureDefinitions) {
		FailureStructure failureStructure = new FailureStructure();
		for(ServiceProcedureDefinition serviceProcedureDefinition:serviceProcedureDefinitions) {
			failureStructure.addServiceProcedure(serviceProcedureDefinition);
		}
		return failureStructure;
	}
	
	public ServiceProcedure findServiceProcedureByDefinitionAndItem(
			ServiceProcedureDefinition serviceProcedureDefinition, Claim claim) {
		FailureStructure failureStructure = getFailureStructureForInventoryItem(claim);
		if (failureStructure != null) {
			return failureStructure
					.findServiceProceduresForDefn(serviceProcedureDefinition);
		}
		return null;
	}

	
	
	public FailureStructure getMergedFailureStructureForItems(Collection<Item> items) {
		List<FailureStructure> failureStructures=new ArrayList<FailureStructure>();
        List<ItemGroup> productItemGroups = new ArrayList<ItemGroup>();
        Map failureStructureMap = new HashMap();
        for(Item item :items){
            productItemGroups.add(item.getProduct());
            failureStructureMap.put(item,item.getProduct());
        }

		if(!failureStructureRepository.findIfFaiureStructureExistsForAllItems(items)){
            List productList ;
            List itemList ;
            for(Item item: items) {
                FailureStructure failureStructureForModel=null;
                FailureStructure failureStructuresForProduct=null;
                List<FailureStructure> failureStructuresForProducts;
                productList =   new ArrayList();
                itemList =   new ArrayList();
                productList.add(item.getProduct());
                itemList.add(item);
                List<FailureStructure> failureStructureForModels =failureStructureRepository
                        .findFailureStructuresForItems(itemList);
                if(failureStructureForModels!=null && failureStructureForModels.size()>0)
                    failureStructureForModel =   failureStructureForModels.get(0);
                if(failureStructureForModel==null){
                    failureStructuresForProducts = failureStructureRepository.findFailureStructuresForItemGroups(productList);
                    if(failureStructuresForProducts.size()>0)
                        failureStructuresForProduct = failureStructuresForProducts.get(0);
                }
                if(failureStructureForModel!=null)
                    failureStructures.add(failureStructureForModel);
                if(failureStructuresForProduct!=null)
                    failureStructures.add(failureStructuresForProduct);
           }
		}else{
			 failureStructures =  failureStructureRepository
			.findFailureStructuresForItems(items);
		}
		return mergeFailureStructures(failureStructures);
	}
	
	   public FailureStructure getFailureStructureForModel(Item item) {
	        List items = new ArrayList();
	        items.add(item);
	        List<FailureStructure> failureStructures=null;
	        if(!failureStructureRepository.findIfFaiureStructureExistsForAllItems(items)){
	            failureStructures = new ArrayList<FailureStructure>();
	        }else{
	            failureStructures =  failureStructureRepository
	                    .findFailureStructuresForItems(items);
	        }
	        return failureStructures.get(0);
	    }
	   
	public List<FaultCodeDefinition> findAllFaultCodeDefinitionsForLabel(Label label) {
		return failureStructureRepository.findAllFaultCodeDefinitionsForLabel(label);
	}
	public List<ServiceProcedureDefinition> findAllJobCodeForLabel(Label label){
		return failureStructureRepository.findAllJobCodeForLabel(label);
	}
	
	
	
	public FailureTypeDefinition findFaultFoundById(final Long faultFoundId)
	{
		return failureStructureRepository.findFaultFoundById(faultFoundId);
	}

	public FailureTypeDefinition findFaultFoundByName(final String faultFoundName)
	{
		return failureStructureRepository.findFaultFoundByName(faultFoundName);
	}

	public PageResult<FailureRootCauseDefinition> fetchFailureRootCausesStartingWith(
			String startingWith, PageSpecification page) {
		return failureStructureRepository.fetchFailureRootCausesStartingWith(startingWith, page);
	}

	public List<FailureRootCause> findFailureRootCausesForFailureType(FailureType failureType) {
		return failureStructureRepository.findFailureRootCausesForFailureType(failureType);	
	}

	public List<FailureRootCauseDefinition> findRootCauseOptionsByModel(String number, String faultFound) {
		return failureStructureRepository.findRootCauseOptionsByModel(number, faultFound);
	}
	
	public FailureRootCauseDefinition findRootCauseOptionsByModelAndFailureDetail(final String number,
			final String faultFound, final String failureDetail) {
		return failureStructureRepository
				.findRootCauseOptionsByModelAndFailureDetail(number, faultFound, failureDetail);
	}

	public FailureRootCauseDefinition findFailureRootCauseDefinitionByName(
			String name) {
		return failureStructureRepository.findFailureRootCauseDefinitionByName(name);
	}
	
	public PageResult<?> findParentJobCodes(ListCriteria listCriteria) {
		return failureStructureRepository.findParentJobCodes(listCriteria);
	}

	public List<ServiceProcedureDefinition> findServiceProcedureDefinitionWhoseCodeStartsWith(
			final String code, final int pageNumber, final int pageSize) {
		return failureStructureRepository.findServiceProcedureDefinitionWhoseCodeStartsWith(code,pageNumber,pageSize);
	}

	public List<ServiceProcedureDefinition> findExistParentJobCodes() {
		return failureStructureRepository.findExistParentJobCodes();
	}
	
	public PageResult<FailureCauseDefinition> fetchFailureCausesStartingWith(String nameStartsWith, PageSpecification page) {
		return failureStructureRepository.fetchFailureCausesStartingWith(nameStartsWith, page);
	}
	

	public FailureCauseDefinition findFailureCauseDefinitionByName(String name) {
		return failureStructureRepository.findFailureCauseDefinitionByName(name);
	}
	
	public List<FailureCauseDefinition> findCausedByOptionsById(final String inventoryItemId, final String faultFoundId){
		return failureStructureRepository.findCausedByOptionsById(inventoryItemId, faultFoundId);
	}
	public List<FailureCauseDefinition> findCausedByOptionsForModelById(final String modelNumber, final String faultFoundId) {
		return failureStructureRepository.findCausedByOptionsForModelById(modelNumber, faultFoundId);
	}
	
	public FailureCauseDefinition findCausedById(final Long causedById) {
		return failureStructureRepository.findCausedById(causedById);
	}
	
	public FailureRootCauseDefinition findRootCauseById(Long rootCauseId) {
		return failureStructureRepository.findRootCauseById(rootCauseId);	
	}

    public AssemblyDefinition fetchAssemblyDefinition(int type, String code) {
		return failureStructureRepository.findAssemblyDefinition(code, type);
	}

    public AssemblyDefinition fetchAssemblyDefinitionByName(int type, String code, String name) {
		return failureStructureRepository.findAssemblyDefinitionByName(type ,code, name);
	}

    public void createAssemblyDefinition(AssemblyDefinition assemblyDefinition) {
		failureStructureRepository.createAssemblyDefinition(assemblyDefinition);
	}

     public void updateAssemblyDefinition(AssemblyDefinition assemblyDefinition) {
         failureStructureRepository.updateAssemblyDefinition(assemblyDefinition);
	}

     public AssemblyLevel findAssemblyLevel(int level) {
		return failureStructureRepository.findAssemblyLevel(level);
	}

    public FailureTypeDefinition fetchFailureTypeDefinition(String code, String name) {
		return failureStructureRepository.findFailureTypeDefinition(code, name);
	}

    public void createFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition) {
		failureStructureRepository.createFailureTypeDefinition(failureTypeDefinition);
	}

     public void updateFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition) {
		failureStructureRepository.updateFailureTypeDefinition(failureTypeDefinition);
	}

     public FailureCauseDefinition fetchFailureCauseDefinition(String code, String name) {
		return failureStructureRepository.findFailureCauseDefinition(code, name);
	}

    public void createFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition) {
		failureStructureRepository.createFailureCauseDefinition(failureCauseDefinition);
	}

     public void updateFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition) {
		failureStructureRepository.updateFailureCauseDefinition(failureCauseDefinition);
	}

      public ActionDefinition fetchActionDefinition(String code, String name) {
		return failureStructureRepository.findActionDefinition(code, name);
	}

    public FailureTypeDefinition findFailureTypeDefinitionByCode(String code) {
		return failureStructureRepository.findFailureTypeDefinitionByCode(code);
	}

    public FailureCauseDefinition findFailureCauseDefinitionByCode(String code) {
            return failureStructureRepository.findFailureCauseDefinitionByCode(code);
        }
    
    public FailureCauseDefinition findFailureCauseDefinitionById(Long id) {
        return failureStructureRepository.findFailureCauseDefinitionById(id);
    }

    public void createActionDefinition(ActionDefinition actionDefinition) {
		failureStructureRepository.createActionDefinition(actionDefinition);
	}

     public void updateActionDefinition(ActionDefinition actionDefinition) {
		failureStructureRepository.updateActionDefinition(actionDefinition);
	}

    public FailureType findFailureTypeForFaultCode(FaultCode faultCode) {
		return failureStructureRepository.findFailureTypeForFaultCode(faultCode);
	}

	public List<FailureTypeDefinition> findFaultFoundOptionsAtProduct(
			String inventoryItemId) {
		return failureStructureRepository.findFaultFoundOptionsAtProduct(inventoryItemId);
	}

	public FailureTypeDefinition findFaultFoundOptionsForProductByFaultName(
			String id, String faultFound) {
		return failureStructureRepository.findFaultFoundOptionsAtProduct(
				id.toString(), faultFound);
	}

	public List<FaultCode> getAnyFaultCodeRefForGivenFaultCode(String faultLocation,final int pageNumber,final int pageSize) {
		// TODO Auto-generated method stub
		return failureStructureRepository.getAnyFaultCodeRefForGivenFaultCode(faultLocation,pageNumber, pageSize);
	}

	  public Assembly findAssemblyById(Long id){
	        return failureStructureRepository.findAssemblyById(id);
	    }

}
