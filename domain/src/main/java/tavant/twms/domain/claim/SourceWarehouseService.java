package tavant.twms.domain.claim;

import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.infra.GenericService;

public interface SourceWarehouseService extends GenericService<SourceWarehouse, Long, Exception> {
	public  SourceWarehouse findSourceWarehouseByCode(final String code);
}
