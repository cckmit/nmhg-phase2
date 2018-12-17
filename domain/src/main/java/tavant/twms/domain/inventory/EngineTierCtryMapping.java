package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.security.SecurityHelper;

@Entity
@Table(name="DIESEL_TIER_COUNTRY_MAPPING")
public class EngineTierCtryMapping {

	@Id
	@GeneratedValue(generator = "EngineTierCountryMapping")
	@GenericGenerator(name = "EngineTierCountryMapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ENGINE_TIER_CTRY_MAPPING_SEQ"),
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
	public Long getDieselTier() {
		return dieselTier;
	}
	public void setDieselTier(Long dieselTier) {
		this.dieselTier = dieselTier;
	}
	public Long getCountry() {
		return country;
	}
	public void setCountry(Long country) {
		this.country = country;
	}
	private Long dieselTier;
	private Long country;
	
}
