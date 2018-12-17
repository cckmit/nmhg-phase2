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
package tavant.twms.domain.common;

class TimeBoundInteger extends TimeBoundValue<Integer> {
    private Integer value;
    
    private TimeBoundIntegers parent;
    
    public TimeBoundInteger(Integer value,CalendarDuration forDuration) {
        super(forDuration);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer newValue) {
        value = newValue;
    }

    @Override
    public void setParent(Object parent) {
        this.parent = (TimeBoundIntegers)parent;
    }
    
    
}