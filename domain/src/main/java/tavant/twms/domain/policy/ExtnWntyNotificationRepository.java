package tavant.twms.domain.policy;

import tavant.twms.infra.GenericRepository;
import tavant.twms.domain.inventory.InventoryItem;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rahul.k
 * Date: Mar 10, 2010
 * Time: 2:47:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExtnWntyNotificationRepository extends GenericRepository<ExtendedWarrantyNotification, Long> {

    public List<ExtendedWarrantyNotification> findExtnWntyPurchaseNotificationForInv(InventoryItem invItem);

    public List<ExtendedWarrantyNotification> findExtnWntyPurchaseNotificationForInv(InventoryItem invItem, PolicyDefinition policy);

}
