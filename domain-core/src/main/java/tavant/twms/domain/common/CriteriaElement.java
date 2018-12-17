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
package tavant.twms.domain.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author radhakrishnan.j
 * 
 */
@Embeddable
public class CriteriaElement implements Comparable<CriteriaElement> {
    private String domainName;
    
    @Column(name="prop_expr")
    private String propertyExpression;


    public CriteriaElement() {
		super();
	}

	public CriteriaElement(String domainName, String propertyExpression) {
		super();
		this.domainName = domainName;
		this.propertyExpression = propertyExpression;
	}

	public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getPropertyExpression() {
        return propertyExpression;
    }

    public void setPropertyExpression(String propertyExpression) {
        this.propertyExpression = propertyExpression;
    }

    public int compareTo(CriteriaElement o) {
        return getDomainName().compareTo( o.getDomainName() );
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((domainName == null) ? 0 : domainName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CriteriaElement other = (CriteriaElement) obj;
        if (domainName == null) {
            if (other.domainName != null)
                return false;
        } else if (!domainName.equals(other.domainName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getDomainName();
    }

    
    
}
