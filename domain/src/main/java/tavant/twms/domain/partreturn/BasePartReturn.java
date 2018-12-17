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
package tavant.twms.domain.partreturn;

import java.sql.Types;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.security.AuditableColumns;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.CalendarInterval;
import com.domainlanguage.timeutil.Clock;

/**
 * A base class for capturing the concept of a Part Return I want minimal
 * regressions as possible....so using concepts like mapped super class
 * @author kannan.ekanath
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class BasePartReturn implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "BasePartReturn")
	@GenericGenerator(name = "BasePartReturn", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "BASE_PART_RETURN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location returnLocation;

    @ManyToOne
    private ServiceProvider returnedBy;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate dueDate;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.partreturn.PartReturnStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private PartReturnStatus status = (this instanceof SupplierPartReturn ? PartReturnStatus.SUP_PART_RETURN_NOT_INITIATED : PartReturnStatus.PART_TO_BE_SHIPPED);

    private int dueDays;

    @Transient
    private boolean dueDaysReadOnly = true;

    @Transient
    private boolean isDueDateUpdated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private OEMPartReplaced oemPartReplaced;

    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentCondition paymentCondition;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Column(name="RMA_NUMBER")
    private String rmaNumber;
    
    @Transient
    protected RecoveryClaimService recoveryClaimService;

    @Required
    public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}
    
    public RecoveryClaimService getRecoveryClaimService() {
		return recoveryClaimService;
	}

	@Transient
    private ConfigParamService configParamService;

    public ConfigParamService getConfigParamService() {
		return configParamService;
	}

    @Required
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public CalendarDate getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(CalendarDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getDueDays() {
        if (this.dueDate == null)
            return this.dueDays;
        else if(this.dueDate!=null && Clock.today().isAfter(this.dueDate))
            return this.dueDays;
        if(configParamService.getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())) {
             if(this.getStatus() != null && this.getStatus().ordinal()>=PartReturnStatus.WPRA_GENERATED.ordinal()){
                       return Math.abs(CalendarInterval.inclusive(Clock.today(), this.dueDate).lengthInDaysInt() - 1);
                }
                else{
                    return this.dueDays;
                }
        }else{
            return Math.abs(CalendarInterval.inclusive(Clock.today(), this.dueDate).lengthInDaysInt() - 1);
        }

    }


	public void setDueDays(int dueDays) {
		this.dueDays = dueDays;
		if (this.dueDays >= 0) {
			this.dueDate = Clock.today().plusDays(this.dueDays);
		}
	}

	public int getActualDueDays() {
		return this.dueDays;
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

    public Location getReturnLocation() {
        return this.returnLocation;
    }

    public void setReturnLocation(Location returnLocation) {
        this.returnLocation = returnLocation;
    }

    public PartReturnStatus getStatus() {
        return this.status;
    }

    public void setStatus(PartReturnStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("due date ", this.dueDate)
                .append("return Location", this.returnLocation).toString();
    }

    public OEMPartReplaced getOemPartReplaced() {
        return this.oemPartReplaced;
    }

    public void setOemPartReplaced(OEMPartReplaced oemPartReplaced) {
        this.oemPartReplaced = oemPartReplaced;
    }

    public ServiceProvider getReturnedBy() {
        return this.returnedBy;
    }

    public void setReturnedBy(ServiceProvider returnedBy) {
        this.returnedBy = returnedBy;
    }

    public boolean isReturnDetailsEditable() {
        // Fields like return location, payment condition etc, wont be editable
        // if the part is already shipped
        return PartReturnStatus.PART_TO_BE_SHIPPED.equals(getStatus());
    }

    /**
     * @return the paymentCondition
     */
    public PaymentCondition getPaymentCondition() {
        return this.paymentCondition;
    }

    /**
     * @param paymentCondition the paymentCondition to set
     */
    public void setPaymentCondition(PaymentCondition paymentCondition) {
        this.paymentCondition = paymentCondition;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public boolean isDueDateUpdated() {
		return isDueDateUpdated;
	}

	public void setDueDateUpdated(boolean isDueDateUpdated) {
		this.isDueDateUpdated = isDueDateUpdated;
	}

	public boolean isDueDaysReadOnly() {
		return dueDaysReadOnly;
	}

	public void setDueDaysReadOnly(boolean isDueDaysReadOnly) {
		this.dueDaysReadOnly = isDueDaysReadOnly;
	}

	public String getRmaNumber() {
		return rmaNumber;
	}

	public void setRmaNumber(String rmaNumber) {
		this.rmaNumber = rmaNumber;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganizationAddress dealerPickupLocation;

    public OrganizationAddress getDealerPickupLocation() {
        return dealerPickupLocation;
    }

    public void setDealerPickupLocation(OrganizationAddress dealerPickupLocation) {
        this.dealerPickupLocation = dealerPickupLocation;
    }
}
