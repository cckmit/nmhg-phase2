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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.TimeBoundValue;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters({
	  @Filter(name="excludeInactive")
	})
public class LaborRate extends TimeBoundValue<Money> {
    @Id
    @GeneratedValue(generator = "LaborRate")
	@GenericGenerator(name = "LaborRate", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "LABOR_RATE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;
    
    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "LABOR_RATE", nullable = false)
	private List<LaborRateValues> laborRateValues = new ArrayList<LaborRateValues>();
    
    @ManyToOne
    private LaborRates laborRates;
    
    public LaborRate() {
        super();
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

    public List<LaborRateValues> getLaborRateValues() {
		return laborRateValues;
	}

	public void setLaborRateValues(List<LaborRateValues> laborRateValues) {
		this.laborRateValues = laborRateValues;
	}

	@Override
	public Money getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParent(Object parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Money newValue) {
		// TODO Auto-generated method stub
		
	}

	public LaborRates getLaborRates() {
		return laborRates;
	}

	public void setLaborRates(LaborRates laborRates) {
		this.laborRates = laborRates;
	}

}