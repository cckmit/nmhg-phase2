package tavant.twms.domain.claim.payment.rates;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name="LABOR_REPAIR_DATE_HISTORY")
public class LaborRateRepairDateAudit implements AuditableColumns {
	
	@Id
	@GeneratedValue(generator = "LaborRateRepairDateAudit")
	@GenericGenerator(name = "LaborRateRepairDateAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "LABOR_REPAIR_DATE_HISTORY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="fromDate",column=@Column(name="from_date",nullable=false)),
            @AttributeOverride(name="tillDate",column=@Column(name="till_date",nullable=false))
    })
    private CalendarDuration duration;
		
	public CalendarDuration getDuration() {
		return duration;
	}

	public void setDuration(CalendarDuration duration) {
		this.duration = duration;
	}

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "AMT"), @Column(name = "CURR") })
	private Money rate;
	
	public Money getRate() {
		return rate;
	}

	public void setRate(Money rate) {
		this.rate = rate;
	}
   
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected AuditableColEntity d = new AuditableColEntity();
	
	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	
	
	
	

}
