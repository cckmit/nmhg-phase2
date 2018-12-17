package tavant.twms.domain.campaign;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class FieldModUpdateAudit implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "FieldModUpdateAudit")
	@GenericGenerator(name = "FieldModUpdateAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FIELD_MOD_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;	
	
	@Version
    private int version;
	
	private String comments;
	
	private String actionTaken;
	
	private String inactiveReason;
	
		
	@ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "for_field_mod", insertable = false, updatable = false)
    private CampaignNotification forFieldModNotification;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public CampaignNotification getForFieldModNotifcation() {
		return forFieldModNotification;
	}

	public void setForFieldModNotifcation(CampaignNotification forFieldModNotification) {
		this.forFieldModNotification = forFieldModNotification;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getInactiveReason() {
		return inactiveReason;
	}

	public void setInactiveReason(String inactiveReason) {
		this.inactiveReason = inactiveReason;
	}
	
}

