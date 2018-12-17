package tavant.twms.web.claim;

public class MultiClaimAttributeMapper {
	private boolean selected;
	private Object attribute;
	private String name;
	
	public boolean isSelected() {
		return this.selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Object getAttribute() {
		return this.attribute;
	}
	public void setAttribute(Object attribute) {
		this.attribute = attribute;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MultiClaimAttributeMapper(Object attribute) {
		super();
		this.attribute = attribute;
	}
	
}
