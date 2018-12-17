package tavant.twms.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.bu.BusinessUnitInfo;

import javax.persistence.Entity;
import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import org.hibernate.annotations.*;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 19, 2009
 * Time: 12:54:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Filters({
  @Filter(name="excludeInactive")
})
public class SourceWarehouse implements AuditableColumns , Comparable<SourceWarehouse> {
    @Id
	@GeneratedValue(generator = "SourceWarehouse")
	@GenericGenerator(name = "SourceWarehouse", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SOURCE_WAREHOUSE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

    private String name;

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    private String code;
    
    public int compareTo(SourceWarehouse other) {
        if (other == null) {
            return 1;
        }
        int nameCompare = this.name.toUpperCase().compareTo(other.getName().toUpperCase());
        return nameCompare;
    }

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

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
        this.businessUnitInfo = businessUnitInfo;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
    
}
