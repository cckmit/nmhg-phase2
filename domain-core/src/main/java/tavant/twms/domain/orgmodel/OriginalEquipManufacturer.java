package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@SuppressWarnings("serial")
@Inheritance(strategy=InheritanceType.JOINED)
public class OriginalEquipManufacturer extends ServiceProvider implements Serializable{
 
	private String orgEquipManufNumber;

public String getOrgEquipManufNumber() {
	return orgEquipManufNumber;
}

public void setOrgEquipManufNumber(String orgEquipManufNumber) {
	this.orgEquipManufNumber = orgEquipManufNumber;
}


}
