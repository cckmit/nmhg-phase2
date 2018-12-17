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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.inventory.I18NTransactionTypeText;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MarketType implements BusinessUnitAware,AuditableColumns{
    @Id
    private Long id;

    @Version
    private int version;

    private String title;

    private String code;
    
    @OneToMany(fetch = FetchType.EAGER)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "I18N_MARKET_TYPE", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<I18NMarketTypeText> marketTypeTexts = new ArrayList<I18NMarketTypeText>();

    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
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

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<I18NMarketTypeText> getMarketTypeTexts() {
        return marketTypeTexts;
    }

    public void setMarketTypeTexts(List<I18NMarketTypeText> marketTypeTexts) {
        this.marketTypeTexts = marketTypeTexts;
    }

    public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}



    public String getDisplayTitle() {
    	String i18ntitle = "";
		for (I18NMarketTypeText marketTypeText : this.marketTypeTexts) {
			if (marketTypeText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && marketTypeText.getTitle() != null) {
				i18ntitle = marketTypeText.getTitle();
				break;
			}
			else if(marketTypeText.getLocale().equalsIgnoreCase("en_US")) {
				i18ntitle = marketTypeText.getTitle();
			}

		}
		return i18ntitle;
    }
}
