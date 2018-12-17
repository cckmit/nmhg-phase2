package tavant.twms.domain.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.security.SecurityHelper;

@Entity
@Table(name = "MISC_ITEM")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class MiscellaneousItem implements BusinessUnitAware {

	@Id
    @GeneratedValue(generator = "MiscellaneousItem")
	@GenericGenerator(name = "MiscellaneousItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MISC_ITEM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	private String partNumber;


    @OneToMany(fetch = FetchType.LAZY )
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Misc_i18n_text",
            joinColumns = @JoinColumn(name = "MISC_ITEM"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NMiscItem> i18nMiscTexts = new ArrayList<I18NMiscItem>();

    private String description;
	
    // Business unit for which the item/part is configured 
	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getDescription() {
		String i18nDescription = "";
		for (I18NMiscItem I18NMiscItem : this.i18nMiscTexts) {
			if (I18NMiscItem.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString())
					&& I18NMiscItem.getDescription() != null) {
				i18nDescription = I18NMiscItem.getDescription();
				break;
			} else if (I18NMiscItem.getLocale().equalsIgnoreCase("en_US")) {
				i18nDescription = I18NMiscItem.getDescription();
			}
		}
		return i18nDescription;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public void removeWhiteSpacesFromFields(){
		this.partNumber = StringUtils.stripToEmpty(this.partNumber);
		this.description = StringUtils.stripToEmpty(this.description);		
	}

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
	}

    public List<I18NMiscItem> getI18nMiscTexts() {
        return i18nMiscTexts;
    }

    public void setI18nMiscTexts(List<I18NMiscItem> i18nMiscTexts) {
        this.i18nMiscTexts = i18nMiscTexts;
    }
    
    /**
	 * This method is required as Jasper Sub Report loses the authentication of main report
	 * Need to revisit while doing i18n of reports
	 * @return
	 */
	public String getDescriptionForPrint(){
		for (I18NMiscItem i18NItemText : this.i18nMiscTexts) {
			 if (i18NItemText.getLocale().equalsIgnoreCase("en_US")) {
				return i18NItemText.getDescription();				
			}
		}
		return "";
	}
}
