package tavant.twms.domain.customReports;

import java.sql.Types;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 10, 2008
 * Time: 11:52:57 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class ReportFormQuestion implements Comparable<ReportFormQuestion>, AuditableColumns {
    @Id
    @GeneratedValue(generator = "ReportFormQuestion")
	@GenericGenerator(name = "ReportFormQuestion", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "REPORT_FORM_QUESTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name ="question_order", nullable = false)
    private Integer order;
    
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.customReports.ReportFormAnswerTypes"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private ReportFormAnswerTypes answerType;

    @Column(nullable = false)
    private Boolean mandatory=Boolean.FALSE;

    @OneToMany( fetch = FetchType.LAZY)
	@Cascade( {org.hibernate.annotations.CascadeType.ALL})
	@JoinTable(name = "section_question_answers",
            joinColumns = @JoinColumn(name = "for_question"),
            inverseJoinColumns = @JoinColumn(name = "answer_options"))
	private List<ReportFormAnswerOption> answerOptions = new ArrayList<ReportFormAnswerOption>();

    @OneToMany(fetch = FetchType.LAZY )
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "Question_i18n_text",
            joinColumns = @JoinColumn(name = "Report_Question"),
            inverseJoinColumns = @JoinColumn(name = "i18n_text"))
    private List<I18NQuestionText> i18nQuestionTexts = new ArrayList<I18NQuestionText>();

    @Embedded
	@Cascade( {org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	private AuditableColEntity d = new AuditableColEntity();

    @ManyToOne(fetch = FetchType.LAZY)
    private ReportSection forSection;

    @Transient
    private Integer noOfOptions;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL })
    private CustomReportInstructions preInstructions;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL })
    private CustomReportInstructions postInstructions;

    private boolean includeOtherAsAnOption;

    public boolean isIncludeOtherAsAnOption() {
        return includeOtherAsAnOption;
    }

    public void setIncludeOtherAsAnOption(boolean includeOtherAsAnOption) {
        this.includeOtherAsAnOption = includeOtherAsAnOption;
    }

    public CustomReportInstructions getPostInstructions() {
        return postInstructions;
    }

    public void setPostInstructions(CustomReportInstructions postInstructions) {
        this.postInstructions = postInstructions;
    }

    public CustomReportInstructions getPreInstructions() {
        return preInstructions;
    }

    public void setPreInstructions(CustomReportInstructions preInstructions) {
        this.preInstructions = preInstructions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public ReportFormAnswerTypes getAnswerType() {
        return answerType;
    }

    public void setAnswerType(ReportFormAnswerTypes answerType) {
        this.answerType = answerType;
    }

    public List<ReportFormAnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<ReportFormAnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }


    public Integer getNoOfOptions() {
        return noOfOptions;
    }

    public void setNoOfOptions(Integer noOfOptions) {
        this.noOfOptions = noOfOptions;
    }

    public String getName() {
        String i18nName = null;
        for (I18NQuestionText i18nQuestionText : getI18nQuestionTexts()) {
			if (i18nQuestionText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) ) {
				i18nName=i18nQuestionText.getDescription();
                break;
            }
			else if(i18nQuestionText.getLocale().equalsIgnoreCase("en_US")) {
				i18nName = i18nQuestionText.getDescription();
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

    public int compareTo(ReportFormQuestion otherQuestion) {
        if (this.order != null) {
            return this.order.compareTo(otherQuestion.getOrder());
        } else {
            return -1;
        }
    }

    public List<I18NQuestionText> getI18nQuestionTexts() {
        return i18nQuestionTexts;
    }

    public void setI18nQuestionTexts(List<I18NQuestionText> i18nQuestionTexts) {
        this.i18nQuestionTexts = i18nQuestionTexts;
    }

    public ReportSection getForSection() {
        return forSection;
    }

    public void setForSection(ReportSection forSection) {
        this.forSection = forSection;
    }
    
	public boolean validAnswerType(){
    boolean isValid = false;
    if(this.answerType!=null && this.answerType.getType()!= ""){
    	if(this.answerType.equals(ReportFormAnswerTypes.DATE) || 
    			this.answerType.equals(ReportFormAnswerTypes.MULTI_SELECT) ||
    			this.answerType.equals(ReportFormAnswerTypes.LARGE_TEXT) ||
    			this.answerType.equals(ReportFormAnswerTypes.MULTI_SELECT_LIST) ||
    			this.answerType.equals(ReportFormAnswerTypes.NUMBER) ||
    			this.answerType.equals(ReportFormAnswerTypes.SINGLE_SELECT) ||
    			this.answerType.equals(ReportFormAnswerTypes.SINGLE_SELECT_LIST) || 
    			this.answerType.equals(ReportFormAnswerTypes.SMALL_TEXT))
    			isValid =true;
    }
    return isValid;
    }
	
	public boolean answerOptionsRequired() {
		if (answerTypeList()) {
			if (CollectionUtils.isEmpty(this.answerOptions))
				return true;
		}
		return false;
	}

	
	public boolean answerOptionsNotAllowed() {
		if (!answerTypeList()) {
				if (!CollectionUtils.isEmpty(this.answerOptions))
					return true;
		}
		return false;
	}
	
	private boolean answerTypeList() {
		if (this.answerType != null && this.answerType.getType() != "") {
			if (this.answerType.equals(ReportFormAnswerTypes.MULTI_SELECT)
					|| this.answerType.equals(ReportFormAnswerTypes.MULTI_SELECT_LIST)
					|| this.answerType.equals(ReportFormAnswerTypes.SINGLE_SELECT)
					|| this.answerType.equals(ReportFormAnswerTypes.SINGLE_SELECT_LIST)) {
				return true;
			}
		}
		return false;
	}
	

	public boolean attachmentsExist() {
		if (this.answerType != null && this.answerType.getType() != "") {
			if (this.answerType.equals(ReportFormAnswerTypes.MULTI_SELECT_LIST)
					|| this.answerType.equals(ReportFormAnswerTypes.SINGLE_SELECT_LIST)) {
				for(ReportFormAnswerOption answerOption : this.answerOptions){
					if(answerOption.getAttachment()!=null){
						return  true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean defaultValueIsValid() {
		if (answerTypeList()) {
			if (this.answerType.equals(ReportFormAnswerTypes.SINGLE_SELECT)
					|| this.answerType.equals(ReportFormAnswerTypes.SINGLE_SELECT_LIST)){
				return validNoOfDefaultAnswers(true);
			}else{
				return validNoOfDefaultAnswers(false);
			}
			
		}
		return false;
	}
	
	private boolean validNoOfDefaultAnswers(boolean onlyOne){
		int noOfDefaultAns=0;
		for(ReportFormAnswerOption ans:answerOptions){
			noOfDefaultAns = ans.getIsDefault() ? noOfDefaultAns +1 : noOfDefaultAns; 
		}
		if(onlyOne){
			return (noOfDefaultAns>1);
		}else{
			return  false;
		}
	}


    public List<ReportFormAnswerOption> getDefaultAnswers(){
        List<ReportFormAnswerOption> defaultAnswers = new ArrayList<ReportFormAnswerOption>();
        for (ReportFormAnswerOption answerOption : answerOptions) {
            if(answerOption.getIsDefault()){
                defaultAnswers.add(answerOption);
            }
        }
        return defaultAnswers;
    }
}
