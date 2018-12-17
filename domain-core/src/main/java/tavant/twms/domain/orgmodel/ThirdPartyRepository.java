package tavant.twms.domain.orgmodel;

import tavant.twms.domain.thirdparty.ThirdPartySearch;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;


/**
 * @author Priyank.Gupta
 *
 */
public interface ThirdPartyRepository
{
	
	/**
	 * This method looks for a third party with a number or name provided to it. If none
	 * are provided it'll get whole lotta of them. It's a really cool cool API!
	 * 
	 * @param thirdPartySearch
	 * @param listCriteria
	 * @return
	 */
	PageResult<ThirdParty> findThirdPartyByNumberOrName(ThirdPartySearch thirdPartySearch, ListCriteria listCriteria);
	

	/**
	 * This method returns true or false based on the fact; whether current user belongs to a third party
	 * organization or not.
	 * 
	 * @param user
	 * @return
	 */
	boolean isThirdParty(User user);
}
