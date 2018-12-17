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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;
import org.springframework.util.Assert;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.TimeBoundValue;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@SuppressWarnings("unused")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class ItemPriceModifier extends TimeBoundValue<BigDecimal> implements
BusinessUnitAware{
    @Id
    @GeneratedValue(generator = "ItemPriceModifier")
	@GenericGenerator(name = "ItemPriceModifier", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ITEM_MODIFIER_PRICE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Column(columnDefinition = "decimal(19,2)")
    private BigDecimal scalingFactor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AdministeredItemPrice parent;

    public ItemPriceModifier() {
    }

    public ItemPriceModifier(BigDecimal percentageChange, CalendarDuration aDuration) {
        super(aDuration);
        Assert.notNull(percentageChange, "percentage cannot be null");
        this.scalingFactor = percentageChange;
    }

    @Override
    public BigDecimal getValue() {
        return this.scalingFactor;
    }

    @Override
    public void setValue(BigDecimal newValue) {
        this.scalingFactor = newValue;
    }

    @Override
    public void setParent(Object parent) {
        this.parent = (AdministeredItemPrice) parent;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the scalingFactor
     */
    public BigDecimal getScalingFactor() {
        return this.scalingFactor;
    }

    /**
     * @param scalingFactor the scalingFactor to set
     */
    public void setScalingFactor(BigDecimal scalingFactor) {
        this.scalingFactor = scalingFactor;
    }
    
    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}
}
