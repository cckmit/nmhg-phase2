package tavant.twms.domain.rules;

import tavant.twms.domain.query.SearchBusinessObjectModel;
@Deprecated
public class IBusinessObjectModelFactory {
	private static IBusinessObjectModelFactory instance=new IBusinessObjectModelFactory();
	
	private IBusinessObjectModelFactory(){
		
	}
	
	public static IBusinessObjectModelFactory getInstance(){
		return instance;
	}
	
	public IBusinessObjectModel getBusinessObjectModel(String context){
		if("ClaimSearches".equals(context))
			return SearchBusinessObjectModel.getInstance();
			
		return BusinessObjectModel.getInstance();
	}
}
