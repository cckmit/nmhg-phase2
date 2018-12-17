package tavant.twms.domain.orgmodel;

import java.util.List;

public class AddressBookServiceImpl implements AddressBookService {

	private AddressBookRepository addressBookRepository;

	private AddressBookAddressMappingRepository addressBookAddressMappingRepository;

	public void setAddressBookRepository(
			AddressBookRepository addressBookRepository) {
		this.addressBookRepository = addressBookRepository;
	}

	public void setAddressBookAddressMappingRepository(
			AddressBookAddressMappingRepository addressBookAddressMappingRepository) {
		this.addressBookAddressMappingRepository = addressBookAddressMappingRepository;
	}

	public void createAddressBook(AddressBook addressBook) {
		addressBookRepository.create(addressBook);
	}

	public AddressBook getAddressBookByOrganizationAndType(
			Organization organization, AddressBookType type) {
		return addressBookRepository.getAddressBookByOrganizationAndType(
				organization, type);
	}

	public void createAddressBookAddressMapping(
			AddressBookAddressMapping addressBookAddressMapping) {
		addressBookAddressMappingRepository.create(addressBookAddressMapping);
	}

	public void updateAddressBookAddressMapping(
			AddressBookAddressMapping addressBookAddressMapping) {
		addressBookAddressMappingRepository.update(addressBookAddressMapping);
	}

	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddress(
			Address address, Organization organization) {
		return addressBookAddressMappingRepository
				.getAddressBookAddressMappingByOrganizationAndAddress(address,
						organization);
	}
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressAndBookType(
			Address address, Organization organization, AddressBookType type){
		return addressBookAddressMappingRepository
		.getAddressBookAddressMappingByOrganizationAndAddressAndBookType(address,
				organization, type);
	}
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressBookAndBookType(Address address,
			AddressBook addressBook, Organization organization, AddressBookType type){
		return addressBookAddressMappingRepository
		.getAddressBookAddressMappingByOrganizationAndAddressBookAndBookType(address,addressBook,
				organization, type);
	}
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndType(
			Organization organization, AddressType type){
		return addressBookAddressMappingRepository
		.getAddressBookAddressMappingByOrganizationAndType(organization,
				type);
	}

	public List<AddressBookAddressMapping> getAddressBookAddressMappingByOrganizationAndListOfAddresses(
			List<Address> addresses, Organization organization) {
		return addressBookAddressMappingRepository
				.getAddressBookAddressMappingByOrganizationAndListOfAddresses(
						addresses, organization);
	}
	
	public List<AddressBookAddressMapping> getAddressBookAddressMappingByListOfAddresses(
			List<Address> addresses) {
		return addressBookAddressMappingRepository
				.getAddressBookAddressMappingByListOfAddresses(
						addresses);
	}

    public AddressBookAddressMapping getAddressBookAddressMappingByAddress(Address address) {
		return addressBookAddressMappingRepository
				.getAddressBookAddressMappingByAddress(address);
	}

	public void delete(AddressBookAddressMapping addressBookAddressMapping) {
		addressBookAddressMappingRepository.delete(addressBookAddressMapping);
	}

	public void deleteAll(List<AddressBookAddressMapping> addressBookAddressMappingList) {
		addressBookAddressMappingRepository.deleteAll(addressBookAddressMappingList);
	}

	public void update(AddressBook addressBook) {
		addressBookRepository.update(addressBook);
	}
	public void createAddressBookAddressMappingWithOutActiveFilter(
			AddressBookAddressMapping addressBookAddressMapping) {
		addressBookAddressMappingRepository.createAddress(addressBookAddressMapping);
		
	}

	public void updateAddressBookAddressMappingWithOutActiveFilter(
			AddressBookAddressMapping addressBookAddressMapping) {
		addressBookAddressMappingRepository.updateAddress(addressBookAddressMapping);
		
	}
    
}
