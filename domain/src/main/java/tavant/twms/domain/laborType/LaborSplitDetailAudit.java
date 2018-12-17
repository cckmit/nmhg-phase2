package tavant.twms.domain.laborType;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.money.Money;


@Entity
public class LaborSplitDetailAudit {
	@Id
	@GeneratedValue(generator = "LaborSplitDetailAudit")
	@GenericGenerator(name = "LaborSplitDetailAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "LABOR_SPLIT_DETAIL_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private LaborType laborType;
	
	private BigDecimal laborHrs;
	 
	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "labor_Rate_amt", nullable = true),
	@Column(name = "labor_Rate_curr", nullable = true) })
	private Money laborRate;
	
	private String name;
	
	private BigDecimal multiplicationValue;
	
	
	@Version
	private int version;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public LaborType getLaborType() {
		return laborType;
	}
	public void setLaborType(LaborType laborType) {
		this.laborType = laborType;
	}
	
	public BigDecimal getLaborHrs() {
		return laborHrs;
	}
	public void setLaborHrs(BigDecimal laborHrs) {
		this.laborHrs = laborHrs;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public Money getLaborRate() {
		return laborRate;
	}
	public void setLaborRate(Money laborRate) {
		this.laborRate = laborRate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public BigDecimal getMultiplicationValue() {
		return multiplicationValue;
	}
	public void setMultiplicationValue(BigDecimal multiplicationValue) {
		this.multiplicationValue = multiplicationValue;
	}
	
	public Money getPaymentDetailForSplitAudit(){
		if(getLaborType()!=null){
			return getLaborRate().times(getLaborHrs()).times(getMultiplicationValue());
		}else{
			return getLaborRate().times(getLaborHrs());
		}
	}
	public Money getLaborRateForSplitAudit(){
		if(getLaborType()!=null){
			return getLaborRate().times(getMultiplicationValue());
		}else if (getLaborRate() != null) {
			return getLaborRate();
		}else {
			return null;
		}
		
	}
	
	@Override
	public LaborSplitDetailAudit clone(){
		LaborSplitDetailAudit split = new LaborSplitDetailAudit();
		split.setLaborHrs(this.laborHrs);
		split.setLaborRate(this.laborRate);
		split.setLaborType(this.laborType);
		split.setMultiplicationValue(this.multiplicationValue);
		split.setName(this.name);
		split.setVersion(0);
		return split;
	}
}
