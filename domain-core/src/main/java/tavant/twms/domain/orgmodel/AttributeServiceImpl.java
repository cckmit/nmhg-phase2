/**
 * 
 */
package tavant.twms.domain.orgmodel;

/**
 * @author fatima.marneni
 *
 */
public class AttributeServiceImpl implements AttributeService {
    
	private AttributeRepository attributeRepository;
	
	public void createAttribute(Attribute attribute) {
	    this.attributeRepository.save(attribute);
	}

	public void updateAttribute(Attribute attribute) {
	    this.attributeRepository.update(attribute);
	}
	
	public Attribute findAttributeByName(String attrName){
		return this.attributeRepository.findAttributeByName(attrName);
	}
	

	public void setAttributeRepository(AttributeRepository attributeRepository) {
		this.attributeRepository = attributeRepository;
	}
	
}
