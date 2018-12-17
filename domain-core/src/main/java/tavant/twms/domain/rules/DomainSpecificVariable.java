/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.rules;

import java.text.MessageFormat;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.IBusinessObjectModel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity(name = "DomainSpecificVariable")
@Table(name = "domain_specific_variable")
@XStreamAlias("domainVariable")
@Configurable
@SuppressWarnings("unused")
public class DomainSpecificVariable implements Value, ExpressionToken {
    private static Logger logger = LogManager.getLogger(DomainSpecificVariable.class);

    private String accessedFromType;

    private String fieldName;
    
    private String context;
    
	public DomainSpecificVariable(Class accessedFromType, String fieldName,String context) {
        this.accessedFromType = accessedFromType.getSimpleName();
        this.fieldName = fieldName;
        //todo-temp
        this.context=context;
    }

    public DomainSpecificVariable() {
    }

    public String getDomainName() {
        return field().getDomainName();
    }

    public String getBaseDomainName() {
        return field().getDomainName(false);
    }

    public String getPrimaryAccessedName() {
        return field().getPrimaryAccessedName();
    }

    public String getType() {
        return field().getType();
    }

    public boolean isCollection() {
        return field().endsInACollection();
    }

    public boolean isCaseSensitive(){
        if(isSimpleVariable()){
            return ((SimpleField) field().targetField()).isCaseSensitive();
        }
        return false;
    }
    
    public boolean isEntity() {
        return field().endsInAOne2One();
    }

    public boolean isSimpleVariable() {
        return field().endsInASimpleField();
    }

    public String getToken() {
        return field().getExpression();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void validate(ValidationContext validationContext) {
    }

    @Override
    public String toString() {
        return MessageFormat.format("name = {0}, type = {1}, expression = {2}, collectionValue = {3}",
                getDomainName(), getType(), getToken(), isCollection());
    }
    //todo-temp method
    public DomainTypeSystem getDomainTypeSystem(){
    	return BusinessObjectModelFactory.getInstance().getBusinessObjectModel(context).getDomainTypeSystem();
    }

    public FieldTraversal field() {
      	IBusinessObjectModel busObject=BusinessObjectModelFactory.getInstance().getBusinessObjectModel(context);
       	FieldTraversal fieldTraversal=busObject.getField(accessedFromType, fieldName);
        return fieldTraversal;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName
     *            the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the accessedFromType
     */
    public String getAccessedFromType() {
        return accessedFromType;
    }

    /**
     * @param accessedFromType
     *            the accessedFromType to set
     */
    public void setAccessedFromType(String accessedFromType) {
        this.accessedFromType = accessedFromType;
    }

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
}
