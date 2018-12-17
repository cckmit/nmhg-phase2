package tavant.twms.domain.orgmodel;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import java.util.List;

public interface ServiceProviderRepository  extends GenericRepository<ServiceProvider, Long> {

	/**
	 * @param dealerName
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	List<String> findServiceProviderNamesStartingWith(String dealerName, int pageNumber, int pageSize);
	
	/**
	 * @param serviceProviderName
	 * @return
	 */
	public ServiceProvider findServiceProviderByName(String serviceProviderName);
	
	/**
	 * @param serviceProviderNumber
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<String> findServiceProviderNumbersStartingWith(String serviceProviderNumber, int pageNumber, final int pageSize);
	
	/**
	 * @param serviceProviderNumber
	 * @return
	 */
	public ServiceProvider findServiceProviderByNumber(String serviceProviderNumber);
	
	/**
	 * @param id
	 * @return
	 */
	public ServiceProvider findServiceProviderById(Long id);
	
	public List<ServiceProvider> findServiceProviderByNumberWithOutBU(final String serviceProviderNumber);
	
	public List<ServiceProvider> findNationalAccountsWhoseNameStartsWith(final String nationalAccountName, final int pageNumber,final int pageSize);
	
	public List<ServiceProvider> findNationalAccountsWhoseNumberStartsWith(	final String nationalAccountNumber, final int pageNumber,final int pageSize);
	
	public PageResult<ServiceProvider> findAllNationalAccounts(String nationalAccountName, ListCriteria listCriteria);
	
	public Dealership findDealerDetailsByNumber(final String dealerNumber);

    public ServiceProvider findServiceProviderByNumberAndBusinessUnit(String serviceProviderNumber, String businessUnit);
	
}
