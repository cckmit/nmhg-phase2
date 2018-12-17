package tavant.twms.domain.orgmodel;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.LocationRepository;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class SupplierServiceImpl extends GenericServiceImpl<Supplier, Long, RuntimeException>
        implements SupplierService {
    private SupplierRepository supplierRepository;
    
    private LocationRepository locationRepository;

    @Override
    public GenericRepository<Supplier, Long> getRepository() {
        return supplierRepository;
    }

    public void setSupplierRepository(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    public void setLocationRepository(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	public Supplier findSupplierByNumber(String supplierNumber) {
        return supplierRepository.findSupplierByNumber(supplierNumber);
    }
	public Supplier findSupplierByNumberWithOutActiveInactiveFilter(String supplierNumber) {
        return supplierRepository.findSupplierByNumberWithOutActiveOrInactiveStaus(supplierNumber);
    }
	
	public Supplier findSupplierByNumberWithOutBU(String supplierNumber) {
        return supplierRepository.findSupplierByNumberWithOutBU(supplierNumber);
    }

    public List<Supplier> findSupplierwithName(String userEntry) {

        return supplierRepository.findSupplierWithNameLike(userEntry);
    }

    public Supplier findSupplier(String supplierName) {
        return supplierRepository.findSupplierByName(supplierName);
    }
    public List<Supplier> findSuppliersForLabel(Label label) {
    	return supplierRepository.findSuppliersForLabel(label);
    }

	public PageResult<Location> findLocationsForSupplier(ListCriteria criteria, Supplier loggedInUserAsSupplier) {
		return supplierRepository.findLocationsForSupplier(criteria, loggedInUserAsSupplier);
	}

	public List<Location> findLocationsForSupplier(Supplier supplier) {
		return supplierRepository.findLocationsForSupplier(supplier);
	}

	@Transactional(readOnly = false)
	public void createLocationForSupplier(Long supplierId, Location supplierLocation, String locationType) {
		Supplier supplier = findById(supplierId);
		supplier.addSupplierLocation(locationType, supplierLocation);
		update(supplier);
	}
	
	@Transactional(readOnly = false)
	public void saveLocation(Location supplierLocation)
	{
		this.locationRepository.save(supplierLocation);
	}
	
	@Transactional(readOnly = false)
	public void updateSupplierLocation(Location supplierLocation)
	{
		this.locationRepository.update(supplierLocation);
	}
	
	public Supplier findById(Long id){
		return this.supplierRepository.findSupplierById(id);
	}
	public Supplier findByIdWithOutActivateInactivate(Long id){
		return this.supplierRepository.findSupplierByIdWithOutActivateInactivate(id);
	}
	public List<Supplier> findSuppliersWithNameLike(String name, int pageNumber,
			int pageSize) {
		return supplierRepository.findSuppliersWithNameLike(name, pageNumber, pageSize);
	}

	public PageResult<Supplier> findAllSuppliers(ListCriteria listCriteria) {
		return supplierRepository.findAllSuppliers(listCriteria);
	}

	public Supplier findSupplierByNameAndNumber(String supplierName,
			String supplierNumber) {
		return supplierRepository.findSupplierByNameAndNumber(supplierName, supplierNumber);
	}
	
}