package tavant.twms.domain.policy;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

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
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Mar 9, 2010
 * Time: 10:46:10 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Filters({
    @Filter(name = "excludeInactive")
        })
@FilterDef(name = "bu_name", parameters = {@ParamDef(name = "name", type = "string")})
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class ExtendedWarrantyNotification implements BusinessUnitAware, AuditableColumns {

    @Id
    @GeneratedValue(generator = "ExtendedWarrantyNotification")
    @GenericGenerator(name = "ExtendedWarrantyNotification", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "EXTENDED_WNTY_NOTIFICATION_SEQ"),
        @Parameter(name = "initial_value", value = "1000"),
        @Parameter(name = "increment_size", value = "20")})
    private Long id;

    @Version
    private int version;

    private String notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization purchasingDealer;

    @ManyToOne(fetch = FetchType.LAZY)
    private InventoryItem forUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    private PolicyDefinition policy;

    private String salesOrderNumber;

    private String salesOrderLineNumber;

    private String invoiceNumber;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate invoiceDate;

    @Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

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

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Organization getPurchasingDealer() {
        return purchasingDealer;
    }

    public void setPurchasingDealer(Organization purchasingDealer) {
        this.purchasingDealer = purchasingDealer;
    }

    public InventoryItem getForUnit() {
        return forUnit;
    }

    public void setForUnit(InventoryItem forUnit) {
        this.forUnit = forUnit;
    }

    public PolicyDefinition getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyDefinition policy) {
        this.policy = policy;
    }

    public String getSalesOrderNumber() {
        return salesOrderNumber;
    }

    public void setSalesOrderNumber(String salesOrderNumber) {
        this.salesOrderNumber = salesOrderNumber;
    }

    public String getSalesOrderLineNumber() {
        return salesOrderLineNumber;
    }

    public void setSalesOrderLineNumber(String salesOrderLineNumber) {
        this.salesOrderLineNumber = salesOrderLineNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public CalendarDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(CalendarDate invoiceDate) {
        this.invoiceDate = invoiceDate;
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
}
