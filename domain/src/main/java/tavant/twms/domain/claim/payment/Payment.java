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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.base.Ratio;
import com.domainlanguage.base.Rounding;
import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@PropertiesWithNestedCurrencyFields({"lineItemGroups","activeCreditMemo"})
public class Payment implements AuditableColumns{

	@Id
	@GeneratedValue(generator = "Payment")
	@GenericGenerator(name = "Payment", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_Payment"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "claimed_amount_amt"),
			@Column(name = "claimed_amount_curr") })
	private Money claimedAmount;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "total_amount_amt"),
			@Column(name = "total_amount_curr") })
	private Money totalAmount;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinTable(name = "line_item_groups", joinColumns = @JoinColumn(name = "for_payment"))
	@OrderBy("id")
	private List<LineItemGroup> lineItemGroups = new ArrayList<LineItemGroup>();

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private CreditMemo activeCreditMemo;

	@SuppressWarnings("unused")
	@Transient
	private boolean paymentToBeMade;
	
	@Transient
	private Money deductibleAmount;
	
	@Transient
	private Money lateFee;
	
	@Transient
	private Money acceptedLateFee;
	
	@Transient
	private Money stateMandateLateFee;

	private Boolean flatAmountApplied = Boolean.FALSE;
	
	
	private Boolean totalAcceptanceChkbox=Boolean.FALSE;
	private Boolean totalAcceptStateMdtChkbox=Boolean.FALSE;
	private Boolean stateMandateActive=Boolean.FALSE;

	public Boolean getTotalAcceptanceChkbox() {
		return totalAcceptanceChkbox;
	}

	public void setTotalAcceptanceChkbox(Boolean totalAcceptanceChkbox) {
		this.totalAcceptanceChkbox = totalAcceptanceChkbox;
	}

	public Boolean getTotalAcceptStateMdtChkbox() {
		return totalAcceptStateMdtChkbox;
	}

	public void setTotalAcceptStateMdtChkbox(Boolean totalAcceptStateMdtChkbox) {
		this.totalAcceptStateMdtChkbox = totalAcceptStateMdtChkbox;
	}	
	
	public Boolean getStateMandateActive() {
		return stateMandateActive;
	}

	public void setStateMandateActive(Boolean stateMandateActive) {
		this.stateMandateActive = stateMandateActive;
	}

	public Money getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(Money deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}

	public Money getLateFee() {
		return lateFee;
	}

	public void setLateFee(Money lateFee) {
		this.lateFee = lateFee;
	}

	public Money getAcceptedLateFee() {
		return acceptedLateFee;
	}

	public void setAcceptedLateFee(Money acceptedLateFee) {
		this.acceptedLateFee = acceptedLateFee;
	}

	public Money getStateMandateLateFee() {
		return stateMandateLateFee;
	}

	public void setStateMandateLateFee(Money stateMandateLateFee) {
		this.stateMandateLateFee = stateMandateLateFee;
	}


	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Payment() {
		Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
		this.claimedAmount = Money.valueOf(0, baseCurrency);
		this.totalAmount = Money.valueOf(0, baseCurrency);
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

	public void setClaimedAmount(Money claimedAmount) {
		this.claimedAmount = claimedAmount;
	}

	public Money getClaimedAmount() {
		return this.claimedAmount;
	}

	public void addToClaimedAmount(Money someAmount) {
		this.claimedAmount = this.claimedAmount.plus(someAmount);
	}

	public void addToTotalAmount(Money someAmount) {
		this.totalAmount = this.totalAmount.plus(someAmount);
	}

	/**
	 * @param totalAmount
	 *            the totalAmount to set
	 */
	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Money getTotalAmount() {
		return this.totalAmount;
	}

	public LineItemGroup addLineItemGroup(String name) {
		LineItemGroup aLineItemGroup = new LineItemGroup().forName(name);
		this.lineItemGroups.add(aLineItemGroup);		
		return aLineItemGroup;
	}

	public LineItemGroup getLineItemGroup(String name) {
		for (LineItemGroup aLineItemGroup : this.lineItemGroups) {
			if (aLineItemGroup.getName()!=null && aLineItemGroup.getName().equals(name)) {
				return aLineItemGroup;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append(super.toString()).append("id",
				this.id).append("claimed amount", this.claimedAmount).append(
				"total amount", this.totalAmount).toString();
	}

	public List<LineItemGroup> getLineItemGroups() {
		return this.lineItemGroups;
	}

	public void setLineItemGroups(List<LineItemGroup> itemLineGroups) {
		this.lineItemGroups = itemLineGroups;
	}

	public boolean isActualPaymentMade() {
		return this.activeCreditMemo != null;
	}

	/**
	 * The assumption is that payment has a amount to be paid, a credit memo
	 * (since actual payment is made). For now tax is stored in CreditMemo,
	 * later we need to include that in line item groups also.
	 * 
	 * I cant throw an exception when credit memo is null because the xmlbean
	 * wrapper will be calling it?
	 */
	public Money getTotalAmountPaidAfterTax() {
		if (this.activeCreditMemo != null) {
			return this.totalAmount.plus(this.activeCreditMemo.getTaxAmount());
		}
		return null;
	}

	public Money getAcceptedTotalAfterGlobalModifiersProrated(String groupName) {
		LineItemGroup totalGroup = getLineItemGroup(Section.TOTAL_CLAIM);
		Money sumOfAllGroups = totalGroup.getGroupTotal();
		Money totalSectionAcceptedAmount = totalGroup.getAcceptedTotal();
		// remaining will be global modifiers
		Ratio ratio = new Ratio(new BigDecimal(1), new BigDecimal(1));
		if(!(sumOfAllGroups.isZero())){
			ratio = totalSectionAcceptedAmount.dividedBy(sumOfAllGroups);
		}
		return getLineItemGroup(groupName).getAcceptedTotal().applying(ratio,
				Rounding.HALF_DOWN);
	}
	
	public Money getAcceptedTotalAfterGlobalModifiersProrated(Money claimedAmount) {
		LineItemGroup totalGroup = getLineItemGroup(Section.TOTAL_CLAIM);
		Money sumOfAllGroups = totalGroup.getGroupTotal();
		Money totalSectionAcceptedAmount = totalGroup.getAcceptedTotal();
		// remaining will be global modifiers
		Ratio ratio = totalSectionAcceptedAmount.dividedBy(sumOfAllGroups);
		return claimedAmount.applying(ratio,
				Rounding.HALF_DOWN);
	}

	public String prettyPrint() {
		StringBuffer buf = new StringBuffer();
		char NEWLINE = '\n';
		buf.append(NEWLINE);
		buf.append("=================").append(NEWLINE).append(NEWLINE);
		buf.append("PAYMENT STATEMENT").append(NEWLINE);
		buf.append("=================").append(NEWLINE).append(NEWLINE);
		buf.append("Claimed Amounts").append(NEWLINE);
		buf.append("=================").append(NEWLINE);
		int indentLevel = 0;
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {

			StringBuffer indent = new StringBuffer();
			StringBuffer padding = new StringBuffer();
			for (int i = 0; i < indentLevel + 2; i++) {
				buf.append(' ');
			}

			buf.append(indent);
			buf.append(lineItemGroup.getName());

			int rightPadding = (30 - indentLevel + 2 - lineItemGroup.getName()
					.length());
			for (int i = 0; i < rightPadding; i++) {
				padding.append(' ');
			}
			buf.append(padding);
			buf.append('\t');
			buf.append('\t');
			buf.append('\t');
			buf.append('\t');
			buf.append('\t');
			buf.append('\n');

		}
		return buf.toString();
	}

	public Money getLineItemGroupsAcceptedTotal() {
		List<Money> acceptedTotal = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (lineItemGroup.getName() != null
					&& !Section.TOTAL_CLAIM.equals(lineItemGroup.getName())
					&& !Section.TRAVEL.equals(lineItemGroup.getName())
					&& !Section.OTHERS.equals(lineItemGroup.getName())
					&& !Section.LATE_FEE.equals(lineItemGroup.getName())) {
				if (lineItemGroup != null) {
					if(Section.DEDUCTIBLE.equals(lineItemGroup.getName()))
					{
						acceptedTotal.add(lineItemGroup.getAcceptedTotal().negated());
					}
					else
					{
						acceptedTotal.add(lineItemGroup.getAcceptedTotal());
					}
				}
			}
		}
		return Money.sum(acceptedTotal);
	}
	
	public Money getLineItemGroupsGroupTotal() {
		List<Money> acceptedTotal = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (lineItemGroup.getName() != null
					&& !Section.TOTAL_CLAIM.equals(lineItemGroup.getName())
					&& !Section.TRAVEL.equals(lineItemGroup.getName())	
					&& !Section.OTHERS.equals(lineItemGroup
							.getName())) {
                if(lineItemGroup!=null){
                	if(Section.DEDUCTIBLE.equals(lineItemGroup.getName()))
                	{
                		acceptedTotal.add(lineItemGroup.getGroupTotal().negated());
                	}
                	else
                	{
                		acceptedTotal.add(lineItemGroup.getGroupTotal());
                	}
                }
            }
		}
		return Money.sum(acceptedTotal);
	}
	
	public Money getLineItemGroupsStateMandateTotal() {
		List<Money> acceptedTotal = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (lineItemGroup.getName() != null
					&& !Section.TOTAL_CLAIM.equals(lineItemGroup.getName())
					&& !Section.TRAVEL.equals(lineItemGroup.getName())
					&& !Section.OTHERS.equals(lineItemGroup.getName())
					&& !Section.LATE_FEE.equals(lineItemGroup.getName())) {
				if (lineItemGroup != null) {
					if(Section.DEDUCTIBLE.equals(lineItemGroup.getName()))
					{
						acceptedTotal.add(lineItemGroup.getGroupTotalStateMandateAmount().negated());
					}
					else
					{
						acceptedTotal.add(lineItemGroup
								.getGroupTotalStateMandateAmount());
					}
				}
			}
		}
		return Money.sum(acceptedTotal);
	}
	
	public Money getLineItemGroupsAcceptedTotalForCP() {
		List<Money> acceptedTotalForCP = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (!Section.TOTAL_CLAIM.equals(lineItemGroup.getName())) {
                if(lineItemGroup!=null){
                	if(lineItemGroup.getAcceptedTotalForCp() != null)
                	{
                		acceptedTotalForCP.add(lineItemGroup.getAcceptedTotalForCp());
                	}	
                }
            }
		}		
		if(!acceptedTotalForCP.isEmpty())
			return Money.sum(acceptedTotalForCP);
		else
			return GlobalConfiguration.getInstance().zeroInBaseCurrency();
	}

	public Money getLineItemGroupsTotal() {
		List<Money> groupTotal = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (!Section.TOTAL_CLAIM.equals(lineItemGroup.getName())) {
				groupTotal.add(lineItemGroup.getGroupTotal());
			}
		}
		return Money.sum(groupTotal);
	}
	
	public Money getLineItemGroupsTotalForCP() {
		List<Money> groupTotalForCp = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (!Section.TOTAL_CLAIM.equals(lineItemGroup.getName())) {
				groupTotalForCp.add(lineItemGroup.getGroupTotalShareForCp());
			}
		}
		return Money.sum(groupTotalForCp);
	}

	public Money getLineItemGroupsAcceptedTotalAtCP() {
		List<Money> acceptedTotal = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (!Section.TOTAL_CLAIM.equals(lineItemGroup.getName())) {
                if(lineItemGroup!=null && lineItemGroup.getAcceptedTotalForCp()!=null)
                acceptedTotal.add(lineItemGroup.getAcceptedTotalForCp());
			}
		}
		return Money.sum(acceptedTotal);
	}
	
	public Money getLineItemGroupsAcceptedTotalCP() {
		List<Money> acceptedTotal = new ArrayList<Money>();
		for (LineItemGroup lineItemGroup : this.lineItemGroups) {
			if (!Section.TOTAL_CLAIM.equals(lineItemGroup.getName())) {
                if(lineItemGroup!=null && lineItemGroup.getAcceptedCpTotal()!=null)
                acceptedTotal.add(lineItemGroup.getAcceptedCpTotal());
			}
		}
		return Money.sum(acceptedTotal);
	}


	public CreditMemo getActiveCreditMemo() {
		return activeCreditMemo;
	}

	public void setActiveCreditMemo(CreditMemo creditMemo) {
		this.activeCreditMemo = creditMemo;
	}

	public void addActiveCreditMemo(CreditMemo newCreditMemo) {
		setActiveCreditMemo(newCreditMemo); 
	}

   
   // Logic Added such that Credit Memo Audit does not get Created when no changes are done on Reopen.
	public boolean getPaymentToBeMade() {
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if (Section.TOTAL_CLAIM.equalsIgnoreCase(lineItemGroup.getName())) {
				Money totalCreditAmount = lineItemGroup.getTotalCreditAmount();
				if (totalCreditAmount.breachEncapsulationOfAmount().compareTo(
						BigDecimal.ZERO) != 0) {
					return Boolean.TRUE;
				}
			}				
		}
		return Boolean.FALSE;
	}

	public boolean isPaymentToBeMade() {
		return getPaymentToBeMade();
	}
	
	public Money getEffectiveAmountToBePaid() {
		return this.getLineItemGroup(Section.TOTAL_CLAIM).getTotalCreditAmount();
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public Money getCreditMemoAmount(){
        LineItemGroup totalAmountGroup = getLineItemGroup(Section.TOTAL_CLAIM);
        return totalAmountGroup.getTotalCreditAmount().negated();
    }

    public BigDecimal getNetAcceptancePercentageForSMRPayment(){
       LineItemGroup summationGroup = getLineItemGroup(Section.TOTAL_CLAIM);
       Money dealerAskedAmount = summationGroup.getAcceptedTotal();
       Money acceptedAmount = summationGroup.getAcceptedTotal();
        return acceptedAmount.breachEncapsulationOfAmount()
                .divide(dealerAskedAmount.breachEncapsulationOfAmount(),2,2)
                .multiply(new BigDecimal(100));
    }
    
    @Override
    public Payment clone(){
    	Payment payment = new Payment();
    	payment.setActiveCreditMemo(this.activeCreditMemo);
    	payment.setClaimedAmount(this.claimedAmount);
    	payment.setTotalAmount(this.totalAmount);
    	payment.setVersion(0);
    	payment.setTotalAcceptanceChkbox(this.totalAcceptanceChkbox);
    	payment.setTotalAcceptStateMdtChkbox(this.totalAcceptStateMdtChkbox);
    	payment.setStateMandateActive(this.stateMandateActive);
    	for(LineItemGroup lineItemGroup : this.lineItemGroups){
    		payment.getLineItemGroups().add(lineItemGroup.clone());
    	}
    	return payment;
    }
    /**
	 * This API first calculated the Total Amount to be Credited to the Dealer
	 * then it adjusts the credit amount with respect to previously credited amount to the dealer
	 * Credit Amount would be adjusted mostly in Claim Reopen/Denied cases
	 * @param claim
	 */
    public void setTotalCreditAmount(Claim claim) {
		List<LineItemGroup> lineItemGroups = this.getLineItemGroups();
		GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
		Payment latestPaidPayment = claim.getLatestPaidPayment();
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if (lineItemGroup.getProratedAmount() != null) {
				Money totalCreditAmount = lineItemGroup.getAcceptedTotal().plus(lineItemGroup.getProratedAmount());
				lineItemGroup.setTotalCreditAmount(totalCreditAmount);
			} else {
				lineItemGroup.setTotalCreditAmount(lineItemGroup.getAcceptedTotal());
			}
			lineItemGroup.setModifierTotalCreditAmount(lineItemGroup.getModifierAcceptedAmount());
			if(lineItemGroup.getName().equals(Section.OEM_PARTS)||lineItemGroup.getName().equals(Section.TOTAL_CLAIM))
			{
				lineItemGroup.setScTotalCreditAmount(lineItemGroup.getAcceptedCpTotal());	
				if(lineItemGroup.getName().equals(Section.OEM_PARTS))
					lineItemGroup.setNetPriceTotalCreditAmount(lineItemGroup.getAcceptedTotal().minus(lineItemGroup.getModifierAcceptedAmount()));
			}
			if (latestPaidPayment != null) {
				Money amount =GlobalConfiguration.getInstance().zeroInBaseCurrency();
				Money previousAcceptedAmount = Money.valueOf(0.0D, claim.getCurrencyForCalculation());
				Money previousSCAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
	            Money previousModifiersAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
	            Money previousNetPriceAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
					if(latestPaidPayment.getLineItemGroup(lineItemGroup.getName())!=null){
						previousAcceptedAmount = latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).getAcceptedTotal();
						if( latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).equals(Section.OEM_PARTS)|| latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).equals(Section.TOTAL_CLAIM))
						{
							previousSCAcceptedAmount=latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).getAcceptedCpTotal();
							if( latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).equals(Section.OEM_PARTS))
								previousNetPriceAcceptedAmount=latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).getAcceptedTotal().minus(latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).getModifierAcceptedAmount());
						}                       
	                        previousModifiersAcceptedAmount=latestPaidPayment.getLineItemGroup(lineItemGroup.getName()).getModifierAcceptedAmount();                     
					}
					if(previousAcceptedAmount != null ){
						if(previousAcceptedAmount.isNegative())
						{
							previousAcceptedAmount=previousAcceptedAmount.abs();
						}
						amount = amount.plus((lineItemGroup.getTotalCreditAmount().minus(previousAcceptedAmount)));
						lineItemGroup.setTotalCreditAmount(amount);
				}
					if(lineItemGroup.getName().equals(Section.OEM_PARTS)||lineItemGroup.getName().equals(Section.TOTAL_CLAIM))
					{

						if(previousSCAcceptedAmount != null ){						
							if(previousSCAcceptedAmount.isNegative())
							{
								previousSCAcceptedAmount=previousSCAcceptedAmount.abs();
							}
							amount = amount.plus((lineItemGroup.getScTotalCreditAmount().minus(previousSCAcceptedAmount)));
							lineItemGroup.setScTotalCreditAmount(amount);
						}
						if(lineItemGroup.getName().equals(Section.OEM_PARTS))
						{
							if(previousNetPriceAcceptedAmount != null ){						
								if(previousNetPriceAcceptedAmount.isNegative())
								{
									previousNetPriceAcceptedAmount=previousNetPriceAcceptedAmount.abs();
								}
								amount = amount.plus((lineItemGroup.getNetPriceTotalCreditAmount().minus(previousNetPriceAcceptedAmount)));
								lineItemGroup.setNetPriceTotalCreditAmount(amount);
							}
						}
					}
					if(previousModifiersAcceptedAmount != null ){						
						if(previousModifiersAcceptedAmount.isNegative())
						{
							previousModifiersAcceptedAmount=previousModifiersAcceptedAmount.abs();
						}
						if(null != lineItemGroup.getModifierTotalCreditAmount()) {
							amount = amount.plus((lineItemGroup.getModifierTotalCreditAmount().minus(previousModifiersAcceptedAmount)));
						}
						lineItemGroup.setModifierTotalCreditAmount(amount);
					}				
			}
		}
	}


    /**
     * Calculates amount at the Payment Level
     * @param currency
     */
    public void setAmountAtPaymentLevel(Currency currency){
    	Money totalClaimedAmount = Money.valueOf(0.0D, currency);
    	Money totalAmount = Money.valueOf(0.0D, currency);
    	for(LineItemGroup lineItemGroup : this.getLineItemGroups()){
    		if(!Section.TOTAL_CLAIM.equalsIgnoreCase(lineItemGroup.getName())){
    			totalClaimedAmount = totalClaimedAmount.plus(lineItemGroup.getBaseAmount());
    			totalAmount = totalAmount.plus(lineItemGroup.getAcceptedTotal());
    		}
    	}
    	this.setClaimedAmount(totalClaimedAmount);
    	this.setTotalAmount(totalAmount);
    }
    //not used anywhere 
	public void setLineItemGrpForDeniedClaim() {
		List<LineItemGroup> lineItemGroups = this.getLineItemGroups();
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if (lineItemGroup.getPercentageAcceptance().doubleValue() != new BigDecimal(100).doubleValue()) {
				lineItemGroup.setPercentageAcceptance(lineItemGroup.getPercentageAcceptance());
			}else{
				lineItemGroup.setPercentageAcceptance(BigDecimalFactory.bigDecimalOf(0));
			}
			lineItemGroup.setBaseAmount(Money.dollars(0));
			lineItemGroup.setModifierAmount(Money.dollars(0));
			lineItemGroup.setGroupTotal(Money.dollars(0));
			lineItemGroup.setAcceptedTotal(Money.dollars(0));
		}
	}

    public LineItemGroup createLineItemGroup(String sectionName) {
        LineItemGroup lineItemGroup = getLineItemGroup(sectionName);
        if (lineItemGroup == null) {
            lineItemGroup = addLineItemGroup(sectionName);
        }
        lineItemGroup.setModifierAmount(null);
        return lineItemGroup;
    }

	/**
	 * Calculate "Total Credit Amount" after Prorating Global Modifiers and % Acceptance
	 */
	public void setTotalCreditAmount() {
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			if(this.getTotalAcceptStateMdtChkbox()!=null&&this.getTotalAcceptStateMdtChkbox().equals(true))
			{
			lineItemGroup.setTotalCreditAmountForStateMandate();		
			}
			else
			{
				lineItemGroup.setTotalCreditAmount();				
			}
			if(lineItemGroup.getModifierAcceptedAmount()!=null)
				lineItemGroup.setModifierTotalCreditAmount(lineItemGroup.getModifierAcceptedAmount());	
			lineItemGroup.setScTotalCreditAmount(lineItemGroup.getAcceptedCpTotal());
			if(lineItemGroup.getModifierTotalCreditAmount()==null)
			{
				lineItemGroup.setModifierTotalCreditAmount(Money.valueOf(0, lineItemGroup.getAcceptedTotal().breachEncapsulationOfCurrency()));
			}
			if(lineItemGroup.getName().equals(Section.OEM_PARTS))
			{
				if(this.getTotalAcceptStateMdtChkbox()!=null&&this.getTotalAcceptStateMdtChkbox().equals(true))
				{
				lineItemGroup.setNetPriceTotalCreditAmount(lineItemGroup.getGroupTotalStateMandateAmount().minus(lineItemGroup.getModifierAcceptedAmount()));
				}
				else
				{
					lineItemGroup.setNetPriceTotalCreditAmount(lineItemGroup.getAcceptedTotal().minus(lineItemGroup.getModifierAcceptedAmount()));
				}
			}
		}
	}
	
	public void setLateFeeCreditAmount(LineItemGroup lineItemGroup) {
	
			if(this.getTotalAcceptStateMdtChkbox()!=null&&this.getTotalAcceptStateMdtChkbox().equals(true))
			{
			lineItemGroup.setTotalCreditAmountForStateMandate();
			}
			else
			{
				lineItemGroup.setTotalCreditAmount();	
			}		
	}

    public void clear(Money zero) {
        setTotalAmount(zero);
        for (LineItemGroup lineItemGroup : lineItemGroups) {
            lineItemGroup.clear(zero);
        }
        setTotalCreditAmount();
    }

    public void resetLineItemGroups(PaymentDefinition paymentDefinition) {
        List<String> applicableSectionNames = new ArrayList<String>();
        for (PaymentSection paymentSection : paymentDefinition.getPaymentSections()) {
            applicableSectionNames.add(paymentSection.getSection().getName());
        }
        for (LineItemGroup lineItemGroup : lineItemGroups) {
        	if(flatAmountApplied){
        		lineItemGroup.setPercentageAcceptanceForCp(BigDecimalFactory.bigDecimalOf(0));
        	}
        	else{
        		lineItemGroup.resetFlatCpAmount(GlobalConfiguration.getInstance().zeroInBaseCurrency());
        	}
            if (!applicableSectionNames.contains(lineItemGroup.getName())) {
            	if(flatAmountApplied)
            	{
            		lineItemGroup.resetLineItem(GlobalConfiguration.getInstance().zeroInBaseCurrency());
            	}
            	else
            	{
            		 lineItemGroup.reset(GlobalConfiguration.getInstance().zeroInBaseCurrency());
            	}
            }
        }
    }

	public Boolean isFlatAmountApplied() {
		return flatAmountApplied;
	}

	public void setFlatAmountApplied(Boolean flatAmountApplied) {
		this.flatAmountApplied = flatAmountApplied;
	}
	
	public void sortLineItemBasedOnDisplayPosition(PaymentContext paymentContext)
	{
		PaymentDefinition paymentDefinition=paymentContext.getPaymentDefinition();
		Map<String,Section> applicableSectionNames = new HashMap<String,Section>();
		for (PaymentSection paymentSection : paymentDefinition.getPaymentSections()) {
			applicableSectionNames.put(paymentSection.getSection().getName(),paymentSection.getSection());
		}
		List<LineItemGroup> sortedItemBAsedOnDspPosList=new ArrayList<LineItemGroup>();
		LineItemGroup []sortedItemBAsedOnDspPos=new LineItemGroup[20];
		//Sort Line Item according to display position
		for (LineItemGroup lineItemGroup : lineItemGroups) {
			Section section=applicableSectionNames.get(lineItemGroup.getName());
			if(section!=null&&section.getDisplayPosition()!=null&&!lineItemGroup.getName().equals(Section.TOTAL_CLAIM))
			{
				//sortedItemBAsedOnDspPos.add(section.getDisplayPosition(), lineItemGroup);
				sortedItemBAsedOnDspPos[section.getDisplayPosition()]= lineItemGroup;
			}
		}
		//sortedItemBAsedOnDspPosList=Arrays.asList(sortedItemBAsedOnDspPos);
		for(LineItemGroup lineItem:sortedItemBAsedOnDspPos)
		{
			if(lineItem!=null)
			{
				sortedItemBAsedOnDspPosList.add(lineItem);
			}
		}
	/*	//List<LineItemGroup> nullItem=new ArrayList<LineItemGroup>();
		//nullItem.add(null);
		sortedItemBAsedOnDspPosList.removeAll(Collections.singleton(null));
		//sortedItemBAsedOnDspPosList.removeAll(nullItem);
		Iterator<LineItemGroup> iterator=sortedItemBAsedOnDspPosList.listIterator();
		while(iterator.hasNext())
		{
			LineItemGroup lineItemGroup=iterator.next();
			if(lineItemGroup==null)
			{
				iterator.remove();
			}
		}*/
		//For deductible and late fee and total
		LineItemGroup lineItem=getLineItemGroup(Section.DEDUCTIBLE);
		if(lineItem!=null)
		{
			sortedItemBAsedOnDspPosList.add(lineItem);
		}
		lineItem=getLineItemGroup(Section.LATE_FEE);
		if(lineItem!=null)
		{
			sortedItemBAsedOnDspPosList.add(lineItem);
		}
		lineItem=getLineItemGroup(Section.TOTAL_CLAIM);
		if(lineItem!=null)
		{
			sortedItemBAsedOnDspPosList.add(lineItem);
		}
		this.lineItemGroups=sortedItemBAsedOnDspPosList;
	}
	
    public void setAmountSelected(LineItemGroup summationGroup,Claim claim)
    {  
    	int result=summationGroup.getAcceptedTotal().compareTo(summationGroup.getGroupTotalStateMandateAmount());
    	if(result==1)
    	{
    		if(claim.getPayment().getTotalAcceptStateMdtChkbox().equals(true))
    		{
    			claim.getPayment().setTotalAcceptStateMdtChkbox(true); 	
    		}
    		else
    		{
    			claim.getPayment().setTotalAcceptanceChkbox(true);    	
    		}
    	}
    	else if(result==-1)					
    	{  
    		if(claim.getPayment().getTotalAcceptanceChkbox().equals(true))
    		{
    			claim.getPayment().setTotalAcceptanceChkbox(true); 
    		}
    		else
    		{
    			claim.getPayment().setTotalAcceptStateMdtChkbox(true);     	
    		}    			 			
    	}	
    	else
    	{
    		if(claim.getPayment().getTotalAcceptanceChkbox().equals(false)&&claim.getPayment().getTotalAcceptStateMdtChkbox().equals(false))
    		{
    			claim.getPayment().setTotalAcceptanceChkbox(true);	
    		}

    	}
    }
    
}
