package tavant.twms.domain.reports;

import tavant.twms.infra.PageResult;

public interface VendorRecoveryService {
	
	public PageResult<VendorRecoveryExtract> findAllRecoveryClaimsForRange(
			VendorRecoveryListCriteria vendorRecoveryListCriteria);
	
	public Long findRecoveryClaimsCountForRange(
			VendorRecoveryListCriteria vendorRecoveryListCriteria);

}