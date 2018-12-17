/**
 * 
 */
package tavant.twms.domain.orgmodel;

import tavant.twms.infra.GenericRepository;

/**
 * @author fatima.marneni
 *
 */
public interface AttributeRepository extends GenericRepository<Attribute, Long> {
	
	public Attribute findAttributeByName(String attrName);
}
