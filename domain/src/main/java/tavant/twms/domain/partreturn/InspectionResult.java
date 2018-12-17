/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.partreturn;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author vineeth.varghese
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class InspectionResult implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "InspectionResult")
	@GenericGenerator(name = "InspectionResult", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INSPECTION_RESULT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private boolean accepted;

    @OneToOne
    private FailureReason failureReason;

    @OneToOne
    private PartAcceptanceReason acceptanceReason;

    private String comments;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    /**
     * @return the failureReason
     */
    public FailureReason getFailureReason() {
        return this.failureReason;
    }

    /**
     * @param failureReason the failureReason to set
     */
    public void setFailureReason(FailureReason failureReason) {
        this.failureReason = failureReason;
    }

    /**
     * @return the accepted
     */
    public boolean isAccepted() {
        return this.accepted;
    }

    /**
     * @param accepted the accepted to set
     */
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("accepted", this.accepted)
                .toString();
    }

    public PartAcceptanceReason getAcceptanceReason() {
        return acceptanceReason;
    }

    public void setAcceptanceReason(PartAcceptanceReason acceptanceReason) {
        this.acceptanceReason = acceptanceReason;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
