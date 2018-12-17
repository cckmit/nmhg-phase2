package tavant.twms.domain.customReports;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.common.AuditableColEntity;

import java.util.List;
import java.util.ArrayList;

import com.domainlanguage.time.CalendarDate;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 6, 2009
 * Time: 5:30:02 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ReportFormAnswer implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "ReportFormAnswer")
	@GenericGenerator(name = "ReportFormAnswer", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "REPORT_FORM_ANSWER_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Column(name="answer_order")
    private Integer order;

    @OneToMany( fetch = FetchType.LAZY)
	@Cascade( {org.hibernate.annotations.CascadeType.ALL})
	@JoinTable(name = "form_answer_options",
            joinColumns = @JoinColumn(name = "for_form_answer"),
            inverseJoinColumns = @JoinColumn(name = "answer_option"))
	private List<ReportFormAnswerOption> answerOptions = new ArrayList<ReportFormAnswerOption>();

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade( {org.hibernate.annotations.CascadeType.ALL})
    private ReportFormQuestion question;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade( {org.hibernate.annotations.CascadeType.ALL})
    private ReportSection section;

    private String answerValue;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate answerDate;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public ReportFormAnswer() {
    }

    public ReportFormAnswer(Integer order, ReportFormQuestion question, ReportSection section) {
        this.order = order;
        this.question = question;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ReportFormAnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<ReportFormAnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public ReportFormQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ReportFormQuestion question) {
        this.question = question;
    }

    public ReportSection getSection() {
        return section;
    }

    public void setSection(ReportSection section) {
        this.section = section;
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public CalendarDate getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(CalendarDate answerDate) {
        this.answerDate = answerDate;
    }
}
