package tavant.twms.domain.loa;

import java.util.List;

import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface LimitOfAuthoritySchemeService extends GenericService<LimitOfAuthorityScheme, Long, Exception>{
    
	public LimitOfAuthorityScheme findByName(String name);
	public List<LimitOfAuthorityScheme> findLOASchemesForUser(final String loaUser);

    public List<LimitOfAuthorityScheme> findLOASchemesByType(final String type);

	public PageResult<LimitOfAuthorityScheme> findAllLOASchemes(ListCriteria listCriteria);
	
	public void deleteLOAScheme(LimitOfAuthorityScheme limitOfAuthorityScheme);
       
}
