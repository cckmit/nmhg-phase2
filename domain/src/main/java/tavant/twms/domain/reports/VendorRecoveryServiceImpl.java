package tavant.twms.domain.reports;

import tavant.twms.infra.PageResult;

public class VendorRecoveryServiceImpl implements VendorRecoveryService {

	private VendorRecoveryRepository vendorRecoveryRepository;
	
	public PageResult<VendorRecoveryExtract> findAllRecoveryClaimsForRange(
			VendorRecoveryListCriteria vendorRecoveryListCriteria) {
		return this.vendorRecoveryRepository.findAllRecoveryClaimsForRange(vendorRecoveryListCriteria);
	}
	
	public Long findRecoveryClaimsCountForRange(
			VendorRecoveryListCriteria vendorRecoveryListCriteria) {
		return this.vendorRecoveryRepository.findRecoveryClaimsCountForRange(vendorRecoveryListCriteria);
	}
	
	public void setVendorRecoveryRepository(
			VendorRecoveryRepository vendorRecoveryRepository) {
		this.vendorRecoveryRepository = vendorRecoveryRepository;
	}
}
