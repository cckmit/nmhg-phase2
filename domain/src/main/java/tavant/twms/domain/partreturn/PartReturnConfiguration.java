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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.domainlanguage.time.CalendarInterval;
import com.domainlanguage.timeutil.Clock;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.TimeBoundObject;
import tavant.twms.domain.orgmodel.ServiceProvider;

import com.domainlanguage.time.CalendarDate;

/**
 * @author Kiran.Kollipara
 */
@Entity
@AttributeOverrides( {
        @AttributeOverride(name = "duration.fromDate", column = @Column(name = "from_date", nullable = false)),
        @AttributeOverride(name = "duration.tillDate", column = @Column(name = "till_date", nullable = false)) })
public class PartReturnConfiguration extends TimeBoundObject {

    @Id
    @GeneratedValue(generator = "PartReturnConfiguration")
	@GenericGenerator(name = "PartReturnConfiguration", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_RETURN_CONFIGURATION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private boolean causalPart;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Location returnLocation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PaymentCondition paymentCondition;

    @Column(nullable = false)
    private Integer dueDays;
    
    private Integer maxQuantity;
    
    private Integer quantityReceived=0;

    private String rmaNumber;

    public String getRmaNumber() {
        return rmaNumber;
    }

    public void setRmaNumber(String rmaNumber) {
        this.rmaNumber = rmaNumber;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partReturnDefinition", nullable = false, insertable=false, updatable=false)
	private PartReturnDefinition partReturnDefinition;

	public PartReturnDefinition getPartReturnDefinition() {
		return partReturnDefinition;
	}

	public void setPartReturnDefinition(PartReturnDefinition partReturnDefinition) {
		this.partReturnDefinition = partReturnDefinition;
	}

	boolean isValidFor(CalendarDate date, boolean isCausalPart) {
        boolean isMatch = false;
        if (this.isValidForDate(date)) {
            isMatch = this.causalPart ? (this.causalPart == isCausalPart) : true;
        }
        return isMatch;
    }

    PartReturn createPartReturn(CalendarDate partReturnTriggerDate, CalendarDate billedDate) {
        PartReturn partReturn = new PartReturn();
        partReturn.setDueDate(partReturnTriggerDate.plusDays(this.dueDays));
        partReturn.setDueDays(this.dueDays);
        partReturn.setPaymentCondition(this.paymentCondition);
        partReturn.setReturnLocation(this.returnLocation);
        return partReturn;
    }

    PartReturn createPartReturn(CalendarDate partReturnTriggerDate, CalendarDate billedDate,
            ServiceProvider returnedBy) {
        PartReturn partReturn = createPartReturn(partReturnTriggerDate, billedDate);        
        partReturn.setReturnedBy(returnedBy);        
        return partReturn;
    }

    public boolean isCausalPart() {
        return this.causalPart;
    }

    public void setCausalPart(boolean causalPart) {
        this.causalPart = causalPart;
    }

    public Integer getDueDays() {
        return this.dueDays;
    }

    public void setDueDays(Integer dueDays) {
        this.dueDays = dueDays;
    }

    public PaymentCondition getPaymentCondition() {
        return this.paymentCondition;
    }

    public void setPaymentCondition(PaymentCondition paymentCondition) {
        this.paymentCondition = paymentCondition;
    }

    public Location getReturnLocation() {
        return this.returnLocation;
    }

    public void setReturnLocation(Location returnLocation) {
        this.returnLocation = returnLocation;
    }

    public Long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Integer getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(Integer maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public Integer getQuantityReceived() {
		if (quantityReceived == null) {
			return 0;
		}
		return quantityReceived;
	}

	public void setQuantityReceived(Integer quantityReceived) {
		if (quantityReceived == null) {
			this.quantityReceived = 0;
		}
		this.quantityReceived = quantityReceived;
	}
}