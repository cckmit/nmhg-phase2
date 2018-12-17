package tavant.twms.failureReports;

import tavant.twms.domain.customReports.CustomReportService;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.catalog.*;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.infra.IntegrationTestCase;

import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;

import junit.framework.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: Mar 23, 2010
 * Time: 6:29:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestFailureReports  extends IntegrationTestCase {

    private CustomReportService customReportService;
    private CatalogService catalogService;
    private InventoryItemRepository inventoryItemRepository;
    private ClaimRepository claimRepository;
    private ClaimService claimService;
    private CatalogRepository catalogRepository;
    private ItemGroupRepository itemGroupRepository;

    public void testFetchingOfFailureReports() throws ItemNotFoundException {
        login("rraible");
        /*List<Item> items = new ArrayList<Item>();
        items.add(catalogService.findParts("1200A05H01").get(0));
        InventoryItem inventoryItem = inventoryItemRepository.findItemBySerialNumber("0001035719").get(0);
        List<CustomReport> failureReports = customReportService.findReportsForParts(items,inventoryItem);*/
        /* Tested for parts claim Claim claim = claimRepository.find(new Long("1119890489800"));*/
        /* Tested for non szd machine claim 1119890486800*/
        Claim claim = claimRepository.find(new Long("1119890426780"));//(new Long("1119890486800"));//mc-slzd"1119890426780"));parts 1119890489800
        ItemGroup itemGroup= itemGroupRepository.findById(new Long("1100000251660"));
        Assert.assertTrue(claimService.isAnyFailureReportPendingOnClaim(claim));
        //Assert.assertEquals(failureReports.size(),1);
    }

    public void _testFetchingOfGroupForItem() throws ItemNotFoundException {
        login("rraible");
        Item item = catalogRepository.findById(new Long("1100106492040"));
        ItemGroup itemGroup= itemGroupRepository.findById(new Long("1100000251660"));
        ItemGroup existingGroup = itemGroupRepository.findItemGroupForItem(itemGroup,item, AdminConstants.FAIURE_REPORT_PURPOSE);
        Assert.assertNotNull(existingGroup);
    }

    public void setCustomReportService(CustomReportService customReportService) {
        this.customReportService = customReportService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setInventoryItemRepository(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }
}
