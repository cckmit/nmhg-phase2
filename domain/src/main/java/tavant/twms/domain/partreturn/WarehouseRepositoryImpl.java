/**
 * 
 */
package tavant.twms.domain.partreturn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.common.Label;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class WarehouseRepositoryImpl extends
		GenericRepositoryImpl<Warehouse, Long> implements WarehouseRepository {

	public Warehouse findByWarehouseCode(final String code) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("code", code);
        return findUniqueUsingQuery("from Warehouse w where w.location.code = :code", params);
    }

    @SuppressWarnings("unchecked")
    public List<String> findWarehouseCodesStartingWith(String code) {    	
    	String query = "select w from Warehouse w where w.location.code like '" +code+ "%'";
    	List<Warehouse> warehouses = findUsingQuery(query, new HashMap<String, Object>());
    	List<String> codes = new ArrayList<String>();
    	for (Warehouse warehouse : warehouses) {
			codes.add(warehouse.getLocation().getCode());
		}
        return codes;
    }
    
    @SuppressWarnings("unchecked")
    public List<Location> findWarehouseLocationsStartingWith(final String code) {    	
    	List<Location> locations = (List<Location>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery("select l from Warehouse w ,Location l where w.location=l and upper(l.code) like :code ");
                query.setParameter("code", code+"%");
                return query.list();
            }

        });
		return locations;    
    }

	public String getInspectorAtLocation(Location location) {
        Warehouse warehouse = findByLocation(location);
        if(warehouse == null){
            return null;
        }
        else if (CollectionUtils.isEmpty(warehouse.getInspectors())) {
            return null;
        } else {
            return warehouse.getInspectors().get(0).getName();
        }
	}

	public String getPartShipperAtLocation(Location location) {
        Warehouse warehouse = findByLocation(location);
        if(warehouse == null){
            return null;
        }
        else  if (CollectionUtils.isEmpty(warehouse.getPartShippers())) {
            return null;
        } else {
            return warehouse.getPartShippers().get(0).getName();
        }
	}

	public String getReceiverAtLocation(Location location) {
		Warehouse warehouse = findByLocation(location);
        if(warehouse == null){
            return null;
        }
		else if (CollectionUtils.isEmpty(warehouse.getRecievers())) {
			return null;
		} else {
			return warehouse.getRecievers().get(0).getName();
		}
	}
	
	private Warehouse findByLocation(Location location) {
		Map<String, Object> params = new HashMap<String, Object>();
    	params.put("location", location);
        return findUniqueUsingQuery("from Warehouse w where w.location = :location", params);
	}

    public Location getDefaultReturnLocation(final String code) {
        Location location = (Location) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery("select l from Warehouse w ,Location l where w.location=l and l.code = :code ");
                query.setParameter("code", code);
                return query.uniqueResult();
            }

        });
        return location;
    }

	public List<Warehouse> findAllWarehouseForLabel(Label label) {
		String query="select w from Warehouse w join w.labels label where label=:label";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", label);
		return findUsingQuery(query, params);
	}

}
