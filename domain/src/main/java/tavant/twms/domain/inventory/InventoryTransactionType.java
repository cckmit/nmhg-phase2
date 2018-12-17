package tavant.twms.domain.inventory;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class InventoryTransactionType implements AuditableColumns{

    @Id
	@GeneratedValue(generator = "InventoryTransactionType")
	@GenericGenerator(name = "InventoryTransactionType", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INV_TXN_TYPE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String trnxTypeKey;

    private String trnxTypeValue;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public InventoryTransactionType() {
    }

    public InventoryTransactionType(String trnxTypeKey) {
        this.trnxTypeKey = trnxTypeKey;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrnxTypeKey() {
        return this.trnxTypeKey;
    }

    public void setTrnxTypeKey(String trnxTypeKey) {
        this.trnxTypeKey = trnxTypeKey;
    }

    public String getTrnxTypeValue() {
        return this.trnxTypeValue;
    }

    public void setTrnxTypeValue(String trnxTypeValue) {
        this.trnxTypeValue = trnxTypeValue;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}


}
