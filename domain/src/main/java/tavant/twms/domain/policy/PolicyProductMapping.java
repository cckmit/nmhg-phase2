package tavant.twms.domain.policy;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import com.domainlanguage.money.Money;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name = "POLICY_PRODUCT_MAPPING")
public class PolicyProductMapping implements AuditableColumns,Comparable<PolicyProductMapping> {
	
	@Id
	@GeneratedValue(generator = "Policy_Prod_Mapping")
	@GenericGenerator(name = "Policy_Prod_Mapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_PROD_MAPPING_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = {@Column(name = "deductible"), @Column(name = "deductible_curr")})
	private Money deductibleFee;
	
	@Version
	private int version;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private ItemGroup product = new ItemGroup();
	
	
	public ItemGroup getProduct() {
		return product;
	}

	public void setProduct(ItemGroup product) {
		this.product = product;
	}

	public PolicyProductMapping(){
    	
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Money getDeductibleFee() {
		return deductibleFee;
	}

	public void setDeductibleFee(Money deductibleFee) {
		this.deductibleFee = deductibleFee;
	}

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
		
	}
	
	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
    public int compareTo(PolicyProductMapping policyProductMapping) {
        if (this.getProduct().getName() != null) {
            return this.getProduct().getName().compareTo(policyProductMapping.getProduct().getName());
        } else {
            return -1; // anything other than 0 is fine
        }
    }
}
