package tavant.twms.domain.query;

import java.util.List;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.security.authz.infra.SecurityHelper;

public class SavedQueryServiceImpl extends
		GenericServiceImpl<SavedQuery, Long, Exception> implements
		SavedQueryService {

	private SavedQueryRepository savedQueryRepository;

	public void saveSearchQuery(SavedQuery savedQuery) throws Exception {
		savedQuery.setCreatedBy(new SecurityHelper().getLoggedInUser());
		save(savedQuery);
	}

	public boolean doesQueryWithNameExists(final String queryName) throws Exception {

		return savedQueryRepository.doesQueryWithNameExists(queryName);

	}

	public GenericRepository<SavedQuery, Long> getRepository() {
		return savedQueryRepository;
	}

	public void setSavedQueryRepository(
			SavedQueryRepository savedQueryRepository) {
		this.savedQueryRepository = savedQueryRepository;
	}

	public void updateSearchQuery(SavedQuery savedQuery) throws Exception {
		update(savedQuery);
	}

	public boolean isQueryNameUniqueForUser(String queryName){
		User loggedInUser = new SecurityHelper().getLoggedInUser();
		return savedQueryRepository.isQueryNameUniqueForUser(queryName, loggedInUser);
	}
	
	public boolean isQueryNameUniqueForUserAndContext(String queryName,String context){
		User loggedInUser = new SecurityHelper().getLoggedInUser();
		return savedQueryRepository.isQueryNameUniqueForUserAndContext(queryName, loggedInUser,context);
	}

	public SavedQuery findByQueryName(String saveQueryName, User loggedInUser)
	{
		return savedQueryRepository.findByQueryName(saveQueryName, loggedInUser);
	}

	public List<SavedQuery> findByName(String context) {
		return savedQueryRepository.findByName(context);
	}

	public void deleteQueryWithId(Long id) {
		savedQueryRepository.deleteQueryWithId(id);
	}

}
