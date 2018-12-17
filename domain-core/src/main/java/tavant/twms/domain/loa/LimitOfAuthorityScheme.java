package tavant.twms.domain.loa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.common.Views;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * @author bharath.kumar
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@SuppressWarnings("serial")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class LimitOfAuthorityScheme implements BusinessUnitAware,AuditableColumns{

    @Id
    @GeneratedValue(generator = "LimitOfAuthorityScheme")
	@GenericGenerator(name = "LimitOfAuthorityScheme", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "LOA_SCHEME_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    @JsonIgnore
    private int version;

    @NotNull
    private String name;
    
    @NotNull
    private String code;
    
    private String description;

    private String type;

	@JsonView(value=Views.Public.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy="loaScheme")
	@Cascade( {org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN} )
	@Filter(name="excludeInactive")		
    private List<LimitOfAuthorityLevel> loaLevels = new ArrayList<LimitOfAuthorityLevel>();
    
    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public LimitOfAuthorityScheme() {
        super();
    }

    public LimitOfAuthorityScheme(String name,String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LimitOfAuthorityLevel> getLoaLevels() {
		return loaLevels;
	}

	public void setLoaLevels(List<LimitOfAuthorityLevel> loaLevels) {
		this.loaLevels.clear();
		for (LimitOfAuthorityLevel limitOfAuthorityLevel : loaLevels) {
			limitOfAuthorityLevel.setLoaScheme(this);
		}
		this.loaLevels.addAll(loaLevels);
	}
	

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}


	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonIgnore
	public List<String> getEligibleLOAProcessorList() {
		List<String> eligibleLOAProcessors = new ArrayList<String>();
		for (LimitOfAuthorityLevel limitOfAuthorityLevel : loaLevels) {
			eligibleLOAProcessors.add(limitOfAuthorityLevel.getLoaUser().getName());
		}
		return eligibleLOAProcessors;
	}
	
	@JsonIgnore
	public boolean isClaimAmountGTApprovalLimit(Money payment, String loginUser) {
		boolean isClaimAmountGTApprovalLimit = false;
		approvalLimit: for (LimitOfAuthorityLevel limitOfAuthorityLevel : getLoaLevels()) {
			if (limitOfAuthorityLevel.getLoaUser().getName().equalsIgnoreCase(loginUser)) {
				for (Money approvalLimit : limitOfAuthorityLevel.getApprovalLimits()) {
					if (approvalLimit.breachEncapsulationOfCurrency().getCurrencyCode().equalsIgnoreCase(payment.breachEncapsulationOfCurrency().getCurrencyCode())) {
						if (approvalLimit.breachEncapsulationOfAmount().doubleValue() < payment
								.breachEncapsulationOfAmount().doubleValue()) {
							isClaimAmountGTApprovalLimit = true;
							break approvalLimit;
						}
					}
				}
			}
		}
		return isClaimAmountGTApprovalLimit;
	}
	
}

