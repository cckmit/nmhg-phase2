package tavant.twms.domain.policy;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;


public interface WarrantyCoverageRequestRepository extends tavant.twms.infra.GenericRepository<WarrantyCoverageRequest,Long> {

	public WarrantyCoverageRequest findByInventoryItemId(Long id);
	
	public PageResult<WarrantyCoverageRequest> findPageForAdminPendingRequests(
			ListCriteria listCriteria) ;

	public PageResult<WarrantyCoverageRequest> findPageForDealerRequests(
			ListCriteria listCriteria, User user, ServiceProvider dealership) ;
	
	public void save(WarrantyCoverageRequest entity);

	public int findExtensionCountForDealer(User user, ServiceProvider dealership);
	
	public Long findExtensionForCoverageRequestsCount();
	
	
}
