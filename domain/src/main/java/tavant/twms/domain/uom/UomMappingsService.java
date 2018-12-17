package tavant.twms.domain.uom;

import java.util.List;
import tavant.twms.domain.catalog.ItemUOMTypes;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly=true)
public interface UomMappingsService extends GenericService<UomMappings, Long, Exception> {

	public abstract UomMappings findUomMappingForBaseUom(String baseUom);
	
	public abstract PageResult<UomMappings> findPage(ListCriteria listCriteria);
	
	public abstract List<ItemUOMTypes> findUnMappedUoms(); 

	@Transactional(readOnly=false)
    public void saveAll(List<UomMappings> entitiesToSave) ;
      
    @Transactional(readOnly=false)
    public void updateAll(List<UomMappings> entitiesToUpdate);
    
    public List<UomMappings> findMappedUoms() ;
}