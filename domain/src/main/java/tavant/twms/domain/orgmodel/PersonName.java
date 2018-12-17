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
package tavant.twms.domain.orgmodel;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author radhakrishnan.j
 * 
 */
@Embeddable
public class PersonName {
    private String firstName;

    @Column(nullable=true)
    private String middleName;

    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getFullName() {
        StringBuffer buf = new StringBuffer();
        buf.append(firstName).append(' ');
        if (middleName!=null) {
            buf.append(middleName).append(' ');
        }        
        buf.append(lastName).append(' ');
        return buf.toString();
    }
}
