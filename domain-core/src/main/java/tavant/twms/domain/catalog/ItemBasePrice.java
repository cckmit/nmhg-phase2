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
package tavant.twms.domain.catalog;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.TimeBoundValues;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "for_item" }))
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class ItemBasePrice extends TimeBoundValues<BigDecimal, BasePriceValue> implements BusinessUnitAware{
    @Id
    @GeneratedValue(generator = "ItemBasePrice")
	@GenericGenerator(name = "ItemBasePrice", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ITEM_BASE_PRICE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Item forItem;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Sort(type = SortType.NATURAL)
    @Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    private SortedSet<BasePriceValue> timeBoundPriceValues = new TreeSet<BasePriceValue>();

    /**
     * @return the forItem
     */
    public Item getForItem() {
        return this.forItem;
    }

    /**
     * @param forItem the forItem to set
     */
    public void setForItem(Item forItem) {
        this.forItem = forItem;
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
     * @return the timeBoundPriceValues
     */
    public SortedSet<BasePriceValue> getTimeBoundPriceValues() {
        return this.timeBoundPriceValues;
    }

    /**
     * @param timeBoundPriceValues the timeBoundPriceValues to set
     */
    public void setTimeBoundPriceValues(SortedSet<BasePriceValue> timeBoundPriceValues) {
        this.timeBoundPriceValues = timeBoundPriceValues;
    }

    @Override
    public SortedSet<BasePriceValue> getEntries() {
        return this.timeBoundPriceValues;
    }

    @Override
    public BasePriceValue newTimeBoundValue(BigDecimal value, CalendarDuration forDuration) {
        return new BasePriceValue(value, forDuration);
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

	@Override
	public BasePriceValue newTimeBoundValueModifier(BigDecimal value,
			CalendarDuration forDuration, Boolean isFlatRate) {
		// TODO Auto-generated method stub
		return null;
	}
}
