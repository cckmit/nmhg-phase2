package tavant.twms.domain.reports;

import tavant.twms.infra.PageResult;

public interface VendorRecoveryRepository {

	public PageResult<VendorRecoveryExtract> findAllRecoveryClaimsForRange(VendorRecoveryListCriteria vendorRecoveryListCriteria);
	
	public Long findRecoveryClaimsCountForRange(VendorRecoveryListCriteria vendorRecoveryListCriteria);

}