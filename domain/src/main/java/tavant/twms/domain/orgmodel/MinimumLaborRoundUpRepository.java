package tavant.twms.domain.orgmodel;



import tavant.twms.infra.GenericRepository;

public interface MinimumLaborRoundUpRepository extends
                     GenericRepository<MinimumLaborRoundUp, Long> 
                                       {

    
	
	public  void updateMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp);
     
	public	void createMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp);
    	
    MinimumLaborRoundUp findMinimumLaborRoundUp();
	
}
 