/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author mritunjay.kumar
 * 
 */
public class AddressBookRepositoryImpl extends
		GenericRepositoryImpl<AddressBook, Long> implements
		AddressBookRepository {
	public AddressBook getAddressBookByOrganizationAndType(
			final Organization organization, final AddressBookType type) {
		AddressBook addressBook = (AddressBook) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						session.disableFilter("excludeInactiveAddress");
						return session
								.createQuery(
										"select addressBook from AddressBook addressBook join addressBook.belongsTo organization where organization= :organization and addressBook.type = :type")
								.setParameter("organization", organization).setMaxResults(1)
								.setParameter("type", type).uniqueResult();
					};
				});
		return addressBook;
	}

	public void create(AddressBook addressBook) {
		getHibernateTemplate().save(addressBook);
	}
	
	public void update(AddressBook addressBook) {
        getHibernateTemplate().update(addressBook);
    }
}
