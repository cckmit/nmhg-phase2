package tavant.twms.domain.policy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;



/**
 * @author jitesh.jain
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "applicable_customer_types")
public class ApplicableCustomerTypes {

	@Id
	@GeneratedValue(generator = "ApplicableCustomerTypes")
	@GenericGenerator(name = "ApplicableCustomerTypes", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "APPLICABLE_CUSTOMER_TYPES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String type;
	
	public ApplicableCustomerTypes() {
        super();
    }

    public ApplicableCustomerTypes(String type) {
        super();
        this.type = type;
    }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
