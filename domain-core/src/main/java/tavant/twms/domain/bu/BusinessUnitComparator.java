package tavant.twms.domain.bu;

import java.io.Serializable;
import java.util.Comparator;

@SuppressWarnings("serial")
public class BusinessUnitComparator implements Comparator<BusinessUnit>,Serializable{
	public int compare(BusinessUnit first, BusinessUnit second){	
		if (second == null) 
        {
            return 1;
        }
	    int nameCompare = first.getDisplayName().compareTo(second.getDisplayName());
	    return nameCompare;
	}
}


