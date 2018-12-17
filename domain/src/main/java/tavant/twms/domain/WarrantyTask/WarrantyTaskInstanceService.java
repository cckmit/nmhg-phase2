package tavant.twms.domain.WarrantyTask;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.PageResult;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.policy.WarrantyListCriteria;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.User;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Aug 29, 2008
 * Time: 12:47:19 PM
 * To change this template use File | Settings | File Templates.
 */


@Transactional(readOnly = true)
public interface WarrantyTaskInstanceService {

    public WarrantyTaskInstance findById(Long id);

    public PageResult<WarrantyTaskInstance> findWarrantiesForFolder(WarrantyListCriteria criteria);

    @Transactional(readOnly = false)
    public void createWarrantyTaskInstance(WarrantyTaskInstance warrantyTaskInstance);

    @Transactional(readOnly = false)
    public void updateWarrantyTaskInstance(WarrantyTaskInstance warrantyTaskInstance);

    public WarrantyTaskInstance findActiveTaskWarranty(String multiDRETRNumber);

    public List<WarrantyFolderView> fetchWarrantyFoldersForTransactionType(String transactionType,
                              boolean isAdmin,boolean isdealer, User filedBy);
   
    public List<Object[]> fetchCountsForTransactionType(String transactionType,
                        boolean isAdmin,boolean isdealer,User filedBy);

}