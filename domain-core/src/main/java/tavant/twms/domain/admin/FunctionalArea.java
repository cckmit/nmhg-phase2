package tavant.twms.domain.admin;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="MST_ADMIN_FNC_AREA")
public class FunctionalArea implements Serializable {

	@Id	
	private Long id;
	
	@Column(nullable = false)	
	private String name;
	
	private String description;
	
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "subject_func_area_mapping", joinColumns = { @JoinColumn(name = "functional_area") }, inverseJoinColumns = { @JoinColumn(name = "subject_area") })	
	private List<SubjectArea> subjectAreas;	

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

	public List<SubjectArea> getSubjectAreas() {
		return subjectAreas;
	}

	public void setSubjectAreas(List<SubjectArea> subjectAreas) {
		this.subjectAreas = subjectAreas;
	}


	
	
}
