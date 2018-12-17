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
package tavant.twms.domain.supplier;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.SupplierItemLocation;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
/**
 * @author kannan.ekanath
 */
public class ItemMapping implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "ItemMapping")
	@GenericGenerator(name = "ItemMapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ITEM_MAPPING_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item fromItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item toItem;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate fromDate;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate toDate;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    private String supplierSitecode;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_mapping")
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<SupplierItemLocation> supplierItemLocations = new ArrayList<SupplierItemLocation>();


    public Item getFromItem() {
        return this.fromItem;
    }

    public void setFromItem(Item fromItem) {
        this.fromItem = fromItem;
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

    public Item getToItem() {
        return this.toItem;
    }

    public void setToItem(Item toItem) {
        this.toItem = toItem;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("from", this.fromItem)
                .append("to", this.toItem).toString();
    }

    public CalendarDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(CalendarDate fromDate) {
        this.fromDate = fromDate;
    }

    public CalendarDate getToDate() {
        return toDate;
    }

    public void setToDate(CalendarDate toDate) {
        this.toDate = toDate;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getSupplierSitecode() {
		return supplierSitecode;
	}

	public void setSupplierSitecode(String supplierSitecode) {
		this.supplierSitecode = supplierSitecode;
	}

	public List<SupplierItemLocation> getSupplierItemLocations() {
		return supplierItemLocations;
	}

	public void setSupplierItemLocations(
			List<SupplierItemLocation> supplierItemLocations) {
		this.supplierItemLocations = supplierItemLocations;
	}

	

}
