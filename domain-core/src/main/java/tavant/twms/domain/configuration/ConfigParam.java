package tavant.twms.domain.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ConfigParam implements AuditableColumns{

	@Id
	@GeneratedValue(generator = "ConfigParam")
	@GenericGenerator(name = "ConfigParam", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CONFIG_PARAM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String displayName;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String type;

    private String logicalGroup;

    private String sections;
	
/*	@ManyToOne
	private LogicalGroup logicalGroup;
*/
    @Filter(name = "bu_name", condition = "business_unit_info in (:name)")
    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "config_param", nullable = false, updatable = false, insertable = true)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<ConfigValue> values = new ArrayList<ConfigValue>();

    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "config_param_options_mapping", joinColumns = { @JoinColumn(name = "param_id") }, inverseJoinColumns = { @JoinColumn(name = "option_id") })
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<ConfigParamOption> paramOptions; 
    
    
    private String paramDisplayType;
    
    @Transient
    private List<String> dbParameterValues;
    
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}


    public List<ConfigValue> getValues() {
		return this.values;
	}

    public void setValues(List<ConfigValue> values) {
		this.values = values;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

/*	public LogicalGroup getLogicalGroup() {
		return logicalGroup;
	}

	public void setLogicalGroup(LogicalGroup logicalGroup) {
		this.logicalGroup = logicalGroup;
	}
*/
	public List<ConfigParamOption> getParamOptions() {
		return paramOptions;
	}

	public void setParamOptions(List<ConfigParamOption> paramOptions) {
		this.paramOptions = paramOptions;
	}

	public String getParamDisplayType() {
		return paramDisplayType;
	}

	public void setParamDisplayType(String paramDisplayType) {
		this.paramDisplayType = paramDisplayType;
	}

	public List<String> getDbParameterValues() {
		return dbParameterValues;
	}

	public void setDbParameterValues(List<String> uiValues) {
		this.dbParameterValues = uiValues;
	}

	public boolean equals(Object obj){
		
		if(obj!=null){
			ConfigParam cfgParam = (ConfigParam) obj;
			if(cfgParam.getId().longValue() == this.id.longValue()){
				return true;
			}
		}
		
		return false;
	}

    public String getLogicalGroup() {
        return logicalGroup;
    }

    public void setLogicalGroup(String logicalGroup) {
        this.logicalGroup = logicalGroup;
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }
}
