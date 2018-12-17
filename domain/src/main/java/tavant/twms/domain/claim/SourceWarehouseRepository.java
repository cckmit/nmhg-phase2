package tavant.twms.domain.claim;

import tavant.twms.infra.GenericRepository;
import tavant.twms.domain.common.SourceWarehouse;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 29, 2009
 * Time: 2:03:30 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SourceWarehouseRepository  extends GenericRepository<SourceWarehouse,Long> {
	public  SourceWarehouse findSourceWarehouseByCode(final String code);
}
