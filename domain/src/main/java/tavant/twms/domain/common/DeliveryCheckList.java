package tavant.twms.domain.common;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="delivery_check_list")
public class DeliveryCheckList {
	
@Id
private String id;
	
private String deliveryCheckList;

public String getDeliveryCheckList() {
	return deliveryCheckList;
}

public void setDeliveryCheckList(String deliveryCheckList) {
	this.deliveryCheckList = deliveryCheckList;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}
	
}
