/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.domain.additionalAttributes;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.SmrReason;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.security.AuditableColumns;

/**
 * @author pradipta.a
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class AttributeAssociation implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "AttributeAssociation")
	@GenericGenerator(name = "AttributeAssociation", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ATTRIBUTE_ASSOCIATION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Item item;

    @OneToOne(fetch = FetchType.LAZY)
    private ItemGroup itemGroup;

    @OneToOne(fetch = FetchType.LAZY)
    private ServiceProcedure serviceProcedure;

    @OneToOne(fetch = FetchType.LAZY)
    private FaultCode faultCode;

    @OneToOne(fetch = FetchType.LAZY)
    private Supplier supplier;
    

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SMR_REASON")
    private SmrReason smrreason;
  
	@Transient
    private ItemGroup product;

    @ManyToOne(fetch = FetchType.LAZY)
    private AdditionalAttributes forAttribute;
    
    @Transient
    private Boolean associated = Boolean.TRUE;

    @Version
    private int version;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public FaultCode getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(FaultCode faultCode) {
        this.faultCode = faultCode;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ItemGroup getProduct() {
        return product;
    }

    public void setProduct(ItemGroup product) {
        this.product = product;
    }

    public AdditionalAttributes getForAttribute() {
        return forAttribute;
    }

    public void setForAttribute(AdditionalAttributes forAttribute) {
        this.forAttribute = forAttribute;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ServiceProcedure getServiceProcedure() {
        return serviceProcedure;
    }

    public void setServiceProcedure(ServiceProcedure serviceProcedure) {
        this.serviceProcedure = serviceProcedure;
    }

	public Boolean getAssociated() {
		return associated;
	}

	public void setAssociated(Boolean associated) {
		this.associated = associated;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
	public SmrReason getSmrreason() {
		return smrreason;
	}

	public void setSmrreason(SmrReason smrreason) {
		this.smrreason = smrreason;
	}

}
