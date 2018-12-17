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

import java.util.Iterator;
import java.util.List;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class FailureStructureRepositoryImplIntegrationTest extends DomainRepositoryTestCase {

	FailureStructureRepository failureStructureRepository;
	CatalogRepository catalogRepository;

	public void testQuery() {
		Item item = new Item();
		item.setId(new Long(99999));
		assertNull(this.failureStructureRepository.findFailureStructureForItem(item));
		ItemGroup itemGroup = (ItemGroup) getSession().load(ItemGroup.class, new Long(1));
		assertNull(this.failureStructureRepository.getFailureStructureForItemGroup(itemGroup));
	}

	public void testFindAssemblyLevel() {
		AssemblyLevel level = this.failureStructureRepository.findAssemblyLevel(1);
		assertNotNull(level);
		level = this.failureStructureRepository.findAssemblyLevel(5);
		assertNull(level);
	}

	public void testFindAssemblyDefintions() {
		PageResult<AssemblyDefinition> result = this.failureStructureRepository.findAssemblyDefinitions("H", 1,
				new PageSpecification(0, 25));
		assertEquals(2, result.getResult().size());
		assertEquals("Hydraulic", result.getResult().get(0).getName());

	}

	public void testCreateAssemblyDefintion() {
		AssemblyDefinition assemblyDefinition = this.failureStructureRepository.createAssemblyDefinition("Transmission", 1);
		assertEquals("AH", assemblyDefinition.getCode());

		assemblyDefinition = this.failureStructureRepository.createAssemblyDefinition("Gear Box", 2);
		assertEquals("004", assemblyDefinition.getCode());
	}

	public void testFindAllAssemblyLevles() {
		List<AssemblyLevel> assemblyLevels = this.failureStructureRepository.findAllAssemblyLevels();
		assertEquals(4, assemblyLevels.size());
		int currentLevel = 0;
		// Check if they are returned in ascending order
		for (Iterator iter = assemblyLevels.iterator(); iter.hasNext();) {
			AssemblyLevel assemblyLevel = (AssemblyLevel) iter.next();
			assertTrue(assemblyLevel.getLevel() > currentLevel);
			currentLevel = assemblyLevel.getLevel();
		}
	}

	public void testfindFailureStructureForItem(){
		Item item = this.catalogRepository.findItem("MC-COUGAR-50-HZ-1");
		assertNotNull(item);
		FailureStructure failureStructure = this.failureStructureRepository.findFailureStructureForItem(item);
		assertNotNull(failureStructure);
	}
	
	public void testFindAllFaultCodeDefinitions() {
    	createFaultCodeDefinition("AA-001");
    	createFaultCodeDefinition("AB-001");
    	createFaultCodeDefinition("AC-002");
    	getSession().flush();
    	ListCriteria listCriteria = new ListCriteria();
    	listCriteria.addFilterCriteria("faultCodeDefinition.components[0].code", "A");
    	PageResult<FaultCodeDefinition> result =  this.failureStructureRepository.findAllFualtCodeDefinitions(listCriteria);
    	assertEquals(3,result.getResult().size());
    	listCriteria.addFilterCriteria("faultCodeDefinition.components[1].code", "002");
    	result =  this.failureStructureRepository.findAllFualtCodeDefinitions(listCriteria);
    	assertEquals(1,result.getResult().size());
    }
	
	public void testFindAllFaultCodeDefinitions_BusinessUnitFilter() {
    	ListCriteria listCriteria = new ListCriteria();
    	listCriteria.addFilterCriteria("faultCodeDefinition.code", "AV");
    	PageResult<FaultCodeDefinition> result =  this.failureStructureRepository.findAllFualtCodeDefinitions(listCriteria);
    	assertEquals(0,result.getResult().size());
    	listCriteria.addFilterCriteria("faultCodeDefinition.code", "B");
    	result =  this.failureStructureRepository.findAllFualtCodeDefinitions(listCriteria);
    	assertEquals(2,result.getResult().size());
    }
	
	public void testFindAllServiceProcedureDefinitions_BusinessUnitFilter() {
    	ListCriteria listCriteria = new ListCriteria();
    	listCriteria.addFilterCriteria("serviceProcedureDefinition.code", "AV");
    	PageResult<ServiceProcedureDefinition> result =  this.failureStructureRepository.findAllServiceProcedureDefinitions(listCriteria);
    	assertEquals(1,result.getResult().size());
    	listCriteria.addFilterCriteria("serviceProcedureDefinition.code", "B");
    	result =  this.failureStructureRepository.findAllServiceProcedureDefinitions(listCriteria);
    	assertEquals(1,result.getResult().size());
	
	}


	public void testAPIsForFailureTypeAndFailureCause() {
		//for failure type
		FailureTypeDefinition ftd = new FailureTypeDefinition();
		ftd.setCode("code");
		ftd.setDescription("desc");
		ftd.setName("name");
		FailureTypeDefinition received = (FailureTypeDefinition) this.failureStructureRepository.saveAndReturnObject(ftd);
		assertNotNull(received.getId());
		FailureType failureType = new FailureType();
		failureType.setForItemGroup(this.catalogRepository.findItemGroup(1L));
		failureType.setDefinition(ftd);
		assertNotNull(((FailureType)this.failureStructureRepository.saveAndReturnObject(failureType)).getId());
		List<FailureType> failureTypesForItemGroup = this.failureStructureRepository.findFailureTypesForItemGroup(this.catalogRepository.findItemGroup(1L));
		assertEquals(1, failureTypesForItemGroup.size());
		PageResult<FailureTypeDefinition> failureTypesStartingWith = this.failureStructureRepository.fetchFailureTypesStartingWith("n", new PageSpecification(0, 10));
		assertEquals(1, failureTypesStartingWith.getResult().size());
		assertTrue(failureTypesStartingWith.getResult().contains(failureType.getDefinition()));
		
		//for failure cause
		FailureCauseDefinition fcd = new FailureCauseDefinition();
		fcd.setCode("code");
		fcd.setDescription("desc");
		fcd.setName("name");
		fcd = (FailureCauseDefinition) this.failureStructureRepository.saveAndReturnObject(fcd);
		assertNotNull(fcd.getId());
		FailureCause fc = new FailureCause();
		fc.setDefinition(fcd);
		fc = (FailureCause) this.failureStructureRepository.saveAndReturnObject(fc);
		Long id = fc.getId();
		assertNotNull(id);
		assertNull(fc.getFailureType());
		PageResult<FailureCauseDefinition> failureCausesStartingWith = this.failureStructureRepository.fetchFailureCausesStartingWith("n", new PageSpecification(0,10));
		assertEquals(1, failureCausesStartingWith.getResult().size());
		assertTrue(failureCausesStartingWith.getResult().contains(fc.getDefinition()));
		
		//Set FailureType for FailureCause and update.
		fc.setFailureType(failureType);
		this.failureStructureRepository.updateAndReturnObject(fc);
		FailureCause fCode = (FailureCause) this.failureStructureRepository.findObjectByPrimaryKey(FailureCause.class, id);
		assertNotNull(fCode);
		assertNotNull(fCode.getFailureType());
		List<FailureCause> failureCausesForFailureType = this.failureStructureRepository.findFailureCausesForFailureType(failureType);
		assertEquals(1, failureCausesForFailureType.size());
	}

	private void createFaultCodeDefinition(String faultCode) {
		FaultCodeDefinition faultCodeDefinition = new FaultCodeDefinition();
		String[] components = faultCode.split(Assembly.FAULT_CODE_SEPARATOR);
		for (int i = 0; i < components.length; i++) {
			faultCodeDefinition.addComponent(this.failureStructureRepository.findAssemblyDefinition(components[i], i + 1));
		}
		this.failureStructureRepository.createFaultCodeDefintion(faultCodeDefinition);
	}

	public void setFailureStructureRepository(FailureStructureRepository failureStructureRepository) {
		this.failureStructureRepository = failureStructureRepository;
	}

}
