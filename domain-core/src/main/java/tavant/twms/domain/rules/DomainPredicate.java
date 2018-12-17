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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Filters({
  @Filter(name="excludeInactive")
})
@XStreamAlias("domainPredicate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DomainPredicate implements Predicate, ExpressionToken, Visitable, NestablePredicate,BusinessUnitAware,AuditableColumns {

	@Id
	@GeneratedValue(generator = "DomainPredicate")
	@GenericGenerator(name = "DomainPredicate", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DOMAIN_PREDICATE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @NotEmpty
    @Column(length = 255)
    private String name;

    @NotEmpty
    private String context;

    // @ManyToOne
    // @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    @Transient
    private Predicate predicate;

    
    @Lob
    @Column(length = 16777210)
    // @NotEmpty
    private String predicateAsXML;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "domain_pred_refers_to_preds")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<DomainPredicate> refersToPredicates = new HashSet<DomainPredicate>(5);
    
    private String systemDefinedConditionName;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    // For frameworks.
    public DomainPredicate() {
    }

    public DomainPredicate(String name, Predicate predicate) {
        super();
        this.name = name;
        this.predicate = predicate;
        Assert.isTrue(!(predicate instanceof DomainPredicate),
                      " Cannot nested a domain predicate inside another "
                              + "domain predicate directly");
    }

    public String getToken() {
        return "";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return this.name;
    }

    public Predicate getPredicate() {
        if (predicate == null && predicateAsXML != null) {
            predicate = getPredicateFromXML();
        }
        return this.predicate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
        XStreamRuleSerializer serializer = new XStreamRuleSerializer();
        setPredicateAsXML(serializer.getSerializer().toXML(predicate));
    }

    public void setPredicateAsXML(String predicateAsXML) {
        this.predicateAsXML = predicateAsXML;
    }

    public String getPredicateAsXML() {
        return this.predicateAsXML;
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

    public void validate(ValidationContext validationContext) {
        if (this.predicate instanceof DomainPredicate) {
            validationContext.addError("A domain predicate cannot directly "
                    + "refer to another domain predicate");
        }
    }

    @Override
    public String toString() {
        return MessageFormat.format("id = {0}, name = {1}", this.id, this.name);
    }

    public String getDomainTerm() {
        return this.name;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Set<DomainPredicate> getRefersToPredicates() {
        return this.refersToPredicates;
    }

    public void setRefersToPredicates(Set<DomainPredicate> refersToPredicates) {
        this.refersToPredicates = refersToPredicates;
    }

    public void addRefersToPredicate(DomainPredicate domainPredicate) {
        this.refersToPredicates.add(domainPredicate);
    }

    public void removeRefersToPredicate(DomainPredicate domainPredicate) {
        this.refersToPredicates.remove(domainPredicate);
    }

    public Predicate getInverse() {
        // TODO -fix this
        throw new UnsupportedOperationException("Method getInverse() is not supported for "
                + this.getClass().getName());
    }

    public List<Predicate> getLeafPredicates() {
        List<Predicate> leafPredicates = new ArrayList<Predicate>();
        if (this.predicate instanceof NestablePredicate) {
            leafPredicates.addAll(((NestablePredicate) this.predicate).getLeafPredicates());
        } else {
            leafPredicates.add(this.predicate);
        }
        return leafPredicates;
    }


	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}
	public String getSystemDefinedConditionName() {
		return systemDefinedConditionName;
	}

	public void setSystemDefinedConditionName(String systemDefinedConditionName) {
		this.systemDefinedConditionName = systemDefinedConditionName;
	}

	public boolean isSystemDefinedCondition(){
		return (StringUtils.hasText(systemDefinedConditionName));
	}

    private Predicate getPredicateFromXML() {
        return (Predicate) new XStreamRuleSerializer().getSerializer().fromXML(getPredicateAsXML());
    }
}
