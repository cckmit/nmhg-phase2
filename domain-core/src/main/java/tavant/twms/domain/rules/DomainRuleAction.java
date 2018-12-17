/*
 *   Copyright (c)2007 Tavant Technologies
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

import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.NotEmpty;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DomainRuleAction implements AuditableColumns{
	@Id
	@GeneratedValue(generator = "DomainRuleAction")
	@GenericGenerator(name = "DomainRuleAction", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DOMAIN_RULEACTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;
    
    @NotEmpty
    protected String name;

    @NotEmpty
    protected String context;

    @NotEmpty
    protected String state;

    @Transient
    public static String CLAIM_STATE_KEY = "claimState";
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public DomainRuleAction() {
    }

    public DomainRuleAction(String name, String state, String context) {
        this.name = name;
        this.state = state;
        this.context = context;
    }

    public void performAction(Map<String, Object> actionContext) {

        String claimState = (String) actionContext.get(CLAIM_STATE_KEY);
        boolean isAlreadyRejected = "rejected".equals(claimState);
        boolean isAlreadyOnHoldForPartReturn = "on hold for part return".equals(claimState);
        boolean isAlreadyOnHold = "on hold".equals(claimState);
        boolean isAlreadyForwarded = "Forwarded".equals(claimState);

        if ("on hold for part return".equals(this.state) && isAlreadyRejected) {
            // If already rejected , then no need to put it on hold.
            return;
        }else if ("on hold".equals(this.state) && (isAlreadyRejected  || isAlreadyOnHoldForPartReturn)) {
            // If already rejected or put on hold, then no need for manual
            // review.
            return;
        }else if ("manual review".equals(this.state) && (isAlreadyRejected || isAlreadyOnHold || isAlreadyOnHoldForPartReturn)) {
            // If already rejected or put on hold, then no need for manual
            // review.
            return;
        } else if("Forwarded".equals(this.state) && (isAlreadyForwarded)) {
        	// If already rejected or put on hold, then no need for Forwarding it again
        	return;
        }

        actionContext.put(CLAIM_STATE_KEY, this.state);
    }
  
    @Transient
    public String getActionName(){
    	//To do ..replace it with constants
    	if("assign".equalsIgnoreCase(this.state)){
    		return "Assign To "+this.name;
    		
    	}else if ("not Assign".equalsIgnoreCase(this.state)) {
    		return "Not Assign to "+ this.name;
    	}
    	
    	else
    		return this.name;
    }
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	
}
