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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Hibernate;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.ClaimState;

/**
 * Criteria for a list.
 * 
 * @author <a href="radhakrishnan.j@tavant.com">radhakrishnan.j</a>
 * @date Sep 16, 2006
 */
public class ListCriteria {
    protected Map<String, String> sortCriteria = new LinkedHashMap<String,String>();
    
    //TODO: A TreeMap iterates the contents in the natural order of the key.
    //TODO: This would serve to generate the same query string for the same set
    //TODO: of parameter names.
    protected Map<String, String> filterCriteria = new TreeMap<String,String>();
    PageSpecification pageSpecification = new PageSpecification();
    protected Map<String,String> paramTypeMap = new HashMap<String,String>(10);
 
	private StringBuffer charactersToBeEscaped = new StringBuffer(20);

    private Pattern escapableCharactersPattern;
    private static final Pattern DOT_MATCHING_PATTERN = Pattern.compile("\\.|\\[|\\]");

    private Boolean showClaimStatusToDealer = false;
    private boolean caseSensitiveSort = false;
    
    String historicalClaimNumber;
    
    public ListCriteria() {
        super();

        // Set defaut escapable characters.
        addCharacterToBeEscaped('_');
    }

    public void addSortCriteria(String columnName, boolean asc) {
        if(StringUtils.hasText(columnName)) { 
            sortCriteria.put(columnName, asc ? "asc" : "desc");
        }
    }

    public void removeSortCriteria() {
        sortCriteria.clear();
    }

    public void addFilterCriteria(String columnName, String value) {
        if(StringUtils.hasText(columnName) ){
            //String escapedValue = escapeSpecialCharacters(value);
            filterCriteria.put(columnName, value);
        }
    }

    /**
     * Escapes (prefixes a '\') the special characters so that they lose
     * their special behavior. For eg., by default the '_' character
     * works us a wildcard character (same as '%' in SQL) in MYSQL,
     * but we escape it since it is not a *standard* wildcard character.
     *
     * @param valueToBeEscaped
     * @return
     */
    protected String escapeSpecialCharacters(String valueToBeEscaped) {
        if(StringUtils.hasText(valueToBeEscaped)) {
            Matcher escapableCharactersMatcher =
                escapableCharactersPattern.matcher(valueToBeEscaped);
            return escapableCharactersMatcher.replaceAll("\\\\$0");
        } else {
            return valueToBeEscaped;
        }
    }

    public final void addCharacterToBeEscaped(char characterToBeEscaped) {
        // Regex itself needs to be escaped.
       // charactersToBeEscaped.append("\\");
        charactersToBeEscaped.append(characterToBeEscaped);

        escapableCharactersPattern = Pattern.compile("([" +
                charactersToBeEscaped+ "])");
    }

    public void removeFilterCriteria() {
        filterCriteria.clear();
    }

    public Map<String, String> getFilterCriteria() {
        return filterCriteria;
    }

    public Map<String, String> getSortCriteria() {
        return sortCriteria;
    }

    public PageSpecification getPageSpecification() {
        return pageSpecification;
    }

    public void setPageSpecification(PageSpecification pageSpecification) {
        this.pageSpecification = pageSpecification;
    }
    
   
    public boolean isFilterCriteriaSpecified() {
        return !filterCriteria.isEmpty();
    }

    @Deprecated
    public String getFilterCriteriaString() {
        if (filterCriteria.size() > 0) {
            StringBuffer dynamicQuery = new StringBuffer();
            for (Iterator it = filterCriteria.keySet().iterator();it.hasNext();) {
                String key = (String)it.next();
                String value = filterCriteria.get(key);
                dynamicQuery.append(key);
                if( value != null ) {
                    dynamicQuery.append(" like ");
                    dynamicQuery.append("'");
                    dynamicQuery.append(value);
                    dynamicQuery.append("%'");
                } else {
                    dynamicQuery.append(" is null ");
                }
                if( it.hasNext() ) {
                    dynamicQuery.append(" and ");
                }
            }
            return dynamicQuery.toString();
        }
        return "";
    }
    
    public String getParamterizedFilterCriteria() {
        boolean isEscapeAdded = false;
        if (filterCriteria.size() > 0) {
            StringBuffer dynamicQuery = new StringBuffer();
            for (Iterator it = filterCriteria.keySet().iterator();it.hasNext();) {
                String thePropertyExpression = (String)it.next();
                String value = filterCriteria.get(thePropertyExpression);
                   if(isDateProperty(thePropertyExpression)) {
                    dynamicQuery.append("datelike(");
                    dynamicQuery.append(thePropertyExpression);
                    dynamicQuery.append(", '");
                    dynamicQuery.append(value);
                    dynamicQuery.append("') > 0");
                }
                   else if (isBooleanExpression(thePropertyExpression)) {
					if ("true".startsWith(value.toLowerCase())) {
						dynamicQuery.append(thePropertyExpression);
						dynamicQuery.append("=");
						dynamicQuery.append(true);
					} else if ("false".startsWith(value.toLowerCase())) {
						dynamicQuery.append(thePropertyExpression);
						dynamicQuery.append("=");
						dynamicQuery.append(false);
					}else{//filter issue has been resolved for Boolean parameters if the value is neither true nor false
						dynamicQuery.append(thePropertyExpression);
						dynamicQuery.append("=");
						dynamicQuery.append(5);// random value instead of 0 or 1
					}

				}
                else if (isNumberProperty(thePropertyExpression))
                {
                	if( value!= null ) {
                		dynamicQuery.append("upper(to_char("+thePropertyExpression+"))");
                		dynamicQuery.append(" like ");
                		String normalizedKey = stripDOTCharactersFrom(thePropertyExpression);
                		dynamicQuery.append(':').append(normalizedKey);
                	}
                    if (value.contains("_") && !isEscapeAdded) {
                        dynamicQuery.append(" ESCAPE '#' ");
                        isEscapeAdded = true;
                    }
                }
                //enum:enumType:propertyExpression  [example - enum:ClaimState:state]
                else if (thePropertyExpression.startsWith("enum:")) {
                	String[] expression = thePropertyExpression.split(":");
                	dynamicQuery.append(expression[2]);
                	value = getEnumFilterCriteria(expression[1],value,getShowClaimStatusToDealer());
                	if( value!= null ) {
	                    dynamicQuery.append(" in ");
	                    dynamicQuery.append(value);
	                } else {
	                    dynamicQuery.append(" is null ");
	                }
                }
                else
                {
	                dynamicQuery.append("upper("+thePropertyExpression+")");
	                if( value!= null ) {
	                    dynamicQuery.append(" like ");
	                    String normalizedKey = stripDOTCharactersFrom(thePropertyExpression);
	                    dynamicQuery.append(':').append(normalizedKey);
	                } else {
	                    dynamicQuery.append(" is null ");
	                }
                    if (value.contains("_") && !isEscapeAdded) {
                        dynamicQuery.append(" ESCAPE '#' ");
                        isEscapeAdded = true;
                    }
                }
                if (it.hasNext()) {
                       dynamicQuery.append(" and ");
                } 
            }
            return dynamicQuery.toString();
        }
        return "";
    }
    
    protected String getEnumFilterCriteria(String enumType, String value, boolean showClaimStatusToDealer) {
    	if(value == null) 
    		return null;
    	if("ClaimState".equals(enumType)) {
    		List<ClaimState> claimStates = ClaimState.getStatesStartingWith(value, showClaimStatusToDealer);
    		if(claimStates.size() == 0)
    			return null;
    		StringBuffer criteriaStr = new StringBuffer();
    		for(ClaimState state : claimStates) {
    			if(criteriaStr.length() == 0)
    				criteriaStr.append("(");
    			else
    				criteriaStr.append(",");
    			criteriaStr.append("'").append(state.name()).append("'");
    		}
    		criteriaStr.append(")");
    		return criteriaStr.toString();
    	}
    	return null;
    }

// Override this method if you have any proeprty which does not end with Date
// Note that the propertyExpression is getAlias().expression
    protected boolean isDateProperty(String propertyExpression) {
     	return (propertyExpression.endsWith("Date") 
    				|| propertyExpression.endsWith("createdOn")|| propertyExpression.endsWith("updatedOn"));
    }

// Override this method if you have any proeprty which does not end with Number
// Note that the propertyExpression is getAlias().expression
    protected boolean isNumberProperty(String propertyExpression){
        return propertyExpression.endsWith("Number")
                        || propertyExpression.endsWith("hoursOnMachine") || propertyExpression.endsWith("priority");
    }
    
    public String getParamterizedFilterCriteriaForDate(boolean isPrefixRequired) {
        if (filterCriteria.size() > 0) {
            StringBuffer dynamicQuery = new StringBuffer();
            for (Iterator it = filterCriteria.keySet().iterator();it.hasNext();) {
                String thePropertyExpression = (String)it.next();
                String value = filterCriteria.get(thePropertyExpression);
                if(isDateProperty(thePropertyExpression)) {
                	dynamicQuery.append(" and ");
                    dynamicQuery.append("datelike(");
                    if (isPrefixRequired) {
                    	dynamicQuery.append("item."+thePropertyExpression);	
                    } else {
                    	dynamicQuery.append(thePropertyExpression);
                    }                    
                    dynamicQuery.append(", '");
                    dynamicQuery.append(value);
                    dynamicQuery.append("') > 0");
                    
                }
              }
            return dynamicQuery.toString();
        }
        return "";
    }

   
    public Map<String,Object> getParameterMap() {
        Map <String,Object> nonNullParamsMap = new HashMap<String,Object>();
        for (String thePropertyExpression : filterCriteria.keySet()) {
            String value = filterCriteria.get(thePropertyExpression);
            if (value != null && !isDateProperty(thePropertyExpression)&& !isBooleanExpression(thePropertyExpression) && !thePropertyExpression.startsWith("enum:")) {
                if (value.contains(charactersToBeEscaped)) {
                    value = value.replace(charactersToBeEscaped.toString(), "#" + charactersToBeEscaped.toString());
                }
                String normalizedKey = stripDOTCharactersFrom(thePropertyExpression);
                nonNullParamsMap.put(normalizedKey, value.toUpperCase() + "%");
            }
        }
        return nonNullParamsMap;
    }
    //this is a temporary solution for supporting filtering on non string based columns.
    //It still doesn't work for date columns.We need to work with appropriate column types
    //instead of default 'STRING' for date columns.
    public Map<String,Object> getTypedParameterMap() {
        Map <String,Object> nonNullParamsMap = new HashMap<String,Object>();
        for (String thePropertyExpression : filterCriteria.keySet()) {
        	if(thePropertyExpression.startsWith("enum:") || isDateProperty(thePropertyExpression))
        		continue;
            String value = filterCriteria.get(thePropertyExpression);
            if (value != null) {
                String normalizedKey = stripDOTCharactersFrom(thePropertyExpression);
                nonNullParamsMap.put(normalizedKey, new TypedQueryParameter(value + "%",Hibernate.STRING));
            }
        }
        return nonNullParamsMap;
    }
    
    public boolean isSortCriteriaSpecified() {
        return !sortCriteria.isEmpty();
    }
    
    public String getSortCriteriaString() { 
        /**
         * TODO : kannan.ekanath This API looks odd to be. Check the caller of the API.
         * It must first append order by before calling this API.
         * 
         * I would want the "order by" string to be appended in this API only
         */
        
        if (sortCriteria.size() > 0) {
            StringBuffer dynamicQuery = new StringBuffer();
            for (Iterator it = sortCriteria.keySet().iterator();it.hasNext();) {
                String key = (String)it.next();
                if(key.startsWith("enum:")) {
                	dynamicQuery.append((key.split(":")[2]));
                } else if (!isDateProperty(key)) {
                	dynamicQuery = (isNumberProperty(key) || caseSensitiveSort)? 
                					dynamicQuery.append(key): dynamicQuery.append("upper (" + key + ")");
                } else {
                    dynamicQuery.append(key);
                }
                dynamicQuery.append(" ");
                dynamicQuery.append(sortCriteria.get(key));
                if( it.hasNext() ) {
                    dynamicQuery.append(", ");
                }
            }
            return dynamicQuery.toString();
        }
        return "";
    }

    protected String stripDOTCharactersFrom(String key) {
        if(StringUtils.hasText(key)) {
            Matcher dotMatcher = DOT_MATCHING_PATTERN.matcher(key);
            return dotMatcher.replaceAll("");
        } else {
            return key;
        }
    }

	public Boolean getShowClaimStatusToDealer() {
		return showClaimStatusToDealer;
	}

	public void setShowClaimStatusToDealer(Boolean showClaimStatusToDealer) {
		this.showClaimStatusToDealer = showClaimStatusToDealer;
	}

	public void addParamType(String paramName, String paramType) {
		if (StringUtils.hasText(paramName)) {
			paramTypeMap.put(paramName, paramType);
		}
	}

	public Map<String, String> getParamTypeMap() {
		return paramTypeMap;
	}

	public boolean isBooleanExpression(String expression) {
		String paramType = paramTypeMap.get(expression);
		return StringUtils.hasText(paramType)
				&& "BOOLEAN".equals(paramType.toUpperCase());
	}

	public void setCaseSensitiveSort(boolean caseSensitiveSort) {
		this.caseSensitiveSort = caseSensitiveSort;
	}

	public String getHistoricalClaimNumber() {
		return historicalClaimNumber;
	}

	public void setHistoricalClaimNumber(String historicalClaimNumber) {
		this.historicalClaimNumber = historicalClaimNumber;
	}
	
}