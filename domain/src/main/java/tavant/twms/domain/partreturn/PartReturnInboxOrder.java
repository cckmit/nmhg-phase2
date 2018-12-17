package tavant.twms.domain.partreturn;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="parts_inbox_types")
public class PartReturnInboxOrder {
	  @Id
	    @GeneratedValue(generator = "PartReturnInboxOrder")
		@GenericGenerator(name = "PartReturnInboxOrder", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
				@Parameter(name = "sequence_name", value = "PARTS_INBOX_SEQ"),
				@Parameter(name = "initial_value", value = "1000"),
				@Parameter(name = "increment_size", value = "20") })
	    private Long id;
	private String inboxName;
	private Integer priority;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getInboxName() {
		return inboxName;
	}
	public void setInboxName(String inboxName) {
		this.inboxName = inboxName;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	

}
