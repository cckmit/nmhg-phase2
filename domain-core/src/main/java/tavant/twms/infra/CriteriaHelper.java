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
package tavant.twms.infra;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import java.util.List;

import tavant.twms.infra.hibernate.DateLikeExpression;

public class CriteriaHelper {

    public Criteria dateLike(Criteria criteria, String property, Object value) {
        return criteria.add(new DateLikeExpression(property, value.toString()));
    }

    public Criteria dateLikeIfNotNull(Criteria criteria, String property, Object value) {
        if(value != null) {
            dateLike(criteria, property, value);
        }

        return criteria;
    }

    public Criteria substringLikeIfNotNull(Criteria criteria, String property, Object value) {

        if (value != null) {
            criteria.add(Restrictions.ilike(property,value.toString(), MatchMode.ANYWHERE));
        }

        return criteria;
    }
    
    public Criteria subStringStartsIfNotNull(Criteria criteria, String property, Object value) {

        if (value != null) {
            criteria.add(Restrictions.ilike(property,value.toString(), MatchMode.START));
        }

        return criteria;
    }    

    public Criteria likeIfNotNull(Criteria criteria, String property, Object value) {

        if (value != null) {
            criteria.add(Restrictions.like(property, value));
        }

        return criteria;
    }

    public Criteria likeIfNotNull(Criteria criteria, String property, Object value, MatchMode matchMode) {

        if (value != null) {
            criteria.add(Restrictions.like(property, (String) value, matchMode));
        }

        return criteria;
    }

    public Criteria ilikeIfNotNull(Criteria criteria, String property, Object value) {

        if (value != null) {
            criteria.add(Restrictions.ilike(property, value));
        }

        return criteria;
    }

    public Criteria ilikeIfNotNull(Criteria criteria, String property, Object value, MatchMode matchMode) {

        if (value != null) {
            criteria.add(Restrictions.ilike(property, (String) value, matchMode));
        }

        return criteria;
    }

    public Criteria ilikeUsingToUpper(Criteria criteria, String property, Object value) {

        return criteria.add(new IlikeExpression(property, value));
    }

    public Criteria ilikeUsingToUpper(Criteria criteria, String property, Object value, MatchMode matchMode) {

        return criteria.add(new IlikeExpression(property, (String) value, matchMode));
    }

    public Criteria ilikeIfNotNullUsingToUpper(Criteria criteria, String property, Object value) {

        if (value != null) {
            criteria.add(new IlikeExpression(property, value));
        }

        return criteria;
    }

    public Criteria ilikeIfNotNullUsingToUpper(Criteria criteria, String property, Object value, MatchMode matchMode) {

        if (value != null) {
            criteria.add(new IlikeExpression(property, (String) value, matchMode));
        }

        return criteria;
    }

    public Criteria betweenIfNotNull(Criteria criteria, String property, Object startValue, Object endValue) {

        if ((startValue != null) && (endValue != null)) {
            criteria.add(Restrictions.between(property, startValue, endValue));
        }

        return criteria;
    }

    public Criteria inIfNotNull(Criteria criteria, String property, Object[] values) {

        if ((values != null) && (values.length > 0)) {
            criteria.add(Restrictions.in(property, values));
        }

        return criteria;
    }

    public Criteria eqIfNotNull(Criteria criteria, String property, Object value) {

        if (value != null) {
            criteria.add(Restrictions.eq(property, value));
        }

        return criteria;
    }

    public Criteria eqIfhasContent(Criteria criteria, String property, String value) {

        if (value != null && StringUtils.hasText(value)) {
            criteria.add(Restrictions.eq(property, value));
        }

        return criteria;
    }

    public Criteria neIfNotNull(Criteria criteria, String property, Object value) {

        if (value != null) {
            criteria.add(Restrictions.ne(property, value));
        }

        return criteria;
    }

    
    private boolean isNotSet(Object value) {
    	return (value instanceof String) ? !StringUtils.hasText((String) value) :
    		(value == null);
    }

    
    public Criteria dateRangeIfNotNull(Criteria criteria, String dateProperty,
                                       Object startDate, Object endDate) {

        if ((startDate != null) && (endDate != null)) {
            criteria.add(Restrictions.between(dateProperty, startDate, endDate));
        } else if (startDate != null) {
            criteria.add(Restrictions.ge(dateProperty, startDate));
        } else if (endDate != null) {
            criteria.add(Restrictions.le(dateProperty, endDate));
        }

        return criteria;
    }
    
    public DetachedCriteria dateRangeIfNotNull(DetachedCriteria criteria, String dateProperty,
            Object startDate, Object endDate) {

		if ((startDate != null) && (endDate != null)) {
			criteria.add(Restrictions.between(dateProperty, startDate, endDate));
		} else if (startDate != null) {
			criteria.add(Restrictions.ge(dateProperty, startDate));
		} else if (endDate != null) {
			criteria.add(Restrictions.le(dateProperty, endDate));
		}
		
		return criteria;
	}

    /**
     * Creates aliases for association nodes encountered in a search expression.
     * Already created aliases are skipped.
     * <p/>
     * For eg., given the search expression "forItem.ofType.productType.id", the
     * following aliases are created:
     * <ul>
     * <li>alias forItem for association path forItem</li>
     * <li>alias ofType for association path forItem.ofType</li>
     * <li>alias productType for association path forItem.ofType.productType</li>
     * </ul>
     *
     * @param propertyName
     * @param addedAliases
     * @return
     */
    public String processNestedAssociations(Criteria criteria, String propertyName,
                                             List<String> addedAliases) {
        return processNestedAssociations(criteria, propertyName,
                CriteriaSpecification.INNER_JOIN, addedAliases);
    }

    /**
     * Creates aliases for association nodes encountered in a search expression.
     * Already created aliases are skipped.
     * <p/>
     * For eg., given the search expression "forItem.ofType.productType.id", the
     * following aliases are created:
     * <ul>
     * <li>alias forItem for association path forItem</li>
     * <li>alias ofType for association path forItem.ofType</li>
     * <li>alias productType for association path forItem.ofType.productType</li>
     * </ul>
     *
     * @param propertyName
     * @param joinType
     * @param addedAliases
     * @return
     */
    public String processNestedAssociations(Criteria criteria, String propertyName,
                                             int joinType, List<String> addedAliases) {

        String[] associationElements =
                StringUtils.delimitedListToStringArray(propertyName, ".");

        if (associationElements.length > 1) { // Property includes an association.
            StringBuffer associationPath = new StringBuffer(30);

            associationPath.append(associationElements[0]);
            addAliasIfRequired(criteria,
                    associationPath.toString(), joinType, addedAliases);

            int lastOne = associationElements.length - 1;

            for (int i = 1; i < lastOne; i++) {
                associationPath.append(".");
                associationPath.append(associationElements[i]);
                addAliasIfRequired(criteria,
                        associationPath.toString(), associationElements[i],
                        joinType, addedAliases);
            }

            StringBuffer processedProperty = new StringBuffer(20);
            processedProperty.append(associationElements[lastOne - 1]);
            processedProperty.append(".");
            processedProperty.append(associationElements[lastOne]);

            return processedProperty.toString();
        } else {
            return propertyName;
        }
    }

    public void addAliasIfRequired(Criteria criteria, String association, String alias,
                List<String> addedAliases) {

        addAliasIfRequired(criteria, association, alias,
                CriteriaSpecification.INNER_JOIN, addedAliases);
    }

    
    public void addAliasIfRequired(Criteria criteria, String association,
                                   int joinType, List<String> addedAliases) {

        addAliasIfRequired(criteria, association, association,
                joinType, addedAliases);
    }

    public void addAliasIfRequired(Criteria criteria, String association, String alias,
                                   int joinType, List<String> addedAliases) {

        if (!addedAliases.contains(alias)) {
            criteria.createAlias(association, alias, joinType);
            addedAliases.add(alias);
        }
    }

}
