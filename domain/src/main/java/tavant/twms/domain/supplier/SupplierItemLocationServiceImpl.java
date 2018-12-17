package tavant.twms.domain.supplier;

import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class SupplierItemLocationServiceImpl extends GenericServiceImpl<SupplierItemLocation, Long, RuntimeException>
implements SupplierItemLocationService{
	
	SupplierItemLocationRepository supplierItemLocationRepository;   
	
	@Override
    public GenericRepository<SupplierItemLocation, Long> getRepository() {
        return supplierItemLocationRepository;
    }

	public SupplierItemLocationRepository getSupplierItemLocationRepository() {
		return supplierItemLocationRepository;
	}

	public void setSupplierItemLocationRepository(
			SupplierItemLocationRepository supplierItemLocationRepository) {
		this.supplierItemLocationRepository = supplierItemLocationRepository;
	}
	
	
	public PageResult<SupplierItemLocation> findAllSupplierLocationItemsForSupplier(Supplier supplier, ListCriteria listCriteria){
	   return supplierItemLocationRepository.findAllSupplierLocationItemsForSupplier(supplier,listCriteria);
	}

	public List<SupplierItemLocation> findSupplierItems(Supplier supplier) {
		return supplierItemLocationRepository.findSupplierItems(supplier);
		
	}

	public Long findLocationsByMapping(ItemMapping itemMapping,CalendarDate fromDate,
			CalendarDate toDate, String locationCode) {
	   return supplierItemLocationRepository.findLocationsByMapping(itemMapping,fromDate,toDate,locationCode);
	}
	
}
