package tavant.twms.domain.query;


import java.util.List;
import tavant.twms.domain.orgmodel.User;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;
@Transactional(readOnly = true)
public interface SavedQueryService extends GenericService<SavedQuery, Long, Exception> {

	@Transactional(readOnly = false)
	public void saveSearchQuery(SavedQuery savedQuery) throws Exception;

	public List<SavedQuery> findByName(String context);

	public boolean doesQueryWithNameExists(final String queryName) throws Exception;

	@Transactional(readOnly = false)
	public void updateSearchQuery(SavedQuery savedQuery) throws Exception;

	public boolean isQueryNameUniqueForUser(String queryName);

	public SavedQuery findByQueryName(String saveQueryName, User loggedInUser);
	
	public void deleteQueryWithId(Long id);
	
	public boolean isQueryNameUniqueForUserAndContext(String queryName,String context);

}