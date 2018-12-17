package tavant.twms.domain.orgmodel;


import java.util.List;

import tavant.twms.infra.GenericRepository;

public interface AdditionalLaborEligibilityRepository extends
                     GenericRepository<AdditionalLaborEligibility, Long> 
                                       {

	public void updateAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility);
	 
	public void createAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility);
	
	AdditionalLaborEligibility findAddditionalLabourEligibility();	
	
}
 