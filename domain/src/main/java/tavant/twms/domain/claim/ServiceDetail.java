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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.domain.laborType.LaborSplit;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.base.Ratio;
import com.domainlanguage.money.Money;

@Entity
@Filters({
        @Filter(name = "excludeInactive")
})
@Table(name = "Service")
@PropertiesWithNestedCurrencyFields({"laborPerformed", "oEMPartsReplaced", "nonOEMPartsReplaced",
        "travelDetails", "miscPartsReplaced"})
public class ServiceDetail implements AuditableColumns {
    private static final Logger logger = Logger.getLogger(ServiceDetail.class);

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<LaborDetail> laborPerformed = new ArrayList<LaborDetail>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "service_oemparts_replaced", inverseJoinColumns = {@JoinColumn(name = "oemparts_replaced")})
    private List<OEMPartReplaced> oemPartsReplaced = new ArrayList<OEMPartReplaced>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_Detail")
    private List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = new ArrayList<HussmanPartsReplacedInstalled>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<NonOEMPartReplaced> nonOEMPartsReplaced = new ArrayList<NonOEMPartReplaced>();

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<NonOEMPartReplaced> miscPartsReplaced = new ArrayList<NonOEMPartReplaced>();


    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "service_Detail", nullable = false)
    @IndexColumn(name = "list_index", nullable = false)
    private List<LaborSplit> laborSplit = new ArrayList<LaborSplit>();

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private TravelDetail travelDetails;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "freight_duty_amt"), @Column(name = "freight_duty_curr")})
    private Money itemFreightAndDuty;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "meals_amt"), @Column(name = "meals_curr")})
    private Money mealsExpense;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "parking_and_toll_expense_amt"),
            @Column(name = "parking_and_toll_expense_curr")})
    private Money parkingAndTollExpense;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "per_diem_amt"), @Column(name = "per_diem_curr")})
    private Money perDiem;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "rental_Charges_amt"), @Column(name = "rental_Charges_curr")})
    private Money rentalCharges;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "local_purchase_amt"), @Column(name = "local_purchase_curr")})
    private Money localPurchaseExpense;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "tolls_amt"), @Column(name = "tolls_curr")})
    private Money tollsExpense;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "other_freight_duty_amt"), @Column(name = "other_freight_duty_curr")})
    private Money otherFreightDutyExpense;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "handling_fee"), @Column(name = "handling_fee_curr")})
    private Money handlingFee;
    
	@Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "others_amt"), @Column(name = "others_curr")})
    private Money othersExpense;
	
	@Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "TRANSPORTATION_AMT"), @Column(name = "TRANSPORTATION_CURR")})
    private Money transportationAmt ;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private User technician;
    
    private String serviceTechnician; //technician field for AMER, as it should be a free text, making a new field

	@Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    private Boolean stdLaborEnabled = Boolean.TRUE;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FREIGHT_DUTY_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document freightDutyInvoice; 
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEALS_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document mealsInvoice;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HANDLING_FEE_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document handlingFeeInvoice;
    
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PER_DIEM_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document perDiemInvoice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARKING_AND_TOLL_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document parkingAndTollInvoice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RENTAL_CHARGES_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document rentalChargesInvoice;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCAL_PURCHASE_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document localPurchaseInvoice;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOLLS_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document tollsInvoice;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OTHER_FREIGHT_DUTY_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document otherFreightDutyInvoice;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OTHERS_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document othersInvoice;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSPORTATION_INVOICE", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private Document transportationInvoice;
    
    private Boolean invoiceAvailable = Boolean.FALSE;
    
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

    /**
     * @return the OEMPartsReplaced
     */
    public List<OEMPartReplaced> getOEMPartsReplaced() {
        return this.oemPartsReplaced;
    }

    public void addOEMPartReplaced(OEMPartReplaced oEMPartReplaced) {
        this.oemPartsReplaced.add(oEMPartReplaced);
    }

    /**
     * @param oemPartsReplaced the OEMPartsReplaced to set
     */
    public void setOEMPartsReplaced(List<OEMPartReplaced> partsReplaced) {
        this.oemPartsReplaced = partsReplaced;
    }


    public List<NonOEMPartReplaced> getMiscPartsReplaced() {
        return miscPartsReplaced;
    }

    public void setMiscPartsReplaced(List<NonOEMPartReplaced> miscPartsReplaced) {
        this.miscPartsReplaced = miscPartsReplaced;
    }

    public void addMiscPartsReplaced(NonOEMPartReplaced miscPartReplaced) {
        this.miscPartsReplaced.add(miscPartReplaced);
    }

    /**
     * @return the nonOEMPartsReplaced
     */
    public List<NonOEMPartReplaced> getNonOEMPartsReplaced() {
        return this.nonOEMPartsReplaced;
    }

    public void addNonOEMPartReplaced(NonOEMPartReplaced nonOEMPartReplaced) {
        this.nonOEMPartsReplaced.add(nonOEMPartReplaced);
    }

    /**
     * @param nonOEMPartsReplaced the nonOEMPartsReplaced to set
     */
    public void setNonOEMPartsReplaced(List<NonOEMPartReplaced> nonOEMPartsReplaced) {
        this.nonOEMPartsReplaced = nonOEMPartsReplaced;
    }

    /**
     * @return the laborPerformed
     */
    public List<LaborDetail> getLaborPerformed() {
        return this.laborPerformed;
    }

    public void addLaborDetail(LaborDetail laborDetail) {
        this.laborPerformed.add(laborDetail);
    }

    /**
     * @param laborPerformed the laborPerformed to set
     */
    public void setLaborPerformed(List<LaborDetail> laborPerformed) {
        this.laborPerformed = laborPerformed;
    }

    /**
     * @return the travelDetails
     */
    public TravelDetail getTravelDetails() {
        return this.travelDetails;
    }

    /**
     * @param travelDetails the travelDetails to set
     */
    public void setTravelDetails(TravelDetail travelDetails) {
        this.travelDetails = travelDetails;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).toString();
    }

    public Money getItemFreightAndDuty() {
        return this.itemFreightAndDuty;
    }

    public void setItemFreightAndDuty(Money itemFreightAndDuty) {
        this.itemFreightAndDuty = itemFreightAndDuty;
    }

    public Money getMealsExpense() {
        return this.mealsExpense;
    }

    public void setMealsExpense(Money mealsExpense) {
        this.mealsExpense = mealsExpense;
    }

    public Money getParkingAndTollExpense() {
        return this.parkingAndTollExpense;
    }

    public void setParkingAndTollExpense(Money parkingAndTollExpense) {
        this.parkingAndTollExpense = parkingAndTollExpense;
    }

    public Money getLocalPurchaseExpense() {
        return this.localPurchaseExpense;
    }

    public void setLocalPurchaseExpense(Money localPurchaseExpense) {
        this.localPurchaseExpense = localPurchaseExpense;
    }

    public Money getTollsExpense() {
        return this.tollsExpense;
    }

    public void setTollsExpense(Money tollsExpense) {
        this.tollsExpense = tollsExpense;
    }

    public Money getOtherFreightDutyExpense() {
        return this.otherFreightDutyExpense;
    }

    public void setOtherFreightDutyExpense(Money otherFreightDutyExpense) {
        this.otherFreightDutyExpense = otherFreightDutyExpense;
    }

    public Money getOthersExpense() {
        return this.othersExpense;
    }

    public void setOthersExpense(Money othersExpense) {
        this.othersExpense = othersExpense;
    }
    
    public Document getHandlingFeeInvoice() {
		return handlingFeeInvoice;
	}

	public void setHandlingFeeInvoice(Document handlingFeeInvoice) {
		this.handlingFeeInvoice = handlingFeeInvoice;
	}
    @Transient
    /**
     * Job of the caller to ensure that they all parts in right currency etc
     */
    public Money getTotalCostOfParts() {
        List<Money> amounts = new ArrayList<Money>();
        for (OEMPartReplaced part : this.oemPartsReplaced) {
            if (logger.isDebugEnabled()) {
                logger.debug("Adding part [" + part + "] with price [" + part.getPricePerUnit() + "]");
            }
            amounts.add(part.cost());
        }
        Money sum = Money.sum(amounts);
        if (logger.isDebugEnabled()) {
            logger.debug("Sum of money is [" + sum + "]");
        }
        return sum;
    }

    /*
     * This API is not used for club car as a single recovery claim is created
     * for all replaced parts so no pro ration is reqd.
     */
    @Transient
    public Map<OEMPartReplaced, Ratio> getPartRatios(List<OEMPartReplaced> partsToBeProrated,
                                                     Contract contract) {
        Money totalCost = this.getTotalCostOfParts();
        Map<OEMPartReplaced, Ratio> partRatios = new HashMap<OEMPartReplaced, Ratio>();
        for (OEMPartReplaced part : partsToBeProrated) {
            if (part.isPartRecoveredFromSupplier(contract, null, null)) {
                Ratio ratio = part.cost().dividedBy(totalCost);
                if (logger.isDebugEnabled()) {
                    logger.debug("Ratio for [" + part + "] is [" + ratio + "}");
                }
                /**
                 * We need to add cost line items in these proportions
                 */
                partRatios.put(part, ratio);
            }
        }
        return partRatios;
    }

    public List<OEMPartReplaced> getOemPartsReplaced() {
        return this.oemPartsReplaced;
    }

    public void setOemPartsReplaced(List<OEMPartReplaced> oemPartsReplaced) {
        this.oemPartsReplaced = oemPartsReplaced;
    }

    public User getTechnician() {
        return this.technician;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public List<HussmanPartsReplacedInstalled> getHussmanPartsReplacedInstalled() {
        return hussmanPartsReplacedInstalled;

    }

    public void setHussmanPartsReplacedInstalled(
            List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled) {
        this.hussmanPartsReplacedInstalled = hussmanPartsReplacedInstalled;
    }

    public void addHussmanPartsReplacedInstalled(
            HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled) {
        this.hussmanPartsReplacedInstalled.add(hussmanPartsReplacedInstalled);
    }

    public List<LaborSplit> getLaborSplit() {
        return laborSplit;
    }

    public void setLaborSplit(List<LaborSplit> laborSplit) {
        this.laborSplit = laborSplit;
    }

    public Money getPerDiem() {
        return perDiem;
    }

    public void setPerDiem(Money perDiem) {
        this.perDiem = perDiem;
    }

    public Money getRentalCharges() {
        return rentalCharges;
    }

    public void setRentalCharges(Money rentalCharges) {
        this.rentalCharges = rentalCharges;
    }

    
    public Document getFreightDutyInvoice() {
		return freightDutyInvoice;
	}

	public void setFreightDutyInvoice(Document freightDutyInvoice) {
		this.freightDutyInvoice = freightDutyInvoice;
	}

	public Document getMealsInvoice() {
		return mealsInvoice;
	}

	public void setMealsInvoice(Document mealsInvoice) {
		this.mealsInvoice = mealsInvoice;
	}

	public Document getPerDiemInvoice() {
		return perDiemInvoice;
	}

	public void setPerDiemInvoice(Document perDiemInvoice) {
		this.perDiemInvoice = perDiemInvoice;
	}

	public Document getParkingAndTollInvoice() {
		return parkingAndTollInvoice;
	}

	public void setParkingAndTollInvoice(Document parkingAndTollInvoice) {
		this.parkingAndTollInvoice = parkingAndTollInvoice;
	}

	public Document getRentalChargesInvoice() {
		return rentalChargesInvoice;
	}

	public void setRentalChargesInvoice(Document rentalChargesInvoice) {
		this.rentalChargesInvoice = rentalChargesInvoice;
	}

	public Document getLocalPurchaseInvoice() {
		return localPurchaseInvoice;
	}

	public void setLocalPurchaseInvoice(Document localPurchaseInvoice) {
		this.localPurchaseInvoice = localPurchaseInvoice;
	}

	public Document getTollsInvoice() {
		return tollsInvoice;
	}

	public void setTollsInvoice(Document tollsInvoice) {
		this.tollsInvoice = tollsInvoice;
	}

	public Document getOtherFreightDutyInvoice() {
		return otherFreightDutyInvoice;
	}

	public void setOtherFreightDutyInvoice(Document otherFreightDutyInvoice) {
		this.otherFreightDutyInvoice = otherFreightDutyInvoice;
	}

	public Document getOthersInvoice() {
		return othersInvoice;
	}

	public void setOthersInvoice(Document othersInvoice) {
		this.othersInvoice = othersInvoice;
	}
	
	 public Money getHandlingFee() {
			return handlingFee;
		}

	public void setHandlingFee(Money handlingFee) {
			this.handlingFee = handlingFee;
		}

	public List<OEMPartReplaced> getReplacedParts() {
        List<OEMPartReplaced> oEMPartReplaced = new ArrayList<OEMPartReplaced>();

        if (this.getHussmanPartsReplacedInstalled() != null
                && !this.getHussmanPartsReplacedInstalled().isEmpty()) {
            for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : this
                    .getHussmanPartsReplacedInstalled()) {
                if (hussmanPartsReplacedInstalled != null &&
                        hussmanPartsReplacedInstalled.getReplacedParts() != null
                        && !hussmanPartsReplacedInstalled.getReplacedParts()
                        .isEmpty()) {//To remove the values which are null while removing and adding elements in widget.
                    hussmanPartsReplacedInstalled.getReplacedParts().removeAll(Collections.singleton(null));
                    oEMPartReplaced.addAll(hussmanPartsReplacedInstalled
                            .getReplacedParts());
                }
            }
        } else if (this.getOemPartsReplaced() != null
                && !this.getOemPartsReplaced().isEmpty()) {
            oEMPartReplaced = this.getOemPartsReplaced();
        }
        return oEMPartReplaced;
    }

    /*
      * This if-else condition is eliminating either one of the Club Car Replaced or
      * the Hussmann Replaced Installed Part widget where the price fetch is done only
      * for the Installed parts in case of HUSS PART REP INS and for the replaced parts in case of
      * Club Car Parts.
      */
    public List<PartReplaced> getPriceFetchedParts() {
        List<PartReplaced> partReplacedList = new ArrayList<PartReplaced>();
        List<OEMPartReplaced> partsReplaced = null;
        List<InstalledParts> partsInstalled = null;
        if (this.getOemPartsReplaced() != null && !this.getOemPartsReplaced().isEmpty()) {
            partsReplaced = this.getOemPartsReplaced();
            partReplacedList.addAll(partsReplaced);
        } else if (this.getHussmanPartsReplacedInstalled() != null) {
            for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled :
                    this.getHussmanPartsReplacedInstalled()) {
                if (hussmanPartReplacedInstalled != null &&
                        hussmanPartReplacedInstalled.getHussmanInstalledParts() != null
                        && !hussmanPartReplacedInstalled.getHussmanInstalledParts().isEmpty()) {
                    partsInstalled = hussmanPartReplacedInstalled.getHussmanInstalledParts();
                    partReplacedList.addAll(partsInstalled);
                }
                if (hussmanPartReplacedInstalled != null &&
                        hussmanPartReplacedInstalled.getReplacedParts() != null
                        && !hussmanPartReplacedInstalled.getReplacedParts().isEmpty()) {
                    partsReplaced = hussmanPartReplacedInstalled.getReplacedParts();
                    partReplacedList.addAll(partsReplaced);
                }
            }
        }
        return partReplacedList;
    }


    public List<PartReplaced> getPriceForInstalledParts() {
        List<PartReplaced> partReplacedList = new ArrayList<PartReplaced>();
        partReplacedList.addAll(this.getOemPartsReplaced());
        for (HussmanPartsReplacedInstalled hussmanPartReplacedInstalled :
                this.getHussmanPartsReplacedInstalled()) {
            partReplacedList.addAll(hussmanPartReplacedInstalled.getHussmanInstalledParts());
        }

        return partReplacedList;
    }


    public Boolean isOEMPartReplaced(PartReplaced partReplaced) {
        if (InstanceOfUtil.isInstanceOfClass(OEMPartReplaced.class, partReplaced)) {
            return true;
        } else if (InstanceOfUtil.isInstanceOfClass(InstalledParts.class, partReplaced)) {
            return false;
        }
        return false;
    }

    public List<PartReplaced> getNonOEMPriceFetchParts() {
        List<PartReplaced> nonOEMPartReplacedList = new ArrayList<PartReplaced>();
        List<NonOEMPartReplaced> partsInstalled = new ArrayList<NonOEMPartReplaced>();
        List<InstalledParts> nonHussmannPartsInstalled = new ArrayList<InstalledParts>();
        if (this.getNonOEMPartsReplaced() != null && !this.getNonOEMPartsReplaced().isEmpty()) {
            partsInstalled = this.getNonOEMPartsReplaced();
            nonOEMPartReplacedList.addAll(partsInstalled);
        } else if (this.getHussmanPartsReplacedInstalled() != null && !this.getHussmanPartsReplacedInstalled().isEmpty()) {
            for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : this.getHussmanPartsReplacedInstalled()) {
                if (hussmanPartsReplacedInstalled != null &&
                        hussmanPartsReplacedInstalled.getNonHussmanInstalledParts() != null &&
                        !hussmanPartsReplacedInstalled.getNonHussmanInstalledParts().isEmpty()) {
                    nonHussmannPartsInstalled = hussmanPartsReplacedInstalled.getNonHussmanInstalledParts();
                    nonOEMPartReplacedList.addAll(nonHussmannPartsInstalled);
                }
            }
        }
        return nonOEMPartReplacedList;
    }

    public Boolean isNonOEMPartReplaced(PartReplaced partReplaced) {
        if (InstanceOfUtil.isInstanceOfClass(NonOEMPartReplaced.class, partReplaced)) {
            return true;
        } else if (InstanceOfUtil.isInstanceOfClass(InstalledParts.class, partReplaced)) {
            return false;
        }
        return false;
    }

    public BigDecimal getDiffOfLaborAskedAndLaborAllowed() {
        boolean isStdLaborHrs = getStdLaborEnabled().booleanValue();
        BigDecimal totalLaborHrsEntered = new BigDecimal(0);
        BigDecimal totalSuggestedLaborHrs = new BigDecimal(0);
        BigDecimal toReturn = new BigDecimal(0);
        for (LaborDetail laborDetail : laborPerformed) {
            final BigDecimal totalHours = laborDetail.getTotalHours(isStdLaborHrs);
            if (totalHours != null) {
                totalLaborHrsEntered = totalLaborHrsEntered.add(totalHours);
            }
            if (laborDetail.getHoursSpent() != null) {
                totalSuggestedLaborHrs = totalSuggestedLaborHrs.add(laborDetail.getHoursSpent());
            }
        }
        toReturn = totalLaborHrsEntered.subtract(totalSuggestedLaborHrs);
        return toReturn;
    }

    public Boolean getStdLaborEnabled() {
        return stdLaborEnabled;
    }

    public void setStdLaborEnabled(Boolean stdLaborEnabled) {
        this.stdLaborEnabled = stdLaborEnabled;
    }

    public List<OEMPartReplaced> getAllOEMPartsReplaced() {
        List<OEMPartReplaced> partsReplaced = new ArrayList<OEMPartReplaced>();
        if (this.oemPartsReplaced != null) {
            partsReplaced.addAll(this.oemPartsReplaced);
        }
        if (this.hussmanPartsReplacedInstalled != null) {
            for (HussmanPartsReplacedInstalled hussmanPart : this.hussmanPartsReplacedInstalled) {
                partsReplaced.addAll(hussmanPart.getReplacedParts());
            }
        }
        return partsReplaced;
    }

    public List<InstalledParts> getInstalledParts() {
        List<InstalledParts> installedParts = new ArrayList<InstalledParts>();
        for (HussmanPartsReplacedInstalled partReplacedInstalled : getHussmanPartsReplacedInstalled()) {
            installedParts.addAll(partReplacedInstalled.getHussmanInstalledParts());
        }
        return installedParts;
    }

    public List<InstalledParts> getNonHussmanInstalledParts() {
        List<InstalledParts> installedParts = new ArrayList<InstalledParts>();
        for (HussmanPartsReplacedInstalled partReplacedInstalled : getHussmanPartsReplacedInstalled()) {
            installedParts.addAll(partReplacedInstalled.getNonHussmanInstalledParts());
        }
        return installedParts;
    }

    public ServiceDetail clone() {
        ServiceDetail serviceDetail = new ServiceDetail();

        for (LaborDetail laborDetail : this.laborPerformed) {
            serviceDetail.getLaborPerformed().add(laborDetail.clone());
        }

        for (OEMPartReplaced oemPartReplaced : this.oemPartsReplaced) {
            serviceDetail.getOemPartsReplaced().add(oemPartReplaced.clone());
        }

        for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : this.hussmanPartsReplacedInstalled) {
            serviceDetail.getHussmanPartsReplacedInstalled().add(hussmanPartsReplacedInstalled.clone());
        }

        for (NonOEMPartReplaced nonOEMPartReplaced : this.nonOEMPartsReplaced) {
            serviceDetail.getNonOEMPartsReplaced().add(nonOEMPartReplaced.clone());
        }

        for (NonOEMPartReplaced miscNonOEMPartReplaced : this.miscPartsReplaced) {
            serviceDetail.getMiscPartsReplaced().add(miscNonOEMPartReplaced.clone());
        }

        for(LaborSplit laborSpt : this.laborSplit) {
            serviceDetail.getLaborSplit().add(laborSpt.clone());
        }

        serviceDetail.setTravelDetails(travelDetails.clone());

        serviceDetail.setItemFreightAndDuty(itemFreightAndDuty);
        serviceDetail.setMealsExpense(mealsExpense);
        serviceDetail.setParkingAndTollExpense(parkingAndTollExpense);
        serviceDetail.setPerDiem(perDiem);
        serviceDetail.setRentalCharges(rentalCharges);
        serviceDetail.setLocalPurchaseExpense(localPurchaseExpense);
        serviceDetail.setTollsExpense(tollsExpense);
        serviceDetail.setOtherFreightDutyExpense(otherFreightDutyExpense);
        serviceDetail.setOthersExpense(othersExpense);
        serviceDetail.setTechnician(technician);
        serviceDetail.setStdLaborEnabled(stdLaborEnabled);
        serviceDetail.setServiceTechnician(serviceTechnician);

        return serviceDetail;
    }

	public Money getTransportationAmt() {
		return transportationAmt;
	}

	public void setTransportationAmt(Money transportationAmt) {
		this.transportationAmt = transportationAmt;
	}

	public Document getTransportationInvoice() {
		return transportationInvoice;
	}

	public void setTransportationInvoice(Document transportationInvoice) {
		this.transportationInvoice = transportationInvoice;
	}

	public Boolean isInvoiceAvailable() {
		return invoiceAvailable;
	}

	public void setInvoiceAvailable(Boolean invoiceAvailable) {
		this.invoiceAvailable = invoiceAvailable;
	}


    public String getServiceTechnician() {
		return serviceTechnician;
	}

	public void setServiceTechnician(String serviceTechnician) {
		this.serviceTechnician = serviceTechnician;
	}

}
