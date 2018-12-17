package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author priyank.gupta - Yep thats me! :) 
 *
 */
@Entity
@Table(name = "event_state")
@SuppressWarnings("serial")
public class EventState implements Serializable, AuditableColumns
{
	
	//These are the possible values for an event state and should always be picked from here.
	//IMPORTANT: Any new state added here, should also be added in app-context file so that it is respectively
	//loaded in hash map for the handler look up. I know, you have never seen a design more slick and smooth,
	//I guess it comes as a birth right to me! ;) 
	public static final String CLAIM_SUBMISSION_STATE = "CLAIM_SUBMISSION_STATE";
	public static final String CLAIM_PROCESSOR_REVIEW_STATE = "CLAIM_PROCESSOR_REVIEW";
	public static final String SERVICE_MANAGER_REVIEW = "SERVICE_MANAGER_REVIEW";
	public static final String SERVICE_MANAGER_RESPONSE = "SERVICE_MANAGER_RESPONSE";
	public static final String CLAIM_FORWARDED = "CLAIM_FORWARDED";
	public static final String CLAIM_TRANSFERED = "CLAIM_TRANSFERED";
	public static final String ADVICE_REQUEST = "ADVICE_REQUEST";
	public static final String CLAIM_REPLIES = "CLAIM_REPLIES";
	public static final String WAITING_FOR_PART_RETURNS = "WAITING_FOR_PART_RETURNS";
	public static final String REJECTED_PART_RETURN = "REJECTED_PART_RETURN";
	public static final String CLAIM_REACCEPTED = "CLAIM_REACCEPTED";
	public static final String CLAIM_ACCEPTED_AND_CLOSED = "CLAIM_ACCEPTED_AND_CLOSED";
	public static final String CLAIM_DENIED_AND_CLOSED = "CLAIM_DENIED_AND_CLOSED";
	public static final String CLAIM_APPEALED = "CLAIM_APPEALED";	
	public static final String PARTS_REJECTED_ON_CLAIM = "PARTS_REJECTED_ON_CLAIM";	
	public static final String CLAIM_DENIED_FOR_LACK_OF_PART_RETURNS = "CLAIM_DENIED_FOR_LACK_OF_PART_RETURNS";
	public static final String WAITING_FOR_LABOR = "WAITING_FOR_LABOR";
	public static final String CP_REVIEW = "CP_REVIEW";
	public static final String CP_TRANSFER = "CP_TRANSFER";
	public static final String PENDING_AUTHORIZATION = "PENDING_AUTHORIZATION";	
	
	//Part Return States
	public static final String PART_RETURN_SHIPPED = "PART_RETURN_SHIPPED";
	public static final String DUE_PART_RETURN_RECEIPT = "DUE_PART_RETURN_RECEIPT";
	public static final String START_PART_RETURN = "START_PART_RETURN";
	public static final String PART_MOVED_TO_OVERDUE = "PART_MOVED_TO_OVERDUE";
	public static final String MISMATCH_OF_COVERAGE = "MISMATCH_OF_COVERAGE";
	
	//Fleet
	public static final String QUOTE_DENIED_AND_CLOSED = "QUOTE_DENIED_AND_CLOSED";
	public static final String CONTRACT_EXPIRY = "CONTRACT_EXPIRY";
	public static final String SERVICE_REQUEST_SUBMITTED = "SERVICE_REQUEST_SUBMITTED";
	public static final String SERVICE_REQUEST_ASSIGNED = "SERVICE_REQUEST_ASSIGNED";
	public static final String SERVICE_REQUEST_DISPATCHED = "SERVICE_REQUEST_DISPATCHED";
	public static final String SERVICE_REQUEST_CLOSED = "SERVICE_REQUEST_CLOSED";
	public static final String SERVICE_REQUEST_DRAFT_DELETED = "SERVICE_REQUEST_DRAFT_DELETED";
	public static final String SERVICE_REQUEST_UNASSIGNED = "SERVICE_REQUEST_UNASSIGNED";
	public static final String QUOTE_RECEIVED_FOR_APPROVAL = "QUOTE_RECEIVED_FOR_APPROVAL";
	public static final String QUOTE_APPROVED = "QUOTE_APPROVED";
	public static final String QUOTE_RECEIVED_FOR_ACCEPTANCE = "QUOTE_RECEIVED_FOR_ACCEPTANCE";
	public static final String QUOTE_RECEIVED_FOR_REVISION = "QUOTE_RECEIVED_FOR_REVISION";
	public static final String QUOTE_FLEET_RECOMMENDATION = "QUOTE_FLEET_RECOMMENDATION";
	public static final String SERVICE_REQUEST_AGING_REMINDER="SERVICE_REQUEST_AGING_REMINDER";
	public static final String EQUIPMENT_TRANSFER_CUSTOMER_LOCATION_RECIEVED="EQUIPMENT_TRANSFER_CUSTOMER_LOCATION_RECIEVED";
	public static final String EQUIPMENT_TRANSFER_CUSTOMER_LOCATION_COMPLETED="EQUIPMENT_TRANSFER_CUSTOMER_LOCATION_COMPLETED";
	public static final String FLEET_CLAIM_REJECTED="FLEET_CLAIM_REJECTED";
	public static final String FLEET_CLAIM_RESUBMITTED="FLEET_CLAIM_RESUBMITTED";
	public static final String FLEET_CLAIM_TRANSFERRED="FLEET_CLAIM_TRANSFERRED";
	public static final String FLEET_CLAIM_RECEIVED_IN_PROCESSOR_REVIEW="FLEET_CLAIM_RECEIVED_IN_PROCESSOR_REVIEW";
	public static final String FLEET_CLAIM_FORWARDED="FLEET_CLAIM_FORWARDED";
	public static final String FLEET_CLAIM_APPEALED="FLEET_CLAIM_APPEALED";
	public static final String QUOTE_REQUESTED_FOR_REVISION = "QUOTE_REQUESTED_FOR_REVISION";
	public static final String FLEET_CLAIM_PRE_INVOICE_APPROVED="FLEET_CLAIM_PRE_INVOICE_APPROVED";
	public static final String FLEET_CLAIM_ACCEPTED="FLEET_CLAIM_ACCEPTED";
	public static final String EQUIPMENT_SCRAPPED_OR_INACTIVE="EQUIPMENT_SCRAPPED_OR_INACTIVE";
	public static final String EQUIPMENT_MOVED="EQUIPMENT_MOVED";
	public static final String QUOTE_PROCESS_REMINDER = "QUOTE_PROCESS_REMINDER";
	public static final String QUOTE_EXPIRY = "QUOTE_EXPIRY";
	public static final String SERVICE_REQUEST_ON_HOLD = "SERVICE_REQUEST_ON_HOLD";
	public static final String SERVICE_REQUEST_WORK_IN_PROGRESS = "SERVICE_REQUEST_WORK_IN_PROGRESS";
	public static final String SERVICE_REQUEST_SEND_BACK_TO_NMHG = "SERVICE_REQUEST_SEND_BACK_TO_NMHG";
	public static final String SERVICE_REQUEST_COMPLETED = "SERVICE_REQUEST_COMPLETED";

	@Id
	@GeneratedValue(generator = "EventState")
	@GenericGenerator(name = "EventState", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "EVENT_STATE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private String name;

	private String displayName;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "event_role_mapping", joinColumns = { @JoinColumn(name = "event_types") }, inverseJoinColumns = { @JoinColumn(name = "roles") })
	private Set<Role> roles = new HashSet<Role>();

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}	
	
	 @Override
	 public String toString() 
	 {
	 	 return new ToStringCreator(this).append("id", this.id).append("name", this.name).toString();
	 }

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public boolean isAssociatedToRole(String role)
	{
		Assert.hasText(role);
		for (Role r : roles) {
			if (role.equals(r.getName())) {
				return true;
			}
		}
		return false;
	}
	 
}
