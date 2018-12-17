package tavant.twms.domain.WarrantyTask;

import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.policy.WarrantyListCriteria;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Aug 29, 2008
 * Time: 12:43:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface WarrantyTaskInstanceRepository extends GenericRepository<WarrantyTaskInstance, Long> {

	public void save(WarrantyTaskInstance warrantyTaskInstance);

    public void update(WarrantyTaskInstance warrantyTaskInstance);

    public void delete(WarrantyTaskInstance warrantyTaskInstance);

    public WarrantyTaskInstance findById(Long id);

    public PageResult<WarrantyTaskInstance> findWarrantiesForFolder(WarrantyListCriteria criteria);

    public WarrantyTaskInstance findActiveTaskWarranty(String multiDRETRNumber);

    public List<Object[]> fetchWarrantyFoldersForTransaction(String transactionType,
            boolean isAdmin,boolean isDealer,User filedBy);

    public List<Object[]> fetchWarrantyFoldersForDRTransaction(
			String transactionType, boolean isAdmin, boolean isdealer,
			User filedBy);
}
