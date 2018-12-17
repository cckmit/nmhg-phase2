/**
 * 
 */
package tavant.twms.domain.orgmodel;

import tavant.twms.infra.GenericRepository;

/**
 * @author mritunjay.kumar
 * 
 */
public interface AddressBookRepository extends
		GenericRepository<AddressBook, Long> {

	public AddressBook getAddressBookByOrganizationAndType(
			final Organization organization, final AddressBookType type);

	public void create(AddressBook addressBook);
	
	public void update(AddressBook addressBook);

}
