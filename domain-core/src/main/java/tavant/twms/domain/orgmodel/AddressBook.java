/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.sql.Types;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@SuppressWarnings("serial")
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "type",
		"belongs_to" }) })
public class AddressBook implements Serializable, AuditableColumns{
	@Id
	@GeneratedValue(generator = "AddressBook")
	@GenericGenerator(name = "AddressBook", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ADDRESS_BOOK_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.orgmodel.AddressBookType"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private AddressBookType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "belongs_to")
	private Organization belongsTo;

	@OneToMany(mappedBy = "addressBook")
	@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private List<AddressBookAddressMapping> addressBookAddressMapping;
	
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

	public AddressBookType getType() {
		return type;
	}

	public void setType(AddressBookType type) {
		this.type = type;
	}

	public Organization getBelongsTo() {
		return belongsTo;
	}

	public void setBelongsTo(Organization belongsTo) {
		this.belongsTo = belongsTo;
	}

	public List<AddressBookAddressMapping> getAddressBookAddressMapping() {
		return addressBookAddressMapping;
	}

	public void setAddressBookAddressMapping(
			List<AddressBookAddressMapping> addressBookAddressMapping) {
		this.addressBookAddressMapping = addressBookAddressMapping;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
