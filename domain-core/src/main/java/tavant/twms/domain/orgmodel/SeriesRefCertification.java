package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@SuppressWarnings("serial")
@Entity
@Filters({ @Filter(name = "excludeInactive") })
@Table(name="SERIES_REF_CERTIFICATION")
public class SeriesRefCertification implements Serializable,AuditableColumns {
	
	@Id
	@GeneratedValue(generator = "SeriesRefCertification")
	@GenericGenerator(name = "SeriesRefCertification", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_SeriesRefCertification"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private ItemGroup series;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate startDate;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate endDate;	
	

	@OneToMany(fetch= FetchType.LAZY,mappedBy="seriesRefCertfication")
	@Cascade({org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	@OrderBy
	private List<SeriesCertification> seriesCertification = new ArrayList<SeriesCertification>();

	public List<SeriesCertification> getSeriesCertification() {
		return seriesCertification;
	}

	public void setSeriesCertification(List<SeriesCertification> seriesCertification) {
		this.seriesCertification = seriesCertification;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ItemGroup getSeries() {
		return series;
	}

	public void setSeries(ItemGroup series) {
		this.series = series;
	}

	public CalendarDate getStartDate() {
		return startDate;
	}

	public void setStartDate(CalendarDate startDate) {
		this.startDate = startDate;
	}

	public CalendarDate getEndDate() {
		return endDate;
	}

	public void setEndDate(CalendarDate endDate) {
		this.endDate = endDate;
	}

//	public List<Certificates> getCertificates() {
//		return certificates;
//	}
//
//	public void setCertificates(List<Certificates> certificates) {
//		this.certificates = certificates;
//	}

}
