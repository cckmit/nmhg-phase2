package tavant.twms.domain.campaign;

import java.util.Comparator;

import tavant.twms.domain.inventory.InventoryItem;

public class SerialNumberComparator implements Comparator<InventoryItem> {

	public int compare(InventoryItem arg0, InventoryItem arg1) {
		String s1=arg0.getSerialNumber().toUpperCase();
		String s2=arg1.getSerialNumber().toUpperCase();
		return s1.compareTo(s2);
	}

}
