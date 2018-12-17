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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.query.QueryTemplate;

/**
 * @author radhakrishnan.j
 */
public class FieldTraversal {
    private static Logger logger = LogManager.getLogger(FieldTraversal.class);
    private List<Field> fieldsInPath = new ArrayList<Field>();

    public FieldTraversal() {
        super();
        // TODO Auto-generated constructor stub
    }

    public FieldTraversal(FieldTraversal propertyPath) {
        super();
        this.fieldsInPath.addAll(propertyPath.getFieldsInPath());
    }

    /**
     * @return the fieldsInPath
     */
    public void addFieldToPath(Field aField) {
        fieldsInPath.add(aField);
    }

    /**
     * @return the fieldsInPath
     */
    public List<Field> getFieldsInPath() {
        return fieldsInPath;
    }

    public String getExpression() {
        Field targetField = targetField();
        String contextExpression = fieldsInPath.get(0).getExpression();
        if (targetField instanceof SimpleField && ((SimpleField) targetField).isHardwiredExpression()) {
            String targetExpression = targetField.getExpression();
            if (logger.isDebugEnabled()) {
                logger.debug(" Target expression is " + targetExpression);
            }
            return targetExpression.replace("{contextExpression}", contextExpression);
        }
        StringBuffer buf = new StringBuffer();
        
        for (Field eachField : fieldsInPath) {
            String thisExpression =
                    eachField.getExpression().replace("{contextExpression}",
                            buf.toString());
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(thisExpression);
        }
        return buf.toString();
    }

    public String getDomainName() {
        return getDomainName(true);
    }

    public String getDomainName(boolean includeFullDomainPath) {
        Field targetField = targetField();

        if (!includeFullDomainPath ||
                (targetField instanceof SimpleField &&
                        ((SimpleField) targetField).isHardwiredExpression())) {
            return targetField.getDomainName();
        }

        StringBuffer buf = new StringBuffer();
        for (Field eachField : fieldsInPath) {
            if (buf.length() > 0) {
                buf.append("'s ");
            }
            buf.append(eachField.getDomainName());
        }
        return buf.toString();
    }

    public boolean endsInACollection() {
        return targetField() instanceof OneToManyAssociation;
    }

    public boolean endsInAOne2One() {
        return targetField() instanceof OneToOneAssociation;
    }

    public boolean endsInASimpleField() {
        Field field = targetField();
        return field instanceof SimpleField;
    }

    public boolean endsInAFunction() {
        return targetField() instanceof FunctionField;
    }

    public boolean endsInASimpleFunction() {
        return endsInATypeofFunction(FunctionField.Types.SIMPLE.getBaseType());
    }

    public boolean endsInAOneToOneFunction() {
        return endsInATypeofFunction(
                FunctionField.Types.ONE_TO_ONE.getBaseType());
    }

    public boolean endsInACollectionFunction() {
        return endsInATypeofFunction(
                FunctionField.Types.ONE_TO_MANY.getBaseType());
    }

    private boolean endsInATypeofFunction(Class baseType) {
        Field field = targetField();
        if(field instanceof FunctionField) {
            FunctionField functionField = (FunctionField) field;
            return functionField.getBaseType().equals(baseType);
        } else {
            return false;
        }
    }
    
    public boolean endsInAQueryTemplate() {
        Field field = targetField();
        if(field instanceof QueryTemplate) {
        	return true;
        } else {
            return false;
        }
    }

    public String getType() {
        return targetField().getType();
    }

    public Field targetField() {
        if (fieldsInPath.isEmpty()) {
            throw new IllegalStateException("No fields added to path yet. Path is empty");
        }
        Field lastElement = null;
        int indexOfLastElement = fieldsInPath.size() - 1;
        lastElement = fieldsInPath.get(indexOfLastElement);
        return lastElement;
    }

    public DomainSpecificVariable getDomainSpecificVariable() {
        DomainSpecificVariable domainSpecificVariable = new DomainSpecificVariable();
        domainSpecificVariable.setAccessedFromType(fieldsInPath.get(0).getType());
        domainSpecificVariable.setFieldName(getExpression());
        return domainSpecificVariable;
    }

    private static MessageFormat format = new MessageFormat("[ Domain name = {0}, expression = {1} ]");

    @Override
    public String toString() {
        if (fieldsInPath.isEmpty()) {
            return "";
        }

        return format.format(new Object[]{getDomainName(), getExpression()});
    }

    public String getPrimaryAccessedName() {
        Field targetField = targetField();

        if (targetField instanceof SimpleField &&
                ((SimpleField) targetField).isHardwiredExpression()) {
            return targetField.getDomainName();
        }

        int numFields = fieldsInPath.size();

        StringBuffer buf = new StringBuffer();

        if (numFields > 2) {
            buf.append(fieldsInPath.get(numFields - 2).getDomainName());
            buf.append("'s ");
        }
        
        buf.append(fieldsInPath.get(numFields - 1).getDomainName());

        return buf.toString();
    }
}
