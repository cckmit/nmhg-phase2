package tavant.twms.domain.customReports;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.ReportType;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 11:32:22 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Filters({
	  @Filter(name="excludeInactive")
	})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class CustomReport implements BusinessUnitAware, AuditableColumns {

    @Id
    @GeneratedValue(generator = "CustomReport")
	@GenericGenerator(name = "CustomReport", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CUSTOM_REPORT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    private String name;

    @ManyToMany(cascade = {javax.persistence.CascadeType.ALL},fetch = FetchType.LAZY)
    @JoinTable(name = "item_groups_in_reports", joinColumns = { @JoinColumn(name = "custom_report") }, inverseJoinColumns = { @JoinColumn(name = "item_group") })
    List<ItemGroup> forItemGroups = new ArrayList<ItemGroup>();
    
    @ManyToMany(cascade = {javax.persistence.CascadeType.ALL},fetch = FetchType.LAZY)
    @JoinTable(name = "inventory_types_in_reports", joinColumns = { @JoinColumn(name = "custom_report") }, inverseJoinColumns = { @JoinColumn(name = "inventory_type") })
    List<InventoryType> forInventoryTypes = new ArrayList<InventoryType>();

    @OneToMany(cascade = {javax.persistence.CascadeType.ALL}, fetch = FetchType.LAZY)
    @OrderBy("order")
	@JoinTable(name = "report_sections", joinColumns = @JoinColumn(name = "for_report"))
	private List<ReportSection> sections = new ArrayList<ReportSection>();

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    private Boolean published = Boolean.FALSE;

    @OneToMany(fetch = FetchType.LAZY )
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Report_i18n_text",
            joinColumns = @JoinColumn(name = "Custom_Report"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NReportText> i18nReportTexts = new ArrayList<I18NReportText>();
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade( { CascadeType.ALL })
    private ReportType reportType;

    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL ,CascadeType.DELETE_ORPHAN,CascadeType.SAVE_UPDATE})
    private List<CustomReportApplicablePart> applicableParts = new ArrayList<CustomReportApplicablePart>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   

	public String getName() {
        String i18nName = null;
        for (I18NReportText i18nReportText : getI18nReportTexts()) {
			if (i18nReportText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) ) {
				i18nName=i18nReportText.getDescription();
                break;
            }
			else if(i18nReportText.getLocale().equalsIgnoreCase("en_US")) {
				i18nName = i18nReportText.getDescription();
			}

		}
        if(StringUtils.hasText(i18nName)){
            return i18nName;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemGroup> getForItemGroups() {
        return forItemGroups;
    }

    public void setForItemGroups(List<ItemGroup> forItemGroups) {
        this.forItemGroups = forItemGroups;
    }

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
        this.businessUnitInfo = businessUnitInfo;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public List<ReportSection> getSections() {
        return sections;
    }

    public void setSections(List<ReportSection> sections) {
        this.sections = sections;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<I18NReportText> getI18nReportTexts() {
        return i18nReportTexts;
    }

    public void setI18nReportTexts(List<I18NReportText> i18nReportTexts) {
        this.i18nReportTexts = i18nReportTexts;
    }

	public List<InventoryType> getForInventoryTypes() {
		return forInventoryTypes;
	}

	public void setForInventoryTypes(List<InventoryType> forInventoryTypes) {
		this.forInventoryTypes = forInventoryTypes;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}
	
    public List<CustomReportApplicablePart> getApplicableParts() {
        return applicableParts;
    }

    public void setApplicableParts(List<CustomReportApplicablePart> applicableParts) {
        this.applicableParts = applicableParts;
    }

    public void addApplicablePart(CustomReportApplicablePart applicablePart) {
        this.applicableParts.add(applicablePart);
    }

}
