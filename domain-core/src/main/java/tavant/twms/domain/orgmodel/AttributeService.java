/**
 * 
 */
package tavant.twms.domain.orgmodel;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author fatima.marneni
 *
 */
@Transactional(readOnly = true)
public interface AttributeService {
	
	@Transactional(readOnly=false)
	void createAttribute(Attribute attribute);
    
	@Transactional(readOnly=false)
	void updateAttribute(Attribute attribute);
	
    public Attribute findAttributeByName(String attrName);

}
