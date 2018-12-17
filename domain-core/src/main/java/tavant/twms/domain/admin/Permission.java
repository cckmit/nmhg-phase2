package tavant.twms.domain.admin;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;

/**
 * @author prashanth.konda
 *
 */
@Entity
@Table(name="ROLE_PERMISSION_MAPPING")
public class Permission implements Serializable {

	@Id
	@GeneratedValue(generator = "RolePermissionMapping")
	@GenericGenerator(name = "RolePermissionMapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ROLE_PERMISSION_MAPPING_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@ManyToOne(targetEntity=UserAction.class,fetch=FetchType.EAGER)
	private UserAction action;
	
	@ManyToOne(targetEntity=FunctionalArea.class,fetch=FetchType.EAGER)	
	private FunctionalArea functionalArea;
	
    @ManyToOne(targetEntity=SubjectArea.class,fetch=FetchType.EAGER)	
	private SubjectArea subjectArea;
        
    private String permissionString; 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserAction getAction() {
		return action;
	}

	public void setAction(UserAction action) {
		this.action = action;
	}

	public FunctionalArea getFunctionalArea() {
		return functionalArea;
	}

	public void setFunctionalArea(FunctionalArea functionalArea) {
		this.functionalArea = functionalArea;
	}
	
	public String getPermissionString(){
		return this.permissionString;
	}


	public SubjectArea getSubjectArea() {
		return subjectArea;
	}

	public void setSubjectArea(SubjectArea subjectArea) {
		this.subjectArea = subjectArea;
	}

	public Permission(UserAction action, FunctionalArea functionalArea,SubjectArea subjectArea) {
		super();
		this.action = action;
		this.functionalArea = functionalArea;
		this.subjectArea = subjectArea;
		this.permissionString = this.subjectArea.getName() + ":" + this.functionalArea.getName() + ":" + this.action.getAction();
	}

	public Permission() {
		super();
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}
}
