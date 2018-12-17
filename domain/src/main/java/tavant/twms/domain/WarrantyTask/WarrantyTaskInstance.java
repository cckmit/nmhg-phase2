package tavant.twms.domain.WarrantyTask;

import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import java.sql.Types;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Aug 29, 2008
 * Time: 12:43:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filters({
  @Filter(name="excludeInactive"),
  @Filter(name = "bu_name", condition = "business_unit_info in (:name)")
})

public class WarrantyTaskInstance implements AuditableColumns, BusinessUnitAware {

    @Id
    @GeneratedValue(generator = "WarrantyTaskInstance")
    @GenericGenerator(name = "WarrantyTaskInstance", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
    @Parameter(name = "sequence_name", value = "WARRANTY_TASK_INSTANCE_SEQ"),
    @Parameter(name = "initial_value", value = "1000"),
    @Parameter(name = "increment_size", value = "20")})
    public Long id;

    @Version
    private int version;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.policy.WarrantyStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    public WarrantyStatus status;

    // In case the task is related to MULTI DR/ETR then it points to the warrantyAudit of the 1st inventory
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    public WarrantyAudit warrantyAudit;

    public Boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    public User assignedTo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "WARRANTY_TASK_INCLUDED_ITEMS", joinColumns = { @JoinColumn(name = "WARRANTY_TASK") }, inverseJoinColumns = { @JoinColumn(name = "INV_ITEM") })
    private List<InventoryItem> forItems = new ArrayList<InventoryItem>();

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    //This acn be treated as a relation between warranty and warrantytaskInstance
    private String multiDRETRNumber;

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WarrantyStatus getStatus() {
        return status;
    }

    public void setStatus(WarrantyStatus status) {
        this.status = status;
    }

    public WarrantyAudit getWarrantyAudit() {
        return warrantyAudit;
    }

    public void setWarrantyAudit(WarrantyAudit warrantyAudit) {
        this.warrantyAudit = warrantyAudit;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<InventoryItem> getForItems() {
        return forItems;
    }

    public void setForItems(List<InventoryItem> forItems) {
        this.forItems = forItems;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMultiDRETRNumber() {
        return multiDRETRNumber;
    }

    public void setMultiDRETRNumber(String multiDRETRNumber) {
        this.multiDRETRNumber = multiDRETRNumber;
    }

    public InventoryItem getFirstItem(){
        return forItems.get(0);
    }

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
        this.businessUnitInfo = businessUnitInfo;
    }
}

