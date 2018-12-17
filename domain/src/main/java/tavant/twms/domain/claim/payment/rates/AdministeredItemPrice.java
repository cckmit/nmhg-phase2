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
package tavant.twms.domain.claim.payment.rates;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.TimeBoundValues;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@Table(name="item_price_criteria")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class AdministeredItemPrice extends TimeBoundValues<BigDecimal,ItemPriceModifier>  implements BusinessUnitAware{
	@Id
	@GeneratedValue(generator = "AdminItemPrice")
	@GenericGenerator(name = "AdminItemPrice", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ITEM_PRICECRITERIA_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name = "itemIdentifier", column = @Column(name = "item_Identifier"))})
    private ItemCriterion itemCriterion = new ItemCriterion();
    
    @Embedded
    @AttributeOverrides({
    	@AttributeOverride(name="relevanceScore",column=@Column(name="relevance_score")),
    	@AttributeOverride(name="warrantyType",column=@Column(name="warranty_type")),
    	@AttributeOverride(name="claimType",column=@Column(name="claim_type")),
    	@AttributeOverride(name = "productName", column = @Column(name = "product_name")),
        @AttributeOverride(name = "identifier", column = @Column(name = "identifier")),
        @AttributeOverride(name = "wntyTypeName", column = @Column(name = "wnty_type_name")),
        @AttributeOverride(name = "clmTypeName", column = @Column(name = "clm_type_name"))}) 
    private Criteria forCriteria = new Criteria();
    
    @OneToMany(fetch=FetchType.LAZY,mappedBy="parent")
    @Sort(type=SortType.NATURAL)
    @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    private SortedSet<ItemPriceModifier> priceModifiers = new TreeSet<ItemPriceModifier>();
    
    @Override
    public SortedSet<ItemPriceModifier> getEntries() {
        return priceModifiers;
    }

    @Override
    public ItemPriceModifier newTimeBoundValue(BigDecimal value, CalendarDuration forDuration) {
        return new ItemPriceModifier(value,forDuration);
    }

    public Criteria getForCriteria() {
        return forCriteria;
    }

    public void setForCriteria(Criteria forCriteria) {
        this.forCriteria = forCriteria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Criteria getCriteria() {
        return forCriteria;
    }

	/**
	 * @return the priceModifiers
	 */
	public SortedSet<ItemPriceModifier> getPriceModifiers() {
		return priceModifiers;
	}

	/**
	 * @param priceModifiers the priceModifiers to set
	 */
	public void setPriceModifiers(SortedSet<ItemPriceModifier> priceModifiers) {
		this.priceModifiers = priceModifiers;
	}

	/**
	 * @return the itemCriterion
	 */
	public ItemCriterion getItemCriterion() {
		return itemCriterion;
	}

	/**
	 * @param itemCriterion the itemCriterion to set
	 */
	public void setItemCriterion(ItemCriterion itemCriterion) {
		this.itemCriterion = itemCriterion;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public void computeCriteriaRelevanceScore() {
		int score = 0;
		int weight = 1;
		
		if( forCriteria.isProductTypeSpecified() ) {
			score = score + weight;
		}
		
		weight = weight*2;
		if( forCriteria.isWarrantyTypeSpecified() ) {
			score = score + weight;
		}
		
		weight = weight*2;
		if( forCriteria.isClaimTypeSpecified() ) {
			score = score + weight;
		}

		weight = weight*2;
		if (forCriteria.isDealerGroupSpecified()) {
			score = score + weight;
		}		
		
		weight = weight*2;
		if( forCriteria.isDealerSpecified() ) {
			score = score + weight;
		}
		
		forCriteria.setRelevanceScore(score);
	}

	@Override
	public ItemPriceModifier newTimeBoundValueModifier(BigDecimal value,
			CalendarDuration forDuration, Boolean isFlatRate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
