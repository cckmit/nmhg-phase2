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
 *   @author Hari Krishna Y D
 */

package tavant.twms.domain.bu;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@SuppressWarnings("serial")
public class BusinessUnit implements Comparable<BusinessUnit>, Serializable {

	@Id
	private String name;

	private String displayName;

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(BusinessUnit other)
	{
        if (other == null)
        {
            return 1;
        }
        int nameCompare = this.displayName.compareTo(other.displayName);
        return nameCompare;
    }
}
