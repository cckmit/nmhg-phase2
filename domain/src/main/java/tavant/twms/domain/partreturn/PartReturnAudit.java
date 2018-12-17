package tavant.twms.domain.partreturn;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class PartReturnAudit implements AuditableColumns {
	
	@Id
    @GeneratedValue(generator = "PartReturnAudit")
	@GenericGenerator(name = "PartReturnAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_RET_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String comments;

    private String prStatus;

    @OneToOne(fetch = FetchType.LAZY,optional = true,cascade = { CascadeType.ALL })
    private PartReturnAction partReturnAction1;
    
    @OneToOne(fetch = FetchType.LAZY,optional = true,cascade = { CascadeType.ALL })
    private PartReturnAction partReturnAction2;

    @OneToOne(fetch = FetchType.LAZY,optional = true,cascade = { CascadeType.ALL })
    private PartReturnAction partReturnAction3;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "for_part_replaced", updatable = false,insertable = false)
    private OEMPartReplaced forPartReplaced;
    
	private String failureCause;

	private String acceptanceCause;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected AuditableColEntity d = new AuditableColEntity();

    public PartReturnAudit() {
    }

    public PartReturnAudit(PartReturnAction partReturnAction1, PartReturnAction partReturnAction2) {
    	this.partReturnAction1=partReturnAction1;
    	this.partReturnAction2=partReturnAction2;
    }

    public PartReturnAudit(PartReturnAction partReturnAction1, PartReturnAction partReturnAction2, PartReturnAction partReturnAction3) {
        this.partReturnAction1=partReturnAction1;
        this.partReturnAction2=partReturnAction2;
        this.partReturnAction3=partReturnAction3;
    }

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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}


	public PartReturnAction getPartReturnAction1() {
		return partReturnAction1;
	}

	public void setPartReturnAction1(PartReturnAction partReturnAction1) {
		this.partReturnAction1 = partReturnAction1;
	}

	public PartReturnAction getPartReturnAction2() {
		return partReturnAction2;
	}

	public void setPartReturnAction2(PartReturnAction partReturnAction2) {
		this.partReturnAction2 = partReturnAction2;
	}

    public PartReturnAction getPartReturnAction3() {
        return partReturnAction3;
    }

    public void setPartReturnAction3(PartReturnAction partReturnAction3) {
        this.partReturnAction3 = partReturnAction3;
    }

    public String getPrStatus() {
        return prStatus;
    }

    public void setPrStatus(String prStatus) {
        this.prStatus = prStatus;
    }

    public OEMPartReplaced getForPartReplaced() {
        return forPartReplaced;
    }

    public void setForPartReplaced(OEMPartReplaced forPartReplaced) {
        this.forPartReplaced = forPartReplaced;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public PartReturnAudit clone() {
        PartReturnAudit partReturnAudit = new PartReturnAudit();
        partReturnAudit.setComments(comments);
        partReturnAudit.setForPartReplaced(forPartReplaced);
        partReturnAudit.setPartReturnAction1(partReturnAction1);
        partReturnAudit.setPartReturnAction2(partReturnAction2);
        partReturnAudit.setPartReturnAction2(partReturnAction3);
        partReturnAudit.setAcceptanceCause(acceptanceCause);
        partReturnAudit.setFailureCause(failureCause);
        partReturnAudit.setPrStatus(prStatus);
        return partReturnAudit;
    }

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

	public String getAcceptanceCause() {
		return acceptanceCause;
	}

	public void setAcceptanceCause(String acceptanceCause) {
		this.acceptanceCause = acceptanceCause;
	}
    
    
}
