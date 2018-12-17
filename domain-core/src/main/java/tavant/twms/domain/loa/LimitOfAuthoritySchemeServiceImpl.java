package tavant.twms.domain.loa;

import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class LimitOfAuthoritySchemeServiceImpl extends GenericServiceImpl<LimitOfAuthorityScheme, Long, Exception> implements LimitOfAuthoritySchemeService {
    private LimitOfAuthoritySchemeRepository limitOfAuthoritySchemeRepository;
    
    @Override
    public GenericRepository<LimitOfAuthorityScheme, Long> getRepository() {
        return this.limitOfAuthoritySchemeRepository;
    }
   
	public void setLimitOfAuthoritySchemeRepository(
			LimitOfAuthoritySchemeRepository limitOfAuthoritySchemeRepository) {
		this.limitOfAuthoritySchemeRepository = limitOfAuthoritySchemeRepository;
	}
	
	public LimitOfAuthorityScheme findByName(String name){
		
		return limitOfAuthoritySchemeRepository.findByName(name);
	}
	
	public List<LimitOfAuthorityScheme> findLOASchemesForUser(final String loaUser){
		return limitOfAuthoritySchemeRepository.findLOASchemesForUser(loaUser);
	}

    public List<LimitOfAuthorityScheme> findLOASchemesByType(final String type){
        return limitOfAuthoritySchemeRepository.findLOASchemesByType(type);
    }

    public PageResult<LimitOfAuthorityScheme> findAllLOASchemes(ListCriteria listCriteria){
		
		return limitOfAuthoritySchemeRepository.findAllLOASchemes(listCriteria);
	}

	public void deleteLOAScheme(LimitOfAuthorityScheme limitOfAuthorityScheme) {
		this.limitOfAuthoritySchemeRepository.deleteLOAScheme(limitOfAuthorityScheme);
	
	}
}
