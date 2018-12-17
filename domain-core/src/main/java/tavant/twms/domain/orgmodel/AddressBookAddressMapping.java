/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author mritunjay.kumar
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@SuppressWarnings("serial")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {
		"address_book_id", "address_id" }) })
public class AddressBookAddressMapping implements Serializable, AuditableColumns{

	@Id
	@GeneratedValue(generator = "AddressBookAddMap")
	@GenericGenerator(name = "AddressBookAddMap", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ADDRESSBOOK_ADDMAP_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_book_id")
	private AddressBook addressBook;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;

	Boolean privilege;

	@Column(name = "is_primary")
	Boolean primary;
	
	@Column(name = "is_end_customer")
	Boolean endCustomer;

	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.orgmodel.AddressType"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private AddressType type;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public AddressBook getAddressBook() {
		return addressBook;
	}

	public void setAddressBook(AddressBook addressBook) {
		this.addressBook = addressBook;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Boolean getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Boolean privilege) {
		this.privilege = privilege;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	public Boolean getEndCustomer() {
		return endCustomer;
	}

	public void setEndCustomer(Boolean endCustomer) {
		this.endCustomer = endCustomer;
	}

	public AddressType getType() {
		return type;
	}

	public void setType(AddressType type) {
		this.type = type;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
