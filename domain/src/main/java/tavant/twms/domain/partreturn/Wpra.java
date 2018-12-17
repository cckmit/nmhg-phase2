package tavant.twms.domain.partreturn;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.springframework.util.Assert;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.security.AuditableColumns;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 2/12/12
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Wpra implements AuditableColumns {
	@Id
	@GeneratedValue(generator = "wpra")
	@GenericGenerator(name = "wpra", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "WPRA_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String wpraNumber;

    private Date generateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location destination;

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceProvider shippedBy;

    // Need to change this to a sorted set later - Vineeth.
    @OneToMany(mappedBy = "wpra", fetch = FetchType.LAZY)
    private final List<PartReturn> parts = new ArrayList<PartReturn>();

    /*//We will need this while shipment from nmhg to suppliers
    @OneToMany(mappedBy = "supplierWpra", fetch = FetchType.LAZY)
    private List<SupplierPartReturn> supplierPartReturns = new ArrayList<SupplierPartReturn>();
*/
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Wpra() {
        // ONLY FOR HIBERNATE
    }

    public Wpra(Location destination,ServiceProvider shippedBy ) {
        super();
        Assert.notNull(destination, "No destination provided for creating Wpra");
        Assert.notNull(shippedBy,"No destination provided for creating Wpra" );
        this.destination = destination;
        this.shippedBy = shippedBy;
        this.generateDate = new Date();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getGenerateDateForDisplay() {
        if (generateDate != null) {
			return generateDate.toString();
		}
        return null;
    }

    public String getWpraNumber() {
        return wpraNumber;
    }

    public void setWpraNumber(String wpraNumber) {
        this.wpraNumber = wpraNumber;
    }

    public Date getGenerateDate() {
        return generateDate;
    }

    public void setGenerateDate(Date generateDate) {
        this.generateDate = generateDate;
    }

    /**
     * @return the parts
     */
    public List<PartReturn> getParts() {
        return this.parts;
    }

    public void addParts(List<PartReturn> parts) {
        for (PartReturn replaced : parts) {
            addPart(replaced);
        }
    }

    public void addPart(PartReturn part) {
        if (this.destination == null) {
            throw new IllegalStateException(
                    "Wpra is not valid since it does not have a destination");
        }
        if (!(this.destination.equals(part.getReturnLocation()))) {
            throw new IllegalArgumentException(
                    "Part with different destination being added to wpra");
        }
        this.parts.add(part);
        part.setWpra(this);
        if(part.getOemPartReplaced() != null)
            part.getOemPartReplaced().setWpra(this);
    }

    public void removePart(PartReturn part) {
        Assert.notNull(part, "The part cannot be null for the remove part flow.");
        this.parts.remove(part);
        part.setWpra(null);
    }

   /* public void addSupplierPartReturns(List<SupplierPartReturn> supplierPartReturns){
    	this.getSupplierPartReturns().addAll(supplierPartReturns);
    	for(SupplierPartReturn supplierPartReturn : supplierPartReturns){
    		supplierPartReturn.setSupplierShipment(this);
    	}
    }*/

    public Location getDestination() {
        return this.destination;
    }

    public void setDestination(Location location) {
        this.destination = location;
    }

    public ServiceProvider getShippedBy() {
        return this.shippedBy;
    }

    public void setShippedBy(ServiceProvider shippedBy) {
        this.shippedBy = shippedBy;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	/*public List<SupplierPartReturn> getSupplierPartReturns() {
		return supplierPartReturns;
	}

	public void setSupplierPartReturns(List<SupplierPartReturn> supplierPartReturns) {
		this.supplierPartReturns = supplierPartReturns;
	}*/
}

