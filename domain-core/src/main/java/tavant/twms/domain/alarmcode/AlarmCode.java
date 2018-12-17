/**
 *
 */
package  tavant.twms.domain.alarmcode;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author surajdeo.prasad
 *
 */

@Entity
//@Table(name="alarm_code")
@Filters({@Filter(name = "excludeInactive")})
@FilterDef(name = "bu_name", parameters = {@ParamDef(name = "name", type = "string")})
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class AlarmCode implements Comparable<AlarmCode>, BusinessUnitAware, AuditableColumns {

    @Id
    @GeneratedValue(generator = "AlarmCode")
    @GenericGenerator(name = "AlarmCode", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "ALARM_CODE_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20")})
    private Long id;
    @Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();
    private String code;
    @Column(length = 4000)
    private String description;
    @ManyToMany(cascade = {javax.persistence.CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "item_groups_alarm_code", joinColumns = {@JoinColumn(name = "alarm_code")}, inverseJoinColumns = {@JoinColumn(name = "item_group")})
    private List<ItemGroup> applicableProducts = new ArrayList<ItemGroup>();
    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    @Version
    private int version;

    /*
      * (non-Javadoc)
      *
      * @see tavant.twms.domain.bu.BusinessUnitAware#getBusinessUnitInfo()
      */
    public BusinessUnitInfo getBusinessUnitInfo() {
        // TODO Auto-generated method stub
        return this.businessUnitInfo;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * tavant.twms.domain.bu.BusinessUnitAware#setBusinessUnitInfo(tavant.twms
      * .domain.bu.BusinessUnitInfo)
      */
    public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
        // TODO Auto-generated method stub
        this.businessUnitInfo = buAudit;
    }

    /*
      * (non-Javadoc)
      *
      * @see tavant.twms.security.AuditableColumns#getD()
      */
    public AuditableColEntity getD() {
        // TODO Auto-generated method stub
        return d;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * tavant.twms.security.AuditableColumns#setD(tavant.twms.domain.common.
      * AuditableColEntity)
      */
    public void setD(AuditableColEntity auditableColEntity) {
        // TODO Auto-generated method stub
        this.d = auditableColEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ItemGroup> getApplicableProducts() {
        return applicableProducts;
    }

    public void setApplicableProducts(List<ItemGroup> applicableProducts) {
        this.applicableProducts = applicableProducts;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int compareTo(AlarmCode other) {
        return this.getCode().compareTo(other.getCode());
    }

    public AlarmCode clone() {
        AlarmCode alarmCode = new AlarmCode();
        //TODO check these lists
        alarmCode.setApplicableProducts(applicableProducts);
        alarmCode.setBusinessUnitInfo(businessUnitInfo);
        alarmCode.setCode(code);
        alarmCode.setDescription(description);
        return alarmCode;
    }
}
