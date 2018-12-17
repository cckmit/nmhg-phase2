package tavant.twms.domain.orgmodel;

public enum RoleCategory {
	FLEET("Fleet"), WARRANTY("Warranty"), BOTH("Both");
	
	private String roleCategory;
	
	private RoleCategory(String roleCategory){
		this.roleCategory = roleCategory;
	}
	
	public String getRoleCategory(){
		return roleCategory;
	}
	
	@Override
	public String toString(){
		return this.roleCategory;
	}
	
}
