package tavant.twms.domain.common;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.security.AuditableColumns;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 21/12/13
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="BU_SETTINGS")
@Filters({
        @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class BUSettings implements AuditableColumns,BusinessUnitAware {

    @Id
    @GeneratedValue(generator = "busettings")
    @GenericGenerator(name = "busettings", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "BU_SETTINGS_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    private String keyName;
    private String keyValue;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    @Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
        this.businessUnitInfo = buAudit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }
}
