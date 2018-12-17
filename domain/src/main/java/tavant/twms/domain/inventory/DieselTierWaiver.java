package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import tavant.twms.security.SecurityHelper;

@Entity
public class DieselTierWaiver {

	@Id
	@GeneratedValue(generator = "DieselTierWaiver")
	@GenericGenerator(name = "DieselTierWaiver", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DIESEL_TIER_WAIVER_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	private Date approvedDateTime;

	private String approvedByAgentName;

	private String agentTitle;

	private String agentTelephone;

	private String agentEmailAddress;

	private String destinationCountry;

	private String countryEmissionRating;

	@ManyToOne(fetch = FetchType.LAZY)
	private InventoryItem inventoryItem;
	
	private String disclaimer;
	
	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}
	
	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getApprovedByAgentName() {
		return approvedByAgentName;
	}
	public void setApprovedByAgentName(String approvedByAgentName) {
		this.approvedByAgentName = approvedByAgentName;
	}
	public String getAgentTitle() {
		return agentTitle;
	}
	public void setAgentTitle(String agentTitle) {
		this.agentTitle = agentTitle;
	}
	public String getAgentTelephone() {
		return agentTelephone;
	}
	public void setAgentTelephone(String agentTelephone) {
		this.agentTelephone = agentTelephone;
	}
	public String getAgentEmailAddress() {
		return agentEmailAddress;
	}
	public void setAgentEmailAddress(String agentEmailAddress) {
		this.agentEmailAddress = agentEmailAddress;
	}
	public String getDestinationCountry() {
		return destinationCountry;
	}
	public void setDestinationCountry(String destinationCountry) {
		this.destinationCountry = destinationCountry;
	}
	public String getCountryEmissionRating() {
		return countryEmissionRating;
	}
	public void setCountryEmissionRating(String countryEmissionRating) {
		this.countryEmissionRating = countryEmissionRating;
	}
	public Date getApprovedDateTime() {
		return approvedDateTime;
	}
	public void setApprovedDateTime(Date approvedDateTime) {
		this.approvedDateTime = approvedDateTime;
	}

	@OneToMany(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "DIESEL_TIER_WAIVER", nullable = false)
    private List<I18NDisclaimer> i18NDisclaimers = new ArrayList<I18NDisclaimer>();
    
    public List<I18NDisclaimer> getI18NDisclaimers() {
		return i18NDisclaimers;
	}

	public void setI18NDisclaimers(List<I18NDisclaimer> i18nDisclaimers) {
		i18NDisclaimers = i18nDisclaimers;
	}

	public String getI18NDisclaimer() {
		String disclaimer = "";
		for (I18NDisclaimer i18NDisclaimer: this.i18NDisclaimers) {
			if (i18NDisclaimer.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString())
					&& i18NDisclaimer.getDescription() != null) {
				disclaimer = i18NDisclaimer.getDescription();
				break;
			} else if (i18NDisclaimer.getLocale().equalsIgnoreCase("en_US")) {
				disclaimer = i18NDisclaimer.getDescription();
			}
		}
		return disclaimer;
	}
    
    public void setI18NDisclaimer(String desclaimer, String locale) {
		if (null != this.getI18NDisclaimers()
				&& this.getI18NDisclaimers().size() != 0) {
			boolean localeFound = false;
			for (I18NDisclaimer i18NDisclaimer : this.getI18NDisclaimers()) {
				if (i18NDisclaimer.getLocale().equalsIgnoreCase(locale)) {
					// update disclaimer
					i18NDisclaimer.setDescription(desclaimer);
					i18NDisclaimer.setLocale(locale);
					localeFound = true;
				}
			}
			// add disclaimer
			if (!localeFound) {
				I18NDisclaimer newI18NDisclaimer = new I18NDisclaimer();
				newI18NDisclaimer.setDescription(desclaimer);
				newI18NDisclaimer.setLocale(locale);
				this.getI18NDisclaimers().add(newI18NDisclaimer);
			}
		}
	}
}
