package tavant.twms.domain.WarrantyTask;

import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.PageResult;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.policy.WarrantyListCriteria;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.User;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: pradyot.rout Date: Aug 29, 2008 Time:
 * 12:43:49 PM To change this template use File | Settings | File Templates.
 */
public class WarrantyTaskInstanceServiceImpl implements
		WarrantyTaskInstanceService {

	private WarrantyTaskInstanceRepository warrantyTaskInstanceRepository;

	public void setWarrantyTaskInstanceRepository(
			WarrantyTaskInstanceRepository warrantyTaskInstanceRepository) {
		this.warrantyTaskInstanceRepository = warrantyTaskInstanceRepository;
	}

	public WarrantyTaskInstance findById(Long id) {
		return warrantyTaskInstanceRepository.findById(id);
	}

	public PageResult<WarrantyTaskInstance> findWarrantiesForFolder(
			WarrantyListCriteria criteria) {
		return warrantyTaskInstanceRepository.findWarrantiesForFolder(criteria);
	}

	public void createWarrantyTaskInstance(
			WarrantyTaskInstance warrantyTaskInstance) {
		this.warrantyTaskInstanceRepository.save(warrantyTaskInstance);
	}

	public void updateWarrantyTaskInstance(
			WarrantyTaskInstance warrantyTaskInstance) {
		this.warrantyTaskInstanceRepository.update(warrantyTaskInstance);
	}

	public WarrantyTaskInstance findActiveTaskWarranty(String multiDRETRNumber) {
		return warrantyTaskInstanceRepository
				.findActiveTaskWarranty(multiDRETRNumber);
	}

	public List<WarrantyFolderView> fetchWarrantyFoldersForTransactionType(
			String transactionType, boolean isAdmin, boolean isdealer,
			User filedBy) {
		List<WarrantyFolderView> folders = new ArrayList<WarrantyFolderView>();
		if (transactionType == "DR") {
			List<Object[]> result = warrantyTaskInstanceRepository
					.fetchWarrantyFoldersForDRTransaction(transactionType,
							isAdmin, isdealer, filedBy);
			setFoldersCount(result, folders);
		} else {
			List<Object[]> result = warrantyTaskInstanceRepository
					.fetchWarrantyFoldersForTransaction(transactionType,
							isAdmin, isdealer, filedBy);
			setFoldersCount(result, folders);
		}

		return folders;
	}

	private void setFoldersCount(List<Object[]> result,
			List<WarrantyFolderView> folders) {
		for (Object[] resultValues : result) {
			WarrantyFolderView folderView = new WarrantyFolderView();
			folderView.setFolderCount((Long) resultValues[0]);
			folderView.setStatus((WarrantyStatus) resultValues[1]);
			folders.add(folderView);
		}
	}

	public List<Object[]> fetchCountsForTransactionType(String transactionType,
			boolean isAdmin, boolean isdealer, User filedBy) {
		List<Object[]> result = null;
		if (transactionType == "DR") {
			result = warrantyTaskInstanceRepository
			.fetchWarrantyFoldersForDRTransaction(transactionType,
					isAdmin, isdealer, filedBy);
		}
		else{
		 result = warrantyTaskInstanceRepository
				.fetchWarrantyFoldersForTransaction(transactionType, isAdmin,
						isdealer, filedBy);
		     }
		return result;
	}
}
