package tavant.twms.domain.supplier;

import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface SupplierItemLocationRepository extends GenericRepository<SupplierItemLocation, Long> {
	
	public PageResult<SupplierItemLocation> findAllSupplierLocationItemsForSupplier(Supplier supplier, ListCriteria listCriteria);
	
	public List<SupplierItemLocation> findSupplierItems(Supplier supplier);
	
	public Long findLocationsByMapping(ItemMapping itemMapping,CalendarDate fromDate,CalendarDate toDate,String locationCode);
 
}
