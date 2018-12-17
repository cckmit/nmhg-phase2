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
package tavant.twms.web.xls.reader;

import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.style.ToStringCreator;
import tavant.twms.infra.GenericsUtil;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */
public class BeanColumnMapping {


    private static final Logger logger = Logger.getLogger(BeanColumnMapping.class);

    public static final String COLLECTION_IDENTIFIER_PATTERN = "\\[\\]\\.";
    public static final String COLLECTION_IDENTIFIER = "[].";

    short column;
    short dependsOn;
    Class dependsOnType;
    boolean dependentOnAnotherColumn;
    String expression;
    String convertorName;
    Convertor convertor;

//    static {
//        //Is this right?
//        OgnlValueStack.reset();
//    }

    //Need to avoid this call explicitly.
    public void initializeConvertor() {
        if (this.convertorName != null) {
            ApplicationContext ctx = ApplicationContextHolder.getInstance().getContext();
            if (ctx == null) {
                throw new IllegalStateException("Unable to get ApplicationContext to look up convertor["
                        + this.convertorName + "]");
            }
            this.convertor = (Convertor)ctx.getBean(this.convertorName);
            if(logger.isDebugEnabled())
            {
                logger.debug("Obtained Convertor[" + this.convertor + "] for the convertor name["
                    + this.convertorName + "]");
            }
        } else {
            if(logger.isDebugEnabled())
            {
                logger.debug("No convertors registered for data of column[" + this.column
                        + "] to go into expression[" + this.expression + "]");
            }
        }
    }

    boolean expectingACollection() {
        return this.expression.contains(COLLECTION_IDENTIFIER);
    }

    PropertyDescriptor getPropertyDescriptor(Class clazz, String expression) {
        String exps[] = expression.split("\\.");
        Class targetClass = clazz;
        PropertyDescriptor propertyDescriptor = null;
        for (int i = 0; i < exps.length; ++i) {
            try {
                propertyDescriptor = OgnlRuntime.getPropertyDescriptor(targetClass, exps[i]);
                targetClass = propertyDescriptor.getPropertyType();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return propertyDescriptor;
    }

    Collection getInitializedCollectionFromObject(Object root, List data, String expression) {
        PropertyDescriptor propertyDescriptor;
        propertyDescriptor = getPropertyDescriptor(root.getClass(), expression);
        if (Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
            Class clazz = new GenericsUtil().getParameterizedCollectionContentType(
                    (ParameterizedType)propertyDescriptor.getReadMethod().getGenericReturnType());
            Collection coll;
            try {
                coll = (Collection)Ognl.getValue(expression, root);
            } catch (OgnlException e) {
                throw new RuntimeException(e);
            }
            if (!(data.size() == coll.size())) {
                coll.clear();
                for(int i = 0; i < data.size(); ++i) {
                    try {
                        coll.add(clazz.newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return coll;
        } else {
            throw new RuntimeException("Used Collection identifier on an expression that is not a Collection");
        }


    }

    public void populate(Object root, List data) {
        if(logger.isDebugEnabled()){
           logger.debug("Obtained data[" + data + "] from excel");
        }
        initializeConvertor();
        populateInternal(root, data);
    }

    void populateInternal(Object root, List data) {
        if (expectingACollection()) {
            data = getPrunedList(data);
            String[] exps = this.expression.split(COLLECTION_IDENTIFIER_PATTERN);
            Collection coll = getInitializedCollectionFromObject(root, data, exps[0]);
            int count = 0;
            for (Object obj : coll) {
                setValue(obj, exps[1], getConvertedData(data.get(count)));
                ++count;
            }
        } else {
            setValue(root, this.expression, getConvertedData(data.get(0)));
        }
    }

    Object getConvertedData(Object data) {
        Object value;
        if (this.convertor != null) {
            value = this.convertor.convert(data);
        } else {
            value = data;
        }
        return value;
    }

    List getPrunedList(List objects) {
        List nonNullList = new ArrayList();
        for (Object object : objects) {
            if (object != null) {
                nonNullList.add(object);
            }
        }
        return nonNullList;
    }

    private void setValue(Object root, String exp, Object value) {
        Map context = Ognl.createDefaultContext(root);
        context.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        try {
            Ognl.setValue(Ognl.parseExpression(exp), context, root, value);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getValue(String exp, Object root) {
        Map context = Ognl.createDefaultContext(root);
        context.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        try {
            return Ognl.getValue(Ognl.parseExpression(exp), context, root);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateWithDependent(Object root, List data, List dependent) {
        initializeConvertor();
        if(logger.isDebugEnabled()){
            logger.debug("Obtained data[" + data.get(0) + "] with dependent[" + dependent.get(0) + "] from excel");
        }
        populateInternalWithDependent(root, data, dependent);
    }

    void populateInternalWithDependent(Object root, List data, List dependent) {
        if (expectingACollection()) {
            data = getPrunedList(data);
            String[] exps = this.expression.split(COLLECTION_IDENTIFIER_PATTERN);
            Collection coll = getInitializedCollectionFromObject(root, data, exps[0]);
            int count = 0;
            for (Object obj : coll) {
                setValue(obj, exps[1], getConvertedDataWithDependency(data.get(count), dependent.get(count)));
                ++count;
            }
        } else {
            setValue(root, this.expression, getConvertedDataWithDependency(data.get(0), dependent.get(0)));
        }
    }

    Object getConvertedDataWithDependency(Object data, Object dependency) {
        Object value;
        if (this.convertor != null) {
            value = this.convertor.convertWithDependency(data, dependency);
        } else {
            value = data;
        }
        return value;
    }


    public void setColumn(short column) {
        this.column = column;
    }

    public ColumnDescription getColumn(Object rootObject) {
        PropertyDescriptor propertyDescriptor;
        if (expectingACollection()) {
            String[] exps = this.expression.split(COLLECTION_IDENTIFIER_PATTERN);
            propertyDescriptor = getPropertyDescriptor(rootObject.getClass(), exps[0]);
            if (Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                Class clazz = new GenericsUtil().getParameterizedCollectionContentType(
                        (ParameterizedType)propertyDescriptor.getReadMethod().getGenericReturnType());
                propertyDescriptor = getPropertyDescriptor(clazz, exps[1]);
            } else {
                throw new RuntimeException("Used Collection identifier on an expression that is not a Collection");
            }
        } else {
            propertyDescriptor = getPropertyDescriptor(rootObject.getClass(), this.expression);
        }

        return new ColumnDescription(this.column, propertyDescriptor.getPropertyType());
    }

    public ColumnDescription getDependsOnDesc() {
        return new ColumnDescription(this.dependsOn, this.dependsOnType);
    }

    public void setDependsOn(short dependsOn) {
        this.dependentOnAnotherColumn = true;
        this.dependsOn = dependsOn;
    }

    public boolean isDependentOnAnotherColumn() {
        return this.dependentOnAnotherColumn;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setConvertorName(String convertorName) {
        this.convertorName = convertorName;
    }

    public void setDependsOnType(Class dependsOnType) {
        this.dependsOnType = dependsOnType;
    }

    void setConvertor(Convertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("column", this.column).append("dependsOn", this.dependsOn)
                .append("dependsOnType", this.dependsOnType).append("expression", this.expression)
                .append("convertorName", this.convertorName).toString();
    }
}
