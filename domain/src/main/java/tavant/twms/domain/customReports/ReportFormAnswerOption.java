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
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 11:56:12 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ReportFormAnswerOption implements Comparable<ReportFormAnswerOption>, AuditableColumns {
    @Id
    @GeneratedValue(generator = "ReportFormAnswerOption")
	@GenericGenerator(name = "ReportFormAnswerOption", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "REPORT_FORM_ANSWER_OPTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Column(name = "ans_order")
    private Integer order;

    private String answerOption;

    @OneToMany(fetch = FetchType.LAZY )
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Ans_option_i18n_text",
            joinColumns = @JoinColumn(name = "Answer_option"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NAnswerOptionText> i18nAnswerOptionTexts = new ArrayList<I18NAnswerOptionText>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Document attachment;
    
    private boolean otherOption;

    private boolean isDefault;

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Document getAttachment() {
        return attachment;
    }

    public void setAttachment(Document attachment) {
        this.attachment = attachment;
    }

    public ReportFormAnswerOption() {
    }

    public ReportFormAnswerOption(Integer order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    

    public boolean isOtherOption() {
		return otherOption;
	}

	public void setOtherOption(boolean otherOption) {
		this.otherOption = otherOption;
	}

	public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getAnswerOption() {
        String i18nName = null;
        for (I18NAnswerOptionText i18nAnswerOptionText : getI18nAnswerOptionTexts()) {
			if (i18nAnswerOptionText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) ) {
				i18nName=i18nAnswerOptionText.getDescription();
                break;
            }
			else if(i18nAnswerOptionText.getLocale().equalsIgnoreCase("en_US")) {
				i18nName = i18nAnswerOptionText.getDescription();
			}

		}
        if(StringUtils.hasText(i18nName)){
            return i18nName;
        }
        return answerOption;
    }

    public void setAnswerOption(String answerOption) {
        this.answerOption = answerOption;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

     public int compareTo(ReportFormAnswerOption otherAnswer) {
        if (this.order != null) {
            return this.order.compareTo(otherAnswer.getOrder());
        } else {
            return -1;
        }
    }

    public List<I18NAnswerOptionText> getI18nAnswerOptionTexts() {
        return i18nAnswerOptionTexts;
    }

    public void setI18nAnswerOptionTexts(List<I18NAnswerOptionText> i18nAnswerOptionTexts) {
        this.i18nAnswerOptionTexts = i18nAnswerOptionTexts;
    }
    
    //The content type varies with IE & Firefox
	public boolean isDocumentImage() {
		if (attachment != null && attachment.getContentType() != null) {
			//if browser used is Firefox
			if (attachment.getContentType().equalsIgnoreCase("image/jpeg")
					|| attachment.getContentType().equalsIgnoreCase("image/png") || 
					//if browser is IE
					attachment.getContentType().equalsIgnoreCase("image/x-png") || 
					attachment.getContentType().equalsIgnoreCase("image/pjpeg"))
				return true;
			else
				return false;
		}
		return true;
	}

    public Long getDocumentId(){
        if(attachment!=null){
        return attachment.getId();
        }
        return new Long(-1);
    }
}
