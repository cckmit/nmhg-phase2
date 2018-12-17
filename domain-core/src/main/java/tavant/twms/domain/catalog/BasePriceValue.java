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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import javax.validation.constraints.NotNull;
import org.springframework.util.Assert;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.TimeBoundValue;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@SuppressWarnings("unused")
public class BasePriceValue extends TimeBoundValue<BigDecimal> {
    @Id
    @GeneratedValue(generator = "BasePriceValue")
	@GenericGenerator(name = "BasePriceValue", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "BASE_PRICE_VALUE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @NotNull
    @Column(columnDefinition = "numeric(19,2)")
    // @Check(name="non_negative_base_price",constraints="price >= 0")
    private BigDecimal price;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ItemBasePrice parent;

    public BasePriceValue() {
    }

    public BasePriceValue(BigDecimal value, CalendarDuration forDuration) {
        super(forDuration);
        Assert.notNull(value, "base price cannot be null");
        this.price = value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public BigDecimal getValue() {
        return this.price;
    }

    @Override
    public void setValue(BigDecimal newValue) {
        this.price = newValue;
    }

    @Override
    public void setParent(Object parent) {
        this.parent = (ItemBasePrice) parent;
    }

}
