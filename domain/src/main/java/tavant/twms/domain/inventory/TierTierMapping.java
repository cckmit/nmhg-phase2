package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.warranty.WarrantyUtil;
import tavant.twms.security.SecurityHelper;

@Entity
public class TierTierMapping {

	@Id
	@GeneratedValue(generator = "TierTierMapping")
	@GenericGenerator(name = "TierTierMapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "TIER_TIER_MAPPING_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20"),
			@Parameter(name = "optimizer", value = "pooled") })
	private Long id;
	
	private String inventoryTier;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private EngineTierCtryMapping customerTier;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInventoryTier() {
		return inventoryTier;
	}

	public void setInventoryTier(String inventoryTier) {
		this.inventoryTier = inventoryTier;
	}

	public EngineTierCtryMapping getCustomerTier() {
		return customerTier;
	}

	public void setCustomerTier(EngineTierCtryMapping customerTier) {
		this.customerTier = customerTier;
	}
	
	@OneToMany(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "TIER_TIER_MAPPING", nullable = false)
    private List<I18NWaiverText> i18NWaiverTexts = new ArrayList<I18NWaiverText>();
	
	public List<I18NWaiverText> getI18NWaiverTexts() {
        return i18NWaiverTexts;
    }

    public void setI18NWaiverTexts(List<I18NWaiverText> i18NWaiverTexts) {
        this.i18NWaiverTexts = i18NWaiverTexts;
    }
    
	public String getI18NWaiverText() {
		String i18nDisclaimer = "";
		for (I18NWaiverText I18NWaiverText : this.i18NWaiverTexts) {
			if (I18NWaiverText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString())
					&& I18NWaiverText.getDescription() != null) {
				i18nDisclaimer = I18NWaiverText.getDescription();
				break;
			} else if (I18NWaiverText.getLocale().equalsIgnoreCase("en_US")) {
				i18nDisclaimer = I18NWaiverText.getDescription();
			}
		}
		return i18nDisclaimer;
	}

	public void setI18NWaiverTexts(String desclaimer, String locale) {
		if (null != this.getI18NWaiverTexts()
				&& this.getI18NWaiverTexts().size() != 0) {
			boolean localeFound = false;
			for (I18NWaiverText i18NWaiverText : this.getI18NWaiverTexts()) {
				if (i18NWaiverText.getLocale().equalsIgnoreCase(locale)) {
					// update disclaimer
					i18NWaiverText.setDescription(desclaimer);
					i18NWaiverText.setLocale(locale);
					localeFound = true;
				}
			}
			// add disclaimer
			if (!localeFound) {
				I18NWaiverText newI18NWaiverText = new I18NWaiverText();
				newI18NWaiverText.setDescription(desclaimer);
				newI18NWaiverText.setLocale(locale);
				this.getI18NWaiverTexts().add(newI18NWaiverText);
			}
		}
	}
}
