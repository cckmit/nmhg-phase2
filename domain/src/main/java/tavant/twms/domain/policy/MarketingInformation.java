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
package tavant.twms.domain.policy;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.AuditableColumns;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class MarketingInformation implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "MarketingInformation")
	@GenericGenerator(name = "MarketingInformation", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MARKETING_INFORMATION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private Boolean customerFirstTimeOwner = null;
    
    private Boolean tradeIn = null;
    
    @Column(name = "FIRST_TIME_OWNER_OF_PRODUCT")
    private Boolean firstTimeOwnerOfProductBeingRegistered = null;

    @ManyToOne(fetch = FetchType.LAZY)
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketType marketType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Market application;

    @ManyToOne(fetch = FetchType.LAZY)
    private CompetitionType competitionType;

    @ManyToOne(fetch = FetchType.LAZY)
	private CompetitorMake competitorMake;

	@ManyToOne(fetch = FetchType.LAZY)
	private CompetitorModel competitorModel;
	
	private String ifPreviousOwner;

    private Integer months;

    private Integer years;

    private String additionalInfo;
    
	@ManyToOne(fetch = FetchType.LAZY)
	private ContractCode contractCode;
	
	private String dealerRepresentative;
	
	private String customerRepresentative;
	
	private String UlClassification;

	public String getUlClassification() {
		return UlClassification;
	}

	public void setUlClassification(String ulClassification) {
		UlClassification = ulClassification;
	}

	public ContractCode getContractCode() {
		return contractCode;
	}

	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
	}

	public MaintenanceContract getMaintenanceContract() {
		return maintenanceContract;
	}

	public void setMaintenanceContract(MaintenanceContract maintenanceContract) {
		this.maintenanceContract = maintenanceContract;
	}

	public IndustryCode getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(IndustryCode industryCode) {
		this.industryCode = industryCode;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    private MaintenanceContract maintenanceContract;
    
	@ManyToOne(fetch = FetchType.LAZY)
    private IndustryCode industryCode;
    
	@ManyToOne(fetch = FetchType.LAZY)
    private InternalInstallType internalInstallType;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }

    public void setCustomerFirstTimeOwner(Boolean customerFirstTimeOwner) {
        this.customerFirstTimeOwner = customerFirstTimeOwner;
    }

    public Integer getMonths() {
        return this.months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getYears() {
        return this.years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }


	public CompetitorMake getCompetitorMake() {
		return competitorMake;
	}

	public void setCompetitorMake(CompetitorMake competitorMake) {
		this.competitorMake = competitorMake;
	}

	public CompetitorModel getCompetitorModel() {
		return competitorModel;
	}

	public void setCompetitorModel(CompetitorModel competitorModel) {
		this.competitorModel = competitorModel;
	}

	public String getIfPreviousOwner() {
		return ifPreviousOwner;
	}

	public void setIfPreviousOwner(String ifPreviousOwner) {
		this.ifPreviousOwner = ifPreviousOwner;
	}

	public Boolean getCustomerFirstTimeOwner() {
		return customerFirstTimeOwner;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public MarketType getMarketType() {
        return marketType;
    }

    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }

    public Market getApplication() {
        return application;
    }

    public void setApplication(Market application) {
        this.application = application;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

	public Boolean getTradeIn() {
		return tradeIn;
	}

	public void setTradeIn(Boolean tradeIn) {
		this.tradeIn = tradeIn;
	}

	public void setFirstTimeOwnerOfProductBeingRegistered(
			Boolean firstTimeOwnerOfProductBeingRegistered) {
		this.firstTimeOwnerOfProductBeingRegistered = firstTimeOwnerOfProductBeingRegistered;
	}

	public Boolean getFirstTimeOwnerOfProductBeingRegistered() {
		return firstTimeOwnerOfProductBeingRegistered;
	}
	

	public String getDealerRepresentative() {
		return dealerRepresentative;
	}

	public void setDealerRepresentative(String dealerRepresentative) {
		this.dealerRepresentative = dealerRepresentative;
	}

	public String getCustomerRepresentative() {
		return customerRepresentative;
	}

	public void setCustomerRepresentative(String customerRepresentative) {
		this.customerRepresentative = customerRepresentative;
	}

	public InternalInstallType getInternalInstallType() {
		return internalInstallType;
	}

	public void setInternalInstallType(InternalInstallType internalInstallType) {
		this.internalInstallType = internalInstallType;
	}
}
