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
package tavant.twms.domain.claim;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.rules.DateType;

/**
 * @author pradipta.a
 */
@Entity
@Filters({
        @Filter(name = "excludeInactive")
})
public class ClaimAttributes implements AuditableColumns {

    @Id
    @GeneratedValue(generator = "ClaimAttributes")
    @GenericGenerator(name = "ClaimAttributes", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "CLAIM_ATTRIBUTES_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20")})
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private AdditionalAttributes attributes;

    private String attrValue;
    
    @Transient
    private String name;

    public static final String DATE_TYPE = "Date";

    public static final String NUMERIC_TYPE = "Number";

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    public ClaimAttributes(AdditionalAttributes attributes, String attrVal) {
        this.attributes = attributes;
        this.attrValue = attrVal;
    }

    public ClaimAttributes() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AdditionalAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(AdditionalAttributes attributes) {
        this.attributes = attributes;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }
	
	public Double getNumericAttrValueFor(String attributeName) {
	    if(this.getAttributes().getAttributeType().equalsIgnoreCase("Number") 
	    			   && this.getAttributes().getName().equalsIgnoreCase(attributeName)&& this.getAttrValue()!=null && !this.getAttrValue().isEmpty()){
	    		   return Double.valueOf(this.getAttrValue());
	    	   
	       }
	       return new Double(-1);
	} 
	
	
	public String getTextAttrValueFor(String attributeName) {
	    if((this.getAttributes().getAttributeType().equalsIgnoreCase("Text") 
	    			   || this.getAttributes().getAttributeType().equalsIgnoreCase("Text Area"))   
	    			   && this.getAttributes().getName().equalsIgnoreCase(attributeName) && this.getAttrValue()!=null){
	    		   return String.valueOf(this.getAttrValue());
	    	   
	       }
	       return "";
	} 
	
	public CalendarDate getDateAttrValueFor(String attributeName) {
	    if(this.getAttributes().getAttributeType().equalsIgnoreCase("Date")   
	    			   && this.getAttributes().getName().equalsIgnoreCase(attributeName) && this.getAttrValue()!=null && !this.getAttrValue().isEmpty()){
	    		   return (CalendarDate)new DateType().getJavaObject(this.getAttrValue());
	    	   
	       }
	       return (CalendarDate)new DateType().getJavaObject("01/01/1900");
	} 

    public ClaimAttributes clone() {
        ClaimAttributes claimAttributes = new ClaimAttributes();
        claimAttributes.setAttributes(attributes);
        claimAttributes.setAttrValue(attrValue);
        return claimAttributes;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
