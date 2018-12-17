package tavant.twms.domain.bu;
import java.io.Serializable;

@SuppressWarnings("serial")
public class BusinessUnitInfo implements Serializable{
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
