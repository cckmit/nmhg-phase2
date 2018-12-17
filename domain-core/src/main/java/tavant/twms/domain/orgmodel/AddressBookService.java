/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author mritunjay.kumar
 * 
 */
public interface AddressBookService {

	@Transactional(readOnly = false)
	public void createAddressBook(AddressBook addressBook);

	public AddressBook getAddressBookByOrganizationAndType(
			Organization organization, AddressBookType type);

	@Transactional(readOnly = false)
	public void createAddressBookAddressMapping(
			AddressBookAddressMapping addressBookAddressMapping);

	@Transactional(readOnly = false)
	public void updateAddressBookAddressMapping(
			AddressBookAddressMapping addressBookAddressMapping);

	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddress(
			Address address, Organization organization);
	
	public List<AddressBookAddressMapping> getAddressBookAddressMappingByListOfAddresses(
			List<Address> addresses);
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressAndBookType(
			Address address, Organization organization, AddressBookType type);
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressBookAndBookType(
			Address address, AddressBook addressBook, Organization organization, AddressBookType type);
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndType(
			Organization organization, AddressType type);

	public List<AddressBookAddressMapping> getAddressBookAddressMappingByOrganizationAndListOfAddresses(
			List<Address> addresses, Organization organization);

     public AddressBookAddressMapping getAddressBookAddressMappingByAddress(Address address);
    
     @Transactional(readOnly = false)
     public void delete(AddressBookAddressMapping addressBookAddressMapping);
     
     @Transactional(readOnly = false)
     public void deleteAll(List<AddressBookAddressMapping> addressBookAddressMappingList);
     
     @Transactional(readOnly = false)
     public void update(AddressBook addressBook);
     
     @Transactional(readOnly = false)
 	public void createAddressBookAddressMappingWithOutActiveFilter(
			AddressBookAddressMapping addressBookAddressMapping);
     @Transactional(readOnly = false)
	public void updateAddressBookAddressMappingWithOutActiveFilter(
			AddressBookAddressMapping addressBookAddressMapping);

     }
