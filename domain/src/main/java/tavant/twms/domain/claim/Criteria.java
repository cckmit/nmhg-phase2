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

import java.sql.Types;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import javax.validation.constraints.NotNull;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.ServiceProvider;

@MappedSuperclass
@Embeddable
public class Criteria {
    private String warrantyType;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.claim.ClaimType"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private ClaimType claimType;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGroup productType;

    @Embedded
    @AssociationOverrides( {
            @AssociationOverride(name = "dealer", joinColumns = @JoinColumn(name = "dealer")),
            @AssociationOverride(name = "dealerGroup", joinColumns = @JoinColumn(name = "dealer_group")) })
    private DealerCriterion dealerCriterion;

    @SuppressWarnings("unused")
    @NotNull
    private long relevanceScore;

    private String identifier;

    private String productName;

    private String wntyTypeName;

    private String clmTypeName;

    public ClaimType getClaimType() {
        return this.claimType;
    }

    public void setClaimType(String claimType) {
        if (StringUtils.hasText(claimType)) {
            this.claimType = ClaimType.typeFor(claimType);
        } else {
            this.claimType = null;
        }
    }

    public void setClaimType(ClaimType claimType) {
        this.claimType = claimType;
    }

    @Deprecated
    public ServiceProvider getDealer() {
        if (this.dealerCriterion != null) {
            return this.dealerCriterion.getDealer();
        } else {
            return null;
        }
    }

    @Deprecated
    public void setDealer(ServiceProvider dealer) {
        this.dealerCriterion = new DealerCriterion(dealer);
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    public ItemGroup getProductType() {
        return this.productType;
    }

    public void setProductType(ItemGroup productType) {
        this.productType = productType;
    }

    public String getWarrantyType() {
        return this.warrantyType;
    }

    public void setWarrantyType(String warrantyType) {
        if (StringUtils.hasText(warrantyType)) {
            this.warrantyType = warrantyType;
        } else {
            this.warrantyType = null;
        }
    }

    /**
     * @param relevanceScore the relevanceScore to set
     */
    public void setRelevanceScore(long relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("claim type", this.claimType).append(
                "warranty type", this.warrantyType).append("dealer or dealerGroup ",
                this.dealerCriterion).append("product type ", this.productType).toString();
    }

    // WIP

    public DealerCriterion getDealerCriterion() {
        return this.dealerCriterion;
    }

    public void setDealerCriterion(DealerCriterion dealerCriterion) {
        this.dealerCriterion = dealerCriterion;
    }

    /**
     * @return the relevanceScore
     */
    public long getRelevanceScore() {
        return this.relevanceScore;
    }

    public boolean isProductTypeSpecified() {
        return this.productType != null;
    }

    public boolean isClaimTypeSpecified() {
        return this.claimType != null;
    }

    public boolean isWarrantyTypeSpecified() {
        return StringUtils.hasText(this.warrantyType);
    }

    public boolean isDealerGroupSpecified() {
        return this.dealerCriterion != null && this.dealerCriterion.isGroupCriterion();
    }

    public boolean isDealerSpecified() {
        return this.dealerCriterion != null && !this.dealerCriterion.isGroupCriterion();
    }

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getWntyTypeName() {
		return wntyTypeName;
	}

	public void setWntyTypeName(String wntyTypeName) {
		this.wntyTypeName = wntyTypeName;
	}

	public String getClmTypeName() {
		if(clmTypeName.equalsIgnoreCase("Machine")){
    		return "Unit";
    	} else {
            return clmTypeName;
    	}
	}

	public void setClmTypeName(String clmTypeName) {
		this.clmTypeName = clmTypeName;
	}
}
