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
package tavant.twms.domain.orgmodel;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author radhakrishnan.j
 *
 */
public interface DealershipRepository {
	/**
	 * Persists a new Dealership to the repository.
	 *
	 * @param newDealership
	 */
	public void createDealership(ServiceProvider newDealership);

	/**
	 * Persists the changes made to a particular dealer.
	 *
	 * @param dealer
	 */
	public void updateDealership(ServiceProvider dealer);
	
	/**
	 * Persists the changes made to a shipment Address
	 *
	 * @param dealer
	 */	
	public void updateShipmentAddress(Address shipmentAddress);

    public void createShipmentAddress(Address shipmentAddress);

	/**
	 *
	 * @param id
	 * @return
	 */
	ServiceProvider findByDealerId(Long id);

	/**
	 *
	 * @param dealerName
	 * @return
	 */
	ServiceProvider findByDealerName(String dealerName);
	
	
	ServiceProvider findCertifiedDealerByNumber(String dealerNumber);

	/**
	 *
	 * @param dealerNumber
	 * @return
	 */
	ServiceProvider findByDealerNumber(String dealerNumber);

	List<String> findDealerNamesStartingWith(String dealerName, int pageNumber,
			int pageSize);

	List<ServiceProvider> findDealersWhoseNameStartsWith(String dealerName,
			int pageNumber, int pageSize);

	List<ServiceProvider> findAllDealers(String name);

	List<ServiceProvider> findAllBUDealers(String businessUnitName);

	List<Supplier> findAllSuppliers(String name);

	boolean isDealer(User user);

	public List<ServiceProvider> findDealersByNumberOrName(
			final String dealerNumber, final String dealerName);

	public List<ServiceProvider> findDealersWhoseNumberStartingWith(
			String dealerNumber, int pageNumber, int pageSize);

	List<String> findDealerNumbersStartingWith(String dealerNumber,
			int pageNumber, int pageSize);


	public List<ServiceProvider> findDealersByFamily(String dealerFamilyCode);

	public List<ServiceProvider> findAllOtherDealersByBUName(String businessUnitName, String dealerFamilyCode);

	public ServiceProvider findDealersByNumberWithoutLike(final String dealerNumber);

	public List<ServiceProvider> findCertifiedDealersWhoseNameStartsWith(
			String dealerName, int pageNumber, int pageSize);	
	
	public String findDealerBrands(Organization organization);

    public ServiceProvider findDealerByServiceProviderID(String dealerId);
    
    public String findMarketingGroupCodeBrandByDealership(Dealership dealer);
    
    public List<ServiceProvider> findByDealerListByNumber(final String dealerNumber) ;

	public List<BigDecimal> findServiceProviderIds(String dealerNumber,
			String businessUnit);
}
