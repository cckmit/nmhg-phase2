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
package tavant.twms.domain.campaign;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.I18NNonOemPartsDescription;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.security.authz.infra.SecurityHelper;

import com.domainlanguage.money.Money;

/**
 * @author Kiran.Kollipara
 */
@Entity
@Table(name = "non_oem_part_to_replace")
@PropertiesWithNestedCurrencyFields({"campaignSectionPrice"})
public class NonOEMPartToReplace extends PartsToReplace {

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "price_per_unit_amt"),
			@Column(name = "price_per_unit_curr") })
	protected Money pricePerUnit;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "NON_OEM_PART")
    protected List<CampaignSectionPrice> campaignSectionPrice = new ArrayList<CampaignSectionPrice>();

    // @NotEmpty(message = "{required.part.number}")
	// private String partNumber;

	@Transient
	private String description;

	@Transient
	private String defaultI18nDescription;

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "NON_OEM_PARTS_DESCRIPTION", nullable = false)
	private List<I18NNonOemPartsDescription> i18nNonOemPartsDescription = new ArrayList<I18NNonOemPartsDescription>();

	@ManyToOne(fetch=FetchType.LAZY,optional=true)
    private MiscellaneousItemConfiguration miscItemConfig;

    @ManyToOne(fetch=FetchType.LAZY,optional=true)
    private MiscellaneousItem miscItem;


	public InstalledParts fetchNonHussmannInstalledParts() {
		InstalledParts nonOEMInstalled = new InstalledParts();
		nonOEMInstalled.setDescription(getDescription());
		nonOEMInstalled.setPartNumber(getDescription());
		nonOEMInstalled.setNumberOfUnits(this.getNoOfUnits());
		nonOEMInstalled.setPricePerUnit(this.getPricePerUnit());
		return nonOEMInstalled;
	}

	public Money cost() {
		GlobalConfiguration globalConfiguration = GlobalConfiguration
				.getInstance();
		if (getNoOfUnits() == null || pricePerUnit == null) {
			return globalConfiguration.zeroInBaseCurrency();
		}
		return pricePerUnit.times(getNoOfUnits());
	}

	public Money getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(Money pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public String getDescription() {
		String description_locale="";
		for (I18NNonOemPartsDescription i18nNonOemPartsDescription : this.i18nNonOemPartsDescription) {
			if (i18nNonOemPartsDescription !=null && StringUtils.hasText(i18nNonOemPartsDescription.getLocale()) &&
					i18nNonOemPartsDescription.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18nNonOemPartsDescription.getDescription() != null) {
				description_locale = i18nNonOemPartsDescription.getDescription();
				break;
			}
			else if(i18nNonOemPartsDescription !=null && StringUtils.hasText(i18nNonOemPartsDescription.getLocale()) &&
					i18nNonOemPartsDescription.getLocale().equalsIgnoreCase("en_US")) {
				description_locale = i18nNonOemPartsDescription.getDescription();
			}
		}

		return description_locale;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("description", description)
				.toString();
	}

	public List<I18NNonOemPartsDescription> getI18nNonOemPartsDescription() {
		return i18nNonOemPartsDescription;
	}

	public void setI18nNonOemPartsDescription(
			List<I18NNonOemPartsDescription> nonOemPartsDescription) {
		i18nNonOemPartsDescription = nonOemPartsDescription;
	}

	public String getDefaultI18nDescription() {
		return defaultI18nDescription;
	}

	public void setDefaultI18nDescription(String defaultI18nDescription) {
		this.defaultI18nDescription = defaultI18nDescription;
	}

    public List<CampaignSectionPrice> getCampaignSectionPrice() {
        return campaignSectionPrice;
    }

    public void setCampaignSectionPrice(List<CampaignSectionPrice> campaignSectionPrice) {
        this.campaignSectionPrice = campaignSectionPrice;
    }

	public MiscellaneousItem getMiscItem() {
		return miscItem;
	}

	public void setMiscItem(MiscellaneousItem miscItem) {
		this.miscItem = miscItem;
	}

	public MiscellaneousItemConfiguration getMiscItemConfig() {
		return miscItemConfig;
	}

	public void setMiscItemConfig(MiscellaneousItemConfiguration miscItemConfig) {
		this.miscItemConfig = miscItemConfig;
	}

	public Money getCampaignSectionPriceForCurrency(Currency currency){
		Money nonOemPrice = null;
		for(CampaignSectionPrice campaignSectionPrice : this.campaignSectionPrice){
			if(campaignSectionPrice.getPricePerUnit().breachEncapsulationOfCurrency().equals(currency)){
				return campaignSectionPrice.getPricePerUnit();
			}
		}
		return nonOemPrice;
	}

}