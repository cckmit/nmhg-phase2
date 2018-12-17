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
package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.LabelRepository;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class PolicyDefinitionRepositoryImplTest
        extends DomainRepositoryTestCase {

    private PolicyDefinitionRepository policyDefinitionRepository;
    private CostCategoryRepository costCategoryRepository;
    private CatalogRepository catalogRepository;
    private InventoryItemRepository inventoryItemRepository;
    private LabelRepository labelRepository;

    public void setLabelRepository(LabelRepository labelRepository) {
		this.labelRepository = labelRepository;
	}

	public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public void setPolicyDefinitionRepository(PolicyDefinitionRepository policyDefinitionRepository) {
        this.policyDefinitionRepository = policyDefinitionRepository;
    }

    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    public void testFindAll_NoFilterCriteria() {
        ListCriteria listCriteria = new ListCriteria();
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageSize(4);
        listCriteria.setPageSpecification(pageSpecification);
        PageResult<PolicyDefinition> pageResult = this.policyDefinitionRepository.findAll(listCriteria);
        assertEquals(2, pageResult.getNumberOfPagesAvailable());
        assertFalse(pageResult.getResult().isEmpty());
    }

    public void testFindAll_SomeFilterCriteria() {
        ListCriteria listCriteria = new ListCriteria();

        listCriteria.addFilterCriteria("policyDefinition.code", "S");

        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageSize(4);
        listCriteria.setPageSpecification(pageSpecification);
        PageResult<PolicyDefinition> pageResult = this.policyDefinitionRepository.findAll(listCriteria);
        assertEquals(2, pageResult.getNumberOfPagesAvailable());
        assertFalse(pageResult.getResult().isEmpty());
    }

    public void testFindById() {
        assertNotNull(this.policyDefinitionRepository.findById(1L));
    }

    public void testFindPoliciesForProduct() {
        Long productId = 5L;
        ItemGroup product = this.catalogRepository.findItemGroup(productId);
        assertNotNull(product);
        List<PolicyDefinition> policyDefinitions = this.policyDefinitionRepository.findPoliciesForProduct(product, CalendarDate.date(2006, 1, 1));
        assertEquals(3, policyDefinitions.size());
    }

    public void testFindPoliciesForProductCode() {
        Long productId = 5L;
        ItemGroup product = this.catalogRepository.findItemGroup(productId);
        assertNotNull(product);
        List<PolicyDefinition> policyDefinitions = this.policyDefinitionRepository.findPoliciesForProductCode(product.getName(), CalendarDate.date(2006, 1, 1));
        assertEquals(3, policyDefinitions.size());
    }

    public void testFindPoliciesForInventoryItem() throws ItemNotFoundException {
        InventoryItem item = this.inventoryItemRepository.findSerializedItem("LX3742");
        List<PolicyDefinition> policyDefinitions = this.policyDefinitionRepository.findPoliciesForInventory(item, CalendarDate.date(2006, 1, 1));
        assertEquals(0, policyDefinitions.size());
    }

    public void testFindPoliciesForItemNumber() {
        CalendarDate date = CalendarDate.date(2006, 1, 1);
        List<PolicyDefinition> policyDefinitions = this.policyDefinitionRepository.findPoliciesForItem("MC-COUGAR-50-HZ-1", date);
        assertEquals(3, policyDefinitions.size());
    }

	public void testFindTransferablePoliciesForInventoryItem() {
		InventoryItem item = this.inventoryItemRepository.findById(new Long(17));
		assertNotNull(item);
		CalendarDate date = CalendarDate.date(2006, 1, 1);
		List<PolicyDefinition> policyDefinitions = this.policyDefinitionRepository
				.findTransferablePoliciesForInventoryItem(item, date);
		assertEquals(2, policyDefinitions.size());
	}

    public void testDeletePolicyDefinition() {
        PolicyDefinition policyDefinition = aNewPolicy();
        assertNotNull(getSession().get(PolicyDefinition.class, policyDefinition.getId()));
        this.policyDefinitionRepository.delete(policyDefinition);
        flush();
        assertNull(getSession().get(PolicyDefinition.class, policyDefinition.getId()));
    }

//    public void testSavePolicyDefinition() {
//        CostCategory OEM_PARTS = costCategoryRepository.findCostCategoryByCode("OEM_PARTS");
//        CostCategory LABOR = costCategoryRepository.findCostCategoryByCode("LABOR");
//        CostCategory TRAVEL = costCategoryRepository.findCostCategoryByCode("TRAVEL");
//
//        List<CostCategory> costCategories = new ArrayList<CostCategory>(3);
//        costCategories.add(OEM_PARTS);
//        costCategories.add(LABOR);
//        costCategories.add(TRAVEL);
//
//        PolicyDefinition policyDefinition = createBasePolicyDefinition(costCategories);
//
//        DomainRule rule = new DomainRule();
//        rule.setName("Test Rule");
//        DomainPredicate aDomainPredicate = new DomainPredicate();
//        aDomainPredicate.setName("Some Condition");
//        aDomainPredicate.setContext("");
//
//        DomainSpecificVariable variable = TypeSystem.getInstance().findById(new Long(101));
//        Equals equals = new Equals(variable, new Constant("Machine", Type.STRING));
//        aDomainPredicate.setPredicate(equals);
//
//        OGNLExpressionGenerator expressionGenerator = new OGNLExpressionGenerator();
//        aDomainPredicate.accept(expressionGenerator);
//
//        String ognExpression = expressionGenerator.getExpressionString();
//
//        rule.setPredicate(aDomainPredicate);
//        policyDefinition.addApplicabilityTerm(rule);
//
//        policyDefinitionRepository.save(policyDefinition);
//        assertNotNull(policyDefinition.getId());
//        flush();
//
//        policyDefinition = (PolicyDefinition) getSession().get(PolicyDefinition.class, policyDefinition.getId());
//
//        assertPolicyDefinitionValidity(policyDefinition, costCategories);
//
//
//        rule = policyDefinition.getApplicabilityTerms().iterator().next();
//        assertNotNull(rule);
//
//        expressionGenerator = new OGNLExpressionGenerator();
//        rule.getPredicate().accept(expressionGenerator);
//
//        assertEquals(ognExpression, expressionGenerator.getExpressionString());
//
//        assertTrue(policyDefinition.isApplicable(new MachineClaim()));
//        assertFalse(policyDefinition.isApplicable(new PartsClaim()));
//    }

/*
    @SuppressWarnings("unchecked")
    public void testPolicyApplicabilityForOne2OneRule() throws Exception {
        PolicyDefinition policyDefinition = aNewPolicy();
        List<DomainRule> matchingRules =
                getDomainRuleRepository().findByName("not New or Refurbished");
        assertEquals(1, matchingRules.size());

        policyDefinition.setApplicabilityTerms(new HashSet(matchingRules));

        InventoryItem anItem = new InventoryItem();
        Claim aClaim = new MachineClaim(anItem, null, null);
        final InventoryItemCondition dummyInventoryItemCondition =
                new InventoryItemCondition("foo bar");

        anItem.setType(InventoryType.STOCK);

        anItem.setConditionType(InventoryItemCondition.NEW);
        assertFalse(policyDefinition.isApplicable(aClaim));

        anItem.setConditionType(InventoryItemCondition.REFURBISHED);
        assertFalse(policyDefinition.isApplicable(aClaim));

        anItem.setConditionType(dummyInventoryItemCondition);
        assertFalse(policyDefinition.isApplicable(aClaim));

        anItem.setType(InventoryType.RETAIL);

        anItem.setConditionType(InventoryItemCondition.NEW);
        assertFalse(policyDefinition.isApplicable(aClaim));

        anItem.setConditionType(InventoryItemCondition.REFURBISHED);
        assertFalse(policyDefinition.isApplicable(aClaim));

        anItem.setConditionType(dummyInventoryItemCondition);
        assertTrue(policyDefinition.isApplicable(aClaim));
    }

    @SuppressWarnings("unchecked")
    public void testPolicyApplicabilityForOne2ManyRule() throws Exception {
        PolicyDefinition policyDefinition = aNewPolicy();
        List<DomainRule> matchingRules =
                getDomainRuleRepository()
                        .findByName("for replaced OEM Valves and Cylinders");

        assertEquals(1, matchingRules.size());

        policyDefinition.setApplicabilityTerms(new HashSet(matchingRules));

        final OEMPartReplaced oemPartOne = new OEMPartReplaced();
        oemPartOne.setItemReference(new ItemReference(new Item()));
        final OEMPartReplaced oemPartTwo = new OEMPartReplaced();
        oemPartTwo.setItemReference(new ItemReference(new Item()));

        List oemParts = new ArrayList() {
            {
                add(oemPartOne);
                add(oemPartTwo);
            }
        };

        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.setOEMPartsReplaced(oemParts);
        ServiceInformation serviceInfo = new ServiceInformation();
        serviceInfo.setServiceDetail(serviceDetail);
        Claim aClaim = new MachineClaim();
        aClaim.setServiceInformation(serviceInfo);

        // Test non-matching case.
        oemPartOne.getItemReference().getUnserializedItem().setName("alpha");
        oemPartTwo.getItemReference().getUnserializedItem().setName("beta");

        assertFalse(policyDefinition.isApplicable(aClaim));

        // Test some-matching case.
        oemPartOne.getItemReference().getUnserializedItem().setName("Valve");
        oemPartTwo.getItemReference().getUnserializedItem().setName("beta");

        assertFalse(policyDefinition.isApplicable(aClaim));

        // Test all-matching case.
        oemPartOne.getItemReference().getUnserializedItem().setName("Valve");
        oemPartTwo.getItemReference().getUnserializedItem().setName("Cylinder");

        assertTrue(policyDefinition.isApplicable(aClaim));
    }
*/

    private PolicyDefinition createBasePolicyDefinition(List<CostCategory> costCategories) {

        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinition.setCode("someCode");

        //Availability
        Availability availability = new Availability();
        CalendarDate today = Clock.today();
        int mnthsFrmShipment = 12;
        CalendarDate oneYearFromNow = today.plusMonths(mnthsFrmShipment);
        CalendarDuration calendarDuration = new CalendarDuration(today, oneYearFromNow);
        availability.setDuration(calendarDuration);
        availability.setPrice(Money.dollars(120));
        policyDefinition.setAvailability(availability);

        //TransferDetails
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setTransferFee(Money.dollars(29));
        policyDefinition.setTransferDetails(transferDetails);

        //CoverageTerms
        CoverageTerms coverage = new CoverageTerms();
        int hoursCovered = 10;
        coverage.setMonthsCoveredFromDelivery(hoursCovered);
        coverage.setMonthsCoveredFromShipment(mnthsFrmShipment);
        coverage.setServiceHoursCovered(hoursCovered);

        policyDefinition.setCoverageTerms(coverage);
        policyDefinition.setPriority(new Long(1));

        return policyDefinition;
    }

    @SuppressWarnings("unused")
    private String getCommentOfSize(int size) {
        char[] comments = new char[size];

        for (int i = 0; i < size; i++) { // 400 times 10 = 4000
            comments[i] = 'a';
        }

        return String.valueOf(comments);
    }

    private void assertPolicyDefinitionValidity(PolicyDefinition policyDefinition, List<CostCategory> costCategories) {
        Availability availability = policyDefinition.getAvailability();
        assertNotNull(availability);

        TransferDetails transferDetails = policyDefinition.getTransferDetails();
        assertNotNull(transferDetails);
        assertFalse(transferDetails.isTransferable());
        assertEquals(Money.dollars(29), transferDetails.getTransferFee());

        CoverageTerms coverage = policyDefinition.getCoverageTerms();
        assertNotNull(coverage);

    }
    
    public void testFindAll_BusinessUnitFilter()
    {
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinition.setCode("someCode");

        CalendarDate today = Clock.today();
        int mnthsFrmShipment = 12;
        CalendarDate oneYearFromNow = today.plusMonths(mnthsFrmShipment);
        CalendarDuration calendarDuration = new CalendarDuration(today, oneYearFromNow);
        
        //Availability
        Availability availability = new Availability();
        availability.setDuration(calendarDuration);
        availability.setPrice(Money.dollars(120));
        policyDefinition.setAvailability(availability);

        //TransferDetails
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setTransferFee(Money.dollars(29));
        policyDefinition.setTransferDetails(transferDetails);

        //CoverageTerms
        CoverageTerms coverage = new CoverageTerms();
        int hoursCovered = 10;
        coverage.setMonthsCoveredFromDelivery(hoursCovered);
        coverage.setMonthsCoveredFromShipment(mnthsFrmShipment);
        coverage.setServiceHoursCovered(hoursCovered);

        policyDefinition.setCoverageTerms(coverage);
        policyDefinition.setPriority(new Long(1));

        policyDefinitionRepository.save(policyDefinition);
        assertNotNull(policyDefinition.getId());
        flush();
        
        ListCriteria listCriteria = new ListCriteria();
        listCriteria.addFilterCriteria("policyDefinition.code", "someCode");
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageSize(4);
        listCriteria.setPageSpecification(pageSpecification);
        PageResult<PolicyDefinition> pageResult = this.policyDefinitionRepository.findAll(listCriteria);
        assertEquals(1, pageResult.getNumberOfPagesAvailable());
        assertFalse(pageResult.getResult().isEmpty());
    }

    public void testUpdatePolicyDefinition() {
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinition.setCode("someCode");

        CalendarDate today = Clock.today();
        int mnthsFrmShipment = 12;
        CalendarDate oneYearFromNow = today.plusMonths(mnthsFrmShipment);
        CalendarDuration calendarDuration = new CalendarDuration(today, oneYearFromNow);
        
        //Availability
        Availability availability = new Availability();
        availability.setDuration(calendarDuration);
        availability.setPrice(Money.dollars(120));
        policyDefinition.setAvailability(availability);

        //TransferDetails
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setTransferFee(Money.dollars(29));
        policyDefinition.setTransferDetails(transferDetails);

        //CoverageTerms
        CoverageTerms coverage = new CoverageTerms();
        int hoursCovered = 10;
        coverage.setMonthsCoveredFromDelivery(hoursCovered);
        coverage.setMonthsCoveredFromShipment(mnthsFrmShipment);
        coverage.setServiceHoursCovered(hoursCovered);

        policyDefinition.setCoverageTerms(coverage);
        policyDefinition.setPriority(new Long(1));

        this.policyDefinitionRepository.save(policyDefinition);
        assertNotNull(policyDefinition.getId());
        flush();

        PolicyDefinition entity = (PolicyDefinition) getSession().get(PolicyDefinition.class, policyDefinition.getId());

        assertEquals(Money.dollars(120), entity.getAvailability().getPrice());


        availability = entity.getAvailability();
        availability.setPrice(Money.dollars(1));
        assertNotNull(availability);

        transferDetails = entity.getTransferDetails();
        assertNotNull(transferDetails);
        assertFalse(transferDetails.isTransferable());


        assertEquals(Money.dollars(29), transferDetails.getTransferFee());

        coverage = entity.getCoverageTerms();
        assertNotNull(coverage);
        this.policyDefinitionRepository.update(entity);
        flush();

        entity = (PolicyDefinition) getSession().get(PolicyDefinition.class, policyDefinition.getId());
        assertEquals(Money.dollars(1), entity.getAvailability().getPrice());
    }
    
    public void testCascadeWithLabelsAndFindByLabel() {
    	Label label = this.labelRepository.findById("Label1");
    	PolicyDefinition policyDefinition = aNewPolicy();
    	policyDefinition.getLabels().add(label);
    	this.policyDefinitionRepository.update(policyDefinition);
    	flush();
    	List<PolicyDefinition> findPolicyDefinitionsForLabel = this.policyDefinitionRepository.findPolicyDefinitionsForLabel(label);
		assertTrue(findPolicyDefinitionsForLabel.contains(policyDefinition));
		this.policyDefinitionRepository.delete(policyDefinition);
		flush();
    	assertTrue(this.policyDefinitionRepository.findPolicyDefinitionsForLabel(label).isEmpty());
    	assertNotNull(this.labelRepository.findById("Label1"));
    }

    /**
     * @return
     */
    protected PolicyDefinition aNewPolicy() {
        CostCategory OEM_PARTS = this.costCategoryRepository.findCostCategoryByCode("OEM_PARTS");
        CostCategory LABOR = this.costCategoryRepository.findCostCategoryByCode("LABOR");
        CostCategory TRAVEL = this.costCategoryRepository.findCostCategoryByCode("TRAVEL");

        List<CostCategory> costCategories = new ArrayList<CostCategory>(3);
        costCategories.add(OEM_PARTS);
        costCategories.add(LABOR);
        costCategories.add(TRAVEL);

        PolicyDefinition policyDefinition = createBasePolicyDefinition(costCategories);

        this.policyDefinitionRepository.save(policyDefinition);
        assertNotNull(policyDefinition.getId());
        flush();

        policyDefinition = (PolicyDefinition) getSession().get(PolicyDefinition.class, policyDefinition.getId());
        assertPolicyDefinitionValidity(policyDefinition, costCategories);

        return policyDefinition;
    }

    public void testFindPolicyDefinitionCodesStartingWith() {
    	List<String> codes = this.policyDefinitionRepository.findPolicyDefinitionCodesStartingWith("STD",
				0, 10);
    	assertNotNull(codes);
    	assertTrue(codes.size()==7);
    }
    public void testFindPolicyDefinitionWithPriority() {
    	PolicyDefinition policy = this.policyDefinitionRepository.findPolicyDefinitionWithPriority(new Long(1));
    	assertNotNull(policy);
    }
    
    @Required
	public void setInventoryItemRepository(
			InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}


    
}
