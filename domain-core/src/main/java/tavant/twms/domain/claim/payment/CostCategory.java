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

package tavant.twms.domain.claim.payment;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({@Filter(name="excludeInactive")})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CostCategory implements AuditableColumns,Comparable<CostCategory>{
    @Id
    @GeneratedValue(generator = "CostCategory")
	@GenericGenerator(name = "CostCategory", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "COST_CATEGORY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String name;

    private String code;

    private String description;

    private String categoryGroup;
    
    @Column(name = "STATE_MANDATE")
    private Boolean stateMandate=Boolean.FALSE;
    
	@Filter(name = "bu_name", condition = "business_unit_info in (:name)") 
     @OneToMany(fetch = FetchType.LAZY)	
	 @JoinTable(name = "COSTCAT_APPL_PRODUCTS", joinColumns = 
	 {@JoinColumn(name = "cost_category") }, inverseJoinColumns = {@JoinColumn(name = "item_group") })	
	 private List<ItemGroup> applicableProducts = new  ArrayList<ItemGroup>();

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public CostCategory(String code) {
        this.code = code;
    }

    // for hibernate
    public CostCategory() {

    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public List<ItemGroup> getApplicableProducts() {
		return applicableProducts;
	}

	public void setApplicableProducts(List<ItemGroup> applicableProducts) {
		this.applicableProducts = applicableProducts;
	}
	
	public Boolean getStateMandate() {
			return stateMandate;
		}

	public void setStateMandate(Boolean stateMandate) {
			this.stateMandate = stateMandate;
		}

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("name", this.name)
                .append("code", this.code).append("description", this.description).toString();
    }
    
    
    public int compareTo(CostCategory costCategory) {
        if (this.name != null) {
            return this.name.compareTo(costCategory.getName());
        } else {
            return -1; // anything other than 0 is fine
        }
    }
    
    public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		// object must be Test at this point
		CostCategory costCategory = (CostCategory)obj;
		return this.code.equals(costCategory.getCode());
	}

	public int hashCode()
	{
		int hash = 7;		
		hash = 31 * hash + (null == code ? 0 : code.hashCode());
		return hash;
	}



	public static final String NON_OEM_PARTS_COST_CATEGORY_CODE = "NON_OEM_PARTS";
	
	public static final String MISC_PARTS_COST_CATEGORY_CODE = "MISC_PARTS";

    public static final String OEM_PARTS_COST_CATEGORY_CODE = "OEM_PARTS";

    public static final String LABOR_COST_CATEGORY_CODE = "LABOR";

    public static final String TRAVEL_DISTANCE_COST_CATEGORY_CODE = "TRAVEL_DISTANCE";
    
    public static final String FREIGHT_DUTY_CATEGORY_CODE = "FREIGHT_DUTY";
    
    public static final String TRAVEL_TRIP_COST_CATEGORY_CODE = "TRAVEL_TRIP";
    
    public static final String TRAVEL_HOURS_COST_CATEGORY_CODE = "TRAVEL_HOURS";
    
    public static final String PARKING_COST_CATEGORY_CODE = "PARKING";
    
    public static final String MEALS_HOURS_COST_CATEGORY_CODE = "MEALS";
    
    public static final String PER_DIEM_COST_CATEGORY_CODE = "PER_DIEM";
    
    public static final String RENTAL_CHARGES_COST_CATEGORY_CODE = "RENTAL_CHARGES";

	public static final String ADDITIONAL_TRAVEL_HOURS_COST_CATEGORY_CODE = "ADDITIONAL_TRAVEL_HOURS";
    
    public static final String LOCAL_PURCHASE_COST_CATEGORY_CODE = "LOCAL_PURCHASE";
    
    public static final String TOLLS_COST_CATEGORY_CODE = "TOLLS";
    
    public static final String OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE = "OTHER_FREIGHT_DUTY";
    
    public static final String HANDLING_FEE_CODE ="HANDLING_FEE";
    
    public static final String OTHERS_CATEGORY_CODE = "OTHERS";
    
    public static final String TRANSPORTATION_COST_CATEGORY_CODE = "TRANSPORTATION";

    public static final String TRAVEL_COST_CATEGORY_CODE = "TRAVEL";

    public static final String DRAYAGE_COST_CATEGORY_CODE = "DRAYAGE";

    public static final String ENVIRONMENTAL_FEE_COST_CATEGORY_CODE = "ENVIRONMENTAL_FEE";

    public static final String FUEL_CHANGE_COST_CATEGORY_CODE = "FUEL_CHANGE";

    public static final String SHOP_SUPPLIES_COST_CATEGORY_CODE = "SHOP_SUPPLIES";

    public static final String LABOR_OUTSIDE_COST_CATEGORY_CODE = "LABOR_OUTSIDE";

    public static final String SHOP_LABOR_HOURS_COST_CATEGORY_CODE = "SHOP_LABOR";

    public static final String MISC_SOURCING_TRAVEL_COST_CATEGORY_CODE = "MISC_SOURCING_TRAVEL";

    public static final String RENTAL_COST_CATEGORY_CODE = "RENTAL";

    public static final String FREIGHT_COST_CATEGORY_CODE = "FREIGHT";

    public static final String US_TAXES_COST_CATEGORY_CODE = "US_TAXES";

    public static final String HST_COST_CATEGORY_CODE = "HST";

    public static final String PST_COST_CATEGORY_CODE = "PST";

    public static final String QST_COST_CATEGORY_CODE = "QST";

    public static final String GST_COST_CATEGORY_CODE = "GST";

    public static final String OTHER_CATEGORY_CODE = "OTHER";

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public String getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }

}
