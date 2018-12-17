/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author mritunjay.kumar
 * 
 */
public class AddressBookAddressMappingRepositoryImpl extends
		GenericRepositoryImpl<AddressBookAddressMapping, Long> implements
		AddressBookAddressMappingRepository {

	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddress(
			Address address, Organization organization) {
		String query = "select abam from AddressBookAddressMapping abam join abam.addressBook "
				+ " addressBook join abam.address address where "
				+ " address = :address and addressBook.belongsTo = :organization ";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("organization", organization);
		params.put("address", address);
		List<AddressBookAddressMapping> addressBookAddressMappings = findUsingQuery(query, params);
		if(addressBookAddressMappings!=null){
			if(addressBookAddressMappings.size()<1)
				return null;
			else
				return addressBookAddressMappings.get(0);
		}
		else
			return null;
	}
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressAndBookType(
			Address address, Organization organization, AddressBookType type){
		String query = "select abam from AddressBookAddressMapping abam join abam.addressBook "
			+ " addressBook join abam.address address where "
			+ " address = :address and addressBook.belongsTo = :organization and addressBook.type = :type ";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("address", address);
		params.put("organization", organization);
		params.put("type", type);
		return findUniqueUsingQuery(query, params);
	}
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndAddressBookAndBookType(Address address,
			AddressBook addressBook, Organization organization, AddressBookType type){
		String query = "select abam from AddressBookAddressMapping abam join abam.addressBook "
			+ " addressBook join abam.address address where  address = :address  and "
			+ " addressBook = :addressBook and addressBook.belongsTo = :organization and addressBook.type = :type and abam.primary=true";
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("address", address);
		params.put("addressBook", addressBook);
		params.put("organization", organization);
		params.put("type", type);
		return findUniqueUsingQuery(query, params);
	}
	
	public AddressBookAddressMapping getAddressBookAddressMappingByOrganizationAndType(
			Organization organization, final AddressType type){
			String query = "select abam from AddressBookAddressMapping abam join abam.addressBook "
				+ " addressBook where "
				+ " abam.type = :type and addressBook.belongsTo = :organization ";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("organization", organization);
		params.put("type", type);
		List<AddressBookAddressMapping> addressBookAddressMappings = findUsingQuery(query, params);
		if(addressBookAddressMappings!=null){
			if(addressBookAddressMappings.size()<1)
				return null;
			else
				return addressBookAddressMappings.get(0);
		}
		else
			return null;
	}

	public void create(AddressBookAddressMapping addressBookAddressMapping) {
		getHibernateTemplate().save(addressBookAddressMapping);
	}

	@Override
	public void update(AddressBookAddressMapping addressBookAddressMapping) {
		getHibernateTemplate().update(addressBookAddressMapping);
	}

	public List<AddressBookAddressMapping> getAddressBookAddressMappingByOrganizationAndListOfAddresses(
			List<Address> addresses, Organization organization) {
		String query = "select abam from AddressBookAddressMapping abam join abam.addressBook "
				+ " addressBook join abam.address address where "
				+ " address in (:addresses) and addressBook.belongsTo = :organization ";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("addresses", addresses);
		params.put("organization", organization);
		return findUsingQuery(query, params);
	}
	
	public List<AddressBookAddressMapping> getAddressBookAddressMappingByListOfAddresses(
			List<Address> addresses) {
		String query = "select abam from AddressBookAddressMapping abam join abam.addressBook "
				+ " addressBook join abam.address address where "
				+ " address in (:addresses)";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("addresses", addresses);
		return findUsingQuery(query, params);
	}

    public AddressBookAddressMapping getAddressBookAddressMappingByAddress(
			Address address) {
		String query = "select abam from AddressBookAddressMapping abam join abam.addressBook "
				+ " addressBook join abam.address address where "
				+ " address = :address ";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("address", address);
		List<AddressBookAddressMapping> addressMappingList = findUsingQuery(
				query, params);
		if (addressMappingList == null) {
			return null;
		} else {
			if (addressMappingList.size() > 1) {
				throw new RuntimeException(
						"The customer is already associated with another dealer.");
			}
			if (addressMappingList.size() == 0) {
				return null;
			} else {
				return addressMappingList.get(0);
			}
		}
	}

	@Override
	public void delete(AddressBookAddressMapping addressBookAddressMapping) {
		getHibernateTemplate().delete(addressBookAddressMapping);
	}

	@Override
	public void deleteAll(List<AddressBookAddressMapping> addressBookAddressMappingList) {
		getHibernateTemplate().deleteAll(addressBookAddressMappingList);
	}
	
	public void createAddress(
			AddressBookAddressMapping addressBookAddressMapping) {
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactiveAddress");
		getHibernateTemplate().save(addressBookAddressMapping);
		getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("excludeInactiveAddress");
		
	}

	public void updateAddress(
			AddressBookAddressMapping addressBookAddressMapping) {
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactiveAddress");
		getHibernateTemplate().update(addressBookAddressMapping);
		getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactiveAddress");
	}
    
    

}
