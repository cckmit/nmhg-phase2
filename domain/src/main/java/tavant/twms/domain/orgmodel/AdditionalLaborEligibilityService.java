package tavant.twms.domain.orgmodel;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;

@Transactional(readOnly = true)
public interface AdditionalLaborEligibilityService extends 
                  GenericService<AdditionalLaborEligibility, Long, Exception> {
   
	@Transactional(readOnly = false)
	void updateAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility);
 
	@Transactional(readOnly = false)
	void createAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility);
	
	AdditionalLaborEligibility findAddditionalLabourEligibility();	

}


