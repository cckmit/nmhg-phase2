package tavant.twms.domain.claim;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.policy.ApplicablePolicy;
import tavant.twms.domain.policy.Policy;
import tavant.twms.security.AuditableColumns;

/**
 * Created by IntelliJ IDEA. User: me Date: 12 Sep, 2007 Time: 11:12:00 PM
 * 
 * @author <a href="mailto:vikas.sasidharan@tavant.com">Vikas Sasidharan</a>
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@AccessType("field")
public class ClaimedItem implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "ClaimedItem")
	@GenericGenerator(name = "ClaimedItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CLAIMED_ITEM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    /**
     * TODO: There is an issue with components if it is null. Refer
     * http://www.hibernate.org/hib_docs/reference/en/html/components.html For
     * now creating an inline item reference.
     */   
    
    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @AttributeOverrides( { @AttributeOverride(name = "serialized", column = @Column(name = "item_ref_szed")) })
    @AssociationOverrides( {
            @AssociationOverride(name = "referredItem", joinColumns = @JoinColumn(name = "item_ref_item")),
            @AssociationOverride(name = "referredInventoryItem", joinColumns = @JoinColumn(name = "item_ref_inv_item")),
            @AssociationOverride(name = "unserializedItem", joinColumns = @JoinColumn(name = "item_ref_unszed_item")),
            @AssociationOverride(name = "model", joinColumns = @JoinColumn(name = "model_ref_for_unszed")) })
    protected ItemReference itemReference = new ItemReference();

    @Column
    private BigDecimal hoursInService;

    @Column
    private boolean processorApproved = true;

    @OneToOne(fetch = FetchType.LAZY, targetEntity = ApplicablePolicy.class)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private Policy applicablePolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Claim claim;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<ClaimAttributes> claimAttributes = new ArrayList<ClaimAttributes>();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    private String vinNumber;
    
   
	public ClaimedItem(ItemReference itemReference) {
        this.itemReference = itemReference;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemReference getItemReference() {
        return this.itemReference;
    }

    public void setItemReference(ItemReference itemReference) {
        this.itemReference = itemReference;
    }

    public boolean isProcessorApproved() {
        return this.processorApproved;
    }

    public void setProcessorApproved(boolean processorApproved) {
        this.processorApproved = processorApproved;
    }

    public Policy getApplicablePolicy() {
        return this.applicablePolicy;
    }

    public void setApplicablePolicy(Policy applicablePolicy) {
        this.applicablePolicy = applicablePolicy;
    }

    public Claim getClaim() {
        return this.claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public ClaimedItem() {
        // For fwks.
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("itemReference",
                this.itemReference).append("hoursInService", this.hoursInService).append(
                "processorApproved", this.processorApproved).append("applicablePolicy",
                this.applicablePolicy).toString();
    }

    public List<ClaimAttributes> getClaimAttributes() {
        return claimAttributes;
    }

    public void setClaimAttributes(List<ClaimAttributes> claimAttributes) {
        this.claimAttributes = claimAttributes;
    }

    public void addClaimAttributes(ClaimAttributes claimAttributes) {
        this.claimAttributes.add(claimAttributes);
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public BigDecimal getHoursInService() {
        return hoursInService;
    }

    public void setHoursInService(BigDecimal hoursInService) {
        this.hoursInService = hoursInService;
    }
    
    public String getVinNumber() {
    	return this.vinNumber;
	}

	public void setVinNumber(String vinNumber) {
		this.vinNumber = vinNumber;
	}

}
