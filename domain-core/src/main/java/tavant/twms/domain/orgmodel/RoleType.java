package tavant.twms.domain.orgmodel;
import java.util.HashMap;
import java.util.Map;


public enum RoleType {
	INTERNAL("Internal"), EXTERNAL("External"), APPLICATION("Application"), DEALER("Dealer"), CUSTOMER("Customer"), DEALER_OWNED("Dealer Owned");

	private String type;

	private RoleType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static Map<RoleType, String> addAllEnums() {
		Map<RoleType, String> listOfEnums = new HashMap<RoleType, String>();
		listOfEnums.put(INTERNAL, INTERNAL.type);
		listOfEnums.put(EXTERNAL, EXTERNAL.type);
		listOfEnums.put(APPLICATION, APPLICATION.type);
        listOfEnums.put(DEALER, DEALER.type);
        listOfEnums.put(CUSTOMER, CUSTOMER.type);
        listOfEnums.put(DEALER_OWNED, DEALER_OWNED.type);
		return listOfEnums;
	}
	

}
