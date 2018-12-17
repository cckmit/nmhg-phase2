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

import org.hibernate.annotations.AccessType;



/**
 * @author radhakrishnan.j
 *
 */
@Embeddable
@AccessType("field")
public class DistanceUnit {
    @Column(nullable=false)
    private String unit;
    
    public static final DistanceUnit mile = new DistanceUnit("mile");
    
    public static final DistanceUnit kilometer = new DistanceUnit("kilometer");    
    
    public static final DistanceUnit meter = new DistanceUnit("meter");    
    
    public static final DistanceUnit foot = new DistanceUnit("foot");
    
    private DistanceUnit(String newUnit) {
        unit = newUnit;
    }
    
    @Override    
    public String toString() {
        return unit;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((unit == null) ? 0 : unit.hashCode());
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
        final DistanceUnit other = (DistanceUnit) obj;
        if (unit == null) {
            if (other.unit != null)
                return false;
        } else if (!unit.equals(other.unit))
            return false;
        return true;
    }
}
