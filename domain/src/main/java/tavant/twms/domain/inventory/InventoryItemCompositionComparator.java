package tavant.twms.domain.inventory;

import java.util.Comparator;

public class InventoryItemCompositionComparator implements Comparator<InventoryItemComposition>{

	public int compare(InventoryItemComposition arg0,
			InventoryItemComposition arg1) {
		try{
		Integer s1=Integer.parseInt(arg0.getSequenceNumber());
		Integer s2=Integer.parseInt(arg1.getSequenceNumber());
		return s1.compareTo(s2);
		}
		catch(NumberFormatException e){
			String s1 = arg0.getSequenceNumber().toUpperCase();
			String s2 = arg1.getSequenceNumber().toUpperCase();
			return s1.compareTo(s2);
		}
	}

}
