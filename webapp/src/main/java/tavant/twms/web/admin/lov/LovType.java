/**
 * 
 */
package tavant.twms.web.admin.lov;

import tavant.twms.domain.common.ListOfValues;

public class LovType implements Comparable<LovType>{
	String name;
	String displayName;
	
	public LovType(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
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
	
	public int compareTo(LovType lov) {
        if (this.displayName != null) {
            return this.displayName.compareTo(lov.displayName);
        } else {
            return -1;
        }
    }

}