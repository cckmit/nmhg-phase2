package tavant.twms.domain.partreturn;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Parameter;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 19/2/13
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ShipmentLoadDimension {

    @Id
    @GeneratedValue(generator = "shipmentLoadDimension")
    @GenericGenerator(name = "shipmentLoadDimension", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "SHIP_LOAD_DIMENSION_SEQ"),
            @org.hibernate.annotations.Parameter(name = "initial_value", value = "100"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "10") })
    private Long id;

    @Version
    private int version;


    @Column
    private String height;

    @Column
    private String weight;

    @Column
    private String breadth;

    @Column
    private String length;

    @Column
    private String loadType;

    @ManyToOne
    private Shipment shipment;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBreadth() {
        return breadth;
    }

    public void setBreadth(String breadth) {
        this.breadth = breadth;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
