package tavant.twms.domain.orgmodel;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface SupplierService extends GenericService<Supplier, Long, RuntimeException> {

    public Supplier findSupplierByNumber(String supplierNumber);
    public Supplier findSupplierByNumberWithOutActiveInactiveFilter(String supplierNumber);
    public Supplier findByIdWithOutActivateInactivate(Long id);
    
    public Supplier findSupplierByNumberWithOutBU(String supplierNumber);

    public List<Supplier> findSupplierwithName(String userEntry);

    public Supplier findSupplier(String supplierName);
    
    public List<Supplier> findSuppliersForLabel(Label label);

	public PageResult<Location> findLocationsForSupplier(ListCriteria criteria, Supplier loggedInUserAsSupplier);

	public List<Location> findLocationsForSupplier(Supplier supplier);

	@Transactional(readOnly = false)
	public void createLocationForSupplier(Long supplierId, Location supplierLocation, String locationType);
	
	@Transactional(readOnly = false)
	public void updateSupplierLocation(Location supplierLocation);

	@Transactional(readOnly = false)
	public void saveLocation(Location supplierLocation);
	
	public Supplier findById(Long id);
	
	public List<Supplier> findSuppliersWithNameLike(final String name,
			final int pageNumber, final int pageSize);
	
	public PageResult<Supplier> findAllSuppliers(ListCriteria listCriteria);
	
	public Supplier findSupplierByNameAndNumber(String supplierName, String supplierNumber);
}
