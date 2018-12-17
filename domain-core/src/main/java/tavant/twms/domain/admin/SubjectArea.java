package tavant.twms.domain.admin;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="MST_ADMIN_SUBJECT_AREA")
public class SubjectArea implements Serializable {

	@Id	
	private Long id;
	
	@Column(nullable = false)	
	private String name;
	
	private String description;
	
	@ManyToMany(fetch=FetchType.LAZY,mappedBy="subjectAreas")
	private List<FunctionalArea> functionalAreas;

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

	public List<FunctionalArea> getFunctionalAreas() {
		return functionalAreas;
	}

	public void setFunctionalAreas(List<FunctionalArea> functionalAreas) {
		this.functionalAreas = functionalAreas;
	}

	
}
