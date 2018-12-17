package tavant.twms.web.actions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class SortedHashMap<K, V> extends HashMap<K, V> {

	public Iterator<Object> iterator() { 
		/* *** The following lines are replaced with the last line, as part of Perf Fixes *** */
		/*
		 * Object[] array = this.entrySet().toArray(); Arrays.sort(array);
		 * return Arrays.asList(array).iterator();
		 */
		return this.entrySet().iterator();
	}

	@SuppressWarnings("unchecked")
	public Set entrySet() {
		Set unsortedSet = super.entrySet();
		TreeSet sortedSet = new TreeSet(
				new Comparator<Map.Entry<String, String>>() {
					public int compare(Map.Entry<String, String> p1,
							Map.Entry<String, String> p2) {
						int nameCompare = p1.getValue()
								.compareTo(p2.getValue());
						return nameCompare;
					}
				});
		sortedSet.addAll(unsortedSet);
		return sortedSet;
	}

}
