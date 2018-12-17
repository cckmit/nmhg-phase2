package tavant.twms.domain.customReports;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;


/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 11:45:28 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ReportSection implements Comparable<ReportSection>, AuditableColumns {
    @Id
    @GeneratedValue(generator = "ReportSection")
	@GenericGenerator(name = "ReportSection", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "REPORT_SECTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "section_order",nullable = false)
    private Integer order;

    @OneToMany(mappedBy = "forSection",fetch = FetchType.LAZY)
    @OrderBy("order")
    @Cascade( { CascadeType.DELETE_ORPHAN })
    @Filter(name="excludeInactive")
    private List<ReportFormQuestion> questionnaire = new ArrayList<ReportFormQuestion>();

    @OneToMany(fetch = FetchType.LAZY )
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Section_i18n_text",
            joinColumns = @JoinColumn(name = "Report_Section"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NReportSectionText> i18nReportSectionTexts = new ArrayList<I18NReportSectionText>();

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
        for (I18NReportSectionText i18nSectionText : getI18nReportSectionTexts()) {
			if (i18nSectionText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) ) {
				i18nName=i18nSectionText.getDescription();
                break;
            }
			else if(i18nSectionText.getLocale().equalsIgnoreCase("en_US")) {
				i18nName = i18nSectionText.getDescription();
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<ReportFormQuestion> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(List<ReportFormQuestion> questionnaire) {
        this.questionnaire = questionnaire;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public int compareTo(ReportSection otherSection) {
        if (this.order != null) {
            return this.order.compareTo(otherSection.getOrder());
        } else {
            return -1;
        }
    }

    public List<I18NReportSectionText> getI18nReportSectionTexts() {
        return i18nReportSectionTexts;
    }

    public void setI18nReportSectionTexts(List<I18NReportSectionText> i18nReportSectionTexts) {
        this.i18nReportSectionTexts = i18nReportSectionTexts;
    }
}