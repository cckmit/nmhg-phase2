package tavant.twms.domain.inventory;

import java.util.Comparator;

public class InventoryItemOptionsComparator implements Comparator<Option>{
	
	public int compare(Option arg0,
			Option arg1) {
		try {
			Integer s1 = Integer.parseInt(arg0.getOrderOptionLineNumber());
			Integer s2 = Integer.parseInt(arg1.getOrderOptionLineNumber());
			return s1.compareTo(s2);
		} catch (NumberFormatException e) {
			String s1 = arg0.getOrderOptionLineNumber().toUpperCase();
			String s2 = arg1.getOrderOptionLineNumber().toUpperCase();
			return s1.compareTo(s2);
		}
	}
}
