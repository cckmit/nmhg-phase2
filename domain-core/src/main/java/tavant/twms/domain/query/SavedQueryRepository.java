/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.query;

import java.util.List;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
/**
 * 
 * @author roopali.agrawal
 *
 */
public interface SavedQueryRepository extends GenericRepository<SavedQuery,Long>{
    public List<SavedQuery> findSavedQueriesByContextAndUser(String context,Long userId);

    public List<SavedQuery> findByName(String context);
    
    public boolean doesQueryWithNameExists(final String queryName) throws Exception; 




    public List<SavedQuery> findSavedQueriesByUserUsingContext(String context,Long userId);

    public boolean isQueryNameUniqueForUser(String queryName, User loggedInUser);

	public SavedQuery findByQueryName(String saveQueryName, User loggedInUser);

	public SavedQuery findByIdAndCreatedBy(Long savedQueryId, User loggedInUser);
	
	public void deleteQueryWithId(Long id);
	
	public boolean isQueryNameUniqueForUserAndContext(String searchQueryName,
			User loggedInUser, String context);
}
