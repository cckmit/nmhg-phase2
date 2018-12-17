package tavant.twms.domain.policy;

public class WarrantyCoverageRequestAuditDescendingComparator implements
		java.util.Comparator<WarrantyCoverageRequestAudit> {

	public int compare(WarrantyCoverageRequestAudit o1,
			WarrantyCoverageRequestAudit o2) {
		if (o1 == null || o1.getId() == null) {
			if (o2 != null && o2.getId()!=null) {
				return 1;
			} else {
				return 0;
			}
		}
		if (o2 == null || o2.getId() == null) {
			return -1;
		}
		if (o1.getId().longValue() < o2.getId().longValue()) {
			return 1;
		} else if (o1.getId().longValue() == o2.getId().longValue()) {
			return 0;
		} else {
			return -1;
		}
	}
}
