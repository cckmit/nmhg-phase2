package tavant.twms.domain.inventory;

import java.sql.Types;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


/**
 * @author Suneetha N
 * 
 */

@Entity
@FilterDefs({
        @FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
        })
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class MachineHours{
    @Id
    @GeneratedValue(generator = "MachineHours")
    @GenericGenerator(name = "MachineHours", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "Machine_Hours_SEQ"),
            @Parameter(name = "initial_value", value = "1500"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    private Long fleetInventoryItemId;
    
    private Long inventoryItemId;
    
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.inventory.SourceType"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private SourceType sourceType;
    
    private String eqTimeZone;
    
	private Date mtrReadingDate;

	private Long mtrReading;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public Long getFleetInventoryItemId() {
        return fleetInventoryItemId;
    }

    public void setFleetInventoryItemId(Long fleetInventoryItemId) {
        this.fleetInventoryItemId = fleetInventoryItemId;
    }

    public Long getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getEqTimeZone() {
        return eqTimeZone;
    }

    public void setEqTimeZone(String eqTimeZone) {
        this.eqTimeZone = eqTimeZone;
    }

    public Date getMtrReadingDate() {
        return mtrReadingDate;
    }

    public void setMtrReadingDate(Date mtrReadingDate) {
        this.mtrReadingDate = mtrReadingDate;
    }

    public Long getMtrReading() {
        return mtrReading;
    }

    public void setMtrReading(Long mtrReading) {
        this.mtrReading = mtrReading;
    }
       
}
