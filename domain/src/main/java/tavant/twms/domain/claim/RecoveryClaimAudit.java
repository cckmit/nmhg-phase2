/*
 *   Copyright (c) 2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.claim;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.common.Document;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

/**
 * @author pradipta.a
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name = "rec_claim_audit")
public class RecoveryClaimAudit implements Comparable<RecoveryClaimAudit>, AuditableColumns {

	 @Id
	    @GeneratedValue(generator = "RecClaimAudit")
		@GenericGenerator(name = "RecClaimAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
				@Parameter(name = "sequence_name", value = "RECCLAIM_AUDIT_SEQ"),
				@Parameter(name = "initial_value", value = "1000"),
				@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate createdOn;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User createdBy;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.claim.RecoveryClaimState"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private RecoveryClaimState recoveryClaimState;

    private String comments;
    
    private String externalComments;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "recovered_cost_amt"),
            @Column(name = "recovered_cost_curr") })
    private Money recoveredAmount;
    
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "accepted_cost_amt", nullable = true),
            @Column(name = "accepted_cost_curr", nullable = true) })
            
     private Money acceptedAmount;
    
   
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "for_recovery_claim", insertable = false, updatable = false)
    private RecoveryClaim forRecoveryClaim;
    
    @OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})
    @JoinTable(name="rec_claim_audit_attachments", joinColumns = { @JoinColumn(name = "REC_CLAIM_AUDIT") }/*, inverseJoinColumns = { @JoinColumn(name = "ATTACHMENTS") }*/)
    private List<Document> attachments = new ArrayList<Document>();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Column(length = 4000)
    private String partReturnCommentsToDealer;
    
    public String getPartReturnCommentsToDealer() {
		return partReturnCommentsToDealer;
	}

	public void setPartReturnCommentsToDealer(String partReturnCommentsToDealer) {
		this.partReturnCommentsToDealer = partReturnCommentsToDealer;
	}
	
    public int compareTo(RecoveryClaimAudit other) {
        if (other == null) {
            return 1;
        }
        int dateCompare = this.createdOn.compareTo(other.createdOn);
        return dateCompare;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalendarDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(CalendarDate createdOn) {
        this.createdOn = createdOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public RecoveryClaim getForRecoveryClaim() {
        return forRecoveryClaim;
    }

    public void setForRecoveryClaim(RecoveryClaim forRecoveryClaim) {
        this.forRecoveryClaim = forRecoveryClaim;
    }

    public RecoveryClaimState getRecoveryClaimState() {
        return recoveryClaimState;
    }

    public void setRecoveryClaimState(RecoveryClaimState recoveryClaimState) {
        this.recoveryClaimState = recoveryClaimState;
    }

    public Money getRecoveredAmount() {
        return recoveredAmount;
    }

    public void setRecoveredAmount(Money recoveredAmount) {
        this.recoveredAmount = recoveredAmount;
    }
    
    public Money getAcceptedAmount() {
		return acceptedAmount;
	}

	public void setAcceptedAmount(Money acceptedAmount) {
		this.acceptedAmount = acceptedAmount;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<Document> getAttachments() {
		return attachments == null? new ArrayList<Document>(): this.attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}
	
	public static RecoveryClaimAudit replicateRecoveryClaimAudit(RecoveryClaimAudit activeRecoveryClaimAudit) {
		final RecoveryClaimAudit audit = new RecoveryClaimAudit();
		audit.setComments(activeRecoveryClaimAudit.getComments());
		audit.setExternalComments(activeRecoveryClaimAudit.getExternalComments());
		audit.setCreatedBy(activeRecoveryClaimAudit.getCreatedBy());
		audit.setCreatedOn(activeRecoveryClaimAudit.getCreatedOn());
		audit.setD(activeRecoveryClaimAudit.getD());
		audit.setForRecoveryClaim(activeRecoveryClaimAudit.getForRecoveryClaim());
		audit.setRecoveredAmount(activeRecoveryClaimAudit.getRecoveredAmount());
		audit.setAcceptedAmount(activeRecoveryClaimAudit.getAcceptedAmount());
		audit.setRecoveryClaimState(activeRecoveryClaimAudit.getRecoveryClaimState());
		for (Document document : activeRecoveryClaimAudit.getAttachments()) {
			audit.getAttachments().add(document);
		}
		return audit;
    }

	public String getExternalComments() {
		return externalComments;
	}

	public void setExternalComments(String externalComments) {
		this.externalComments = externalComments;
	}
	
	public String displayUserNameForSupplier() {
		String userName = "";
		if (createdBy.isDealer()) {
			userName = "Dealer";
		} else {
			userName = createdBy.getCompleteNameAndLogin();
		}
		return userName;
	}

}
