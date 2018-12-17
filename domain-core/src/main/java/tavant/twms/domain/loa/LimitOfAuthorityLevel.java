package tavant.twms.domain.loa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.validation.constraints.NotNull;

import tavant.twms.common.Views;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class LimitOfAuthorityLevel implements Comparable<LimitOfAuthorityLevel>,AuditableColumns {

	@Id
	@GeneratedValue(generator = "LimitOfAuthorityLevel")
	@GenericGenerator(name = "LimitOfAuthorityLevel", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "LOA_LEVEL_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
    @JsonIgnore
    private int version;

	@NotNull
	private String name;

	private Integer loaLevel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonView(value=Views.Public.class)
	private User loaUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonView(value=Views.Public.class)
	private LimitOfAuthorityScheme loaScheme;
	
	 @Cascade( { org.hibernate.annotations.CascadeType.ALL,
	 org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
     @CollectionOfElements
     @JoinTable(name = "LOA_AMOUNT",joinColumns = @JoinColumn(name="loa_level"))
     @Type(type = "tavant.twms.infra.MoneyUserType")
     @AttributeOverrides({
          @AttributeOverride(name = "element.amount", column = @Column(name = "amount")),
          @AttributeOverride(name = "element.currency", column = @Column(name = "currency"))})
	private List<Money> approvalLimits = new ArrayList<Money>();
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public LimitOfAuthorityLevel() {
		super();
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLoaLevel() {
		return loaLevel;
	}

	public void setLoaLevel(Integer loaLevel) {
		this.loaLevel = loaLevel;
	}

	public User getLoaUser() {
		return loaUser;
	}

	public void setLoaUser(User loaUser) {
		this.loaUser = loaUser;
	}

	public LimitOfAuthorityScheme getLoaScheme() {
		return loaScheme;
	}

	public void setLoaScheme(LimitOfAuthorityScheme loaScheme) {
		this.loaScheme = loaScheme;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<Money> getApprovalLimits() {
		return approvalLimits;
	}

	public void setApprovalLimits(List<Money> approvalLimits) {
		this.approvalLimits = approvalLimits;
	}

	@JsonIgnore
	public int compareTo(LimitOfAuthorityLevel other) {
		if (other == null) {
			return 1;
		}
		return this.loaLevel.compareTo(other.loaLevel);
		
	}

}
