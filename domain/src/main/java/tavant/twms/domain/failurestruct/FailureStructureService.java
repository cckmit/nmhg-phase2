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
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Job;
import tavant.twms.domain.common.Label;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;


@Transactional(readOnly = true)
public interface FailureStructureService {

	FailureStructure getFailureStructureForItem(Item item);
    
	FailureStructure getFailureStructureForItemGroup(ItemGroup itemGroup);

    List<FaultCodeDefinition> findFaultCodesForPart(Item part);
    
    List<FailureCauseDefinition> findCausedByOptions(String serialNumber, String faultFound);

    FailureStructure getFailureStructure(Claim claim, Item causalPart);
    
    @Transactional(readOnly=false)
    void update(FailureStructure failureStructure);

    public List<FailureTypeDefinition> findFaultFoundOptions(String inventoryItemId);
    
    public FailureTypeDefinition findFaultFoundOptionsForModelsByFaultName(final String modelId, final String faultName);
    
    List<FailureTypeDefinition> findFaultFoundOptionsForModels(String modelId);
    
   
	List<FailureCauseDefinition> findCausedByOptionsForModel(final String modelNumber,
			final String faultFound, String partialNameOrCode);

	List<FailureCauseDefinition> findCausedByOptionsForModel(final String modelNumber, final String faultFound);

   
    @Transactional(readOnly=false)
	AssemblyDefinition createAssemblyDefintion(String name, int level);

    AssemblyDefinition findAssemblyDefinition(Long id);
    
    PageResult<AssemblyDefinition> findAssemblyDefinitions(String nameStartsWith, 
    			int level, PageSpecification page,String locale);
    
    @Transactional(readOnly=false)
	ActionDefinition createActionDefinition(String name);

	ActionDefinition findActionDefinition(Long id);

	PageResult<ActionDefinition> findActionDefinitions(String nameStartsWith,
    			PageSpecification page,String locale);
    
    Collection<Job> findJobsStartingWith(final String jobCodePrefix);
    
    Collection<TreadBucket> findTreadBuckets();
	
	TreadBucket findTreadBucket(String code);
	
	@Transactional(readOnly=false)
	FaultCodeDefinition findOrCreateFaultCodeDefinition(String fullCode);

    @Transactional(readOnly=false)
	FaultCodeDefinition findOrCreateFaultCodeDefinition(Assembly assembly);


	FaultCode findFaultCode(Item item,String fullCode);
	
	FaultCode findFaultCodeByItemGroup(ItemGroup itemGroup,String fullCode);
	
	ServiceProcedure findServiceProcedure(Item item,String code);
	
	ServiceProcedure findServiceProcedureForRoundUp(String code);
	
	PageResult<FaultCodeDefinition> findAllFaultCodeDefinitions(ListCriteria listCriteria);
	
	PageResult<FailureCauseDefinition> fetchFailureCausesStartingWith(
			String nameStartsWith, PageSpecification page);
	
	public List<FailureCauseDefinition> findCausedByOptionsById(final String inventoryItemId, final String faultFoundId);
	
    public List<FailureCauseDefinition> findCausedByOptionsForModelById(final String modelNumber, final String faultFoundId);
	
	public FailureCauseDefinition findCausedById(final Long causedById);
	
	
	List<AssemblyLevel> findAllAssemblyLevels();
	
	public FaultCodeDefinition findFaultCodeDefinitionById(Long id);
	
	public List<FaultCodeDefinition> findFaultCodeDefinitionsByIds(Collection<Long> ids);
	
	@Transactional(readOnly=false)
	public void updateFaultCodeDefinition(FaultCodeDefinition definition);

	@Transactional(readOnly=false)
	ServiceProcedureDefinition findOrCreateServiceProcedureDefinition(String fullCode);
	
	PageResult<ServiceProcedureDefinition> findAllServiceProcedureDefinitions(ListCriteria listCriteria);
	
	ServiceProcedureDefinition findServiceProcedureDefinitionById(Long id);
	
	List<ServiceProcedureDefinition> findServiceProcedureDefinitionsByIds(Collection<Long> ids);
	
	@Transactional(readOnly=false)
	void updateServiceProcedureDefinition(ServiceProcedureDefinition definition);
	
	PageResult<FailureTypeDefinition> fetchFailureTypesStartingWith(
			String nameStartsWith, PageSpecification page);
	
	@Transactional(readOnly=false)
	public Object saveAndReturnObject(Object obj);
	
	@Transactional(readOnly=false)
	public Object updateAndReturnObject(Object obj);
	
	@Transactional(readOnly=false)
	public void deleteObject(Object obj);
	
	public FailureTypeDefinition findFailureTypeDefinitionByName(String name);
	
	public FailureRootCauseDefinition findFailureRootCauseDefinitionByName(String name);
	
	public Object findObjectByPrimaryKey(Class clazz, Serializable id);
	
	List<FailureType> findFailureTypesForItemGroup(ItemGroup itemGroup);
	
	List<FailureCause> findFailureCausesForFailureType(FailureType failureType);
	
	FailureStructure getMergedFailureStructureForItems(Collection<Item> items);
	
	FailureStructure getMergedFailureStructure(Collection<ItemGroup> itemGroups);
	
	public ServiceProcedure findServiceProcedureByDefinitionAndItem(
			ServiceProcedureDefinition serviceProcedureDefinitions, Claim claim);
	
	public List<FaultCodeDefinition> findAllFaultCodeDefinitionsForLabel(Label label) ;
	
	public List<ServiceProcedureDefinition> findAllJobCodeForLabel(Label label);
	
	public FailureTypeDefinition findFaultFoundById(final Long faultFoundId);
	
	public FailureTypeDefinition findFaultFoundByName(final String faultFoundName);

	PageResult<FailureRootCauseDefinition> fetchFailureRootCausesStartingWith(String startingWith, PageSpecification page);

	List<FailureRootCause> findFailureRootCausesForFailureType(FailureType failureType);

	List<FailureRootCauseDefinition> findRootCauseOptionsByModel(String number, String faultFound);
	
	FailureRootCauseDefinition findRootCauseOptionsByModelAndFailureDetail(final String number,
			final String faultFound, final String failureDetail);
	
	PageResult<?> findParentJobCodes(ListCriteria listCriteria);

	List<ServiceProcedureDefinition> findServiceProcedureDefinitionWhoseCodeStartsWith(final String code, final int pageNumber, final int pageSize);

	List<ServiceProcedureDefinition> findExistParentJobCodes();	
	
	FailureRootCauseDefinition findRootCauseById(Long rootCauseId);

    public AssemblyDefinition fetchAssemblyDefinition(int type, String code);

    public AssemblyDefinition fetchAssemblyDefinitionByName(int type, String code , String name);

    public void createAssemblyDefinition(AssemblyDefinition assemblyDefinition);

    public void updateAssemblyDefinition(AssemblyDefinition assemblyDefinition);

    public AssemblyLevel findAssemblyLevel(int level);

    public FailureTypeDefinition fetchFailureTypeDefinition(String code , String name);

    public void createFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition);

    public void updateFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition);

    public FailureCauseDefinition fetchFailureCauseDefinition(String code , String name);

    public void createFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition);

    public void updateFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition);

    public ActionDefinition fetchActionDefinition(String code , String name);

    public FailureTypeDefinition findFailureTypeDefinitionByCode(String code);

    public FailureCauseDefinition findFailureCauseDefinitionByCode(String code);

    public void createActionDefinition(ActionDefinition actionDefinition);

    public void updateActionDefinition(ActionDefinition actionDefinition);

    public FailureType findFailureTypeForFaultCode(FaultCode faultCode);

	public List<FailureTypeDefinition> findFaultFoundOptionsAtProduct(String invItemId);
	
	public FailureCauseDefinition findFailureCauseDefinitionById(Long id);

	FailureTypeDefinition findFaultFoundOptionsForProductByFaultName(String id,
			String faultFound);

	public List<FaultCode> getAnyFaultCodeRefForGivenFaultCode(String faultLocation,final int pageNumber,final int pageSize);
	
	 public Assembly findAssemblyById(Long id);

}
