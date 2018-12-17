package tavant.twms.domain.customReports;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.ReportType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 2:29:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional(readOnly = true)
public interface CustomReportService extends GenericService<CustomReport, Long, Exception> {
    @Transactional(readOnly = false)
    public ReportSection createReportSection(ReportSection section);

    @Transactional(readOnly = false)
    public ReportSection updateReportSection(ReportSection section);

    @Transactional(readOnly = false)
    public ReportFormQuestion createReportFormQuestion(ReportFormQuestion formQuestion);

    @Transactional(readOnly = false)
    public ReportFormQuestion updateReportFormQuestion(ReportFormQuestion formQuestion);

    public PageResult<CustomReport> findReports(ListCriteria criteria);

    @Transactional(readOnly = false)
    public CustomReportAnswer createCustomReportAmswer(CustomReportAnswer reportAnswer);
    
    @Transactional(readOnly = false)
    public CustomReportAnswer updateCustomReportAnswer(CustomReportAnswer reportAnswer);

    @Transactional(readOnly = false)
    public ReportFormQuestion createReportFormQuestion(ReportSection reportSection,
                                    ReportFormQuestion formQuestion);

    public List<CustomReport> findReportsForInventory(InventoryItem inventoryItem);
    
    public List<CustomReport> findPublishedReportsForProducts(List<ItemGroup> itemGroups,List<InventoryType> inventoryTypes,Boolean published, ReportType reportType);
    
    public List<CustomReport> findConflictingReports(List<InventoryType> inventoryTypes,Boolean published, ReportType reportType,List<ItemGroup> itemGroups);
    
    public List<ItemGroup> findConflictingProductsModelsInReports(CustomReport userDefinedReport,CustomReport existingReport);
    
    public boolean isReportNameDuplicate(String name,Long id);
    

    public List<CustomReport> findReportsForParts(Collection<Item> items, Claim claim);
    
    public List<CustomReport> findReportsWithConflictingParts(CustomReport customReport,List<CustomReport> conflictingCustomReports);
    public boolean isPdiReportLinkAvailable(InventoryItem inventoryItem);
}
