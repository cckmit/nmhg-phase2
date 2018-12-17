package tavant.twms.web.admin.campaign;

import java.util.Comparator;
import tavant.twms.domain.campaign.CampaignNotification;

public class SerialNumberComparator implements Comparator<CampaignNotification> {

	public int compare(CampaignNotification arg0, CampaignNotification arg1) {
		String s1=arg0.getItem().getSerialNumber().toUpperCase();
		String s2=arg1.getItem().getSerialNumber().toUpperCase();
		return s1.compareTo(s2);
	}

}
