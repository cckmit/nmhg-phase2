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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.Assert;

import tavant.twms.domain.query.QueryTemplate;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 */
@XStreamAlias("DomainType")
public class DomainType extends AbstractType {
    private Set<Class<? extends Predicate>> predicates;

    private Set<Field> fields = new LinkedHashSet<Field>();

    private String typeName;

    private String domainName;


    /**
     * 
     */
    public String getName() {
        return typeName;
    }
    
    /**
     * @return the domainName
     */
    public String getDomainName() {
        return domainName;
    }
    
    public DomainType(String domainName, String typeName) {
        predicates = new HashSet<Class<? extends Predicate>>();
        setDefaultPredicates();
        Assert.notNull(domainName, "Domain name must be specified.");
        Assert.notNull(typeName, "Type name must be specified.");
        this.domainName = domainName;
        this.typeName = typeName;
    }

    public void setPredicates(Set<Class<? extends Predicate>> predicates) {
        this.predicates = predicates;
    }

    public boolean supportsLiteral() {
        return false;
    }

    /**
     * @param name
     *            the name to set
     */
    public Field simpleField(String domainName, String expression, String type) {
        SimpleField simpleField = new SimpleField(domainName, expression, type,
                false);
        fields.add(simpleField);
        return simpleField;
    }

    public Field simpleField(String domainName, String expression, String type, boolean isCaseSensitive) {
        SimpleField simpleField = new SimpleField(domainName, expression, type,
                false, isCaseSensitive);
        fields.add(simpleField);
        return simpleField;
    }

    
    public Field oneToOne(String domainName, String expression, DomainType ofType) {
        OneToOneAssociation oneToOne = new OneToOneAssociation(domainName, expression, ofType);
        fields.add(oneToOne);
        return oneToOne;
    }

    public Field oneToMany(String domainName, String expression, DomainType ofType) {
        OneToManyAssociation oneToMany = new OneToManyAssociation(domainName, expression, ofType);
        fields.add(oneToMany);
        return oneToMany;
    }

    public Field functionField(String domainName, String expression,
                               String type, boolean isHardWired, 
                               Class baseType) {
        FunctionField functionField =
                new FunctionField(domainName, expression, type, isHardWired,
                        baseType);
        fields.add(functionField);
        return functionField;
    }
    
    public Field queryTemplate(String domainName, String expression,
            String type, boolean isHardWired, 
            Class baseType,String joinedEntityname,String aliasName) {
    	QueryTemplate functionField =
    		new QueryTemplate(domainName, expression, type, isHardWired,
    				baseType,joinedEntityname,aliasName);
    	fields.add(functionField);
    	return functionField;
    }

    public Field queryTemplateForMultipleJoins(String domainName, String expression,
            String type, boolean isHardWired,
            Class baseType,String[] joinedEntitynames,String[] aliasNames) {
    	QueryTemplate functionField =
    		new QueryTemplate(domainName, expression, type, isHardWired,
    				baseType,joinedEntitynames,aliasNames);
    	fields.add(functionField);
    	return functionField;
    }
    
    public Field queryTemplateForAggregateFunctions(String domainName, String expression,
            String type, boolean isHardWired, 
            Class baseType,String joinedEntityname,String groupBy,String aliasName) {
    	QueryTemplate functionField =
    		new QueryTemplate(domainName, expression, type, isHardWired,
    				baseType,joinedEntityname,groupBy,aliasName);
    	fields.add(functionField);
    	return functionField;
    }

    @Override
    public Set<Class<? extends Predicate>> supportedPredicates() {
        return this.predicates;
    }

    @Override
    public void setDefaultPredicates() {
        super.setDefaultPredicates();
        Set<Class<? extends Predicate>> predicates = supportedPredicates();
        predicates.add(BelongsTo.class);
        predicates.add(DoesNotBelongTo.class);
        predicates.remove(Equals.class);
        predicates.remove(NotEquals.class);
    }

    /**
     * @return the fields
     */
    public Set<Field> getFields() {
        return fields;
    }
    
    private static MessageFormat format = new MessageFormat("[ Domain Name = {0}, Type Name = {1} ]");
    
    @Override
    public String toString() {
        return format.format(new Object[]{domainName,typeName});
    }
}
