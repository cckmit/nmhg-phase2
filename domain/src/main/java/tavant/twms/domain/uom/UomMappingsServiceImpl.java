package tavant.twms.domain.uom;

import java.util.ArrayList;
import tavant.twms.domain.catalog.ItemUOMTypes;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class UomMappingsServiceImpl extends GenericServiceImpl<UomMappings,Long,Exception> implements UomMappingsService {

	private UomMappingsRepository uomMappingsRepository;
	
	private CatalogService catalogService;
	
	@Override
	public GenericRepository<UomMappings,Long> getRepository() {
		return uomMappingsRepository;
	}

	public void setUomMappingsRepository(UomMappingsRepository uomMappingsRepository) {
		this.uomMappingsRepository = uomMappingsRepository;
	}
	

	public UomMappings findUomMappingForBaseUom(String baseUom){
		return uomMappingsRepository.findUomMappingByBaseUom(baseUom);				
	}

	public PageResult<UomMappings> findPage(ListCriteria listCriteria) {
		return  uomMappingsRepository.findPage(" from UomMappings uom", listCriteria);
	}

	public List<ItemUOMTypes> findUnMappedUoms() {
		List<ItemUOMTypes> uoms = catalogService.findAllUoms();		
		List<String> mappedUoms = uomMappingsRepository.getListOfMappedUoms();		
		uoms.removeAll(mappedUoms);	
		uoms.remove(ItemUOMTypes.valueOf("EACH"));
		return uoms;
	}

	 public List<UomMappings> findMappedUoms() {
		 return uomMappingsRepository.findMappedUoms();
	 }
	
	
    public void saveAll(List<UomMappings> entitiesToSave) {
        getRepository().saveAll(entitiesToSave);	
      	
      }	
      
      public void updateAll(List<UomMappings> entitiesToUpdate) {
          getRepository().updateAll(entitiesToUpdate);	      	
       }

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

      
	
}
