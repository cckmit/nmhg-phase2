/*
 *   Copyright (c)2008 Tavant Technologies
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
package tavant.twms.domain.customReports;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Cascade;
import org.hibernate.Hibernate;

import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import tavant.twms.infra.HibernateCast;

/**
 * @author kaustubhshobhan.b
 * @author amritha.k
 */
@Entity
@Table(name ="CUST_REPORT_APP_PART")
public class CustomReportApplicablePart implements AuditableColumns {

    @Id
    @GeneratedValue(generator = "CustomReportApplicablePart")
    @GenericGenerator(name = "CustomReportApplicablePart", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CUST_REPORT_APP_PART_SEQ"),
			@Parameter(name = "initial_value", value = "200"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;
   
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name = "itemIdentifier", column = @Column(name = "item_Identifier"))})
    private ItemCriterion itemCriterion;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.customReports.Applicability"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    @CollectionOfElements
    @JoinTable(name="applicability_types_in_part",
            joinColumns = @JoinColumn(name="cust_rep_app_part")
    )
    @Column(name="applicability", nullable=false)
    private List<Applicability> applicabilityList;

    private boolean byQuantity;
    
    @Transient
    private boolean itemCriterionItemGroup;

    @Embedded
	@Cascade( {org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	private AuditableColEntity d = new AuditableColEntity();

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemCriterion getItemCriterion() {
        return itemCriterion;
    }

    public void setItemCriterion(ItemCriterion itemCriterion) {
        this.itemCriterion = itemCriterion;
    }

   

	public List<Applicability> getApplicabilityList() {
		return applicabilityList;
	}

	public void setApplicabilityList(List<Applicability> applicabilityList) {
		this.applicabilityList = applicabilityList;
	}

	public boolean isByQuantity() {
        return byQuantity;
    }

    public void setByQuantity(boolean byQuantity) {
        this.byQuantity = byQuantity;
    }

	public boolean isItemCriterionItemGroup() {
		return itemCriterionItemGroup;
	}

	public void setItemCriterionItemGroup(boolean itemCriterionItemGroup) {
		this.itemCriterionItemGroup = itemCriterionItemGroup;
	}

	public boolean isApplicableForType(Applicability applicability){
        boolean isApplicable = false;
        for (Applicability applicabilityTypes : applicabilityList) {
            if(applicability.getName().equalsIgnoreCase(applicabilityTypes.getName())){
                return true;
            }
        }
        return isApplicable;
    }

}
