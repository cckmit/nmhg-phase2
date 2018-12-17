package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;



@Entity
public class Technician implements Serializable {
	
	@Id
	@GeneratedValue(generator = "Technician")
	@GenericGenerator(name = "Technician", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters ={
			@Parameter(name = "sequence_name", value = "TECHNICIAN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20")
	})
	private Long id;
	
	private String emailId;
	
	private String serviceManagerName;
	
	private String comments;
	
	private String status;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy="techUser")
	@Cascade( { CascadeType.SAVE_UPDATE})
	@OrderBy
	private List<TechnicianCertification> technicianCertifications;
	
    @OneToOne(fetch = FetchType.LAZY)
    private User orgUser;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getServiceManagerName() {
		return serviceManagerName;
	}

	public void setServiceManagerName(String serviceManagerName) {
		this.serviceManagerName = serviceManagerName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public List<TechnicianCertification> getTechnicianCertifications() {
		return technicianCertifications;
	}

	public void setTechnicianCertifications(
			List<TechnicianCertification> technicianCertifications) {
		this.technicianCertifications = technicianCertifications;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getOrgUser() {
		return orgUser;
	}

	public void setOrgUser(User orgUser) {
		this.orgUser = orgUser;
	}

}
