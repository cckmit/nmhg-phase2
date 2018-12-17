package tavant.twms.failureReports;
/**
 * @author amritha.k
 */

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.ReportType;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.customReports.CustomReportApplicablePart;
import tavant.twms.domain.customReports.CustomReportRepository;
import tavant.twms.domain.customReports.CustomReportResultSet;
import tavant.twms.infra.IntegrationTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;


public class CustomReportTest extends IntegrationTestCase {
    private CustomReportRepository customReportRepository;
    private CatalogRepository catalogRepository;
    private ItemGroupRepository itemGroupRepository;
    private LovRepository lovRepository;


    public void testFetchingConflictingReports() throws Exception {
        login("rraible");
        try{
        	List<Item> listOfItems = new ArrayList<Item>();
        	listOfItems.add(catalogRepository.findById(new Long("1100111503640")));
        	listOfItems.add(catalogRepository.findById(new Long("1100105573900")));
        	listOfItems.add(catalogRepository.findById(new Long("1100111484940")));
        	List<CustomReport> listOfCustomReports = new ArrayList<CustomReport>();
        	listOfCustomReports.add(customReportRepository.findById(new Long("1100000007681")));
        	listOfCustomReports.add(customReportRepository.findById(new Long("1100000007800")));
        	ItemGroup itemGroup = itemGroupRepository.findById(new Long("1100000251540"));
        	ItemGroup itemGroup1 = itemGroupRepository.findById(new Long("1100000251620"));
        	List<ItemGroup> listOfItemGroups=new ArrayList<ItemGroup>();
        	listOfItemGroups.add(itemGroup1);
        	List<String> invType=new ArrayList<String>();
        	invType.add("RETAIL");
        	invType.add("STOCK");
        	ReportType repType = (ReportType)lovRepository.findById("ReportType",new Long("1100000039378"));
        	List<CustomReportResultSet> a = customReportRepository.findConflictingReportsBasedOnItemGroups(listOfItemGroups, listOfCustomReports);
        	List<CustomReport> b = customReportRepository.findConflictingReportsForStandAlonePartsClaim(true,repType);
        	//List<CustomReportResultSet> a= customReportRepository.findConflictingReportsBasedOnItems(listOfItems,listOfCustomReports);
        	System.out.println(a.size());
        	System.out.println(a.get(0));
        	Assert.notNull(a);
        }catch(Exception ex){
            throw ex;
        }
    }




	public CustomReportRepository getCustomReportRepository() {
		return customReportRepository;
	}




	public void setCustomReportRepository(CustomReportRepository customReportRepository) {
		this.customReportRepository = customReportRepository;
	}




	public ItemGroupRepository getItemGroupRepository() {
		return itemGroupRepository;
	}




	public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
		this.itemGroupRepository = itemGroupRepository;
	}




	public CatalogRepository getCatalogRepository() {
		return catalogRepository;
	}


	public void setCatalogRepository(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

    

  
 
}
