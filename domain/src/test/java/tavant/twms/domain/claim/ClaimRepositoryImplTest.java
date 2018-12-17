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

package tavant.twms.domain.claim;

import java.util.Collection;
import com.domainlanguage.time.CalendarDate;
import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.rules.model.RuleRepository;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
/**
 * @author kamal.govindraj
 *
 */
public class ClaimRepositoryImplTest extends DomainRepositoryTestCase {

    ClaimRepository claimRepository;

    InventoryService itemService;

    InventoryItem inventoryItem;

    RuleRepository ruleRepository;
    
    UserRepository userRepository;

    Claim newClaim;

    ClaimedItem claimedItem;

    CalendarDate failureDate = CalendarDate.date(2006, 5, 1);

    CalendarDate repairDate = this.failureDate.nextDay();

    ClaimXMLConverter claimXMLConverter;

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        this.inventoryItem = this.itemService.findSerializedItem("ABCD123456");

        // Test create
        this.newClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
        this.claimedItem = getOrCreateFirstClaimedItemFromClaim(this.newClaim);
        this.claimedItem.setHoursInService(10);
        this.newClaim.setState(ClaimState.DRAFT);
        User dealer =userRepository.findById(new Long(1));
        this.newClaim.setFiledBy(dealer);
        this.newClaim.setUpdated(false);
    }

    public void testCRUD() {
        this.claimRepository.save(this.newClaim);
        assertNotNull("Id should have been assigned", this.newClaim.getId());

        flushAndClear();
        // test find
        Claim retreivedFromDB = this.claimRepository.find(this.newClaim.getId());
        assertNotNull(retreivedFromDB);
        InventoryItem inventoryItem =
                retreivedFromDB.getClaimedItems().get(0).getItemReference().getReferredInventoryItem();
        assertEquals(this.inventoryItem.getId(), inventoryItem.getId());
        assertEquals(this.newClaim.getFailureDate(), retreivedFromDB.getFailureDate());
        assertEquals(this.newClaim.getRepairDate(), retreivedFromDB.getRepairDate());

        // test update
        flushAndClear();
        retreivedFromDB = this.claimRepository.find(this.newClaim.getId());
        retreivedFromDB.setFailureDate(this.newClaim.getFailureDate().nextDay());
        this.claimRepository.update(retreivedFromDB);
        flushAndClear();
        retreivedFromDB = this.claimRepository.find(this.newClaim.getId());
        assertEquals(this.newClaim.getFailureDate().nextDay(), retreivedFromDB.getFailureDate());
    }

    // TODO: Enable this after the March 13 demo.
    /*
     * public void testSaveClaimWithRuleFailure() { RuleSet setupRule = new
     * RuleSet("SetupRules", "Claim Setup Rules", "SomeScript");
     * setupRule.setPath("somePath"); ruleRepository.save(setupRule); RuleSet
     * duplicateClaimRule = new RuleSet("DuplicateClaimRule", "Duplicate Claim
     * Rule", "SomeScript"); duplicateClaimRule.setPath("againSomePath");
     * ruleRepository.save(duplicateClaimRule); RuleSet laborOverChargeRule =
     * new RuleSet("LaborOverChargeRule", "Labor Over Charge Rule",
     * "SomeScript"); laborOverChargeRule.setPath("againSomePath");
     * ruleRepository.save(laborOverChargeRule);
     *
     * assertTrue(newClaim.addRuleFailure(new RuleFailure(setupRule, new
     * String[] { "PriceNotSetupForDealer", "PriceNotSetupForProductType" })));
     *
     * assertTrue(newClaim.addRuleFailure(new RuleFailure(duplicateClaimRule,
     * new String[] { "TravelDetailsOnTwoClaimForSameDayRepair",
     * "DuplicateClaim" })));
     *
     * assertTrue(newClaim.addRuleFailure(new RuleFailure(laborOverChargeRule,
     * new String[] { "LaborHoursMoreThanAllowed" })));
     *
     * claimRepository.save(newClaim); assertNotNull(newClaim.getId());
     *
     * flushAndClear();
     *
     * Claim fetchedFromDB = claimRepository.find(newClaim.getId());
     * assertEquals(3, fetchedFromDB.getRuleFailures().size()); //
     * assertEquals("Sort of rule failures not working", "DuplicateClaimRule",
     * fetchedFromDB //
     * .getRuleFailures().first().getFailedRuleSet().getName());
     * assertEquals("Saving rule names as collection not working", 2,
     * fetchedFromDB.getRuleFailures() .first().getFailedRules().size()); }
     */

    public void testCreatedDateAuditingOnSave() {
        assertNull("createdDate should have been null for unsaved claim.", this.newClaim
                .getFiledOnDate());

        // Save the claim.
        this.claimRepository.save(this.newClaim);
        flushAndClear();

        assertNotNull("createdDate should have been automatically " + "set for saved claim.",
                this.newClaim.getFiledOnDate());

    }

    public void testLastUpdatedDateAuditingOnUpdate() {
        assertNull(this.newClaim.getLastUpdatedOnDate());
        assertNull(this.newClaim.getFiledOnDate());
        assertNull(this.newClaim.getFiledBy());
        assertNull(this.newClaim.getLastUpdatedBy());

        // Save the claim.
        this.claimRepository.save(this.newClaim);

        /*
         * [Vikas S] IMPORTANT: We can't use flushAndClear() here since it will
         * trigger the hibernate bug [HHH-511]
         * (http://opensource.atlassian.com/projects/hibernate/browse/HHH-511)
         * which causes a "Found two representations of same collection"
         * HibernateException to be thrown if you do a session.clear(), followed
         * by a session.update() in the *same* session.
         *
         * This is currently marked as a "minor" bug. The suggested workarounds
         * are:
         *
         * 1) Use merge/saveOrUpdate instead of update. 2) Use different
         * sessions for clear and update. 3) Don't do a clear(). ;-)
         *
         * We are using approach (3) since we do not depend that much on clear()
         * being run.
         *
         */
        getSession().flush();

        // Perform an update.
        this.newClaim.setConditionFound("test");
        this.claimRepository.update(this.newClaim);

        assertNotNull(this.newClaim.getLastUpdatedOnDate());
        assertNotNull(this.newClaim.getFiledOnDate());
        assertNotNull(this.newClaim.getFiledBy());
        assertNotNull(this.newClaim.getLastUpdatedBy());
    }

    // todo-this should move to a separate class and it is incomplete as of now
    // !!
    public void disable_testClaimXMLTwoWayConversion() throws Exception {
        this.inventoryItem = this.itemService.findSerializedItem("ABCD123456");

        // Test create
        this.newClaim = new MachineClaim(this.inventoryItem, this.failureDate, this.repairDate);
        this.claimedItem.setHoursInService(10);
        this.newClaim.setState(ClaimState.DRAFT);
        this.claimRepository.save(this.newClaim);
        assertNotNull(this.newClaim.getId());

        flushAndClear();

        Claim fetchedFromDB = this.claimRepository.find(this.newClaim.getId());
        String xml = this.claimXMLConverter.convertObjectToXML(fetchedFromDB);
        assertNotNull(xml);
        Claim claim = (Claim) this.claimXMLConverter.convertXMLToObject(xml);
        assertNotNull(claim);
        InventoryItem inventoryItem = claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem();
        assertNotNull(inventoryItem);

    }
    public void testFindAllPreviousClaimsForItem()
    {
    	Collection<Claim> claims = claimRepository.findAllPreviousClaimsForItem(null);
    	assertEquals(1,claims.size());
    }

    /**
     * @param claimRepository the claimRepository to set
     */
    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    /**
     * @param itemService the itemService to set
     */
    public void setItemService(InventoryService itemService) {
        this.itemService = itemService;
    }

    public void setRuleRepository(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public ClaimXMLConverter getClaimXMLConverter() {
        return this.claimXMLConverter;
    }

    public void setClaimXMLConverter(ClaimXMLConverter claimXMLConverter) {
        this.claimXMLConverter = claimXMLConverter;
    }

    public void testFindAllClaimsInStateForExistingState() {
        assertEquals(2, this.claimRepository.findAllClaimsInState(ClaimState.DRAFT).size());
    }

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
}
