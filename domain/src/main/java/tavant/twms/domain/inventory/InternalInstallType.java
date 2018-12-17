package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.List;

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
@Filters({ @Filter(name = "excludeInactive") })
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class InternalInstallType implements BusinessUnitAware, AuditableColumns {

	@Id
	@GeneratedValue(generator = "InternalInstallType")
	@GenericGenerator(name = "InternalInstallType", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INTERNAL_INSTALL_TYPE_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1") })
	private Long id;

	@Version
	private int version;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	@OneToMany(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "I18NINTERNAL_INSTALL_TYPE", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<I18NInternalInstallType> internalInstallTypeTexts = new ArrayList<I18NInternalInstallType>();

	public String internalInstallType;

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

	public String getInternalInstallType() {
		return internalInstallType;
	}

	public void setInternalInstallType(String internalInstallType) {
		this.internalInstallType = internalInstallType;
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

	public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
	}

	public List<I18NInternalInstallType> getInternalInstallTypeTexts() {
		return internalInstallTypeTexts;
	}

	public void setInternalInstallTypeTexts(
			List<I18NInternalInstallType> internalInstallTypeTexts) {
		this.internalInstallTypeTexts = internalInstallTypeTexts;
	}
	
	public String getDisplayInternalInstallType() {
		String i18ntype = "";
		for (I18NInternalInstallType internalInstallTypeText : this.internalInstallTypeTexts) {
			if (internalInstallTypeText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString())
					&& internalInstallTypeText.getInternalInstallType() != null) {
				i18ntype = internalInstallTypeText.getInternalInstallType();
				break;
			} else if (internalInstallTypeText.getLocale().equalsIgnoreCase(
					"en_US")) {
				i18ntype = internalInstallTypeText.getInternalInstallType();
			}

		}
		return i18ntype;
	}

}
