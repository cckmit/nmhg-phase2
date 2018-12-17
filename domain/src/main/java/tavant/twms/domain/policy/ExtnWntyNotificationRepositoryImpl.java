package tavant.twms.domain.policy;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.infra.GenericRepositoryImpl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Mar 10, 2010
 * Time: 3:11:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtnWntyNotificationRepositoryImpl
           extends GenericRepositoryImpl<ExtendedWarrantyNotification,Long> implements ExtnWntyNotificationRepository{

    public List<ExtendedWarrantyNotification> findExtnWntyPurchaseNotificationForInv(InventoryItem invItem) {
        String query="select ew from ExtendedWarrantyNotification ew where ew.forUnit = :inventory";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("inventory", invItem);
        return findUsingQuery(query, params);
    }

    public List<ExtendedWarrantyNotification> findExtnWntyPurchaseNotificationForInv(InventoryItem invItem, PolicyDefinition policy) {
        String query="select ew from ExtendedWarrantyNotification ew where ew.forUnit = :inventory and ew.policy = :policy";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("inventory", invItem);
        params.put("policy", policy);
        return findUsingQuery(query, params);
    }


}
