/**
 *
 */
package tavant.twms.domain.orgmodel;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import javax.validation.constraints.NotNull;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author mritunjay.kumar
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class MSA implements AuditableColumns{

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private int version;

	private Integer zip;

	private Character zipType;

	@NotNull
	private String st;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private String city;

	private String county;

	private String msa;

	private String zip2;

	private String country;

	@Column(nullable = false)
	private Long msaId;

	private Long timeZone;

	@Column(name = "daylight_saving")
	private Character dayLightSaving;

	private String fips;

	private Long msaCode;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Integer getZip() {
		return zip;
	}

	public void setZip(Integer zip) {
		this.zip = zip;
	}

	public Character getZipType() {
		return zipType;
	}

	public void setZipType(Character zipType) {
		this.zipType = zipType;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getMsa() {
		return msa;
	}

	public void setMsa(String msa) {
		this.msa = msa;
	}

	public String getZip2() {
		return zip2;
	}

	public void setZip2(String zip2) {
		this.zip2 = zip2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getMsaId() {
		return msaId;
	}

	public void setMsaId(Long msaId) {
		this.msaId = msaId;
	}

	public Long getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(Long timeZone) {
		this.timeZone = timeZone;
	}

	public Character getDayLightSaving() {
		return dayLightSaving;
	}

	public void setDayLightSaving(Character dayLightSaving) {
		this.dayLightSaving = dayLightSaving;
	}

	public String getFips() {
		return fips;
	}

	public void setFips(String fips) {
		this.fips = fips;
	}

	public Long getMsaCode() {
		return msaCode;
	}

	public void setMsaCode(Long msaCode) {
		this.msaCode = msaCode;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
