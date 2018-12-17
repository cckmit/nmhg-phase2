package tavant.twms.domain.orgmodel;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({ @Filter(name = "excludeInactive") })
@Table(name="TECHNICIAN_CERTIFICATION")
public class TechnicianCertification implements AuditableColumns{

	@Id
    @GeneratedValue(generator = "TechnicianCertification")
	@GenericGenerator(name = "TechnicianCertification", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "TECHNICIAN_CERTIFICATION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"), 
			@Parameter(name = "increment_size", value = "20")})
	private Long id;
	
	@Version
	private int version;
	
	private String certificationName;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate certificationFromDate;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate certificationToDate;
	
	private String brand;	
	
	@Transient
	private String categoryLevel;
	
	@Transient
	private String categoryName;
	
	public String getCategoryLevel() {
		return categoryLevel;
	}

	public void setCategoryLevel(String categoryLevel) {
		this.categoryLevel = categoryLevel;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tech_user")
	private Technician techUser;
	
	@OneToOne(fetch = FetchType.LAZY)
	private CoreCertification coreCertification;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name="TECH_PRODUCT_CERTIFICATIONS",joinColumns = @JoinColumn( name="TECHNICIAN_CERTIFICATION"),
            inverseJoinColumns = @JoinColumn( name="PRODUCT_CERTIFICATION"))
	@Cascade({CascadeType.ALL})
	private List<SeriesRefCertification> seriesCertification;
	
	private Boolean isCoreLevel;

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

	public String getCertificationName() {
		return certificationName;
	}

	public void setCertificationName(String certificationName) {
		this.certificationName = certificationName;
	}

	public CalendarDate getCertificationFromDate() {
		return certificationFromDate;
	}

	public void setCertificationFromDate(CalendarDate certificationFromDate) {
		this.certificationFromDate = certificationFromDate;
	}

	public CalendarDate getCertificationToDate() {
		return certificationToDate;
	}

	public void setCertificationToDate(CalendarDate certificationToDate) {
		this.certificationToDate = certificationToDate;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Technician getTechUser() {
		return techUser;
	}

	public void setTechUser(Technician techUser) {
		this.techUser = techUser;
	}

	public CoreCertification getCoreCertification() {
		return coreCertification;
	}

	public void setCoreCertification(CoreCertification coreCertifications) {
		this.coreCertification = coreCertifications;
	}

	public Boolean getIsCoreLevel() {
		return isCoreLevel;
	}

	public void setIsCoreLevel(Boolean isCoreLevel) {
		this.isCoreLevel = isCoreLevel;
	}

	public List<SeriesRefCertification> getSeriesCertification() {
		return seriesCertification;
	}

	public void setSeriesCertification(
			List<SeriesRefCertification> seriesCertification) {
		this.seriesCertification = seriesCertification;
	}
	
}
