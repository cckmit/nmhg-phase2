package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class AdditionalLaborEligibilityServiceImpl extends 
          GenericServiceImpl<AdditionalLaborEligibility, Long, Exception>
           implements AdditionalLaborEligibilityService{

	
	
	AdditionalLaborEligibilityRepository additionalLaborRepo;
	
	public AdditionalLaborEligibilityRepository getAdditionalLaborRepo() {
		return additionalLaborRepo;
	}

	public void setAdditionalLaborRepo(AdditionalLaborEligibilityRepository additionalLaborRepo) {
		this.additionalLaborRepo = additionalLaborRepo;
	}

	public void updateAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility){
		additionalLaborRepo.updateAdditionalLaborEligibility(additionalLaborEligibility);
	}
 
	public void createAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility){
		additionalLaborRepo.createAdditionalLaborEligibility(additionalLaborEligibility);
	}
	
	public AdditionalLaborEligibility findAddditionalLabourEligibility() {
		return additionalLaborRepo.findAddditionalLabourEligibility();
	}
	
	

	@Override
	public GenericRepository<AdditionalLaborEligibility, Long> getRepository() {
		
		return null;
	}
	
}


