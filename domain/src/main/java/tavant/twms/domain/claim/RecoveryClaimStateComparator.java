package tavant.twms.domain.claim;

import java.util.Comparator;

public class RecoveryClaimStateComparator implements
		Comparator<RecoveryClaimState> {

	public int compare(RecoveryClaimState o1, RecoveryClaimState o2) {
		return o1.getState().compareToIgnoreCase(o2.getState());
	}
}
