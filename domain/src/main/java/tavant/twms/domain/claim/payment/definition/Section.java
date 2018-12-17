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
package tavant.twms.domain.claim.payment.definition;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.security.SecurityHelper;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


/**
 * @author Sayedaamir
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Section implements AuditableColumns, BUSpecificSectionNames {

    public static final String OEM_PARTS = "Oem Parts";

    public static final String NON_OEM_PARTS = "Non Oem Parts";

    public static final String LABOR = "Labor";

    public static final String TRAVEL_BY_DISTANCE = "Travel By Distance";

    public static final String TRAVEL_BY_TRIP = "Travel By Trip";

    public static final String TRAVEL_BY_HOURS = "Travel by Hours";

    public static final String MEALS = "Meals";

    public static final String PARKING = "Parking";

    public static final String ITEM_FREIGHT_DUTY = "Item Freight And Duty";

    public static final String TOTAL_CLAIM = "Claim Amount";
    
    public static final String MISCELLANEOUS_PARTS = "Miscellaneous Parts";
    
    public static final String PER_DIEM = "Per Diem";
    
    public static final String RENTAL_CHARGES = "Rental Charges";
    
    public static final String ADDITIONAL_TRAVEL_HOURS = "Additional Travel Hours";

    public static final String ADDITIONAL_TRAVEL_HRS = "Additional Travel Hours";
    
    public static final String LOCAL_PURCHASE = "Local Purchase";
    
    public static final String TOLLS = "Tolls";
    
    public static final String OTHER_FREIGHT_DUTY = "Other Freight And Duty";
    
    public static final String OTHERS = "Others";
    
    public static final String HANDLING_FEE="Handling Fee";
    
    public static final String TRANSPORTATION_COST = "Transportation";
    
    public static final String TRAVEL="Travel";
    
    //TODO: Retrieve this constant from other constants file since Late_FEE is not present in Section table
    public static final String LATE_FEE="Late Fee";  
	public static final String DEDUCTIBLE="Deductible";   

    @Id
    @GeneratedValue(generator = "Section")
	@GenericGenerator(name = "Section", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SECTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "SECTION_I18NNAME", nullable = false)
	private List<I18NSectionText> sectionNameTexts = new ArrayList<I18NSectionText>();

    private Integer displayPosition;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(inverseJoinColumns = { @JoinColumn(name = "cost_category") })
    @IndexColumn(name = "display_position")
    private List<CostCategory> costCategories = new ArrayList<CostCategory>();

    private String messageKey;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Section() {
        super();
    }

    public Section(String name) {
        super();
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CostCategory> getCostCategories() {
        return this.costCategories;
    }

    public void setCostCategories(List<CostCategory> includedCategories) {
        this.costCategories = includedCategories;
    }

    public Integer getDisplayPosition() {
        return this.displayPosition;
    }

    public void setDisplayPosition(Integer position) {
        this.displayPosition = position;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("name", this.name)
                .append("displayPosition", this.displayPosition).toString();
    }


	public List<I18NSectionText> getSectionNameTexts() {
		return sectionNameTexts;
	}

	public void setSectionNameTexts(List<I18NSectionText> sectionNameTexts) {
		this.sectionNameTexts = sectionNameTexts;
	}

    public String getDisplayName(){
        String i18nname = "";
		for (I18NSectionText sectionText : this.sectionNameTexts) {
			if (sectionText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && sectionText.getSectionName() != null) {
				i18nname = sectionText.getSectionName();
				break;
			}
			else if(sectionText.getLocale().equalsIgnoreCase("en_US")) {
				i18nname = sectionText.getSectionName();
			}

		}
		return i18nname;
    }


	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getI18NMessageKey(String name){
        return NAMES_AND_KEY.get(name);
    }
}
