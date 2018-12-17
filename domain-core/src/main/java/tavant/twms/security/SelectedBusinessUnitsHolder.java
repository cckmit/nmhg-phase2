package tavant.twms.security;


public class SelectedBusinessUnitsHolder {
	
//	private static ThreadLocal<String[]> selectedBusinessUnit = null;
//   
//	public static void setSelectedBusinessUnit(String[] bus) {
//		if(bus != null && bus.length > 0)
//		{
//			selectedBusinessUnit = new ThreadLocal<String[]>();					
//			selectedBusinessUnit.set(bus);
//		}	
//	}
//	
//	public static ThreadLocal<String[]> getSelectedBusinessUnit() {
//		return selectedBusinessUnit;
//	}
//	
//	public static void clearChosenBusinessUnitFilter() {
//		selectedBusinessUnit.remove();
//	}
//	
	private static ThreadLocal<String> selectedBusinessUnit = new ThreadLocal<String>();
	   
	public static void setSelectedBusinessUnit(String buName) {
			selectedBusinessUnit.set(buName);			
	}
	
	public static String getSelectedBusinessUnit() {
		return selectedBusinessUnit.get();
	}
	
	public static void clearChosenBusinessUnitFilter() {
		selectedBusinessUnit.remove();		
	}
}
