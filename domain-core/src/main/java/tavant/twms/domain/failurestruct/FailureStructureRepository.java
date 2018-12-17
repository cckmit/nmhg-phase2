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

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.I18NAssemblyDefinition;
import tavant.twms.domain.common.Label;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public interface FailureStructureRepository extends GenericRepository<FailureStructure, Long> {

	FailureStructure findFailureStructureForItem(Item item);

	FailureStructure getFailureStructureForItemGroup(ItemGroup itemGroup);

	List<FaultCodeDefinition> findFaultCodesForPart(Item part);

	void update(FailureStructure failureStructure);

	public List<FailureTypeDefinition> findFaultFoundOptions(final String serialNumber);

	List<FailureTypeDefinition> findFaultFoundOptionsForModels(final String modelId);

	public FailureTypeDefinition findFaultFoundOptionsForModelsByFaultName(final String modelId, final String faultName);

	AssemblyLevel findAssemblyLevel(int level);

	AssemblyDefinition findAssemblyDefiniton(Long id);

	PageResult<AssemblyDefinition> findAssemblyDefinitions(String nameStartsWith, int level, PageSpecification page, String locale);

	ActionDefinition createActionDefintion(String name);

	ActionDefinition findActionDefinition(Long id);

	PageResult<ActionDefinition> findActionDefinition(String nameStartsWith, PageSpecification page, String locale);

	AssemblyDefinition createAssemblyDefinition(String name, int level);

	Collection<TreadBucket> findTreadBuckets();

	TreadBucket findTreadBucket(String code);

	FaultCodeDefinition findFaultCodeDefintiion(String faultCode);

	void createFaultCodeDefintion(FaultCodeDefinition faultCodeDefintion);

	AssemblyDefinition findAssemblyDefinition(String string, int i);

	AssemblyDefinition findAssemblyDefinitionByName(int i, String string, String name);

	PageResult<FaultCodeDefinition> findAllFualtCodeDefinitions(ListCriteria listCriteria);

	List<AssemblyLevel> findAllAssemblyLevels();

	public FaultCodeDefinition findFaultCodeDefinitionById(Long id);

	public List<FaultCodeDefinition> findFaultCodeDefinitionsByIds(Collection<Long> ids);

	public void updateFaultCodeDefinition(FaultCodeDefinition definition);

	ServiceProcedureDefinition findServiceProcedureDefintiion(String fullCode);

	ActionDefinition findActionDefinition(String code);

	void createServiceProcedureDefinition(ServiceProcedureDefinition serviceProcedureDefinition);

	PageResult<ServiceProcedureDefinition> findAllServiceProcedureDefinitions(ListCriteria listCriteria);

	ServiceProcedureDefinition findServiceProcedureDefinitionById(Long id);

	List<ServiceProcedureDefinition> findServiceProcedureDefinitionsByIds(Collection<Long> ids);

	void updateServiceProcedureDefinition(ServiceProcedureDefinition definition);

	PageResult<FailureTypeDefinition> fetchFailureTypesStartingWith(String nameStartsWith, PageSpecification page);

	public Object saveAndReturnObject(Object obj);

	public Object updateAndReturnObject(Object obj);

	FailureTypeDefinition findFailureTypeDefinitionByName(String name);

	Object findObjectByPrimaryKey(Class clazz, Serializable id);

	List<FailureType> findFailureTypesForItemGroup(ItemGroup itemGroup);

	List<FailureCause> findFailureCausesForFailureType(FailureType failureType);

	void deleteObject(Object obj);

	List<FailureStructure> findFailureStructuresForItemGroups(Collection<ItemGroup> itemGroups);

	List<FailureStructure> findFailureStructuresForItems(Collection<Item> items);

	public List<FaultCodeDefinition> findAllFaultCodeDefinitionsForLabel(Label label);

	public List<ServiceProcedureDefinition> findAllJobCodeForLabel(Label label);

	public FailureTypeDefinition findFaultFoundById(final Long faultFoundId);

	public FailureTypeDefinition findFaultFoundByName(final String faultFoundName);

	PageResult<FailureRootCauseDefinition> fetchFailureRootCausesStartingWith(String startingWith, PageSpecification page);

	List<FailureRootCause> findFailureRootCausesForFailureType(FailureType failureType);

	List<FailureRootCauseDefinition> findRootCauseOptionsByModel(String number, String faultFound);

	public FailureRootCauseDefinition findRootCauseOptionsByModelAndFailureDetail(final String number, final String faultFound, final String failureDetail);

	FailureRootCauseDefinition findRootCauseById(Long rootCauseId);

	FailureRootCauseDefinition findFailureRootCauseDefinitionByName(String name);

	public ServiceProcedure findServiceProcedureForRoundUp(String fullCode);

	PageResult<?> findParentJobCodes(ListCriteria listCriteria);

	List<ServiceProcedureDefinition> findServiceProcedureDefinitionWhoseCodeStartsWith(final String code, final int pageNumber, final int pageSize);

	List<ServiceProcedureDefinition> findExistParentJobCodes();

	public boolean findIfFaiureStructureExistsForAllItems(Collection<Item> items);

	List<FailureCauseDefinition> findCausedByOptions(final String serialNumber, final String faultFound);

	List<FailureCauseDefinition> findCausedByOptionsForModel(final String modelNumber, final String faultFound, String partialNameOrCode);

	List<FailureCauseDefinition> findCausedByOptionsForModel(final String modelNumber, final String faultFound);

	PageResult<FailureCauseDefinition> fetchFailureCausesStartingWith(String nameStartsWith, PageSpecification page);

	public List<FailureCauseDefinition> findCausedByOptionsById(final String serialNumber, final String faultFoundId);

	public List<FailureCauseDefinition> findCausedByOptionsForModelById(final String modelNumber, final String faultFoundId);

	public FailureCauseDefinition findCausedById(final Long causedById);

	FailureCauseDefinition findFailureCauseDefinitionByName(String name);

	public void createAssemblyDefinition(AssemblyDefinition assemblyDefinition);

	public void updateAssemblyDefinition(AssemblyDefinition assemblyDefinition);

	FailureTypeDefinition findFailureTypeDefinition(String string, String name);

	public void createFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition);

	public void updateFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition);

	FailureCauseDefinition findFailureCauseDefinition(String string, String name);

	public void createFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition);

	public void updateFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition);

	ActionDefinition findActionDefinition(String string, String name);

	FailureTypeDefinition findFailureTypeDefinitionByCode(String code);

	FailureCauseDefinition findFailureCauseDefinitionByCode(String code);

	public void createActionDefinition(ActionDefinition actionDefinition);

	public void updateActionDefinition(ActionDefinition actionDefinition);

	FailureType findFailureTypeForFaultCode(FaultCode faultCode);

	public List<FailureTypeDefinition> findFaultFoundOptionsAtProduct(String inventoryItemId);

	public FailureCauseDefinition findFailureCauseDefinitionById(Long id);

	public Assembly findAssemblyById(Long id);
	
	public FailureTypeDefinition findFaultFoundOptionsAtProduct(
			final String inventoryItemId, final String faultFoundName);

	List<FaultCode> getAnyFaultCodeRefForGivenFaultCode(String faultLocation,final int pageNumber,final int pageSize);
	
	public ServiceProcedureDefinition findJobCodeForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String jobCode);
	
	public FaultCodeDefinition findFaultCodeForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String faultCode);
	
	public FailureType findFaultFoundForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String faultFound);
	
	public List<FailureTypeDefinition> findAllFaultFoundCodes();
	
	public List<FailureCauseDefinition> findCuasedByUsingFaultFound(
			Long faultFoundId) ;
	public ServiceProcedure getServiceProcedureForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String jobCode) ;
	
	public FailureCauseDefinition findCausedByOptionsForModelByIdAndCausedByName(final String modelNumber, final String faultFoundId, final String causedByName);
}
