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
package tavant.twms.domain.query.view;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class InboxField implements Comparable<InboxField> {
	private String id;
	private String expression;
	private String type;
	private String displayName;
	private boolean allowSort;
	private boolean allowFilter;
	private boolean hidden;
	private boolean allowDefaultSort;
	private Integer fixedWidth;

	public InboxField() {

	}

	/**
	 * Constructor to create inboxField for inbox view By default it allowSort
	 * is set to true i.e. it allows to sort on the field unless explcitly set
	 * to false
	 * 
	 * @param name
	 * @param type
	 * @param label
	 */
	public InboxField(String name, String type, String label) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = true;
		this.allowFilter = true;
	}

	public InboxField(String id, String name, String type, String label) {
		this.id = id;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = true;
		this.allowFilter = true;
	}
	
	public InboxField(String name, String type, String label,
			boolean allowSort, boolean allowFilter, boolean hidden,
			boolean defaultSort) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = allowSort;
		this.allowFilter = allowFilter;
		this.hidden = hidden;
		this.allowDefaultSort = defaultSort;
	}

	public InboxField(String name, String type, String label, boolean allowSort) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = allowSort;
		this.allowFilter = true;
	}

	public InboxField(String name, String type, String label,
			boolean allowSort, boolean allowFilter) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = allowSort;
		this.allowFilter = allowFilter;
	}
	
	//new constructors
	public InboxField(String name, String type, String label,Integer fixedWidth) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = true;
		this.allowFilter = true;
		this.fixedWidth = fixedWidth;
	}

    public InboxField(String id, String name, String type, String label,Integer fixedWidth) {
        this.id = id;
        this.expression = name;
        this.type = type;
        this.displayName = label;
        this.allowSort = true;
        this.allowFilter = true;
        this.fixedWidth = fixedWidth;
    }

	public InboxField(String name, String type, String label,
			boolean allowSort, boolean allowFilter, boolean hidden,
			boolean defaultSort,Integer fixedWidth) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = allowSort;
		this.allowFilter = allowFilter;
		this.hidden = hidden;
		this.allowDefaultSort = defaultSort;
		this.fixedWidth = fixedWidth;
	}

	public InboxField(String name, String type, String label, boolean allowSort,Integer fixedWidth) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = allowSort;
		this.allowFilter = true;
		this.fixedWidth = fixedWidth;
	}

	public InboxField(String name, String type, String label,
			boolean allowSort, boolean allowFilter,Integer fixedWidth) {
		this.id = name;
		this.expression = name;
		this.type = type;
		this.displayName = label;
		this.allowSort = allowSort;
		this.allowFilter = allowFilter;
		this.fixedWidth = fixedWidth;
	}
	
	public InboxField(String id,String expression, String type, String label,
            boolean allowSort, boolean allowFilter, boolean hidden,
            boolean defaultSort,Integer fixedWidth) {
        this.id = id;
        this.expression = expression;
        this.type = type;
        this.displayName = label;
        this.allowSort = allowSort;
        this.allowFilter = allowFilter;
        this.hidden = hidden;
        this.allowDefaultSort = defaultSort;
        this.fixedWidth = fixedWidth;
    }


	public String getExpression() {
		return expression;
	}

	public void setExpression(String name) {
		this.expression = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setdisplayName(String label) {
		this.displayName = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(InboxField otherField) {
		String otherDisplayName = (otherField).getDisplayName();
		return this.getDisplayName().compareTo(otherDisplayName);
	}

	public boolean isAllowSort() {
		return allowSort;
	}

	public void setAllowSort(boolean allowSort) {
		this.allowSort = allowSort;
	}

	public boolean isAllowFilter() {
		return allowFilter;
	}

	public void setAllowFilter(boolean allowFilter) {
		this.allowFilter = allowFilter;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isAllowDefaultSort() {
		return allowDefaultSort;
	}

	public void setAllowDefaultSort(boolean allowDefaultSort) {
		this.allowDefaultSort = allowDefaultSort;
	}

	public Integer getFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(Integer fixedWidth) {
		this.fixedWidth = fixedWidth;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
