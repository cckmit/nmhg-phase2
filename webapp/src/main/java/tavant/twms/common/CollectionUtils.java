
package tavant.twms.common;

import java.util.Iterator;
import java.util.List;

/**
 * @author kuldeep.patil
 *
 */
public class CollectionUtils {
	static public void removeNullsFromList(List oList) {
		if(!org.springframework.util.CollectionUtils.isEmpty(oList)){
			for(Iterator iter = oList.iterator();iter.hasNext();){
				if (iter.next() == null) {
					iter.remove();
				}
			}
		}
	}
}
