package tavant.twms.domain.orgmodel;


import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class MinimumLaborRoundUpServiceImpl extends 
                      GenericServiceImpl<MinimumLaborRoundUp, Long, Exception>
                      implements MinimumLaborRoundUpService{

	MinimumLaborRoundUpRepository minimumLaborRoundUpRepo;
	
	public MinimumLaborRoundUpRepository getMinimumLaborRoundUpRepo() {
		return minimumLaborRoundUpRepo;
	}

	public void setMinimumLaborRoundUpRepo(
			MinimumLaborRoundUpRepository minimumLaborRoundUpRepo) {
		this.minimumLaborRoundUpRepo = minimumLaborRoundUpRepo;
	}

	public void updateMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp){
		minimumLaborRoundUpRepo.updateMinimumLaborRoundUp(minimumLaborRoundUp);
	}
 
	public void createMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp){
		minimumLaborRoundUpRepo.createMinimumLaborRoundUp(minimumLaborRoundUp);
	}
	
	public MinimumLaborRoundUp findMinimumLaborRoundUp() {
		return minimumLaborRoundUpRepo.findMinimumLaborRoundUp();
	}
	
	

	@Override
	public GenericRepository<MinimumLaborRoundUp, Long> getRepository() {
		
		return null;
	}
    
  
	
	
}


