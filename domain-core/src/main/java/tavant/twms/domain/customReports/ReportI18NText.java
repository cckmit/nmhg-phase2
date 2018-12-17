package tavant.twms.domain.customReports;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

import tavant.twms.security.Auditable;
import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.common.AuditableColEntity;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 23, 2009
 * Time: 1:04:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type",discriminatorType = DiscriminatorType.STRING)
public abstract class ReportI18NText implements AuditableColumns {
    @Id
	@GeneratedValue(generator = "ReportI18NText")
	@GenericGenerator(name = "ReportI18NText", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "Report_I18N_Text_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String description;

    private String locale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }
}
