/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.infra.GenericRepository;

/**
 * @author mritunjay.kumar
 * 
 */
public interface AddressBookAddressMappingRepository extends
		GenericRepository<AddressBookAddressMapping, Long> {

	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddress(
			Address address, Organization organization);
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressAndBookType(
			Address address, Organization organization, AddressBookType type);
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressBookAndBookType(
			Address address, AddressBook addressBook, Organization organization, AddressBookType type);
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndType(
			Organization organization, AddressType type);

	public void create(AddressBookAddressMapping addressBookAddressMapping);

	public void update(AddressBookAddressMapping addressBookAddressMapping);

	public List<AddressBookAddressMapping> getAddressBookAddressMappingByOrganizationAndListOfAddresses(
			List<Address> addresses, Organization organization);
	
	public List<AddressBookAddressMapping> getAddressBookAddressMappingByListOfAddresses(
			List<Address> addresses);

    public AddressBookAddressMapping getAddressBookAddressMappingByAddress(Address address);
         
    public void delete(AddressBookAddressMapping addressBookAddressMapping);
    
    public void deleteAll(List<AddressBookAddressMapping> addressBookAddressMappingList);

	public void createAddress(
			AddressBookAddressMapping addressBookAddressMapping);

	public void updateAddress(
			AddressBookAddressMapping addressBookAddressMapping);
    
    
}
