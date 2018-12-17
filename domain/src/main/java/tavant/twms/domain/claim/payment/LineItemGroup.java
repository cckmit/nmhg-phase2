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
package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.ExcludeConversion;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.domain.laborType.LaborSplitDetailAudit;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.base.Rounding;
import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@PropertiesWithNestedCurrencyFields( {"currentPartPaymentInfo","additionalPaymentInfo",
	"modifiers","forLaborSplitAudit"})
public class LineItemGroup implements AuditableColumns,BUSpecificSectionNames{

    @Id
	@GeneratedValue(generator = "LineItemGroup")
	@GenericGenerator(name = "LineItemGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_LineItemGroup"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL })
    @JoinTable(name = "current_part_info")
	private List<PartPaymentInfo> currentPartPaymentInfo = new ArrayList<PartPaymentInfo>();

	@OneToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @JoinTable(name="add_payment_info")
	private List<AdditionalPaymentInfo> additionalPaymentInfo = new ArrayList<AdditionalPaymentInfo>();
	
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( {CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinTable(name = "modifiers", joinColumns = @JoinColumn(name = "line_item_group"))
	@OrderBy("id")
	private List<LineItem> modifiers = new ArrayList<LineItem>();
    
    
    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( {CascadeType.ALL})
	@JoinColumn(name = "line_item_group")
	@OrderBy("id")
	private List<IndividualLineItem> individualLineItems = new ArrayList<IndividualLineItem>();
   

    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "base_amt", nullable = true),
			@Column(name = "base_curr", nullable = true) })
	private Money baseAmount;
    
    @Transient
    @Type(type = "tavant.twms.infra.MoneyUserType")
	private Money modifierAmount;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "modifier_accepted_amt", nullable = true),
			@Column(name = "modifier_accepted_curr", nullable = true) })
	private Money modifierAcceptedAmount;
    
    @Transient
    @Type(type = "tavant.twms.infra.MoneyUserType")
	private Money SMandateModifierAmount;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "grouptotal_amt", nullable = true),
			@Column(name = "grouptotal_curr", nullable = true) })
	private Money groupTotal;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "accepted_amt", nullable = true),
			@Column(name = "accepted_curr", nullable = true) })
	private Money acceptedTotal;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "grouptotal_statemandate_amt", nullable = true),
			@Column(name = "grouptotal_statemandate_curr", nullable = true) })
	private Money groupTotalStateMandateAmount;

    private BigDecimal percentageAcceptance = BigDecimalFactory.bigDecimalOf(100);
    
    private BigDecimal percentageApplicable = BigDecimalFactory.bigDecimalOf(100);
    
    private BigDecimal stateMandateRatePercentage = BigDecimalFactory.bigDecimalOf(100);

    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "accepted_cp_amt", nullable = true),
			@Column(name = "accepted_cp_curr", nullable = true) })
	@ExcludeConversion
	private Money acceptedCpTotal;
    
   
    @Type(type = "tavant.twms.infra.MoneyUserType")     
	@Columns(columns = { @Column(name = "total_cp_amt", nullable = true),
			@Column(name = "total_cp_curr", nullable = true) })
	private Money acceptedTotalForCp ;

    
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "accepted_wnty_amt", nullable = true),
			@Column(name = "accepted_wnty_curr", nullable = true) })
	private Money acceptedTotalForWnty ;

    @Transient
    private BigDecimal percentageAcceptanceForCp = BigDecimalFactory.bigDecimalOf(0);
    
    @Transient
    private BigDecimal percentageAcceptanceForWnty = BigDecimalFactory.bigDecimalOf(100);
    
    @Transient
    @Type(type = "tavant.twms.infra.MoneyUserType")
	private Money proratedAmount;

    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "total_credit_amt", nullable = true),
			@Column(name = "total_credit_curr", nullable = true) })
	private Money totalCreditAmount;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
 	@Columns(columns = { @Column(name = "sc_total_credit_amt", nullable = true),
 			@Column(name = "sc_total_credit_curr", nullable = true) })
 	private Money scTotalCreditAmount;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
 	@Columns(columns = { @Column(name = "modifier_total_credit_amt", nullable = true),
 			@Column(name = "modifier_total_credit_curr", nullable = true) })
 	private Money modifierTotalCreditAmount;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
 	@Columns(columns = { @Column(name = "net_price_total_credit_amt", nullable = true),
 			@Column(name = "net_price_total_credit_curr", nullable = true) })
 	private Money netPriceTotalCreditAmount;     
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "rate", nullable = true),
			@Column(name = "rate_curr", nullable = true) })
	private Money rate;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "state_mandate_rate", nullable = true),
			@Column(name = "state_mandate_rate_curr", nullable = true) })
    private Money stateMandateRate;
    
    @OneToMany(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @JoinTable(name = "labor_split_details", joinColumns = { @JoinColumn(name = "line_item_group") }, 
    		inverseJoinColumns = { @JoinColumn(name = "labor_split_detail_audit") })
    private List<LaborSplitDetailAudit> forLaborSplitAudit =new ArrayList<LaborSplitDetailAudit>();
    
  
    private String askedQtyHrs;
   
    private String acceptedQtyHrs;     
    
    
    @Transient
	@Type(type = "tavant.twms.infra.MoneyUserType")
	private Money flatCpAmount;
    
   /* @Transient
    private boolean isBuConfigAMER;*/
    
    public Money getFlatCpAmount() {
		if(flatCpAmount == null){
			GlobalConfiguration gc=	GlobalConfiguration.getInstance();
			
			gc.setClaimCurrency(this.acceptedTotal.breachEncapsulationOfCurrency());
			return getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP)!=null?getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP):gc.zeroInBaseCurrency();
		}else{
			return flatCpAmount;
		}
	}

/*	public boolean isBuConfigAMER() {
		return isBuConfigAMER;
	}

	public void setBuConfigAMER(boolean isBuConfigAMER) {
		this.isBuConfigAMER = isBuConfigAMER;
	}*/

	public void setFlatCpAmount(Money flatCpAmount) {
		this.flatCpAmount = flatCpAmount;
	}	
		
   	public String getAskedQtyHrs() {
		return askedQtyHrs;
	}

	public void setAskedQtyHrs(String askedQtyHrs) {
		this.askedQtyHrs = askedQtyHrs;
	}

	public String getAcceptedQtyHrs() {
		return acceptedQtyHrs;
	}

	public void setAcceptedQtyHrs(String acceptedQtyHrs) {
		this.acceptedQtyHrs = acceptedQtyHrs;
	}	

	public LineItemGroup forName(String name) {
		this.setName(name);
		return this;
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.id)
			.append("name",this.name)
			.append("percentageAcceptance",this.percentageAcceptance)
			.append("groupTotal", this.getGroupTotal())
			.append("acceptedTotal",this.getAcceptedTotal()).toString();
	}

	public List<PartPaymentInfo> getCurrentPartPaymentInfo() {
		return currentPartPaymentInfo;
	}

	public void setCurrentPartPaymentInfo(
			List<PartPaymentInfo> currentPartPaymentInfo) {
		this.currentPartPaymentInfo = currentPartPaymentInfo;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public LineItemGroup getLatestAudit(){
        return this;
    }

    public BigDecimal getPercentageAcceptance() {
        return percentageAcceptance;
    }

    public void setPercentageAcceptance(BigDecimal percentageAcceptance) {
        this.percentageAcceptance = percentageAcceptance;
    }

    public BigDecimal getStateMandateRatePercentage() {
		return stateMandateRatePercentage;
	}

	public void setStateMandateRatePercentage(BigDecimal stateMandateRatePercentage) {
		this.stateMandateRatePercentage = stateMandateRatePercentage;
	}

	public BigDecimal getPercentageAcceptanceForCp() {
		return percentageAcceptanceForCp;
	}

	public void setPercentageAcceptanceForCp(BigDecimal percentageAcceptanceForCp) {
		this.percentageAcceptanceForCp = percentageAcceptanceForCp;
	}

    public String getMessageKey(String sectionName){
        return NAMES_AND_KEY.get(sectionName);
    }

    public List<LineItem> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<LineItem> modifiers) {
        this.modifiers = modifiers;
    }
    
    public HashMap<String, LineItem> getModifierMap(){
    	HashMap<String, LineItem> modifierNameValue = new HashMap<String, LineItem>();
    	if(this.modifiers != null && !this.modifiers.isEmpty()){
	    	for(LineItem lineItem : this.modifiers){
	    		modifierNameValue.put(lineItem.getName(), lineItem);
	    	}
    	}
    	return modifierNameValue;
    }

	public Money getAcceptedTotal() {
        return acceptedTotal;
    }

    public void setAcceptedTotal(Money acceptedTotal) {
        	this.acceptedTotal = acceptedTotal;
    }  

    public Money getGroupTotalStateMandateAmount() {
		return groupTotalStateMandateAmount;
	}

	public void setGroupTotalStateMandateAmount(Money groupTotalStateMandateAmount) {
		this.groupTotalStateMandateAmount = groupTotalStateMandateAmount;
	}

	public Money getAcceptedTotalForCp() {
			return acceptedTotalForCp;
	}

	public void setAcceptedTotalForCp() {
		if(acceptedTotalForCp != null && acceptedTotalForCp.breachEncapsulationOfAmount().doubleValue()<0){
			if(flatCpAmount != null && this.flatCpAmount.breachEncapsulationOfAmount().doubleValue() > 0 && acceptedTotal.breachEncapsulationOfAmount().doubleValue() > 0){
				this.acceptedTotalForCp = this.flatCpAmount;
			}else{
				this.acceptedTotalForCp = GlobalConfiguration.getInstance().zeroInBaseCurrency();
			}
		}else{
			if(flatCpAmount != null && flatCpAmount.breachEncapsulationOfAmount().doubleValue() > 0 && acceptedTotal.breachEncapsulationOfAmount().doubleValue() > 0){
				acceptedTotalForCp = flatCpAmount;
			}else{
				this.acceptedTotalForCp =  getAcceptedTotalForWnty().times(percentageAcceptanceForCp).dividedBy(100.00);
			}
		}
		setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_CP);
	}
	
	public void setAcceptedTotalForCpOfTotalClaimSection() {
		if(acceptedTotalForCp != null && acceptedTotalForCp.breachEncapsulationOfAmount().doubleValue()<0){
			this.acceptedTotalForCp = GlobalConfiguration.getInstance().zeroInBaseCurrency();
		}else{
			if (acceptedCpTotal.equals(Money.valueOf(BigDecimal.ZERO, acceptedCpTotal.breachEncapsulationOfCurrency()))) {
				this.acceptedTotalForCp =  getAcceptedTotal().times(percentageAcceptanceForCp).dividedBy(100.00);
				this.flatCpAmount = acceptedCpTotal;
			} else {
				this.flatCpAmount = acceptedCpTotal;
				if(percentageAcceptanceForCp == BigDecimalFactory.bigDecimalOf(0.00)){
					this.acceptedTotalForCp = acceptedCpTotal;
				}else{
					this.acceptedTotalForCp =  getAcceptedTotalForWnty().times(percentageAcceptanceForCp).dividedBy(100.00);
				}
			}
		}
		setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_CP);		
	}
	
	public void setAcceptedTotalForCpOfTotalClaimSection(Claim claim) {	
		Payment payment=claim.getPayment();
		if(claim.isGoodWillPolicy())
		{
			payment.setTotalAcceptStateMdtChkbox(false);	
		}
		LineItemGroup oemPart=payment.getLineItemGroup(Section.OEM_PARTS);
		if(payment.getTotalAcceptStateMdtChkbox()!=null&&payment.getTotalAcceptStateMdtChkbox().equals(true))
		{		
			this.acceptedTotalForCp=this.groupTotalStateMandateAmount.minus(oemPart.getGroupTotalStateMandateAmount()).plus(oemPart.getSMandateModifierAmount()).plus(payment.getLineItemGroup(Section.OEM_PARTS).getAcceptedCpTotal());
			this.acceptedTotalForWnty=this.groupTotalStateMandateAmount;
		}
		else
		{
			if(oemPart.getModifierAcceptedAmount()!=null){
			this.acceptedTotalForCp =this.acceptedTotal.minus(oemPart.getAcceptedTotal()).plus(oemPart.getModifierAcceptedAmount()).plus(payment.getLineItemGroup(Section.OEM_PARTS).getAcceptedCpTotal());
			}else{
				this.acceptedTotalForCp =this.acceptedTotal.minus(oemPart.getAcceptedTotal()).plus(payment.getLineItemGroup(Section.OEM_PARTS).getAcceptedCpTotal());
			}
			acceptedTotalForWnty=this.acceptedTotal;
		}
		setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_WNTY);
		setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_CP);		
		}
	
	public void setAcceptedForWarrantyAfterLateFee(Claim claim) {	
		Payment payment=claim.getPayment();
		if(claim.isGoodWillPolicy())
		{
			payment.setTotalAcceptStateMdtChkbox(false);	
		}

		if(payment.getTotalAcceptStateMdtChkbox()!=null&&payment.getTotalAcceptStateMdtChkbox().equals(true))
		{			
			this.acceptedTotalForWnty=this.groupTotalStateMandateAmount;
		}
		else
		{			
			acceptedTotalForWnty=this.acceptedTotal;
		}
		setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_WNTY);	
	}
	
	public void setAcceptedForCostPriceAfterLateFee(Claim claim) {			
		acceptedTotalForCp=this.acceptedCpTotal;	
		setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_CP);	
	}

    private void setAdditionalPaymentInfoValues(AdditionalPaymentType paymentInfoType) {
        AdditionalPaymentInfo currentPaymentInfo = getAdditionalPaymentInfoForType(paymentInfoType);
        if (currentPaymentInfo == null) {
            AdditionalPaymentInfo paymentInfo = null;
            if (AdditionalPaymentType.ACCEPTED_FOR_CP.equals(paymentInfoType)) {
            	if (Section.TOTAL_CLAIM.equals(name) && BigDecimal.ZERO.equals(percentageAcceptanceForCp)) {
            	/*	if(!this.isBuConfigAMER)
            		{
            		paymentInfo = new AdditionalPaymentInfo(acceptedCpTotal,
	                        percentageAcceptanceForCp, paymentInfoType);  
            		}
            		else
            		{*/
            		paymentInfo = new AdditionalPaymentInfo(acceptedTotalForCp,
	                        percentageAcceptanceForCp, paymentInfoType);  
            		//}
                } else {
	                paymentInfo = new AdditionalPaymentInfo(acceptedTotalForCp,
	                        percentageAcceptanceForCp, paymentInfoType);
                }
            	// This is for setting final warranty amount after CP amount deduction for the claims which have been reopened after denied and closed(HUSS-887)
                if (Section.TOTAL_CLAIM.equals(name)) {
                /*	if(!this.isBuConfigAMER)
            		{
                	getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    				.setAdditionalAmount(getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    														.getAdditionalAmount()
    														.minus(paymentInfo.getAdditionalAmount()));
            		}
                	else
                	{*/
                	getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    				.setAdditionalAmount(getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    														.getAdditionalAmount());
                	//}
                } 
            } else {
                paymentInfo = new AdditionalPaymentInfo(acceptedTotalForWnty,
                        percentageAcceptanceForWnty, paymentInfoType);
            }
            this.addAdditionalPaymentInfo(paymentInfo);
        } else {
            if (AdditionalPaymentType.ACCEPTED_FOR_CP.equals(paymentInfoType)) {
                currentPaymentInfo.setPercentageAcceptance(percentageAcceptanceForCp);
                if (Section.TOTAL_CLAIM.equals(name) && BigDecimal.ZERO.equals(percentageAcceptanceForCp)) {
                /*	if(!this.isBuConfigAMER)
            		{
                	currentPaymentInfo.setAdditionalAmount(acceptedCpTotal);  
            		}
                	else
                	{*/
                	currentPaymentInfo.setAdditionalAmount(acceptedTotalForCp);
                	//}
                } else {
                	currentPaymentInfo.setAdditionalAmount(acceptedTotalForCp);  
                }
               // This is for setting final warranty amount after CP amount deduction
                if (Section.TOTAL_CLAIM.equals(name)) {
                /*	if(!this.isBuConfigAMER)
            		{
                	getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    				.setAdditionalAmount(getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    														.getAdditionalAmount()
    														.minus(currentPaymentInfo.getAdditionalAmount()));
            		}
                	else
                	{*/
                	getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    				.setAdditionalAmount(getAdditionalPaymentInfoForType(AdditionalPaymentType.ACCEPTED_FOR_WNTY)
    														.getAdditionalAmount());
                	//}
                }        		              
            } else {
                currentPaymentInfo.setPercentageAcceptance(percentageAcceptanceForWnty);
                if (Section.TOTAL_CLAIM.equals(name) && BigDecimal.ZERO.equals(percentageAcceptanceForCp)) {
                /*	if(!this.isBuConfigAMER)
            		{
                	currentPaymentInfo.setAdditionalAmount(acceptedTotal);  
            		}
                	else
                	{*/
                	currentPaymentInfo.setAdditionalAmount(acceptedTotalForWnty);  
                	//}
                } else {
                	currentPaymentInfo.setAdditionalAmount(acceptedTotalForWnty);  
                }                
            }
        }
    }

    private AdditionalPaymentInfo getAdditionalPaymentInfoForType(AdditionalPaymentType paymentType) {
        List<AdditionalPaymentInfo> existingPaymentInfos = this.getAdditionalPaymentInfo();
        for (AdditionalPaymentInfo existingPaymentInfo : existingPaymentInfos) {
            if (paymentType.equals(existingPaymentInfo.getType())) {
                return existingPaymentInfo;
            }
        }
        return null;
    }

    public void setAcceptedTotalForCp(Money acceptedTotalForCP) {
		this.acceptedTotalForCp =  acceptedTotalForCP;
		if(this.flatCpAmount.breachEncapsulationOfAmount().doubleValue() > 0){
			setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_CP);
		}
		if(this.acceptedTotalForCp.breachEncapsulationOfAmount().compareTo(BigDecimal.ZERO)!=0){
			setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_CP);
		}
	}
    public void setAcceptedTotalFrCp(Money acceptedTotalForCP) {
		this.acceptedTotalForCp =  acceptedTotalForCP;		
	}

    public Money getGroupTotal() {
        return groupTotal;
    }

    public void setGroupTotal(Money groupTotal) {
        this.groupTotal = groupTotal;
    }

    public Money getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Money baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Money getProratedAmount() {
        return proratedAmount;
    }

    public void setProratedAmount(Money proratedAmount) {
        this.proratedAmount = proratedAmount;
    }

    public Money getAcceptedCpTotal() {
        return acceptedCpTotal;
    }

    public void setAcceptedCpTotal(Money acceptedCpTotal) {
        if(acceptedCpTotal != null && acceptedCpTotal.breachEncapsulationOfAmount().doubleValue()<0)
        	this.acceptedCpTotal = GlobalConfiguration.getInstance().zeroInBaseCurrency();
        else
        	this.acceptedCpTotal = acceptedCpTotal;
    }

    public LineItem addLineItemToAudits(PaymentVariable paymentVariable, int level,
            Money previousLevelAmt,Double modifierPercentage, Boolean flatRate,Claim claim) {
		LineItem lineItem = null;
		Money modifiedAmt = GlobalConfiguration.getInstance().zeroInBaseCurrency();
		Money stateMandateAmt = GlobalConfiguration.getInstance().zeroInBaseCurrency();
		Money stateMandateModifiedAmt = GlobalConfiguration.getInstance().zeroInBaseCurrency();
		for (LineItem li : this.modifiers) {
			if (li.getName().equals(paymentVariable.getName())) {
				lineItem = li;
				break;
			}
		}
		if (lineItem == null) {
			lineItem = new LineItem();
			lineItem.setName(paymentVariable.getName());
            lineItem.setPaymentVariable(paymentVariable); 
            this.modifiers.add(lineItem);
		}
		lineItem.setLevel(level);
		lineItem.setModifierPercentage(modifierPercentage);
		lineItem.setPercentageConfigured(modifierPercentage);
		
		// Applying the modifiers to base amount
		if (!flatRate) {
			modifiedAmt = previousLevelAmt.times(modifierPercentage).dividedBy(100);
		}
		if (flatRate) {
			modifiedAmt = Money.valueOf(modifierPercentage.doubleValue(),GlobalConfiguration.getInstance().getBaseCurrency());
			lineItem.setIsFlatRate(true);
		}
		
		//NMHGSLMS-425 Changes for state mandate modifier
		stateMandateAmt=modifiedAmt;

		if(claim.getStateMandate()!=null)
		{
			if(this.getName().equals(Section.OEM_PARTS))
			{
				if(!claim.isGoodWillPolicy())
				{					
						setStateMandateModifier(claim,lineItem,previousLevelAmt,stateMandateModifiedAmt,modifiedAmt,modifierPercentage,flatRate);				
				}
				else
				{
					setStateMandateModifier(claim,lineItem,previousLevelAmt,stateMandateModifiedAmt,modifiedAmt,modifierPercentage,flatRate);
				}			
			}
			else
			{
				setStateMandateModifier(claim,lineItem,stateMandateAmt,stateMandateAmt,stateMandateAmt,modifierPercentage,flatRate);	
			}
		}
		
		//End		
		
        lineItem.setValue(modifiedAmt);
        if(getModifierAmount()==null){
            setModifierAmount(GlobalConfiguration.getInstance().zeroInBaseCurrency());
            setSMandateModifierAmount(GlobalConfiguration.getInstance().zeroInBaseCurrency());
        }
        setModifierAmount(getModifierAmount().plus(modifiedAmt));
        if(lineItem.getStateMandateAmount()!=null)
        	setSMandateModifierAmount(getSMandateModifierAmount().plus(lineItem.getStateMandateAmount()));
        lineItem.setCpValue(GlobalConfiguration.getInstance().zeroInBaseCurrency());
		if(lineItem.getClaimedValue() == null){
			lineItem.setClaimedValue(modifiedAmt);
		}
		// Update the accepted total for the group
		return lineItem;
	}
    
    public void setStateMandateModifier(Claim claim,LineItem lineItem,Money previousLevelAmt,Money stateMandateModifiedAmt,Money modifiedAmt,Double modifierPercentage,Boolean flatRate)
    {
    	if(this.getName().equals(Section.OEM_PARTS))
    	{	
    		double StateMandateModifierPercentage=claim.getStateMandate().getOemPartsPercent().doubleValue();
    		if (!flatRate) {
    			stateMandateModifiedAmt = previousLevelAmt.times(StateMandateModifierPercentage).dividedBy(100);
    		}
    		if (flatRate) {
    			stateMandateModifiedAmt = Money.valueOf(StateMandateModifierPercentage,GlobalConfiguration.getInstance().getBaseCurrency());
    			lineItem.setIsFlatRate(true);
    		}
    		lineItem.setStateMandateAmount(stateMandateModifiedAmt);
    		lineItem.setSMandateModifierPercent(StateMandateModifierPercentage);
    		lineItem.setPercentageConfiguredSMandate(StateMandateModifierPercentage);
    	}
    	else
    	{
    		lineItem.setStateMandateAmount(stateMandateModifiedAmt)	;
    		lineItem.setSMandateModifierPercent(modifierPercentage);
    		lineItem.setPercentageConfiguredSMandate(modifierPercentage);
    	}
    }

    @SuppressWarnings("unused")
	private Money moneyValueOf(double amount) {
		return Money.valueOf(amount, GlobalConfiguration.getInstance()
				.getBaseCurrency());
	}

    public Money getModifierAmount() {
        return modifierAmount;
    }

    public void setModifierAmount(Money modifierAmount) {
        this.modifierAmount = modifierAmount;
    }

    public Money getModifierAcceptedAmount() {
		return modifierAcceptedAmount;
	}

	public void setModifierAcceptedAmount(Money modifierAcceptedAmount) {
		this.modifierAcceptedAmount = modifierAcceptedAmount;
	}

	public Money getSMandateModifierAmount() {
		return SMandateModifierAmount;
	}

	public void setSMandateModifierAmount(Money sMandateModifierAmount) {
		SMandateModifierAmount = sMandateModifierAmount;
	}

	public Double findModifierPercentage(){
		for (LineItem lineItem : modifiers) {
			return lineItem.getModifierPercentage();
		}
		return 0d;
    }

    public void setAcceptedTotal(){
        acceptedTotal=groupTotal.times(percentageAcceptance).dividedBy(100.00);
    }  
  

    public void setGroupTotal(){
    	if(!Section.TRAVEL.equals(this.getName())&&!Section.OTHERS.equals(this.getName()))
    		groupTotal = baseAmount;
		if (modifierAmount != null) {
				groupTotal = groupTotal.plus(modifierAmount);
				if(groupTotal != null && groupTotal.isNegative())
				groupTotal =GlobalConfiguration.getInstance().zeroInBaseCurrency();
			
		}
    }
	
    public BigDecimal getPercentageAcceptanceForWnty() {
        return percentageAcceptanceForWnty;
    }

    public void setPercentageAcceptanceForWnty(BigDecimal percentageAcceptanceForWnty) {
        this.percentageAcceptanceForWnty = percentageAcceptanceForWnty;
    }

    public Money getAcceptedTotalForWnty() {
		return acceptedTotalForWnty;
    }

    public void setAcceptedTotalFrWnty(Money acceptedTotalForWnty)
    {
    	this.acceptedTotalForWnty=acceptedTotalForWnty;
    }
    
    public void setAcceptedTotalForWnty() {
    	if (Section.TOTAL_CLAIM.equals(name)) {
    		this.acceptedTotalForWnty = acceptedTotal;
    	} else {
    		this.acceptedTotalForWnty = getAcceptedTotal().times(percentageAcceptanceForWnty).dividedBy(100.00);
    	}
    	setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_WNTY);
    }
    
	public void setAcceptedTotalForWntyAMER(Payment payment) {
		if (Section.TOTAL_CLAIM.equals(name)) {
			if (payment.getTotalAcceptStateMdtChkbox()!=null&&payment.getTotalAcceptStateMdtChkbox().equals(true)) {
				this.acceptedTotalForWnty = groupTotalStateMandateAmount;
			} else {
				this.acceptedTotalForWnty = acceptedTotal;
			}
		} else {
			if (payment.getTotalAcceptStateMdtChkbox()!=null&&payment.getTotalAcceptStateMdtChkbox().equals(true)) {
				this.acceptedTotalForWnty = groupTotalStateMandateAmount;
			} else {
				this.acceptedTotalForWnty = getAcceptedTotal().times(
						percentageAcceptanceForWnty).dividedBy(100.00);
			}
		}
		setAdditionalPaymentInfoValues(AdditionalPaymentType.ACCEPTED_FOR_WNTY);
	}

    public Money getGroupTotalShareForCp(){
       return getAcceptedTotal().times(percentageAcceptance).dividedBy(100.00);
    }

    public Money getGroupTotalShareForWnty(){
       return getAcceptedTotal().times(percentageAcceptance).dividedBy(100.00);
    }

    public List<LaborSplitDetailAudit> getForLaborSplitAudit() {
		return forLaborSplitAudit;
	}

	public void setForLaborSplitAudit(List<LaborSplitDetailAudit> forLaborSplitAudit) {
		this.forLaborSplitAudit = forLaborSplitAudit;
	}

	public Money getTotalCreditAmount() {
		return totalCreditAmount;
	}

	public void setTotalCreditAmount(Money totalCreditAmount) {
		this.totalCreditAmount = totalCreditAmount;
	}
	
	public Money getScTotalCreditAmount() {
		return scTotalCreditAmount;
	}

	public void setScTotalCreditAmount(Money scTotalCreditAmount) {
		this.scTotalCreditAmount = scTotalCreditAmount;
	}

	public Money getModifierTotalCreditAmount() {
		return modifierTotalCreditAmount;
	}

	public void setModifierTotalCreditAmount(Money modifierTotalCreditAmount) {
		this.modifierTotalCreditAmount = modifierTotalCreditAmount;
	}

	public Money getNetPriceTotalCreditAmount() {
		return netPriceTotalCreditAmount;
	}

	public void setNetPriceTotalCreditAmount(Money netPriceTotalCreditAmount) {
		this.netPriceTotalCreditAmount = netPriceTotalCreditAmount;
	}

	public Money getAcceptedTotalForWarranty() {
		if(acceptedTotal==null){//need to correct this
			return GlobalConfiguration.getInstance().zeroInBaseCurrency();
		}else if(getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP)==null){
			return acceptedTotal;
		}else{
			return(acceptedTotal.minus(getAdditionalPaymentInfoOfType(AdditionalPaymentType.ACCEPTED_FOR_CP)));
		}
	}
	public Money getRate() {
		return rate;
	}

	public void setRate(Money rate) {
		this.rate = rate;
	}

	public Money getStateMandateRate() {
		return stateMandateRate;
	}

	public void setStateMandateRate(Money stateMandateRate) {
		this.stateMandateRate = stateMandateRate;
	}

	public List<AdditionalPaymentInfo> getAdditionalPaymentInfo() {
		return additionalPaymentInfo;
	}

	public void setAdditionalPaymentInfo(
			List<AdditionalPaymentInfo> additionalPaymentInfo) {
		this.additionalPaymentInfo = additionalPaymentInfo;
	}
	
	public Money getAdditionalPaymentInfoOfType(AdditionalPaymentType type){
		Money acceptedTotal = null;
		for(AdditionalPaymentInfo paymentInfo : this.getAdditionalPaymentInfo()){
			if(type.getType().equalsIgnoreCase(paymentInfo.getType().getType())){
				//return this.getAcceptedTotalForWnty();
				return paymentInfo.getAdditionalAmount();
			}
		}
		GlobalConfiguration gc=	GlobalConfiguration.getInstance();
		gc.setClaimCurrency(this.acceptedTotal.breachEncapsulationOfCurrency());
		return acceptedTotal!=null?acceptedTotal:gc.zeroInBaseCurrency();
	}

	public BigDecimal getPercentageAcceptedForAdditionalInfo(AdditionalPaymentType type) {
		BigDecimal percentageAccepted = BigDecimalFactory.bigDecimalOf(0.0);
		for(AdditionalPaymentInfo paymentInfo : this.getAdditionalPaymentInfo()){
			if(type.getType().equalsIgnoreCase(paymentInfo.getType().getType())){
				return paymentInfo.getPercentageAcceptance();
			}
		}
		return percentageAccepted;
	}

	
	public void addAdditionalPaymentInfo(AdditionalPaymentInfo paymentInfo){
		this.getAdditionalPaymentInfo().add(paymentInfo);
	}
	
	@Override
	public LineItemGroup clone(){
		LineItemGroup lineItemGroup = new LineItemGroup();
		lineItemGroup.setName(this.name);
		lineItemGroup.setAcceptedCpTotal(this.acceptedCpTotal);
		lineItemGroup.setAcceptedTotal(this.acceptedTotal);
		lineItemGroup.setBaseAmount(this.baseAmount);
		lineItemGroup.setGroupTotal(this.groupTotal);
		lineItemGroup.setPercentageAcceptance(this.percentageAcceptance);
		lineItemGroup.setRate(this.rate);
		lineItemGroup.setAskedQtyHrs(this.getAskedQtyHrs());
		lineItemGroup.setAcceptedQtyHrs(this.getAcceptedQtyHrs());
		lineItemGroup.setPercentageApplicable(this.getPercentageApplicable());
		lineItemGroup.setStateMandateRate(this.getStateMandateRate());
		lineItemGroup.setStateMandateRatePercentage(this.getStateMandateRatePercentage());
		lineItemGroup.setGroupTotalStateMandateAmount(this.getGroupTotalStateMandateAmount());
		lineItemGroup.setTotalCreditAmount(this.totalCreditAmount);
		lineItemGroup.setScTotalCreditAmount(this.scTotalCreditAmount);
		lineItemGroup.setModifierTotalCreditAmount(this.modifierTotalCreditAmount);
		lineItemGroup.setNetPriceTotalCreditAmount(this.netPriceTotalCreditAmount);
		lineItemGroup.setModifierAcceptedAmount(this.getModifierAcceptedAmount());
		for(IndividualLineItem individualLineItem : this.individualLineItems){
			lineItemGroup.getIndividualLineItems().add(individualLineItem.clone());
		}
		for(AdditionalPaymentInfo info : this.additionalPaymentInfo){
			lineItemGroup.getAdditionalPaymentInfo().add(info.clone());
		}
		for(PartPaymentInfo info : this.currentPartPaymentInfo){
			lineItemGroup.getCurrentPartPaymentInfo().add(info.clone());
		}
		for(LaborSplitDetailAudit audit: this.forLaborSplitAudit){
			lineItemGroup.getForLaborSplitAudit().add(audit.clone());	
		}
		for(LineItem item : this.modifiers){
			lineItemGroup.getModifiers().add(item.clone());
		}
		lineItemGroup.setVersion(0);
		return lineItemGroup;
	}

    public void setPercentageForCPCalculation() {
        setPercentageAcceptanceForCp(getPercentageAcceptanceForCp());
        setPercentageAcceptanceForWnty(getPercentageAcceptance());
        setAcceptedTotalForWnty();
        setAcceptedTotalForCp();
    }
    
    public void setAcceptedTotalForCpClaimSection(Payment payment)
    {
    	  setPercentageAcceptanceForCp(getPercentageAcceptanceForCp());
          setPercentageAcceptanceForWnty(getPercentageAcceptance());
          setAcceptedTotalForWntyAMER(payment);
          setAcceptedTotalForCp();
    }

    public void setGroupAndAcceptedTotal(Claim claim,LineItemGroup lineItemGroup) {
        setGroupTotal();
        //setAcceptedTotal();
        setAcceptedTotal(claim,lineItemGroup);
    }
    
	public void setTotalCreditAmount() {
		if (getProratedAmount() != null) {
			Money totalCreditAmount = getAcceptedTotal().plus(getProratedAmount());
			setTotalCreditAmount(totalCreditAmount);
		} else {
			setTotalCreditAmount(getAcceptedTotal());
		}
	}
	
	public void setTotalCreditAmountForStateMandate() {
		if (getProratedAmount() != null) {
			Money totalCreditAmount = getGroupTotalStateMandateAmount().plus(getProratedAmount());
			setTotalCreditAmount(totalCreditAmount);
		} else {
			setTotalCreditAmount(getGroupTotalStateMandateAmount());
		}
	}
	
	public void setTotalCreditAmountForTotal() {
		if (getProratedAmount() != null) {
			Money totalCreditAmount = getAcceptedTotalForWnty().plus(getProratedAmount());
			setTotalCreditAmount(totalCreditAmount);
			//setScTotalCreditAmount(getAcceptedTotalForCp());
		} else {
			setTotalCreditAmount(getAcceptedTotalForWnty());
			//setScTotalCreditAmount(getAcceptedTotalForCp());
		}
	}
	public void setLateFeeCreditAmount() {
		if (getProratedAmount() != null) {
			Money totalCreditAmount = getAcceptedTotal().plus(getProratedAmount());
			setTotalCreditAmount(totalCreditAmount);
		} else {
			setTotalCreditAmount(getAcceptedTotal().plus(getProratedAmount()));
		}
	}


    public void clear(Money zero) {

       // if (this.percentageAcceptance.doubleValue() == new BigDecimal(100).doubleValue()) {
            this.percentageAcceptance = BigDecimalFactory.bigDecimalOf(0);
            this.percentageAcceptanceForWnty = BigDecimalFactory.bigDecimalOf(0);
       // }

        reset(zero);
        this.additionalPaymentInfo.clear();

        for (LaborSplitDetailAudit laborSplitDetailAudit : this.forLaborSplitAudit) {
            laborSplitDetailAudit.setLaborHrs(BigDecimal.ZERO);
        }

        if (this.modifiers != null) {
            for (LineItem modifier : this.modifiers) {
                modifier.clear(zero);
            }
        }
        
        if (this.individualLineItems != null) {
            for (IndividualLineItem individualLineItem : this.individualLineItems) {
            	individualLineItem.clear(zero);
            }
        }
    }

    public void reset(Money zero) {
        this.baseAmount = zero;
        this.modifierAmount = zero;
        this.SMandateModifierAmount=zero;
        this.groupTotal = zero;
        this.acceptedTotal = zero;
        this.groupTotalStateMandateAmount=zero;
        this.acceptedCpTotal=zero;
        this.modifierAcceptedAmount=zero;
    }
    public void resetLineItem(Money zero) {
        this.baseAmount = zero;
        this.modifierAmount = zero;
        this.SMandateModifierAmount=zero;
        this.groupTotal = zero;
        this.groupTotalStateMandateAmount=zero;
    }

	public void setGroupAndAcceptedTotal(Boolean flatAmountApplied) {
		setGroupTotal();
		if(flatAmountApplied)
		{
			 this.percentageAcceptance = BigDecimalFactory.bigDecimalOf(0);
		}
		else 
		{
        setAcceptedTotal();
		}
		
	}
	
	   public void setGroupAndAcceptedTotal() {
	        setGroupTotal();
	        setAcceptedTotal();
	    }

	public List<IndividualLineItem> getIndividualLineItems() {
		return individualLineItems;
	}

	public void setIndividualLineItems(List<IndividualLineItem> individualLineItems) {
		this.individualLineItems = individualLineItems;
	}
	
	public IndividualLineItem createIndividualLineItem(PartReplaced replacePart)
	{		
		InstalledParts installedPart=null;
		if(replacePart instanceof InstalledParts)
		{
			installedPart=(InstalledParts)replacePart;
		}
		for(IndividualLineItem individualLineItem:this.individualLineItems)
		{
			if(individualLineItem.getBrandItem()!=null)
			{
				if(individualLineItem.getBrandItem().getId()==installedPart.getBrandItem().getId())
				{
					return individualLineItem;
				}
			}
		}
		return null;
	}
	
	public IndividualLineItem createNonOemIndividualLineItem(PartReplaced replacePart)
	{		
		NonOEMPartReplaced nonOEMPartReplaced=null;
		if(replacePart instanceof NonOEMPartReplaced)
		{
			nonOEMPartReplaced=(NonOEMPartReplaced)replacePart;
		}
		for(IndividualLineItem individualLineItem:this.individualLineItems)
		{
			if(individualLineItem.getNonOemPartReplaced().equals(nonOEMPartReplaced.getDescription()))
			{
				return individualLineItem;
			}
		}
		return null;
	}
	
/*	public IndividualLineItem getIndividualLineItem(PartReplaced replacePart)
	{		
		NonOEMPartReplaced nonOEMPartReplaced=null;
		if(replacePart instanceof NonOEMPartReplaced)
		{
			nonOEMPartReplaced=(NonOEMPartReplaced)replacePart;
		}
		for(IndividualLineItem individualLineItem:this.individualLineItems)
		{
			if(individualLineItem.getDescription().equals(nonOEMPartReplaced.getDescription()))
			{
				return individualLineItem;
			}
		}
		return null;
	}
*/


    public IndividualLineItem createLaborIndividualLineItem(LaborDetail laborDetail,Claim claim)
	{	
				
		for(IndividualLineItem individualLineItem:this.individualLineItems)
		{	
			if(claim.getCompetitorModelBrand()!=null)
			{
				/*if(individualLineItem.getItem().equals(LaborPaymentComputer.COMPETITOR_MODEL_ITEM))
				{
					return individualLineItem;
				}
				else
				{
					return null;
				}*/
				return individualLineItem;

			}			
			if(individualLineItem.getServiceProcedureDefinition().getId().equals(laborDetail.getServiceProcedure().getDefinition().getId()))
			{
				return individualLineItem;
			}
		}
		return null;
	}
	
	public IndividualLineItem getIndividualLineItem(BrandItem item) {
		for (IndividualLineItem individualLineItem : this.individualLineItems) {
			if (individualLineItem.getBrandItem()!=null && individualLineItem.getBrandItem().equals(item)) {
				return individualLineItem;
			}
		}
		return null;
	}

	public Money setAcceptedModifier(Claim claim,LineItemGroup lineItemGroup){
		//OemModifier percentage and total amount calculation
		Money totalAcceptedAmount=Money.valueOf(BigDecimal.ZERO, GlobalConfiguration.getInstance().getBaseCurrency()); 
		Money totalSMandateAcceptedAmount=Money.valueOf(BigDecimal.ZERO, GlobalConfiguration.getInstance().getBaseCurrency()); 
		Money SMandateCost=Money.valueOf(BigDecimal.ZERO, GlobalConfiguration.getInstance().getBaseCurrency()); 
		for(LineItem modifier:lineItemGroup.getModifiers())
		{
			Money acceptedCost=null;
			if(modifier.getStateMandateAmount()!=null)
				SMandateCost=modifier.getStateMandateAmount();
			if(claim.getPayment().isFlatAmountApplied()){
			acceptedCost=modifier.getAcceptedCost();
			if(acceptedCost==null&&modifier.getValue()!=null)	
			{
				acceptedCost=modifier.getValue();
				modifier.setAcceptedCost(acceptedCost);		
			}
			totalAcceptedAmount=totalAcceptedAmount.plus(acceptedCost);
			modifier.setPercentageAcceptance(BigDecimalFactory.bigDecimalOf(0));
			}
			else
			{					
				//acceptedCost=Money.valueOf(modifier.getValue().breachEncapsulationOfAmount().multiply(modifier.getPercentageAcceptance()).divide(new BigDecimal(100)),modifier.getValue().breachEncapsulationOfCurrency(),Rounding.HALF_UP);
				acceptedCost=modifier.getValue().times(modifier.getPercentageAcceptance()).dividedBy(100.00);;
				SMandateCost=SMandateCost.times(modifier.getPercentageAcceptance()).dividedBy(100.00);;
				totalAcceptedAmount=totalAcceptedAmount.plus(acceptedCost);
				//totalSMandateAcceptedAmount=totalSMandateAcceptedAmount.plus(SMandateCost);
				modifier.setAcceptedCost(acceptedCost);	
				modifier.setStateMandate(claim,SMandateCost,this.getName());
				totalSMandateAcceptedAmount=totalSMandateAcceptedAmount.plus(modifier.getStateMandateAmount());			
				
			}			
			
		}
		this.setSMandateModifierAmount(totalSMandateAcceptedAmount);

		if(claim.getPayment().getTotalAcceptStateMdtChkbox()!=null&&claim.getPayment().getTotalAcceptStateMdtChkbox().equals(true))
		{
		this.setModifierAcceptedAmount(totalSMandateAcceptedAmount);
		}
		else
		{
			this.setModifierAcceptedAmount(totalAcceptedAmount);
		}
		return totalAcceptedAmount;
		
	}
	
	public void setAcceptedTotal(Claim claim,LineItemGroup lineItemGroup){	    	
	    	 BigDecimal totalPercentageAcceptance = BigDecimalFactory.bigDecimalOf(0);
	    	 Currency baseCurrency = GlobalConfiguration.getInstance()
             .getBaseCurrency();
	    	Money modifierAcceptedAmount=Money.valueOf(0.0D, baseCurrency);
	    	Money SMandatemodifierAcceptedAmount=Money.valueOf(0.0D, baseCurrency);	    	
			modifierAcceptedAmount=setAcceptedModifier(claim,lineItemGroup);
			SMandatemodifierAcceptedAmount=lineItemGroup.getSMandateModifierAmount();
			if(claim.getPayment().isFlatAmountApplied())
			{
				lineItemGroup.setPercentageAcceptance(BigDecimalFactory.bigDecimalOf(0));
				if(lineItemGroup.getName().equals(Section.HANDLING_FEE))
				{
					lineItemGroup.setAcceptedTotal(Money.valueOf(0.0D, claim.getCurrencyForCalculation()));
				}
				else
				{				
				lineItemGroup.setAcceptedTotal(lineItemGroup.getAcceptedTotal().plus(modifierAcceptedAmount));
				
				}
			}
			else
			{
				if (lineItemGroup.getAcceptedTotal() != null
						&& (lineItemGroup.getName().equals(Section.OEM_PARTS)
								|| lineItemGroup.getName().equals(Section.NON_OEM_PARTS)
								|| lineItemGroup.getName().equals(Section.LABOR)
								|| lineItemGroup.getName().equals(Section.TRAVEL)
								|| lineItemGroup.getName().equals(Section.TRAVEL_BY_TRIP)
								|| lineItemGroup.getName().equals(Section.TRAVEL_BY_HOURS)
								|| lineItemGroup.getName().equals(
										Section.ADDITIONAL_TRAVEL_HOURS)
										|| lineItemGroup.getName().equals(Section.OTHERS)
										|| lineItemGroup.getName().equals(Section.LATE_FEE)))
				{
					lineItemGroup.setAcceptedTotal(lineItemGroup.getAcceptedTotal().plus(modifierAcceptedAmount));
				}
				else
				{
					acceptedTotal=groupTotal.times(percentageAcceptance).dividedBy(100.00);
					lineItemGroup.setAcceptedTotal(acceptedTotal);
					
				}
				if (lineItemGroup.getGroupTotal().breachEncapsulationOfAmount().floatValue() != new BigDecimal(
						0).floatValue()) 
				{
					if(lineItemGroup.getName().equals(Section.TRAVEL_BY_HOURS)||lineItemGroup.getName().equals(Section.ADDITIONAL_TRAVEL_HOURS))
					{
						totalPercentageAcceptance=lineItemGroup.getPercentageAcceptance();
					}
					else
					{
						totalPercentageAcceptance=lineItemGroup.getAcceptedTotal().breachEncapsulationOfAmount().multiply(new BigDecimal(100)).divide(lineItemGroup.getGroupTotal().breachEncapsulationOfAmount(),RoundingMode.HALF_UP);
					}
				}
				else
				{
					totalPercentageAcceptance=	new BigDecimal(100);
				}
				if(!lineItemGroup.getName().equals(Section.LATE_FEE))
				{
				lineItemGroup.setPercentageAcceptance(totalPercentageAcceptance);
				}
				//State mandate amount after %(Acceptance)
				Money StateMandateAmount=lineItemGroup.getAcceptedTotal().minus(modifierAcceptedAmount).plus(SMandatemodifierAcceptedAmount);
				if(Section.LABOR.equals(this.getName())||Section.TRAVEL.equals(this.getName())||Section.OTHERS.equals(this.getName()))
				{
					StateMandateAmount=lineItemGroup.getGroupTotalStateMandateAmount().plus(SMandatemodifierAcceptedAmount);
				}
				else if(Section.HANDLING_FEE.equals(this.getName()))
				{
					StateMandateAmount=	lineItemGroup.getGroupTotal();
				}				
				lineItemGroup.setStateMandateAfterModifier(claim, StateMandateAmount,this.getName());
			}	
			
			
	    }	
	
	public void setStateMandate(Claim claim, Money baseAmount,String costCategoryName)
	{
		Money stateMandateAmt=Money.valueOf(BigDecimal.ZERO,claim.getCurrencyForCalculation());
		if(baseAmount!=null){
			Currency baseCurrency =  baseAmount.breachEncapsulationOfCurrency();
			stateMandateAmt= Money.valueOf(BigDecimal.ZERO, baseCurrency);
		}    	

		//Policy applicablePolicy = claim.getApplicablePolicy();
		 boolean isGoodWillPolicy=claim.isGoodWillPolicy();
		if(this.getName().equals(Section.TOTAL_CLAIM)||this.getName().equals(Section.OTHERS)||this.getName().equals(Section.TRAVEL))
		{
			this.setGroupTotalStateMandateAmount(baseAmount);	
		}
		else
		{
			boolean applyStateMandate=false;
			if (claim.getStateMandate() != null) {
				if (!this.getName().equals(Section.OEM_PARTS)&& claim.getStateMandate().isStateMandateApplyForCostCategory(costCategoryName)) {
					applyStateMandate = true;
				} else if(this.getName().equals(Section.OEM_PARTS)) {
					applyStateMandate = true;
				}
				if (applyStateMandate&&!isGoodWillPolicy) {					
					this.setGroupTotalStateMandateAmount(baseAmount);
				}			 
				else
				{
					this.setGroupTotalStateMandateAmount(stateMandateAmt); 
				}
			}
			else
			{
				this.setGroupTotalStateMandateAmount(stateMandateAmt);  							
			} 
		}    	 

	}	

	public void setStateMandateAfterModifier(Claim claim, Money baseAmount,String costCategoryName)
	{
		Money stateMandateAmt=Money.valueOf(BigDecimal.ZERO,claim.getCurrencyForCalculation());
		
			//Policy applicablePolicy = claim.getApplicablePolicy();
			 boolean isGoodWillPolicy=claim.isGoodWillPolicy();
			boolean applyStateMandate=false;
			if (claim.getStateMandate() != null) {
				if (this.getName().equals(Section.OEM_PARTS)|| claim.getStateMandate().isStateMandateApplyForCostCategory(costCategoryName)&&!isGoodWillPolicy) {								
						this.setGroupTotalStateMandateAmount(baseAmount);
				}			
				else
				{
					this.setGroupTotalStateMandateAmount(stateMandateAmt);
				}
			}
			else
			{
				this.setGroupTotalStateMandateAmount(stateMandateAmt);  							
			}		   	
	}

	public void resetFlatCpAmount(Money zero){
		this.flatCpAmount = zero;
	}

	public BigDecimal getPercentageApplicable() {
		return percentageApplicable;
	}

	public void setPercentageApplicable(BigDecimal percentageApplicable) {
		this.percentageApplicable = percentageApplicable;
	}

}
