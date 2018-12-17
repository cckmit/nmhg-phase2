package tavant.twms.domain.bu;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class LogicalGroup{
	
	@Id
	@GeneratedValue(generator = "LogicalGroup")
	@GenericGenerator(name = "LogicalGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "Logical_Group_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	private String name;
	
	private String description;

	
/*	@OneToMany(mappedBy="logicalGroup", fetch = FetchType.LAZY)
	private List<ConfigParam> configParam;
*/	
	public long getId() {
		return id;
	}

	public void setId(long id) {
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

/*	public List<ConfigParam> getConfigParam() {
		return configParam;
	}

	public void setConfigParam(List<ConfigParam> configParam) {
		this.configParam = configParam;
	}*/

	public void setId(Long id) {
		this.id = id;
	}
	
}
