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
package tavant.twms.domain.claim;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Parameter;

import org.hibernate.annotations.*;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author kamal.govindraj
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class RuleFailure implements Comparable<RuleFailure>, AuditableColumns {
	@Id
	@GeneratedValue(generator = "RuleFailure")
	@GenericGenerator(name = "RuleFailure", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "RULE_FAILURE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private String failedRuleSet;

	@Temporal(TemporalType.DATE)
	private Date recordedDate;


    @CollectionOfElements
    @JoinTable(name = "failed_rule", joinColumns = @JoinColumn(name = "rule_detail"))
    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @AttributeOverrides({
            @AttributeOverride(name = "element.ruleMsg", column = @Column(name = "rule_msg")),
            @AttributeOverride(name = "element.ruleNumber", column = @Column(name = "rule_number")),
            @AttributeOverride(name = "element.defaultRuleMsgInUS", column = @Column(name = "default_rule_msg_in_us")),
            @AttributeOverride(name = "element.ruleAction", column = @Column(name = "rule_action"))})
    private List<FailedRuleDetail> failedRules = new ArrayList<FailedRuleDetail>();
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public RuleFailure(String ruleSet) {
		failedRuleSet = ruleSet;
		recordedDate = new Date();
	}

	public RuleFailure() {

	}
    public List<FailedRuleDetail> getFailedRules() {
        return failedRules;
    }

    public void setFailedRules(List<FailedRuleDetail> failedRules) {
        this.failedRules = failedRules;
    }

    public void addFailedRule(FailedRuleDetail failedRule) {
		failedRules.add(failedRule);
	}

	public String getFailedRuleSet() {
		return failedRuleSet;
	}

	public void setFailedRuleSet(String failedRuleSet) {
		this.failedRuleSet = failedRuleSet;
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

	// TODO : Make it a CalendarDate later. It throws some
	// ConstraintViolationIssues
	// when a claim gets *reprocessed*
	public Date getRecordedDate() {
		return recordedDate;
	}

	public void setRecordedDate(Date recordedDate) {
		this.recordedDate = recordedDate;
	}

	public int compareTo(RuleFailure other) {
		if (other == null) {
			return 1;
		}
		int dateCompare = recordedDate.compareTo(other.getRecordedDate());
		if (dateCompare == 0) {
			return failedRuleSet.compareTo(other.getFailedRuleSet());
		} else {
			return dateCompare;
		}
	}

    public RuleFailure clone() {
        RuleFailure ruleFailure = new RuleFailure();
        ruleFailure.setFailedRuleSet(failedRuleSet);
        ruleFailure.setRecordedDate(recordedDate);

        for (FailedRuleDetail detail : failedRules) {
            ruleFailure.getFailedRules().add(detail.clone());
        }
        return ruleFailure;
    }

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append(
				"recorded date", recordedDate).toString();
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
