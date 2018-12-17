package tavant.twms.domain.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import tavant.twms.domain.orgmodel.User;

@Entity
public class RoleDefinition {

	@Id
	@GeneratedValue(generator = "RoleDefinition")
	@GenericGenerator(name = "RoleDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ROLE_DEFINITION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@Column(nullable = false)	
	private String name;
	
	
	private String description;
	
    @OneToMany(fetch = FetchType.EAGER)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "ROLE_DEF_ID", nullable = false)
    private List<Permission> permissions  = new ArrayList<Permission>();
    
    @ManyToMany(mappedBy = "roleList")
  	private Set<User> userList = new HashSet<User>();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public Set<User> getUserList() {
		return userList;
	}

	public void setUserList(Set<User> userList) {
		this.userList = userList;
	}

	@Transient
	public void removeWhiteSpacesFromFields(){
		this.name = StringUtils.stripToEmpty(this.name);
		this.description = StringUtils.stripToEmpty(this.description);		
	}
	
}
