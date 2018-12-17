package tavant.twms.domain.laborType;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.claim.ServiceDetail;


@Entity
public class LaborSplit {
    @Id
    @GeneratedValue(generator = "LaborSplit")
    @GenericGenerator(name = "LaborSplit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "LABOR_SPLIT_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20")})
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_Detail ", insertable = false, updatable = false)
    private ServiceDetail serviceDetail;

    @OneToOne(fetch = FetchType.LAZY)
    private LaborType laborType;

    private BigDecimal hoursSpent;

    private String reason;

    private boolean inclusive;

    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceDetail getServiceDetail() {
        return serviceDetail;
    }

    public void setServiceDetail(ServiceDetail serviceDetail) {
        this.serviceDetail = serviceDetail;
    }

    public LaborType getLaborType() {
        return laborType;
    }

    public void setLaborType(LaborType laborType) {
        this.laborType = laborType;
    }

    public BigDecimal getHoursSpent() {
        return hoursSpent;
    }

    public void setHoursSpent(BigDecimal hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean getInclusive() {
        return inclusive;
    }

    public void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public LaborSplit clone() {
        LaborSplit laborSplit = new LaborSplit();
        laborSplit.setHoursSpent(hoursSpent);
        laborSplit.setInclusive(inclusive);
        laborSplit.setLaborType(laborType);
        laborSplit.setServiceDetail(serviceDetail);
        laborSplit.setReason(reason);
        return laborSplit;
    }
}
