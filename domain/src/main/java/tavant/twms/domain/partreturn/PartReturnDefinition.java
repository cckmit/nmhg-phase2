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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

/**
 * @author vineeth.varghese
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class PartReturnDefinition implements BusinessUnitAware,AuditableColumns {


    @Id
    @GeneratedValue(generator = "PartReturnDefinition")
	@GenericGenerator(name = "PartReturnDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_RETURN_DEFINITION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "relevanceScore", column = @Column(name = "relevance_score")),
            @AttributeOverride(name = "warrantyType", column = @Column(name = "warranty_type")),
            @AttributeOverride(name = "claimType", column = @Column(name = "claim_type")),
            @AttributeOverride(name = "productName", column = @Column(name = "product_name")),
            @AttributeOverride(name = "identifier", column = @Column(name = "identifier")),
            @AttributeOverride(name = "wntyTypeName", column = @Column(name = "wnty_type_name")),
            @AttributeOverride(name = "clmTypeName", column = @Column(name = "clm_type_name"))})
    private Criteria forCriteria = new Criteria();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "part_ret_dealer_exclusions", joinColumns = { @JoinColumn(name = "part_return_definition") }, inverseJoinColumns = { @JoinColumn(name = "dealer") })
	private Set<ServiceProvider> excludedDealers = new HashSet<ServiceProvider>();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "part_ret_dealer_grp_exclusions", joinColumns = { @JoinColumn(name = "part_return_definition") }, inverseJoinColumns = { @JoinColumn(name = "dealer_group") })
	private Set<DealerGroup> excludedDealerGroups = new HashSet<DealerGroup>();

    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name = "itemIdentifier", column = @Column(name = "item_Identifier"))})
    private ItemCriterion itemCriterion = new ItemCriterion();

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @JoinColumn(name = "partReturnDefinition", nullable = false)
    @OrderBy(clause = "from_date asc")
    private List<PartReturnConfiguration> configurations = new ArrayList<PartReturnConfiguration>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Column(length = 4000)
    private String shippingInstructions;

    @Column(length = 4000)
    private String receiverInstructions;
    
    @OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
        org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @IndexColumn(name = "list_index", nullable = false)
    @JoinColumn(name="for_definition")
    private List<PartReturnDefinitionAudit> partReturnDefinitionAudits = new ArrayList<PartReturnDefinitionAudit>();
    
    @Column(length = 4000)
    private String comments;
    private String status;
    
    public PartReturnConfiguration findConfigurationFor(CalendarDate date, boolean isCausalPart) {
        for (PartReturnConfiguration configuration : this.configurations) {
            if (configuration.isValidFor(date, isCausalPart)) {
                return configuration;
            }
        }
        return null;
    }

    // Need some validation logic here
    public void addPartReturnConfiguration(PartReturnConfiguration partReturnConfiguration) {
        this.configurations.add(partReturnConfiguration);
    }

    // Only getters and setters follow.

    public Criteria getForCriteria() {
        return this.forCriteria;
    }

    public void setForCriteria(Criteria forCriteria) {
        this.forCriteria = forCriteria;
    }

    public List<PartReturnConfiguration> getConfigurations() {
        return this.configurations;
    }

    public void setConfigurations(List<PartReturnConfiguration> configurations) {
        this.configurations = configurations;
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

    public ItemCriterion getItemCriterion() {
        return this.itemCriterion;
    }

    public void setItemCriterion(ItemCriterion itemCriterion) {
        this.itemCriterion = itemCriterion;
    }

    public Criteria getCriteria() {
        return this.forCriteria;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public String getShippingInstructions() {
		return shippingInstructions;
	}

	public void setShippingInstructions(String shippingInstructions) {
		this.shippingInstructions = shippingInstructions;
	}

	public String getReceiverInstructions() {
		return receiverInstructions;
	}

	public void setReceiverInstructions(String receiverInstructions) {
		this.receiverInstructions = receiverInstructions;
	}

	public List<PartReturnDefinitionAudit> getPartReturnDefinitionAudits() {
		return partReturnDefinitionAudits;
	}

	public void setPartReturnDefinitionAudits(
			List<PartReturnDefinitionAudit> partReturnDefinitionAudits) {
		this.partReturnDefinitionAudits = partReturnDefinitionAudits;
	}
	
	public void addPartReturnDefinitionAudit(PartReturnDefinitionAudit partReturnDefinitionAudit){
		this.partReturnDefinitionAudits.add(partReturnDefinitionAudit);
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Set<ServiceProvider> getExcludedDealers() {
		return excludedDealers;
	}

	public void setExcludedDealers(Set<ServiceProvider> excludedDealers) {
		this.excludedDealers = excludedDealers;
	}

	public Set<DealerGroup> getExcludedDealerGroups() {
		return excludedDealerGroups;
	}

	public void setExcludedDealerGroups(Set<DealerGroup> excludedDealerGroups) {
		this.excludedDealerGroups = excludedDealerGroups;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

