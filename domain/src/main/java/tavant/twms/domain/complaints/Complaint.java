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
package tavant.twms.domain.complaints;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

// TODO : need a better name as it represents field reports as well.
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Complaint implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "Complaint")
	@GenericGenerator(name = "Complaint", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "COMPLAINT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    // TODO : manyToOne ?
    @ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private Consumer consumer;

    /**
     * TODO: There is an issue with components if it is null. Refer
     * http://www.hibernate.org/hib_docs/reference/en/html/components.html For
     * now creating an inline item reference with a delete orphan for cascade
     */
    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @AttributeOverrides( { @AttributeOverride(name = "serialized", column = @Column(name = "item_ref_szed")) })
    @AssociationOverrides( {
            @AssociationOverride(name = "referredItem", joinColumns = @JoinColumn(name = "item_ref_item")),
            @AssociationOverride(name = "referredInventoryItem", joinColumns = @JoinColumn(name = "item_ref_inv_item")),
            @AssociationOverride(name = "unserializedItem", joinColumns = @JoinColumn(name = "item_ref_unszed_item")) })
    private ItemReference itemReference = new ItemReference();

    private String product;

    private String model;

    private String year;

    // FieldReport or Consumer
    private String complaintType;

    // Incident Details - Do we need a seperate entity ?
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate incidentDate;

    private String incidentDescription;

    private Integer numberOfFailures;

    private Integer numberOfDeaths;

    private Integer numberOfInjuredPersons;

    private String failedComponent;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private FaultCode faultCodeRef;

    private boolean isThereAFire;

    private boolean isThereACrash;

    private boolean isPropertyDamaged;

    private boolean hasReportedToPolice;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Complaint() {

    }

    // Enforcing valid conditions for object creation.
    // public Complaint(InventoryItem invItem) {
    // if(invItem == null) {
    // throw new IllegalArgumentException();
    // }
    // this.itemReference = new ItemReference(invItem);
    // }
    //	
    // public Complaint(Item item) {
    // if(item == null) {
    // throw new IllegalArgumentException();
    // }
    // this.itemReference = new ItemReference(item);
    // }
    //	
    // public Complaint(String product, String model, String year) {
    // if(product == null || model == null || year == null) {
    // throw new IllegalArgumentException();
    // }
    // this.product = product;
    // this.model = model;
    // this.year = year;
    // }

    public String getSerialNumber() {
        return (this.itemReference != null && this.itemReference.isSerialized()) ? this.itemReference
                .getReferredInventoryItem().getSerialNumber()
                : null;
    }

    public String getItemNumber() {
        return (this.itemReference != null && this.itemReference.getUnserializedItem() != null) ? this.itemReference
                .getUnserializedItem().getNumber()
                : null;
    }

    public Consumer getConsumer() {
        return this.consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public ItemReference getItemReference() {
        return this.itemReference;
    }

    // ITEM_GROUP_FIX:
    public String getModel() {
        return (this.itemReference == null || this.itemReference.getUnserializedItem() == null) ? this.model
                : this.itemReference.getUnserializedItem().getModel().getName();
    }

    public String getYear() {
        return (this.itemReference == null || this.itemReference.getUnserializedItem() == null) ? this.year
                : this.itemReference.getUnserializedItem().getItemYear();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getProduct() {
        return (this.itemReference.getUnserializedItem() == null) ? this.product
                : this.itemReference.getUnserializedItem().getProduct().getName();
    }

    public String getFailedComponent() {
        return this.failedComponent;
    }

    public void setFailedComponent(String failedComponent) {
        this.failedComponent = failedComponent;
    }

    public CalendarDate getIncidentDate() {
        return this.incidentDate;
    }

    public void setIncidentDate(CalendarDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getIncidentDescription() {
        return this.incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public Integer getNumberOfDeaths() {
        return this.numberOfDeaths;
    }

    public void setNumberOfDeaths(Integer numberOfDeaths) {
        this.numberOfDeaths = numberOfDeaths;
    }

    public Integer getNumberOfFailures() {
        return this.numberOfFailures;
    }

    public void setNumberOfFailures(Integer numberOfFailures) {
        this.numberOfFailures = numberOfFailures;
    }

    public Integer getNumberOfInjuredPersons() {
        return this.numberOfInjuredPersons;
    }

    public void setNumberOfInjuredPersons(Integer numberOfInjuredPersons) {
        this.numberOfInjuredPersons = numberOfInjuredPersons;
    }

    public void setItemReference(ItemReference itemReference) {
        this.itemReference = itemReference;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getComplaintType() {
        return this.complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public FaultCode getFaultCodeRef() {
        return this.faultCodeRef;
    }

    public void setFaultCodeRef(FaultCode faultCodeRef) {
        this.faultCodeRef = faultCodeRef;
    }

    public boolean getIsPropertyDamaged() {
        return this.isPropertyDamaged;
    }

    public void setIsPropertyDamaged(boolean isPropertyDamaged) {
        this.isPropertyDamaged = isPropertyDamaged;
    }

    public boolean getIsThereACrash() {
        return this.isThereACrash;
    }

    public void setIsThereACrash(boolean isThereACrash) {
        this.isThereACrash = isThereACrash;
    }

    public boolean getIsThereAFire() {
        return this.isThereAFire;
    }

    public void setIsThereAFire(boolean isThereAFire) {
        this.isThereAFire = isThereAFire;
    }

    public boolean getHasReportedToPolice() {
        return this.hasReportedToPolice;
    }

    public void setHasReportedToPolice(boolean hasReportedToPolice) {
        this.hasReportedToPolice = hasReportedToPolice;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
