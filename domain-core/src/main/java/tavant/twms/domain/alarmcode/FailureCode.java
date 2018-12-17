package tavant.twms.domain.alarmcode;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.common.Views;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class FailureCode {

    @Id
    @GeneratedValue(generator = "FailureCode")
    @GenericGenerator(name = "FailureCode", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "Failure_Code_SEQ"),
            @Parameter(name = "initial_value", value = "1500"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;
    
    @JsonView(value = Views.Public.class)
    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({ CascadeType.SAVE_UPDATE })
    private AlarmCode alarmCode;
    
    private Long inventoryItemId;
    
    private Date failureDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlarmCode getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(AlarmCode alarmCode) {
        this.alarmCode = alarmCode;
    }

    public Long getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Date getFailureDate() {
        return failureDate;
    }

    public void setFailureDate(Date failureDate) {
        this.failureDate = failureDate;
    }
    
    
}
