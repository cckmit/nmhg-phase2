package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface SupplierRepository extends GenericRepository<Supplier, Long> {

	public Supplier findSupplierByNumber(String supplierNumber);
	public Supplier findSupplierByNumberWithOutActiveOrInactiveStaus(String supplierNumber);
	
	public Supplier findSupplierByNumberWithOutBU(String supplierNumber);

	public List<Supplier> findSupplierWithNameLike(String userEntry);

	public List<Supplier> findSuppliersWithNameLike(final String name,
			final int pageNumber, final int pageSize);

	public Supplier findSupplierByName(String supplierName);
	
	public Supplier findSupplierByNameAndNumber(String supplierName, String supplierNumber);

	public List<Supplier> findSuppliersForLabel(Label label);

	public PageResult<Location> findLocationsForSupplier(ListCriteria criteria,
			Supplier loggedInUserAsSupplier);

	public List<Location> findLocationsForSupplier(Supplier supplier);

	public Supplier findSupplierById(final Long id);
	 public Supplier findSupplierByIdWithOutActivateInactivate(final Long id);

	public PageResult<Supplier> findAllSuppliers(ListCriteria listCriteria);

}
