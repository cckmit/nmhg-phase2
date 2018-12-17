package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
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

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: May 27, 2009
 * Time: 2:53:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class Market implements BusinessUnitAware, AuditableColumns {
    @Id
    @GeneratedValue(generator = "Market")
	@GenericGenerator(name = "Market", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MARKET_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @OneToMany(fetch = FetchType.LAZY )
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Market_i18n_text",
            joinColumns = @JoinColumn(name = "MARKET"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NMarketText> i18nMarketTexts = new ArrayList<I18NMarketText>();

    @OneToOne(fetch = FetchType.LAZY)
    private Market parentId;

    private String type;

    private String code;
    
    private String title;

  

	@OneToMany(fetch = FetchType.LAZY )
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Market_Desc_i18n_text",
            joinColumns = @JoinColumn(name = "MARKET"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NMarketDescText> i18nMarketDescTexts = new ArrayList<I18NMarketDescText>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

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

    public List<I18NMarketText> getI18nMarketTexts() {
        return i18nMarketTexts;
    }

    public void setI18nMarketTexts(List<I18NMarketText> i18nMarketTexts) {
        this.i18nMarketTexts = i18nMarketTexts;
    }

    public Market getParentId() {
        return parentId;
    }

    public void setParentId(Market parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<I18NMarketDescText> getI18nMarketDescTexts() {
        return i18nMarketDescTexts;
    }

    public void setI18nMarketDescTexts(List<I18NMarketDescText> i18nMarketDescTexts) {
        this.i18nMarketDescTexts = i18nMarketDescTexts;
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

    public String getName() {
		String i18nDescription = "";
		for (I18NMarketText I18NMarketText : getI18nMarketTexts()) {
			if (I18NMarketText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString())
					&& I18NMarketText.getDescription() != null) {
				i18nDescription = I18NMarketText.getDescription();
				break;
			} else if (I18NMarketText.getLocale().equalsIgnoreCase("en_US")) {
				i18nDescription = I18NMarketText.getDescription();
			}
		}
		return i18nDescription;
	}

    public String getDescription() {
		String i18nDescription = "";
		for (I18NMarketDescText I18NMarketDescText : getI18nMarketDescTexts()) {
			if (I18NMarketDescText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString())
					&& I18NMarketDescText.getDescription() != null) {
				i18nDescription = I18NMarketDescText.getDescription();
				break;
			} else if (I18NMarketDescText.getLocale().equalsIgnoreCase("en_US")) {
				i18nDescription = I18NMarketDescText.getDescription();
			}
		}
		return i18nDescription;
	}
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
