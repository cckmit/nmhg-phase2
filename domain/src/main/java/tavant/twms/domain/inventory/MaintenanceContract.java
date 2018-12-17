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
public class MaintenanceContract implements BusinessUnitAware, AuditableColumns{
    
	@Id
	@GeneratedValue(generator = "MaintenanceContract")
	@GenericGenerator(name = "MaintenanceContract", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MAINTENANCE_CONTRACT_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;
	
	@OneToMany(fetch = FetchType.EAGER)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "I18N_MAINTENANCE_CONTRACT", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<I18NMaintenanceContractText> maintenanceContractTexts = new ArrayList<I18NMaintenanceContractText>();
    
	public List<I18NMaintenanceContractText> getMaintenanceContractTexts() {
		return maintenanceContractTexts;
	}

	public void setMaintenanceContractTexts(
			List<I18NMaintenanceContractText> maintenanceContractTexts) {
		this.maintenanceContractTexts = maintenanceContractTexts;
	}

	@Column(name="MAINTENANCE_CONTRACT")
	private String maintenanceContract;


	public String getMaintenanceContract() {
		return maintenanceContract;
	}

	public void setMaintenanceContract(String maintenanceContract) {
		this.maintenanceContract = maintenanceContract;
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
    
    public String getDisplayMaintenanceContract() {
    	String i18ntype = "";
		for (I18NMaintenanceContractText maintenanceContractText : this.maintenanceContractTexts) {
			if (maintenanceContractText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && maintenanceContractText.getMaintenanceContract() != null) {
				i18ntype = maintenanceContractText.getMaintenanceContract();
				break;
			}
			else if(maintenanceContractText.getLocale().equalsIgnoreCase("en_US")) {
				i18ntype = maintenanceContractText.getMaintenanceContract();
			}

		}
		return i18ntype;
    }

}
