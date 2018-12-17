package tavant.twms.domain.policy;

import java.util.TimeZone;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class WarrantyCoverageRequestServiceImpl extends
		GenericServiceImpl<WarrantyCoverageRequest, Long, Exception> implements
		WarrantyCoverageRequestService {

	private WarrantyCoverageRequestRepository warrantyCoverageRequestRepository;

	@Override
	public GenericRepository<WarrantyCoverageRequest, Long> getRepository() {
		return warrantyCoverageRequestRepository;
	}

	public void setWarrantyCoverageRequestRepository(
			WarrantyCoverageRequestRepository warrantyCoverageRequestRepository) {
		this.warrantyCoverageRequestRepository = warrantyCoverageRequestRepository;
	}

	public WarrantyCoverageRequest findByInventoryItemId(Long id) {
		return this.warrantyCoverageRequestRepository.findByInventoryItemId(id);
	}

	public PageResult<WarrantyCoverageRequest> findPageForAdminPendingRequests(
			ListCriteria listCriteria) {

		return warrantyCoverageRequestRepository
				.findPageForAdminPendingRequests(listCriteria);
	}

	public PageResult<WarrantyCoverageRequest> findPageForDealerRequests(
			ListCriteria listCriteria, User user, ServiceProvider dealership) {
		return warrantyCoverageRequestRepository.findPageForDealerRequests(
				listCriteria, user, dealership);
	}

	public boolean hasPoliciesWithReducedCoverage(Warranty warranty) {
		if (warranty != null) {
			for (RegisteredPolicy policy : warranty.getPolicies()) {
				if (policy.reductionInCoverage() != null) {
					return true;
				}
			}
		}
		return false;
	}

	public WarrantyCoverageRequest storeReducedCoverageInformationForInventory(InventoryItem item) {
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		WarrantyCoverageRequest wcr = new WarrantyCoverageRequest();
		wcr.setInventoryItem(item);
        wcr.setRequestedBy(item.getOwner());
        wcr.setUpdatedOnDate(Clock.today());
        wcr.setStatus(WarrantyCoverageRequestAudit.WAITING_FOR_YOUR_RESPONSE);
		WarrantyCoverageRequestAudit wcra = new WarrantyCoverageRequestAudit();
		wcra.setStatus(WarrantyCoverageRequestAudit.WAITING_FOR_YOUR_RESPONSE);
		wcra.setComments("INITIAL");
		wcra.getD().setCreatedOn(
				CalendarDate.from(Clock.now(), TimeZone.getDefault()));
		wcra.getD().setCreatedTime(Clock.now().asJavaUtilDate());
		wcra.setAssignedTo(item.getWarranty().getFiledBy());
		wcr.getAudits().add(wcra);
		save(wcr);
		return wcr;
	}

	public void save(WarrantyCoverageRequest wcr) {
		this.warrantyCoverageRequestRepository.save(wcr);
	}

	public int findExtensionCountForDealer(User user, ServiceProvider dealership) {
		return warrantyCoverageRequestRepository
				.findExtensionCountForDealer(user, dealership);
	}

	public Long findExtensionForCoverageRequestsCount() {		
		return warrantyCoverageRequestRepository.findExtensionForCoverageRequestsCount();
	}

}
