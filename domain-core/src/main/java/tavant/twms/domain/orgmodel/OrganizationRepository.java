package tavant.twms.domain.orgmodel;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface OrganizationRepository extends GenericRepository<Party, Long> {
	public Organization findByName(String name);

	public Organization findOrgById(Long id);
	
	public PageResult<Party> findAllSuppliers(ListCriteria listCriteria);

	public void updateOrganization(Organization organization);
	
	public List<OrganizationAddress> getAddressesForOrganization(
			Organization organization);
	
	public OrganizationAddress getPrimaryOrganizationAddressForOrganization(
			Organization organization);
	
	/**
	 * 
	 * @param organizationName
	 * @return
	 */
	Organization findByOrganizationName(String organizationName);

	public PageResult<OrganizationAddress> getAddressesForOrganization(ListCriteria criteria, Organization organization);

	public List<Currency> listUniqueCurrencies();
	
	public void updateOrganizationAddress(OrganizationAddress orgAddress) ;
	public void updateAddress(Address address) ;
	public OrganizationAddress getAddressesForOrganizationBySiteNumber(
				final Organization organization, final String siteNumber);
	public OrganizationAddress getOrganizationAddressBySiteNumber(final String siteNumber);
    public OrganizationAddress getOrgAddressBySiteNumberForUpload(final String siteNumber, final Long orgId); 
	public void removeAddressesForOrganization(final String siteNumber);

    public List<Organization> getChildOrganizations(final Long orgId);
    public List<Long> getChildOrganizationIds(final Long orgId);
    public List<Long> getParentOrganizationIds(final Long orgId);

    public Address findAddressWithMandatoryFields(String addressLine1,String country, String city, String state, String zipCode);

    public List<User> getDealersFromDealerShip(List<Long> dealerShips);

    public List<User> getCustomers(List<Long> customerIds);

    public Long checkLoggedInDealerForDualBrand(Long id);
    
    public PageResult<Party> findDealersByOrganizations(ListCriteria listCriteria , List<Organization> organizations);
}
