package tavant.twms.domain.orgmodel;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;

@Transactional(readOnly = true)
public interface MinimumLaborRoundUpService extends 
                  GenericService<MinimumLaborRoundUp, Long, Exception> {
   
	@Transactional(readOnly = false)
	void updateMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp);
 
	@Transactional(readOnly = false)
	void createMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp);
	
	MinimumLaborRoundUp findMinimumLaborRoundUp();

}


