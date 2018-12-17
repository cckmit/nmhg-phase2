package tavant.twms.domain.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionalMapping {

	private FunctionalArea functionalArea;	
	private Map<String, UserAction> permissions = new HashMap<String, UserAction>();    
	
	public FunctionalArea getFunctionalArea() {
		return functionalArea;
	}

	public void setFunctionalArea(FunctionalArea funcArea) {
		this.functionalArea = funcArea;
	}

	public Map<String, UserAction> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, UserAction> permissions) {
		this.permissions = permissions;
	}
	
	public List<Permission> getPermissionList(SubjectArea subjectArea){
		List<Permission> list = new ArrayList<Permission>();
		for (UserAction element : permissions.values()) {			
				list.add(new Permission(element,functionalArea,subjectArea));
			}
		return list;
	}
	
	public static List<FunctionalMapping> convertMapToList(Map<FunctionalArea, Map<String, UserAction>> map){
		List<FunctionalMapping> mappings = new ArrayList<FunctionalMapping>();
		for (Map.Entry<FunctionalArea, Map<String, UserAction>>  functionalMapping : map.entrySet()) {
			FunctionalMapping fuMapping = new FunctionalMapping();
			fuMapping.setFunctionalArea(functionalMapping.getKey());
			fuMapping.setPermissions(functionalMapping.getValue());
			mappings.add(fuMapping);			
		}		
		Collections.sort(mappings, new  FunctionalMappingComparator());
		return mappings;		
	}
	
	
 	 static class FunctionalMappingComparator implements java.util.Comparator<FunctionalMapping> {
		public  int compare(FunctionalMapping fm1, FunctionalMapping fm2) {
			if (fm2 == null || fm2.getFunctionalArea() == null) {
				return 0;
			}
			if (fm1 == null || fm1.getFunctionalArea() == null) {
				return 1;
			}
			return fm1.getFunctionalArea().getId().compareTo(
					fm2.getFunctionalArea().getId());
		}
	}
	
}
