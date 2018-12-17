package tavant.twms.domain.rules;

import java.util.Set;
import java.util.SortedMap;
@Deprecated
public interface IBusinessObjectModel {
	public Set<String> listAllContexts();
	

	public DomainTypeSystem getDomainTypeSystem();
	
	public SortedMap<String, FieldTraversal> getDataElementsForType(
			DomainType businessObject);

	public void discoverPathsToFields(DomainType domainType, String expression);

	public SortedMap<String, FieldTraversal> getAllLevelDataElementsForRule(String context);

	public SortedMap<String, FieldTraversal> getTopLevelDataElementsForRule(String context);

	public FieldTraversal getField(String typeName, String fieldName) ;

}
