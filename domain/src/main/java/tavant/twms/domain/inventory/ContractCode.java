package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ContractCode implements BusinessUnitAware, AuditableColumns{
    
	@Id
	@GeneratedValue(generator = "ContractCode")
	@GenericGenerator(name = "ContractCode", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CONTRACT_CODE_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;
	

	 @OneToMany(fetch = FetchType.EAGER)
	 @Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	 @JoinColumn(name = "I18N_CONTRACT_CODE", nullable = false)
	 @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	 private List<I18NContractCodeText> contractCodeTexts = new ArrayList<I18NContractCodeText>();

    
	@Column(name="CONTRACT_CODE")
	private String contractCode;

	public String getContractCode() {
		return contractCode;
	}

	public List<I18NContractCodeText> getContractCodeTexts() {
		return contractCodeTexts;
	}

	public void setContractCodeTexts(List<I18NContractCodeText> contractCodeTexts) {
		this.contractCodeTexts = contractCodeTexts;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	@Version
    private int version;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDisplayContractCode() {
    	String i18ntype = "";
		for (I18NContractCodeText contractCodeText : this.contractCodeTexts) {
			if (contractCodeText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && contractCodeText.getContractCode() != null) {
				i18ntype = contractCodeText.getContractCode();
				break;
			}
			else if(contractCodeText.getLocale().equalsIgnoreCase("en_US")) {
				i18ntype = contractCodeText.getContractCode();
			}

		}
		return i18ntype;
    }

}
