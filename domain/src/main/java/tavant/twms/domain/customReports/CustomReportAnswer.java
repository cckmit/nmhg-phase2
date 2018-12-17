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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 6, 2009
 * Time: 5:40:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class CustomReportAnswer implements AuditableColumns {
    @Id
    @GeneratedValue(generator = "CustomReportAnswer")
	@GenericGenerator(name = "CustomReportAnswer", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CUSTOM_REPORT_ANSWER_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private CustomReport customReport ;

    @OneToMany( fetch = FetchType.LAZY)
	@Cascade( {org.hibernate.annotations.CascadeType.ALL})
	@JoinTable(name = "custom_report_answers",
            joinColumns = @JoinColumn(name = "for_report_answer"), 
            inverseJoinColumns = @JoinColumn(name = "report_form_answer"))
	private List<ReportFormAnswer> formAnswers = new ArrayList<ReportFormAnswer>();

    @ManyToOne(fetch = FetchType.LAZY)
    private InventoryItem forInventory;
    
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
	@Column(nullable = true)
	private CalendarDate installationDate; 
    
    @OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "report_answer_attachments")        
    private List<Document> attachments = new ArrayList<Document>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomReport getCustomReport() {
        return customReport;
    }

    public void setCustomReport(CustomReport customReport) {
        this.customReport = customReport;
    }

    public List<ReportFormAnswer> getFormAnswers() {
        return formAnswers;
    }

    public void setFormAnswers(List<ReportFormAnswer> formAnswers) {
        this.formAnswers = formAnswers;
    }

    public InventoryItem getForInventory() {
        return forInventory;
    }

    public void setForInventory(InventoryItem forInventory) {
        this.forInventory = forInventory;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

	public CalendarDate getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(CalendarDate installationDate) {
		this.installationDate = installationDate;
	}

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
