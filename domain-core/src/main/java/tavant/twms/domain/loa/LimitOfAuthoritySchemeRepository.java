package tavant.twms.domain.loa;

import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface LimitOfAuthoritySchemeRepository extends GenericRepository<LimitOfAuthorityScheme, Long> {
	
	public LimitOfAuthorityScheme findByName(String name);
	public List findLOASchemesForUser(final String loaUser);

    public List findLOASchemesByType(final String type);

    public PageResult<LimitOfAuthorityScheme> findAllLOASchemes(ListCriteria listCriteria);
	
	public void deleteLOAScheme(LimitOfAuthorityScheme limitOfAuthorityScheme);

}
