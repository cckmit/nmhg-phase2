/**
 * 
 */
package tavant.twms.domain.partreturn;

import java.util.List;

import tavant.twms.domain.common.Label;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface WarehouseRepository extends GenericRepository<Warehouse, Long>{
	
	public Warehouse findByWarehouseCode(final String code);

    public List<String> findWarehouseCodesStartingWith(String code);

	public String getInspectorAtLocation(Location location);

	public String getPartShipperAtLocation(Location location);

	public String getReceiverAtLocation(Location location);
	
	public List<Location> findWarehouseLocationsStartingWith(String code);

    public Location getDefaultReturnLocation(String code);
    
    public List<Warehouse> findAllWarehouseForLabel(Label label);
    
}
