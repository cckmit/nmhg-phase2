package tavant.twms.domain.claim;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class MatchReadInfo implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "MatchReadInfo")
	@GenericGenerator(name = "MatchReadInfo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MATCH_READ_INFO_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
 	private Long id;
	
	@Column
	private String ownerName ;
	
	@Column
	private String ownerCity ;
	
	@Column
	private String ownerState ;
	
	@Column
	private String ownerCountry ;
	
	@Column
	private String ownerZipcode ;
	
	@Column
	private Long score;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
	public boolean isValid() {
		return(StringUtils.hasText(this.ownerName) && StringUtils.hasText(this.ownerCity)
				&& StringUtils.hasText(this.ownerCountry) && StringUtils.hasText(this.ownerState)
					&& StringUtils.hasText(this.ownerZipcode));
	}

	public Long getScore() {
		return this.score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerCity() {
		return this.ownerCity;
	}

	public void setOwnerCity(String ownerCity) {
		this.ownerCity = ownerCity;
	}

	public String getOwnerState() {
		return this.ownerState;
	}

	public void setOwnerState(String ownerState) {
		this.ownerState = ownerState;
	}

	public String getOwnerCountry() {
		return this.ownerCountry;
	}

	public void setOwnerCountry(String ownerCountry) {
		this.ownerCountry = ownerCountry;
	}

	public String getOwnerZipcode() {
		return this.ownerZipcode;
	}

	public void setOwnerZipcode(String ownerZipcode) {
		this.ownerZipcode = ownerZipcode;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
}
