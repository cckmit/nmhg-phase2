/**
 * 
 */
package tavant.twms.domain.uom;

import java.util.List;

import tavant.twms.infra.GenericRepository;


public interface UomMappingsRepository extends GenericRepository<UomMappings,Long> {

	public UomMappings findUomMappingByBaseUom(String baseUom);
	
	public List<String> findUnMappedUoms();
	
	public List<String> getListOfMappedUoms();
	
	public List<UomMappings> findMappedUoms();
}

