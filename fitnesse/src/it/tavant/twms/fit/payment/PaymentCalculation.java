package tavant.twms.fit.payment;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentComponent;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.fit.infra.BeanWiredColumnFixture;

import com.domainlanguage.money.Money;

@SuppressWarnings("hiding")
public class PaymentCalculation extends BeanWiredColumnFixture {

    public String serialNumber;

    public String itemNumber;

    public String failureDate;

    public int laborHours;

    public int travelDistance;

    public int travelTime;

    public int numberOfTrips;

    public String oemPartNumber;

    public int numberOfOemParts;

    public double pricePerNonOemPart;

    public int numberOfNonOemParts;

    CostCategory laborCostCategory;

    CostCategory travelCostCategory;

    CostCategory oemCostCategory;

    CostCategory nonOemCostCategory;

    Payment payment;

    @Override
    public void execute() throws Exception {
        loadAllCostCategories();
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        serviceInformation.setServiceDetail(serviceDetail);
        Claim claim = getClaim();
        claim.setServiceInformation(serviceInformation);
        updateLabourDetailsTo(claim);
        updateTravelDetailsTo(claim);
        updateOemPartDetailsTo(claim);
        updateNonOemPartDetailsTo(claim);
        updatePaymentInformationTo(claim);
        payment = claim.getPayment();
    }

    private void loadAllCostCategories() {
        laborCostCategory = getCostCategoryRepository().findCostCategoryByCode("LABOR");
        travelCostCategory = getCostCategoryRepository().findCostCategoryByCode("TRAVEL");
        oemCostCategory = getCostCategoryRepository().findCostCategoryByCode("OEM_PARTS");
        nonOemCostCategory = getCostCategoryRepository().findCostCategoryByCode("NON_OEM_PARTS");
    }

    /* Labour Details */
    private void updateLabourDetailsTo(Claim claim) {
        ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
        LaborDetail laborDetail = new LaborDetail();
        laborDetail.setHoursSpent(laborHours);
        serviceDetail.addLaborDetail(laborDetail);
    }

    public double claimedLaborAmount() {
        return claimedAmountFor(laborCostCategory);
    }

    public double acceptedLaborAmount() {
        return acceptedAmountFor(laborCostCategory);
    }

    /* Travel Details */
    private void updateTravelDetailsTo(Claim claim) {
        ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
        TravelDetail travelDetail = new TravelDetail();
        travelDetail.setDistance(travelDistance);
        travelDetail.setHours(travelTime);
        travelDetail.setTrips(numberOfTrips);
        serviceDetail.setTravelDetails(travelDetail);
    }

    public double claimedTravelAmount() {
        return claimedAmountFor(travelCostCategory);
    }

    public double acceptedTravelAmount() {
        return acceptedAmountFor(travelCostCategory);
    }

    /* OEM Details */
    private void updateOemPartDetailsTo(Claim claim) throws Exception {
        Item item = catalogService.findItem(oemPartNumber);
        ItemReference itemReference = new ItemReference(item);
        OEMPartReplaced _OEMPartReplaced = new OEMPartReplaced(itemReference, numberOfOemParts);
        claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(_OEMPartReplaced);
    }

    public double claimedOemAmount() {
        return claimedAmountFor(oemCostCategory);
    }

    public double acceptedOemAmount() {
        return acceptedAmountFor(nonOemCostCategory);
    }

    /* Non OEM Details */
    private void updateNonOemPartDetailsTo(Claim claim) throws Exception {
        NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
        nonOEMPartReplaced.setNumberOfUnits(numberOfNonOemParts);
        nonOEMPartReplaced.setPricePerUnit(Money.dollars(pricePerNonOemPart));
        claim.getServiceInformation().getServiceDetail().addNonOEMPartReplaced(nonOEMPartReplaced);
    }

    public double claimedNonOemAmount() {
        return claimedAmountFor(nonOemCostCategory);
    }

    public double acceptedNonOemAmount() {
        return acceptedAmountFor(nonOemCostCategory);
    }

    private double claimedAmountFor(CostCategory costCategory) {
        PaymentComponent paymentComponent = payment.getComponent(costCategory);
        if (paymentComponent != null) {
            return paymentComponent.getClaimedAmount().breachEncapsulationOfAmount().doubleValue();
        }
        return 0D;
    }

    private double acceptedAmountFor(CostCategory costCategory) {
        PaymentComponent paymentComponent = payment.getComponent(costCategory);
        if (paymentComponent != null) {
            return 0D;
        }
        return 0D;
    }

    public double totalClaimedAmount() {
        return payment.getClaimedAmount().breachEncapsulationOfAmount().doubleValue();
    }

    public double totalAcceptedAmount() {
        return payment.getTotalAmount().breachEncapsulationOfAmount().doubleValue();
    }

    protected Claim getClaim() {
        if (hasText(serialNumber) && hasText(itemNumber)) {
            throw new IllegalArgumentException("Cannot give both serial number and "
                    + "item number at the same time");
        }
        Claim claim = null;
        if (hasText(serialNumber)) {
            InventoryItem invItem = null;
            try {
                invItem = getInventoryService().findSerializedItem(serialNumber);
            } catch (ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
            claim = new MachineClaim();
            
            ItemReference itemReference = new ItemReference(invItem);
            claim.setItemReference(itemReference);
        } else if (hasText(itemNumber)) {
            Item item = null;
            try {
                item = getCatalogService().findItem(itemNumber);
            } catch (CatalogException e) {
                throw new RuntimeException(e);
            }
            claim = new PartsClaim();
            ItemReference itemReference = new ItemReference(item);            
            claim.setItemReference(itemReference);
        } else {
            throw new IllegalArgumentException("Unable to test without either an serial number"
                    + " or an item number");
        }
        claim.setFailureDate(getDate(failureDate));
        getClaimService().createClaim(claim);
        return claim;
    }

    protected void updatePaymentInformationTo(Claim claim) throws Exception {
        Policy applicablePolicy = policyService.findApplicablePolicy(claim);
        claimService.updateApplicablePolicy(claim,applicablePolicy);
        claimService.updatePaymentInformation(claim);
    }

    private CatalogService catalogService;

    private InventoryService inventoryService;

    private ClaimService claimService;

    private CostCategoryRepository costCategoryRepository;
    
    private PolicyService policyService;

    /**
     * @return the claimService
     */
    public ClaimService getClaimService() {
        return claimService;
    }

    /**
     * @param claimService
     *            the claimService to set
     */
    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * @return the inventoryService
     */
    public InventoryService getInventoryService() {
        return inventoryService;
    }

    /**
     * @param inventoryService
     *            the inventoryService to set
     */
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * @return the catalogService
     */
    public CatalogService getCatalogService() {
        return catalogService;
    }

    /**
     * @param catalogService
     *            the catalogService to set
     */
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * @return the costCategoryRepository
     */
    public CostCategoryRepository getCostCategoryRepository() {
        return costCategoryRepository;
    }

    /**
     * @param costCategoryRepository
     *            the costCategoryRepository to set
     */
    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    public void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }
}