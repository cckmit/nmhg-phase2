package tavant.twms.domain.supplier;

import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface SupplierItemLocationService extends GenericService<SupplierItemLocation, Long, RuntimeException> {
	
	public PageResult<SupplierItemLocation> findAllSupplierLocationItemsForSupplier(Supplier supplier, ListCriteria listCriteria);
	
	public List<SupplierItemLocation> findSupplierItems(Supplier supplier);
	
	public Long findLocationsByMapping(ItemMapping itemMapping,CalendarDate fromDate,CalendarDate toDate,String locationCode);
	
}
