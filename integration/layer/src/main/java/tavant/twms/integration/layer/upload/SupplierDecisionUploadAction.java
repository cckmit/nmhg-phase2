package tavant.twms.integration.layer.upload;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.RecoveryClaimAcceptanceReason;
import tavant.twms.domain.common.RecoveryClaimRejectionReason;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.BasePartReturn;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.domain.partreturn.WarehouseRepository;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.upload.controller.PostUploadAction;
import tavant.twms.domain.upload.staging.FileReceiver;
import tavant.twms.external.PaymentAsyncService;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

public class SupplierDecisionUploadAction implements PostUploadAction,BeanFactoryAware {
	
	private Logger logger = Logger.getLogger(SupplierDecisionUploadAction.class);
	
	private static final String SELECT_CLAUSE = "select ";

	private static final String COL_RECOVERY_CLAIM_NUMBER = "RECOVERY_CLAIM_NUMBER";

	private static final String SELECT_DELIMITER = ", ";

	private static final String COL_DECISION = "DECISION";

	private static final String COL_DECISION_REASON = "DECISION_REASON";

	private static final String COL_DECISION_COMMENTS = "SUPPLIER_COMMENTS";
	
	private static final String COL_SUPPLIER_CONTRACT_CODE="SUPPLIER_CONTRACT_CODE";
	
	private static final String COL_PART_RETURN_REQUEST="PART_RETURN_REQUEST";
	
	private static final String COL_RETURN_LOCATION_CODE="RETURN_LOCATION_CODE";
	
	private static final String COL_RMA_NUMBER="RMA_NUMBER";
	
	private static final String COL_CREDIT_MEMO_DATE="CREDIT_MEMO_DATE";
	
	private static final String COL_CREDIT_MEMO_NUMBER="CREDIT_MEMO_NUMBER";
	
	private static final String COL_CLAIM_AMOUNT_BEING_ACCEPTED="CLAIM_AMOUNT_BEING_ACCEPTED";
	
	private static final String COL_CREDIT_MEMO_AMOUNT="CREDIT_MEMO_AMOUNT";
	
	private static final String COL_CREDIT_MEMO_CURRENCY="CREDIT_MEMO_CURRENCY";
	
	private static final String COL_LOCALE = "LOCALE";

	private static final String YES_STRING = "Y";

	private static final String COL_ID = "ID";
	
	private String stagingTable;
	
	private JdbcTemplate jdbcTemplate;
	
	private TransactionTemplate transactionTemplate;
	
	private FileReceiver fileReceiver;
	
	private Map<Long, Long> recoveryClaimFiles = new HashMap<Long, Long>();

	private RecoveryClaimService recoveryClaimService;
	
	private LovRepository lovRepository;
	
	private WorkListItemService workListItemService;
	
	private WarehouseRepository warehouseRepository;
	
	private ContractService contractService;
	
	protected PartReturnProcessingService partReturnProcessingService;
	
	private PartReturnService partReturnService;
	
	private BeanFactory beanFactory;
	
	protected ConfigParamService configParamService;
	
	private PaymentAsyncService paymentAsyncService;
	
	private final String CANADA_COUNTRY_CODE="CA";
	
	private OrgService orgService;
	
	private ClaimService claimService;
	
	private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;
	
	private PartReplacedService partReplacedService;
	
	public void doUplaodPostProcessing(final long auditLogId) throws Exception {

		SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
		final List<List<String>> resultSetData = new ArrayList<List<String>>();

		this.jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException, DataAccessException {
				PreparedStatement ps = null;
				ResultSet recoveryClaimFilesRs = null;
				try {
					ps = conn.prepareStatement(supplierDecisionRecoveryClaimsQuery());
					recoveryClaimFilesRs = ps.executeQuery();
					while (recoveryClaimFilesRs.next()) {
						List<String> columnValues = new ArrayList<String>();
						columnValues.add(recoveryClaimFilesRs.getString(COL_RECOVERY_CLAIM_NUMBER));
						columnValues.add(recoveryClaimFilesRs.getString(COL_DECISION_COMMENTS));
						columnValues.add(recoveryClaimFilesRs.getString(COL_DECISION));
						columnValues.add(recoveryClaimFilesRs.getString(COL_DECISION_REASON));
						columnValues.add(recoveryClaimFilesRs.getString(COL_LOCALE));
						columnValues.add(recoveryClaimFilesRs.getString(COL_SUPPLIER_CONTRACT_CODE));
						columnValues.add(recoveryClaimFilesRs.getString(COL_PART_RETURN_REQUEST));
						columnValues.add(recoveryClaimFilesRs.getString(COL_RETURN_LOCATION_CODE));
						columnValues.add(recoveryClaimFilesRs.getString(COL_RMA_NUMBER));
						columnValues.add(recoveryClaimFilesRs.getString(COL_CLAIM_AMOUNT_BEING_ACCEPTED));
						columnValues.add(recoveryClaimFilesRs.getString(COL_CREDIT_MEMO_DATE));
						columnValues.add(recoveryClaimFilesRs.getString(COL_CREDIT_MEMO_NUMBER));
						columnValues.add(recoveryClaimFilesRs.getString(COL_CREDIT_MEMO_AMOUNT));
						columnValues.add(recoveryClaimFilesRs.getString(COL_CREDIT_MEMO_CURRENCY));
						
						resultSetData.add(columnValues);
						PreparedStatement prepSt = conn.prepareStatement("update STG_SUPPLIER_DECISION set upload_status = ? "
								+ " where id = ?");
						prepSt.setString(1, YES_STRING); // Success
						prepSt.setLong(2, recoveryClaimFilesRs.getLong(COL_ID));
						prepSt.executeUpdate();
					}
				} catch (Exception e) {
					logger.error("Error while trying to upload draft claim: ", e);
				} finally {
					if (recoveryClaimFilesRs != null)
						recoveryClaimFilesRs.close();
					if (ps != null)
						ps.close();
				}
				return null;
			}
		});
		updateRecoveryClaims(resultSetData);
	

	}
	
	private String supplierDecisionRecoveryClaimsQuery(){
		return SELECT_CLAUSE + COL_RECOVERY_CLAIM_NUMBER + SELECT_DELIMITER + 
		COL_DECISION + SELECT_DELIMITER + "SSD." + COL_ID + SELECT_DELIMITER +
		COL_DECISION_REASON + SELECT_DELIMITER + COL_DECISION_COMMENTS  + SELECT_DELIMITER  + COL_LOCALE + SELECT_DELIMITER + COL_SUPPLIER_CONTRACT_CODE +
		SELECT_DELIMITER + COL_PART_RETURN_REQUEST + SELECT_DELIMITER + COL_RETURN_LOCATION_CODE + SELECT_DELIMITER + COL_RMA_NUMBER +
		SELECT_DELIMITER + COL_CLAIM_AMOUNT_BEING_ACCEPTED + SELECT_DELIMITER + COL_CREDIT_MEMO_DATE + SELECT_DELIMITER + COL_CREDIT_MEMO_NUMBER +
		SELECT_DELIMITER + COL_CREDIT_MEMO_AMOUNT + SELECT_DELIMITER + COL_CREDIT_MEMO_CURRENCY +
		" from STG_SUPPLIER_DECISION SSD, FILE_UPLOAD_MGT FUM, ORG_USER U " +
		" where SSD.FILE_UPLOAD_MGT_ID = FUM.ID AND FUM.UPLOADED_BY = U.ID  AND SSD.ERROR_STATUS = 'Y' AND SSD.UPLOAD_STATUS IS NULL "; 
	}

	public String getStagingTable() {
		return stagingTable;
	}

	public void setStagingTable(String stagingTable) {
		this.stagingTable = stagingTable;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<Long, Long> getRecoveryClaimFiles() {
		return recoveryClaimFiles;
	}

	public void setRecoveryClaimFiles(Map<Long, Long> recoveryClaimFiles) {
		this.recoveryClaimFiles = recoveryClaimFiles;
	}

	public FileReceiver getFileReceiver() {
		return fileReceiver;
	}

	public void setFileReceiver(FileReceiver fileReceiver) {
		this.fileReceiver = fileReceiver;
	}

	public RecoveryClaimService getRecoveryClaimService() {
		return recoveryClaimService;
	}

	public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public WorkListItemService getWorkListItemService() {
		return workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public WarehouseRepository getWarehouseRepository() {
		return warehouseRepository;
	}

	public void setWarehouseRepository(WarehouseRepository warehouseRepository) {
		this.warehouseRepository = warehouseRepository;
	}

	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.beanFactory = arg0;
	}
	
	public PartReturnService getPartReturnService() {
		return partReturnService;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public PartReturnProcessingService getPartReturnProcessingService() {
		return partReturnProcessingService;
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

	public ContractService getContractService() {
		return contractService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}
	
	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public PaymentAsyncService getPaymentAsyncService() {
		return paymentAsyncService;
	}

	public void setPaymentAsyncService(PaymentAsyncService paymentAsyncService) {
		this.paymentAsyncService = paymentAsyncService;
	}
	
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	
	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}
	
	public void setSupplierRecoveryWorkListDao(
			SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
		this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
	}
	
	public PartReplacedService getPartReplacedService() {
		return partReplacedService;
	}

	public void setPartReplacedService(PartReplacedService partReplacedService) {
		this.partReplacedService = partReplacedService;
	}
	
	private void updateClaimAndPerformTransition(RecoveryClaim recoveryClaim,String transition,String rmaNumber, Location returnLoc,String partRtnReq) {
		recoveryClaim.setLoggedInUser(getSecurityHelper().getLoggedInUser());
		if( null!=recoveryClaim.getRecoveryClaimInfo().getRecoverableParts() && !recoveryClaim.getRecoveryClaimInfo().getRecoverableParts().isEmpty()){
			for(RecoverablePart recoverablePart : recoveryClaim.getRecoveryClaimInfo().getRecoverableParts()){
				if(recoverablePart.isSupplierPartReturnModificationAllowed()){
			if(!StringUtils.isEmpty(partRtnReq) && partRtnReq.equalsIgnoreCase("YES")){
				recoverablePart.setSupplierReturnNeeded(true);
			}else
				recoverablePart.setSupplierReturnNeeded(false);
		}
			}
		}
		if(routingRequired(recoveryClaim)){
		startRecoveryRoutingFlow(recoveryClaim,returnLoc,rmaNumber);
		}
		else if(isReturnThroughDealerDirectly(recoveryClaim)){
            startPartReturnForRecoveryClaim(recoveryClaim,rmaNumber,returnLoc);
        }else {
		startSupplyPartReturnForClaim(recoveryClaim, returnLoc,rmaNumber);
		}
		if (WorkflowConstants.ACCEPT.equals(transition)) {
			recoveryClaimService.updatePayment(recoveryClaim);
		}

		List<String> taskNames = new ArrayList<String>();
		taskNames.add(WorkflowConstants.NEW);
		taskNames.add(WorkflowConstants.SUPPLIER_DISPUTED_CLAIMS_TASK_NAME);
		taskNames.add(WorkflowConstants.REOPENED_CLAIMS);
		taskNames.add(WorkflowConstants.SUPPLIER_ACCEPTED);
		taskNames.add(WorkflowConstants.ON_HOLD_FOR_PART_RETURN);
		taskNames.add(WorkflowConstants.ACCEPTED);
		if (!"".equals(transition)) {
			TaskInstance taskInstance = this.workListItemService.findTaskForRecClaimsWithTaskNames(recoveryClaim.getId(), taskNames);
			this.workListItemService.endTaskWithTransition(taskInstance, transition);
		}
	}
	
	private void updateRecoveryClaims(final List<List<String>> resultSetData){ 
		this.transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				if(!resultSetData.isEmpty()){
				for (List<String> columnValues : resultSetData) {
					Location returnLoc  =null;
					if(!StringUtils.isEmpty(columnValues.get(6)) && columnValues.get(6).equalsIgnoreCase("YES") && !StringUtils.isEmpty(columnValues.get(7)))
						returnLoc= warehouseRepository.getDefaultReturnLocation(columnValues.get(7));
					String rmaNumber = columnValues.get(8);
					String partRtnReq =columnValues.get(6);
					Money acceptedRecClaimAmt = null;
					RecoveryClaim recoveryClaim = recoveryClaimService.findByRecoveryClaimNumber(columnValues.get(0));
					recoveryClaim.setExternalComments(columnValues.get(1));
					if(columnValues.get(2).equalsIgnoreCase(WorkflowConstants.ACCEPTED)) {
						RecoveryClaimAcceptanceReason recoveryClaimAcceptanceReason = (RecoveryClaimAcceptanceReason) lovRepository
								.findByDescription(RecoveryClaimAcceptanceReason.class.getSimpleName(), columnValues.get(3), columnValues
										.get(4), recoveryClaim.getBusinessUnitInfo().getName());
						recoveryClaim.setRecoveryClaimAcceptanceReason(recoveryClaimAcceptanceReason);
					}
					
					if(columnValues.get(2).equalsIgnoreCase(WorkflowConstants.SUPPLIER_DISPUTED_CLAIMS_TASK_NAME)) {
						RecoveryClaimRejectionReason recoveryClaimRejectionReason = (RecoveryClaimRejectionReason) lovRepository
								.findByDescription(RecoveryClaimRejectionReason.class.getSimpleName(), columnValues.get(3), columnValues
										.get(4), recoveryClaim.getBusinessUnitInfo().getName());
						recoveryClaim.setRecoveryClaimRejectionReason(recoveryClaimRejectionReason);
					}
					
					if(columnValues.get(2).equalsIgnoreCase(WorkflowConstants.ACCEPTED) || columnValues.get(2).equalsIgnoreCase(WorkflowConstants.PART_RETURN_REQUESTED)){
						if(StringUtils.isEmpty(columnValues.get(9)))
							acceptedRecClaimAmt =recoveryClaim.getTotalRecoveredCost();
						else
							acceptedRecClaimAmt = Money.valueOf(Double.parseDouble(columnValues.get(9)),recoveryClaim.getClaim().getCurrencyForCalculation());
						if(columnValues.get(2).equalsIgnoreCase(WorkflowConstants.ACCEPTED)){
						recoveryClaim.getActiveRecoveryClaimAudit().setAcceptedAmount(acceptedRecClaimAmt);
						recoveryClaim.getActiveRecoveryClaimAudit().setRecoveredAmount(acceptedRecClaimAmt);
						}
						recoveryClaim.setAcceptedAmount(acceptedRecClaimAmt);
					}
					
					String transition = null;
					if(columnValues.get(2).equalsIgnoreCase(WorkflowConstants.ACCEPTED)){
						transition="Accept";
					}
							
					if(columnValues.get(2).equalsIgnoreCase(WorkflowConstants.SUPPLIER_DISPUTED_CLAIMS_TASK_NAME)){
						transition="Reject";
						acceptedRecClaimAmt = Money.valueOf(0.00, recoveryClaim.getClaim().getCurrencyForCalculation());
						recoveryClaim.getActiveRecoveryClaimAudit().setAcceptedAmount(acceptedRecClaimAmt);
						recoveryClaim.getActiveRecoveryClaimAudit().setRecoveredAmount(acceptedRecClaimAmt);
						recoveryClaim.setAcceptedAmount(acceptedRecClaimAmt);
					}
						
					if(columnValues.get(2).equalsIgnoreCase(WorkflowConstants.PART_RETURN_REQUESTED)){
					transition="On Hold For Part Return";
					}
					
					updateClaimAndPerformTransition(recoveryClaim,transition,rmaNumber,returnLoc,partRtnReq);
					
						if ((!StringUtils.isEmpty(columnValues.get(10))
								&& !StringUtils.isEmpty(columnValues.get(11))
								&& !StringUtils.isEmpty(columnValues.get(12)) && !StringUtils
									.isEmpty(columnValues.get(13)))
								&& transition.equals("Accept")) {
						CalendarDate creditMemoDate = CalendarUtil.convertDateFromXLSToCalendarDate(columnValues.get(10));
						String creditMemoNum = columnValues.get(11);
						String creditMemoAmt = columnValues.get(12);
						String creditMemoCurr = columnValues.get(13);
						if(recoveryClaim.getRecoveryClaimState().getState().equalsIgnoreCase(RecoveryClaimState.READY_FOR_DEBIT.getState()))
						syncPaymentMadeForSRClaims(recoveryClaim,creditMemoDate,creditMemoNum,creditMemoAmt,creditMemoCurr);
					}
						
					       //CR 1071 auto-confirm part return if supplier taking action from on hold from part return inbox
				        //pending action item: Need to end task instances which are on the way, waiting for client approval
				        if( recoveryClaim.getBusinessUnitInfo().getName().equalsIgnoreCase("AMER")){
				            List<TaskInstance> partReturnInstances = new ArrayList<TaskInstance>();
				            List<TaskInstance> claimPartReturnInstances = new ArrayList<TaskInstance>();
				            partReturnInstances.addAll(supplierRecoveryWorkListDao.getConfirmPartReturnsInstances(recoveryClaim));
				            claimPartReturnInstances = supplierRecoveryWorkListDao.getConfirmDealerPartReturnsInstances(recoveryClaim.getClaim());
				            List<TaskInstance> partReturnShipmentGeneratedInstances = supplierRecoveryWorkListDao.getPartReturnShipmentInstances(recoveryClaim.getClaim());
				            List<TaskInstance> partRecoveryShipmentGeneratedInstances = supplierRecoveryWorkListDao.getRecoveryShipmentGeneratedInstances(recoveryClaim);

				            partReturnInstances.addAll(claimPartReturnInstances);
				            workListItemService.endAllTasksWithTransition(partReturnInstances, "Received");
				            workListItemService.endAllTasksWithTransition(partReturnShipmentGeneratedInstances, "autoConfirmed");
				            workListItemService.endAllTasksWithTransition(partRecoveryShipmentGeneratedInstances, "autoConfirmed");

				            claimPartReturnInstances.addAll(partReturnShipmentGeneratedInstances);

				            List<Long> oemIds = new ArrayList<Long>();
				            for(TaskInstance instance : claimPartReturnInstances){
				                BasePartReturn partReturn = (BasePartReturn) instance.getVariable("partReturn");
				                if(partReturn.getOemPartReplaced() != null){
				                    oemIds.add(partReturn.getOemPartReplaced().getId());
				                }
				            }

				            Map<Long, OEMPartReplaced> uniqueValues = new HashMap<Long, OEMPartReplaced>();
				            for(TaskInstance instance : claimPartReturnInstances){
				                BasePartReturn partReturn = (BasePartReturn) instance.getVariable("partReturn");
				                if(partReturn.getOemPartReplaced() != null && uniqueValues.get(partReturn.getOemPartReplaced().getId()) == null){
				                    uniqueValues.put(partReturn.getOemPartReplaced().getId(), partReturn.getOemPartReplaced());
				                }
				            }

				            for(OEMPartReplaced part : uniqueValues.values()){
				                int count = Collections.frequency(oemIds, part.getId());
				                part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus(),count));
				                getPartReturnService().updatePartStatus(part);
				                getPartReplacedService().updateOEMPartReplaced(part);
				            }

				        }
				}
				}
				return null;
			}
		});
	}
	
	
	private void startSupplyPartReturnForClaim(RecoveryClaim claim,Location returnLoc,String rmaNumber) {
		List<RecoverablePart> recoverableParts = claim.getRecoveryClaimInfo()
				.getRecoverableParts();
		for (int i = 0; i < recoverableParts.size(); i++) {
			if (recoverableParts.get(i).isSupplierReturnNeeded()) {
				if (recoverableParts.get(i).isSupplierReturnNeeded() && recoverableParts.get(i).isSupplierPartReturnModificationAllowed()) {
					this.contractService.updateSupplierPartReturn(
							recoverableParts.get(i), returnLoc, claim.getContract().getCarrier(),
							rmaNumber);
					this.partReturnProcessingService
							.startRecoveryPartReturnProcess(
									recoverableParts.get(i), claim);
				}
			} else {
				if (!recoverableParts.get(i).getOemPart().getPartReturns()
						.isEmpty()) {
					if (!this.partReturnService.isPartShipped(recoverableParts
							.get(i).getOemPart())
							&& recoverableParts.get(i).getOemPart()
									.isPartReturnInitiatedBySupplier()) {
						this.partReturnProcessingService
								.endRecoveryPartReturnProcess(recoverableParts
										.get(i).getOemPart());
					} else if (this.partReturnService
							.isPartShipped(recoverableParts.get(i).getOemPart())
							&& !recoverableParts.get(i).getOemPart()
									.isPartReturnInitiatedBySupplier()) {
						this.partReturnProcessingService
								.endPartReturnNotIntiatedBySupplier(claim);
					}
				} else {
					this.partReturnProcessingService
							.endRecoveryPartReturnProcess(recoverableParts.get(
									i).getOemPart());
				}
			}
		}

	}
	
	private boolean routingRequired(RecoveryClaim claim){
		List<ConfigValue> configValues= configParamService.getValuesForConfigParam(ConfigName.SUPPLIER_PART_RETURN_ROUTING_REQUIRED.getName());
        String routing = null;
        for(ConfigValue cfgVl :configValues ){
        	if(cfgVl.getBusinessUnitInfo().getName().equalsIgnoreCase(claim.getBusinessUnitInfo().getName()))
        		routing = cfgVl.getConfigParamOption().getValue();
        }
		
		Boolean routingRequired = Boolean.valueOf(routing);
		if(routingRequired){
			Currency recClaimCurrency = claim.getAcceptedAmount().breachEncapsulationOfCurrency();
			List<ConfigValue> thrAmts =  configParamService.getValuesForConfigParam(ConfigName.RECOVERY_CLAIM_THRESHOLD_AMOUNT.getName());
			String thrsAmt = null;
			Money thresholdAmount = null;
			 for(ConfigValue cfgVl :thrAmts ){
		        	if(cfgVl.getBusinessUnitInfo().getName().equalsIgnoreCase(claim.getBusinessUnitInfo().getName())){
		        		thrsAmt = cfgVl.getValue();
		        	thresholdAmount = new Money(
					new BigDecimal(thrsAmt)
								.setScale(recClaimCurrency.getDefaultFractionDigits()),
								recClaimCurrency);
		        	}
		        }
			
			 List<ConfigValue> countries  =  configParamService.getValuesForConfigParam(ConfigName.RECOVERY_CLAIM_ROUTING_DEALER_COUNTRIES.getName());
			 Map<Object, Object> cntrsSelect = new HashMap<Object, Object>();
			 for(ConfigValue cfgVl :countries){
				 if(cfgVl.getBusinessUnitInfo().getName().equalsIgnoreCase(claim.getBusinessUnitInfo().getName())){
					 cntrsSelect.put(cfgVl.getConfigParamOption().getValue(), cfgVl.getConfigParamOption().getDisplayValue());
				 }
			 }
			 
			if((cntrsSelect != null && cntrsSelect.containsKey(claim.getClaim().getForDealer().getAddress().getCountry()))
					|| claim.getAcceptedAmount().isLessThan(thresholdAmount)){
				return true;
			}
		}
		return false;
	}
	
	private void startRecoveryRoutingFlow(RecoveryClaim claim,Location returnLoc, String rmaNumber) {
		List<RecoverablePart> recoverableParts = claim.getRecoveryClaimInfo()
				.getRecoverableParts();
		for (int i = 0; i < recoverableParts.size(); i++) {
			if (recoverableParts.get(i).isSupplierReturnNeeded() && recoverableParts.get(i).isSupplierPartReturnModificationAllowed()) {
					this.contractService.updateSupplierPartReturn(
							recoverableParts.get(i), returnLoc, claim.getContract().getCarrier(),
							rmaNumber);
					this.partReturnProcessingService
							.startRecoveryRoutingProcess(
									recoverableParts.get(i), claim);
				//}
			}
		}
	}
	
	private SecurityHelper getSecurityHelper() {
		return (SecurityHelper) this.beanFactory.getBean("securityHelper");
	}
	
	private void syncPaymentMadeForSRClaims(RecoveryClaim claim,CalendarDate crdMemoDt, String crdMemoNo,String crdMemoAmt,String crdMemoCurr) {
		CreditMemo creditMemo = new CreditMemo();
		creditMemo.setRecoveryClaim(claim);
		creditMemo.setClaimNumber(claim.getClaim().getClaimNumber());
		creditMemo.setCreditMemoDate(crdMemoDt);
		creditMemo.setCreditMemoNumber(crdMemoNo);
		Money totalRecoveredCost = null;
		List<RecoveryClaimAudit> recoveryClaimAudit = claim
				.getRecoveryClaimAudits();
		Money acceptedAmount = recoveryClaimAudit.get(
				recoveryClaimAudit.size() - 1).getAcceptedAmount();
		if (acceptedAmount != null) {
			totalRecoveredCost = acceptedAmount;
		} else {
			if (claim.getRecoveryPayment() != null
					&& claim.getRecoveryPayment().getPreviousPaidAmount() != null) {
				totalRecoveredCost = claim.getTotalRecoveredCost().minus(
						claim.getRecoveryPayment().getPreviousPaidAmount());
			} else {
				totalRecoveredCost = claim.getTotalRecoveredCost();
			}
		}
		creditMemo.setPaidAmount(totalRecoveredCost);
		creditMemo.setTaxAmount(Money.valueOf(0,
				totalRecoveredCost.breachEncapsulationOfCurrency()));
		if (totalRecoveredCost.isNegative()) {
			creditMemo.setCrDrFlag("CR");
		} else {
			creditMemo.setCrDrFlag("DR");
		}
		   
		Currency creditMemoCurrency = Currency.getInstance(crdMemoCurr); // Assumed that proper currency code will be sent
	    Money creditMemoAmount = null;
	    creditMemoAmount = Money.valueOf(Double.parseDouble(crdMemoAmt), creditMemoCurrency);
	    creditMemo.setCreditAmount(creditMemoAmount);
		this.paymentAsyncService.syncCreditMemo(creditMemo);
	}
	
	private boolean isReturnThroughDealerDirectly(RecoveryClaim recClaim){
		List<ConfigValue> configValues= configParamService.getValuesForConfigParam(ConfigName.PART_RECOVERY_DIRECTLY_THROUGH_DEALER.getName());
		String rtrn = null;
		for(ConfigValue cfgVl :configValues ){
    	if(cfgVl.getBusinessUnitInfo().getName().equalsIgnoreCase(recClaim.getBusinessUnitInfo().getName()))
    		rtrn = cfgVl.getConfigParamOption().getValue();
		}
		
        return Boolean.valueOf(rtrn);
    }
	
	  public void startPartReturnForRecoveryClaim(RecoveryClaim claim,String rmaNumber,Location returnLoc) {
	        List<RecoverablePart> recoverableParts = claim
	                .getRecoveryClaimInfo().getRecoverableParts();
	        for (int i = 0; i < recoverableParts.size(); i++) {
	            if (!recoverableParts.get(i).getOemPart().isPartToBeReturned() && !recoverableParts.get(i).getOemPart().isPartReturnsPresent()) {
	            	recoverableParts.get(i).getOemPart().setPartToBeReturned(true);
	            	recoverableParts.get(i).getOemPart().setReturnDirectlyToSupplier(true);
	            	if(recoverableParts.get(i).isSupplierReturnNeeded() && recoverableParts.get(i).isSupplierPartReturnModificationAllowed()){
	                    if(recoverableParts.get(i).getOemPart().getPartReturn() == null){
	                        recoverableParts.get(i).getOemPart().setPartReturn(new PartReturn());
	                    }
	                    if(recoverableParts.get(i).getOemPart().getPartReturn().getDueDays() == 0){
	                    	Integer dueDays = 0;
	                    	List<ConfigValue> configValues= configParamService.getValuesForConfigParam(ConfigName.DEFAULT_DUE_DAYS_FOR_PART_RETURN.getName());
	                        for(ConfigValue cfgVl :configValues ){
	                        	if(cfgVl.getBusinessUnitInfo().getName().equalsIgnoreCase(claim.getBusinessUnitInfo().getName()))
	                        		dueDays = Integer.valueOf(cfgVl.getValue());
	                        }
	                        recoverableParts.get(i).getOemPart().getPartReturn().setDueDays(dueDays);
	                    }
	                    recoverableParts.get(i).getOemPart().getPartReturn().setRmaNumber(rmaNumber);
	                    //Set the default payment condition
	                    PaymentCondition condition = new PaymentCondition();
	                    condition.setCode("PAY");
	                    condition.setDescription("Pay without Part Return");
	                    recoverableParts.get(i).getOemPart().getPartReturn().setPaymentCondition(condition);
	                    recoverableParts.get(i).getOemPart().getPartReturn().setOemPartReplaced(recoverableParts.get(i).getOemPart());
	                    if(recoverableParts.get(i).getOemPart().isReturnDirectlyToSupplier()){
	                        recoverableParts.get(i).getOemPart().getPartReturn().setReturnLocation(returnLoc);
	                        //hack for canadian dealers here
	                            if(isClaimFiledByCanadianDealer(claim.getClaim().getForDealer().getId())){
	                                //2 return required -- dealer -> nmhg, nmhg-> supplier scheduler
	                                //initiate the dealer --> nmhg data -- > return location change
	                                recoverableParts.get(i).getOemPart().getPartReturn().setReturnLocation(claimService.getLocationForDefaultPartReturn(getDefaultPartReturnLocation(claim)));
	                                //initiate recovery return
	                                recoverableParts.get(i).setSupplierReturnNeeded(true);
	                                partReturnProcessingService.startRecoveryPartReturnProcessForRecPart(recoverableParts.get(i),claim, returnLoc);
	                                //Make direct return to false
	                                recoverableParts.get(i).getOemPart().setReturnDirectlyToSupplier(false);

	                            }
	                        }else{
	                        recoverableParts.get(i).getOemPart().getPartReturn().setReturnLocation(returnLoc);
	                        //Since shipping is happening for dealer -> nmhg one more shipment is required from nmhg -> supplier
	                        recoverableParts.get(i).setSupplierReturnNeeded(false);
	                    }
	                    partReturnService.updateExistingPartReturns(recoverableParts.get(i).getOemPart(), claim.getClaim());
	                    this.partReturnProcessingService.startPartReturnProcessForPart(claim.getClaim(),recoverableParts.get(i).getOemPart());
	            	}
	            }
	            else if(recoverableParts.get(i).isSupplierReturnNeeded()){
	                startSupplyPartReturnForClaim(claim, returnLoc,rmaNumber);
	            }
	        }

	    }
	  
	  private boolean isClaimFiledByCanadianDealer(Long id) {
	        ServiceProvider dealership = orgService.findDealerById(id);
	        return dealership.getAddress().getCountry().equalsIgnoreCase(CANADA_COUNTRY_CODE);
	    }
	  
	  private String getDefaultPartReturnLocation(RecoveryClaim claim){
			String centralLogisticName  = getConfigParamService().getStringValueByBU(ConfigName.DEFAULT_RETURN_LOCATION_CODE.getName(), claim.getBusinessUnitInfo().getName());
			return centralLogisticName;		
		}
}
