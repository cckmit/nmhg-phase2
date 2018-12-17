/**
 * 
 */
package tavant.twms.domain.orgmodel;

import java.util.HashMap;
import java.util.Map;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author fatima.marneni
 *
 */
public class AttributeRepositoryImpl extends GenericRepositoryImpl<Attribute, Long> implements
		AttributeRepository {
	
	public Attribute findAttributeByName(String attrName){
		Map<String,Object> params = new HashMap<String,Object>();
        params.put("name", attrName);        
        return findUniqueUsingQuery("from Attribute where name=:name", params);
	}

}
