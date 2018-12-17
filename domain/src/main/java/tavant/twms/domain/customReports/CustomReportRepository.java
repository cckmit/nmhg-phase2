package tavant.twms.domain.customReports;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.ListCriteria;
import tavant.twms.domain.common.ReportType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.Claim;

import java.util.List;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 2:27:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CustomReportRepository extends GenericRepository<CustomReport, Long> {
    public ReportSection createReportSection(ReportSection section);

    public ReportSection updateReportSection(ReportSection section);

    public ReportFormQuestion createReportFormQuestion(ReportFormQuestion formQuestion);

    public ReportFormQuestion updateReportFormQuestion(ReportFormQuestion formQuestion);

    public PageResult<CustomReport> findReports(ListCriteria criteria);

    public CustomReportAnswer createCustomReportAmswer(CustomReportAnswer reportAnswer);
    
    public CustomReportAnswer updateCustomReportAnswer(CustomReportAnswer reportAnswer);

    public List<CustomReport> findReportsForInventory(final InventoryItem inventoryItem);    
    
	public List<CustomReport> findPublishedReportsForProducts(final List<Long> itemGroupIds,List<String> inventoryTypeNames
    		,final Boolean published, final ReportType reportType);
	
	public List<CustomReport> findConflictingReports(List<String> inventoryTypeNames
    		,final Boolean published, final ReportType reportType);	
	
	public List<CustomReport> findConflictingReportsForStandAlonePartsClaim(final Boolean published, final ReportType reportType);	

    public List<CustomReport> findReportsForParts(Collection<Item> items, Claim claim);
    
    public List<CustomReportResultSet> findConflictingReportsBasedOnItems(List<Item> items,List<CustomReport> conflictingCustomReports);
	
    public List<CustomReportResultSet> findConflictingReportsBasedOnItemGroups(List<ItemGroup> itemGroups,List<CustomReport> conflictingCustomReports);
    
    public boolean isReportNameDuplicate(String name,Long id);

}
