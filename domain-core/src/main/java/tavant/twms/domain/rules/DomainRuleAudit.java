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
package tavant.twms.domain.rules;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.TimePoint;

/**
 * @author mritunjay.kumar
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DomainRuleAudit implements AuditableColumns,Comparable<DomainRuleAudit> {

	public static final String ACTIVE = "ACTIVE";

	public static final String INACTIVE = "INACTIVE";

	@Id
	@GeneratedValue(generator = "DomainRuleAudit")
	@GenericGenerator(name = "DomainRuleAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DOMAIN_RULEAUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;


    @Type(type = "tavant.twms.infra.CalendarTimeUserType")
    private TimePoint createdOn;

    private Date createdTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User createdBy;

    // current snapshot of the rule object.


    private String status;

    @Lob
    @Column(length = 16777210, name = "rule_snapshot_string")
    private String ruleSnapshotAsString;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	private String name;

	private String failureMessage;

	@OneToOne(fetch = FetchType.EAGER)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private DomainRuleAction action;

	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "domain_rule", insertable = false, updatable = false)
	private DomainRule domainRule;
	
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleSnapshotAsString() {
        return this.ruleSnapshotAsString;
    }

    public void setRuleSnapshotAsString(String ruleSnapshotAsString) {
        this.ruleSnapshotAsString = ruleSnapshotAsString;
    }

    public TimePoint getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(TimePoint createdOn) {
        this.createdOn = createdOn;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public DomainRuleAction getAction() {
		return action;
	}

	public void setAction(DomainRuleAction action) {
		this.action = action;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}


	public int compareTo(DomainRuleAudit domainRuleAudit) {
		if (domainRuleAudit == null) {
			return 0;
		}
		if (this.createdOn != null && domainRuleAudit.createdOn != null) {
			if (this.createdOn.isBefore(domainRuleAudit.createdOn)) {
				return 1;
			}
			if (this.createdOn.isAfter(domainRuleAudit.createdOn)) {
				return -1;
			}
		}
		return 0;
	}



	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DomainRule getDomainRule() {
		return domainRule;
	}

	public void setDomainRule(DomainRule domainRule) {
		this.domainRule = domainRule;
	}
	
}



