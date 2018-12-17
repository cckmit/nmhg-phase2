package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity
public class DieselTier {

	@Id
	@GeneratedValue(generator = "DieselTier")
	@GenericGenerator(name = "DieselTier", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DIESEL_TIER_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20"),
			@Parameter(name = "optimizer", value = "pooled") })
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private List<EngineTierCtryMapping> engineTierCountryMapping = new ArrayList<EngineTierCtryMapping>();
	
	public List<EngineTierCtryMapping> getEngineTierCountryMapping() {
		return engineTierCountryMapping;
	}
	public void setEngineTierCountryMapping(List<EngineTierCtryMapping> engineTierCountryMapping) {
		this.engineTierCountryMapping = engineTierCountryMapping;
	}

	private String tier;
	public String getTier() {
		return tier;
	}
	public void setTier(String tier) {
		this.tier = tier;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
	
	