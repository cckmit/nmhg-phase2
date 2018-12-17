/**
 * 
 */
package tavant.twms.domain.inventory;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

/**
 * @author fatima.marneni
 *
 */
@Entity
public class InventoryStolenTransaction implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "ServiceProcedureDef")
	@GenericGenerator(name = "ServiceProcedureDef", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SERVICE_PROCEDUREDEF_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@Version
	private int version;
	
	private String comments;
	
	private CalendarDate conditionUpdatedOn;
	
	private String previousItemCondition;
	
	private CalendarDate dateOfStolenOrUnstolen;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
	public InventoryStolenTransaction(String comments, CalendarDate conditionUpdatedOn, String previousItemCondition){
		super();
		this.comments = comments;
		this.conditionUpdatedOn = conditionUpdatedOn;
		this.previousItemCondition = previousItemCondition;		
	}
	
	public InventoryStolenTransaction(String comments, CalendarDate conditionUpdatedOn, String previousItemCondition, CalendarDate dateOfStolenOrUnstolen){
		super();
		this.comments = comments;
		this.conditionUpdatedOn = conditionUpdatedOn;
		this.previousItemCondition = previousItemCondition;
		this.dateOfStolenOrUnstolen = dateOfStolenOrUnstolen;
	}
	
	//for hibernate
	
	public InventoryStolenTransaction(){
		super();
	}

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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public CalendarDate getConditionUpdatedOn() {
		return conditionUpdatedOn;
	}

	public void setConditionUpdatedOn(CalendarDate conditionUpdatedOn) {
		this.conditionUpdatedOn = conditionUpdatedOn;
	}
    	
	public String getPreviousItemCondition() {
		return previousItemCondition;
	}

	public void setPreviousItemCondition(String previousItemCondition) {
		this.previousItemCondition = previousItemCondition;
	}

	@Override
	public String toString(){
		return new ToStringCreator(this).append("id", this.id).append("comments",this.comments).
		append("conditionUpdatedOn",this.conditionUpdatedOn).
		append("previousItemCondition",this.previousItemCondition).append("dateOfStolenOrUnstolen", this.dateOfStolenOrUnstolen).toString();		
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public CalendarDate getDateOfStolenOrUnstolen() {
		return dateOfStolenOrUnstolen;
	}

	public void setDateOfStolenOrUnstolen(CalendarDate dateOfStolenOrUnstolen) {
		this.dateOfStolenOrUnstolen = dateOfStolenOrUnstolen;
	}
	
	

	
	
}
