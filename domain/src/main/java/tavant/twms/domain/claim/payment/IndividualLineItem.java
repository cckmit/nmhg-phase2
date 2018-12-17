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
import java.util.Currency;
import java.util.List;


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
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

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
import tavant.twms.domain.catalog.BrandItemRepository;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.PartReplacedServiceImpl;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.domain.failurestruct.FailureStructureRepository;
import tavant.twms.domain.failurestruct.FailureStructureRepositoryImpl;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.laborType.LaborSplitDetailAudit;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.base.Ratio;
import com.domainlanguage.base.Rounding;
import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})

public class IndividualLineItem implements AuditableColumns{

	  @Id
		@GeneratedValue(generator = "IndividualLineItem")
		@GenericGenerator(name = "IndividualLineItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
				@Parameter(name = "sequence_name", value = "SEQ_Individual_line_item"),
				@Parameter(name = "initial_value", value = "200"),
				@Parameter(name = "increment_size", value = "20") })
	private Long id;	
	
	@ManyToOne
	@JoinColumn(name="brand_item")	
	private BrandItem brandItem;
	
	@ManyToOne
	@JoinColumn(name="service_procedure_definition")
	private ServiceProcedureDefinition serviceProcedureDefinition;
	
	private String nonOemPartReplaced;
    
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "base_amt", nullable = false),
			@Column(name = "base_curr", nullable = false) })
    private Money baseAmount;      
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "accepted_amt", nullable = false),
			@Column(name = "accepted_curr", nullable = false) })
    private Money acceptedAmount;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "state_mandate_amt", nullable = false),
			@Column(name = "state_mandate_curr", nullable = false) })
    private Money stateMandateAmount;
    
    @Embedded
   	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
   	private AuditableColEntity d = new AuditableColEntity();
    
    private BigDecimal percentageAcceptance = BigDecimalFactory.bigDecimalOf(100);
    
    private BigDecimal askedHrs = BigDecimalFactory.bigDecimalOf(0);    
   
    private BigDecimal acceptedHrs = BigDecimalFactory.bigDecimalOf(0);   
    
    private Integer askedQty;
    
    private Integer acceptedQty;
    
    private Boolean dealerNetpriceUpdated= Boolean.FALSE;  

	public Money getBaseAmount() {
		return baseAmount;
	}

	public BrandItem getBrandItem() {
		return brandItem;
	}

	public void setBrandItem(BrandItem brandItem) {
		this.brandItem = brandItem;
	}

	public ServiceProcedureDefinition getServiceProcedureDefinition() {
		return serviceProcedureDefinition;
	}

	public void setServiceProcedureDefinition(
			ServiceProcedureDefinition serviceProcedureDefinition) {
		this.serviceProcedureDefinition = serviceProcedureDefinition;
	}

	public String getNonOemPartReplaced() {
		return nonOemPartReplaced;
	}

	public void setNonOemPartReplaced(String nonOemPartReplaced) {
		this.nonOemPartReplaced = nonOemPartReplaced;
	}

	public void setBaseAmount(Money baseAmount) {
		this.baseAmount = baseAmount;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}	

	public BigDecimal getPercentageAcceptance() {
		return percentageAcceptance;
	}

	public void setPercentageAcceptance(BigDecimal percentageAcceptance) {
		this.percentageAcceptance = percentageAcceptance;
	}

	public Money getAcceptedAmount() {
		return acceptedAmount;
	}

	public void setAcceptedAmount(Money acceptedAmount) {
		this.acceptedAmount = acceptedAmount;
	}

	public Money getStateMandateAmount() {
		return stateMandateAmount;
	}

	public void setStateMandateAmount(Money stateMandateAmount) {
		this.stateMandateAmount = stateMandateAmount;
	}

	public BigDecimal getAskedHrs() {
		return askedHrs;
	}

	public void setAskedHrs(BigDecimal askedHrs) {
		this.askedHrs = askedHrs;
	}

	public BigDecimal getAcceptedHrs() {
		return acceptedHrs;
	}

	public void setAcceptedHrs(BigDecimal acceptedHrs) {
		this.acceptedHrs = acceptedHrs;
	}

	public Integer getAskedQty() {
		return askedQty;
	}

	public void setAskedQty(Integer askedQty) {
		this.askedQty = askedQty;
	}

	public Integer getAcceptedQty() {
		return acceptedQty;
	}

	public void setAcceptedQty(Integer acceptedQty) {
		this.acceptedQty = acceptedQty;
	}

	public Boolean getDealerNetpriceUpdated() {
		return dealerNetpriceUpdated;
	}

	public void setDealerNetpriceUpdated(Boolean dealerNetpriceUpdated) {
		this.dealerNetpriceUpdated = dealerNetpriceUpdated;
	}

	@Override
	public IndividualLineItem clone(){
		IndividualLineItem individuallineItem = new IndividualLineItem();
		individuallineItem.setBrandItem(this.getBrandItem());
		individuallineItem.setServiceProcedureDefinition(this.getServiceProcedureDefinition());
		individuallineItem.setNonOemPartReplaced(this.getNonOemPartReplaced());
		individuallineItem.setBaseAmount(this.getBaseAmount());		
		individuallineItem.setAcceptedAmount(this.getAcceptedAmount());
		individuallineItem.setStateMandateAmount(this.getStateMandateAmount());
		individuallineItem.setAskedHrs(this.getAskedHrs());
		individuallineItem.setAskedQty(this.getAskedQty());
		individuallineItem.setAcceptedHrs(this.getAcceptedHrs());
		individuallineItem.setAcceptedQty(this.getAcceptedQty());
		individuallineItem.setPercentageAcceptance(this.getPercentageAcceptance());
		
		return individuallineItem;
	}
	
    public void setStateMandate(Claim claim, Money baseAmount,String costCategoryName)
    {
    	LineItemGroup lineItemGroup=claim.getPayment().getLineItemGroup(costCategoryName);
    	Currency baseCurrency = baseAmount.breachEncapsulationOfCurrency();
    	 Money stateMandateAmt = Money.valueOf(BigDecimal.ZERO, baseCurrency);
    	/* Policy applicablePolicy = claim.getApplicablePolicy();*/
    	 boolean isGoodWillPolicy=claim.isGoodWillPolicy();
    	 boolean applyStateMandate=false;
		if (claim.getStateMandate() != null) {
			if (!lineItemGroup.getName().equals(Section.OEM_PARTS)
					&& claim.getStateMandate()
							.isStateMandateApplyForCostCategory(
									costCategoryName)) {
				applyStateMandate = true;
			} else if (lineItemGroup.getName().equals(Section.OEM_PARTS)) {
				applyStateMandate = true;
			}
			if (applyStateMandate&&!isGoodWillPolicy) {				
					this.setStateMandateAmount(baseAmount);
			} 
			else
			{
				this.setStateMandateAmount(stateMandateAmt);
			}
		} else {
			this.setStateMandateAmount(stateMandateAmt);
		}
	}
    
    public void clear(Money zero) {
        this.baseAmount = zero;
        this.acceptedAmount = zero;
        this.stateMandateAmount=zero;
        this.percentageAcceptance=BigDecimal.ZERO;
    }
    
    public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
    
}
