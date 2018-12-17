package tavant.twms.domain.policy;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly=true)
public interface WarrantyCoverageRequestService extends
		GenericService<WarrantyCoverageRequest, Long, Exception> {

	public WarrantyCoverageRequest findByInventoryItemId(Long id);

	public PageResult<WarrantyCoverageRequest> findPageForAdminPendingRequests(
			ListCriteria listCriteria);

	public PageResult<WarrantyCoverageRequest> findPageForDealerRequests(
			ListCriteria listCriteria, User user, ServiceProvider dealership);

	public boolean hasPoliciesWithReducedCoverage(Warranty warranty);

	@Transactional(readOnly = false)
	public WarrantyCoverageRequest storeReducedCoverageInformationForInventory(InventoryItem item);

	@Transactional(readOnly = false)
	public void save(WarrantyCoverageRequest entity);

	@Transactional(readOnly = true)
	public int findExtensionCountForDealer(User user, ServiceProvider dealership);
	
	public Long findExtensionForCoverageRequestsCount();
}
