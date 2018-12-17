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

package tavant.twms.domain.businessobject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.Field;
import tavant.twms.domain.rules.FieldTraversal;
import tavant.twms.domain.rules.OneToManyAssociation;
import tavant.twms.domain.rules.OneToOneAssociation;
/**
 * 
 * @author roopali.agrawal
 *
 */
public abstract class AbstractBusinessObjectModel implements IBusinessObjectModel{
	private static Logger logger = LogManager.getLogger(AbstractBusinessObjectModel.class);

	public abstract Set<DomainType> getDomainTypes();
	
	public abstract DomainTypeSystem getDomainTypeSystem();
	
	public abstract String getExpressionForDomainType(String typeName);
	
	public Map<DomainType, SortedMap<String, FieldTraversal>> fieldsInBO = 
		new HashMap<DomainType, SortedMap<String, FieldTraversal>>() {
		@Override
		public SortedMap<String, FieldTraversal> get(Object key) {
			SortedMap<String, FieldTraversal> value = super.get(key);
			if (value == null) {
				value = new TreeMap<String, FieldTraversal>();
				put((DomainType) key, value);
			}
			return value;
		}

	};

	public SortedMap<String, FieldTraversal> getDataElementsForType(
			DomainType businessObject) {
		SortedMap<String, FieldTraversal> fields = new TreeMap<String, FieldTraversal>();
		/*String name = businessObject.getDomainName();
		Set keys = fieldsInBO.keySet();
		for (Object type : keys) {
			DomainType dtype = (DomainType) type;
			//todo-verify this.
			if (dtype.getDomainName().equals(name)) {
				fields.putAll(fieldsInBO.get(dtype));
				break;
			}
		}*/
		fields.putAll(fieldsInBO.get(businessObject));
		return fields;

	}
	
	public void addDataElementsForType(	DomainType domainType, String expression) {
				discoverPathsToFields(domainType, expression);
	}

	protected void discoverPathsToFields(DomainType domainType, String expression) {

		if (logger.isDebugEnabled()) {
			logger.debug(" Building field traversal paths for type ["
					+ domainType + "] with expression [" + expression + "]");
		}

		SortedMap<String, FieldTraversal> fieldsInThisBO = fieldsInBO
				.get(domainType);

		// SortedMap<String, FieldTraversal> fieldsInThisBO =
		// fieldsInBO.get(domainType);

		// Recursively traverse the domain type hierarchy
		// 1. For simple fields, identify some unique key ( for now, the
		// property path )
		// 2. For one-to-many associations get the domain type of the many end
		// and repeat recursively.
		OneToOneAssociation field = new OneToOneAssociation(domainType
				.getDomainName(), expression, domainType);
		FieldTraversal root = new FieldTraversal();
		root.addFieldToPath(field);

		// fieldsInThisBO.put(root.getExpression(), root);

		for (Field aField : field.getFields()) {
			FieldTraversal path = new FieldTraversal(root);
			path.addFieldToPath(aField);

			if (logger.isDebugEnabled()) {
				logger.debug(" Adding nested field " + path + " of "
						+ field.getDomainName());
			}

			fieldsInThisBO.put(path.getExpression(), path);

			// FIX-ME: This recursive build should happen for One-To-Ones as
			// well
			// FIX-ME: That can be done once the UI is sorted out.
			if (aField instanceof OneToManyAssociation) {
				OneToManyAssociation oneToMany = (OneToManyAssociation) aField;
				DomainType collectionElementType = oneToMany.getOfType();
				discoverPathsToFields(collectionElementType, "");
			} else if (aField instanceof OneToOneAssociation) {
				OneToOneAssociation oneToOne = (OneToOneAssociation) aField;
				DomainType entityElementType = oneToOne.getOfType();
				discoverPathsToFields(entityElementType, "");
			}
		}
	}

	public SortedMap<String, FieldTraversal> getAllLevelDataElements() {
		SortedMap<String, FieldTraversal> fields = new TreeMap<String, FieldTraversal>();
		for (SortedMap<String, FieldTraversal> fieldsForAllTypes : fieldsInBO
				.values()) {
			for (Map.Entry<String, FieldTraversal> field : fieldsForAllTypes
					.entrySet()) {
				fields.put(field.getValue().getDomainName(), field.getValue());
			}
		}

		return fields;
	}

	public SortedMap<String, FieldTraversal> getTopLevelDataElements() {
		SortedMap<String, FieldTraversal> fields = new TreeMap<String, FieldTraversal>();

		 for (DomainType businessObject : getDomainTypes()) {			 
			 fields.putAll(fieldsInBO.get(businessObject));
		 }
		return fields;
	}
	
	public FieldTraversal getField(String typeName, String fieldName) {

        DomainType domainType =
                (DomainType) getDomainTypeSystem().getType(typeName);

        // Detect function fields and process them immediately.
        FieldTraversal fieldFromFieldsInBO = 
            fieldsInBO.get(domainType).get(fieldName);
        
        if (fieldFromFieldsInBO != null) {
            return fieldFromFieldsInBO;
        }

        String expression=null;
        expression = getExpressionForDomainType(typeName);
        boolean isTopLevelBO = (expression != null);

        if (!isTopLevelBO) {
            expression = "";
        }

        OneToOneAssociation field =
                new OneToOneAssociation(domainType.getDomainName(), expression,
                        domainType);
        FieldTraversal fieldTraversal = new FieldTraversal();
        fieldTraversal.addFieldToPath(field);

        String[] fieldNameParts = fieldName.split("\\.");
        if (buildField(typeName, fieldNameParts, 0, fieldTraversal)) {
            return fieldTraversal;
        } else {
            throw new IllegalArgumentException("No field found for " +
                    "expression [" + fieldName + "]!");
        }
   }


    private boolean buildField(String typeName,
                               String[] fieldNameParts, int startIndex,
                               FieldTraversal requiredField) {
        DomainType businessObject = (DomainType) getDomainTypeSystem().getType(typeName);
        String baseFieldName = fieldNameParts[startIndex++];

        String expression=null;
        expression = getExpressionForDomainType(typeName);
        
        boolean isTopLevelBO = (expression != null);

        if (isTopLevelBO) {
            if (startIndex == 1) {
                baseFieldName += "." + fieldNameParts[startIndex++];
            } else {
                baseFieldName = businessObject.getName().toLowerCase() + "." +
                        baseFieldName;
            }
        }

        FieldTraversal baseField =
                getDataElementsForType(businessObject).get(baseFieldName);

        while (baseField == null && startIndex < fieldNameParts.length) {
            baseFieldName += "." + fieldNameParts[startIndex++];
            baseField =
                    getDataElementsForType(businessObject).get(baseFieldName);
        }

        if (baseField == null) {
            return false;
        }

        requiredField.addFieldToPath(baseField.targetField());

        return startIndex >= fieldNameParts.length ||
                buildField(baseField.getType(), fieldNameParts, startIndex,
                        requiredField);
    }    
    


}
