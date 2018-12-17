package tavant.twms.domain.reports;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

@SuppressWarnings("serial")
@Entity
@Table(name="WARRANTY_PAYOUT_VIEW")
public class WarrantyPayoutView  implements Serializable {

	@Id
	private Long id;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate filedOnDate;
	
	private BigDecimal acceptedAmt;
	
	private String acceptedCurr;
	
	private String name;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate repairDate;
	
	private BigDecimal exchangeRate;

	public BigDecimal getAcceptedAmt() {
		return acceptedAmt;
	}

	public void setAcceptedAmt(BigDecimal acceptedAmt) {
		this.acceptedAmt = acceptedAmt;
	}

	public String getAcceptedCurr() {
		return acceptedCurr;
	}

	public void setAcceptedCurr(String acceptedCurr) {
		this.acceptedCurr = acceptedCurr;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public CalendarDate getFiledOnDate() {
		return filedOnDate;
	}

	public void setFiledOnDate(CalendarDate filedOnDate) {
		this.filedOnDate = filedOnDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CalendarDate getRepairDate() {
		return repairDate;
	}

	public void setRepairDate(CalendarDate repairDate) {
		this.repairDate = repairDate;
	}
	
	
}
