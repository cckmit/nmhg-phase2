package tavant.twms.domain.claim;

import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class SourceWarehouseServiceImpl extends GenericServiceImpl<SourceWarehouse, Long, Exception> implements SourceWarehouseService {

	private SourceWarehouseRepository sourceWarehouseRepository;
	@Override
	public GenericRepository<SourceWarehouse, Long> getRepository() {		
		return sourceWarehouseRepository;
	}
	
	public void setSourceWarehouseRepository(SourceWarehouseRepository sourceWarehouseRepository) {
        this.sourceWarehouseRepository = sourceWarehouseRepository;
    }
	public  SourceWarehouse findSourceWarehouseByCode(final String code){
		return sourceWarehouseRepository.findSourceWarehouseByCode(code);
	}

}
