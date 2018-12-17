package tavant.twms.web.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.UserComment;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.worklist.WorkListItemService;

import com.domainlanguage.timeutil.Clock;

public class RecoveryClaimInitiation extends I18nActionSupport {
	
	private Logger logger = Logger.getLogger(RecoveryClaimInitiation.class);
	
	private static final String COL_CLAIM_NUMBER = "CLAIM_NUMBER";
	private static final String COL_IS_CAUSAL_PART = "IS_CAUSAL_PART";
	private static final String COL_PART_NUMBER = "PART_NUMBER";
	private static final String COL_CONTRACT_ID = "CONTRACT_ID";
	private static final String COL_COMMENTS = "COMMENTS";
	
	final List<List<String>> resultSetData = new ArrayList<List<String>>();
	
	private JdbcTemplate jdbcTemplate;
	private ClaimService claimService;
	private ContractService contractService;
	private ConfigParamService configParamService;
	private RecoveryInfoService recoveryInfoService;
	private WorkListItemService workListItemService;
	
	private Claim claim;
	private RecoveryInfo recoveryInfo;
	private Contract selectedContractForCausalPart;
	private HashMap<String,Contract> partContractMap = new HashMap<String,Contract>();;
	private List<Contract> selectedContractForRemovedPart = new ArrayList<Contract>();
	private List<OEMPartReplaced> completeListOfPartsReplaced;
	private String comments;
	private String errorMessage;
	private String successMessage;
	
	public String run() {
		load();
		initiateRecovery();
		return SUCCESS;
	}
	
	private static String stagingDataQuery = "select claim_number,is_causal_part,part_number," +
			" contract_id, comments from STG_INIT_RECOVERY where upload_status is null " +
			" and claim_number is not null" +
			" and contract_id is not null" +
			" and (is_causal_part='Y' or (is_causal_part='N' and part_number is not null))" +
			" order by claim_number ";
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}
	
	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}
	
	public void setRecoveryInfoService(RecoveryInfoService recoveryInfoService) {
		this.recoveryInfoService = recoveryInfoService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public void load() {
		this.jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException, DataAccessException {
				PreparedStatement ps = null;
				ResultSet resultSet = null;
				try {
					ps = conn.prepareStatement(stagingDataQuery);
					resultSet = ps.executeQuery();
					while (resultSet.next()) {
						List<String> columnValues = new ArrayList<String>();
						columnValues.add(resultSet.getString(COL_CLAIM_NUMBER));
						columnValues.add(resultSet.getString(COL_IS_CAUSAL_PART));
						columnValues.add(resultSet.getString(COL_PART_NUMBER));
						columnValues.add(resultSet.getString(COL_CONTRACT_ID));
						columnValues.add(resultSet.getString(COL_COMMENTS));
						resultSetData.add(columnValues);
					}
				} catch (Exception e) {
					logger.error("Error while trying to initiate supplier recovery", e);
				} finally {
					if (resultSet != null)
						resultSet.close();
					if (ps != null)
						ps.close();
				}
				return null;
			}
		});

	}
	
	public void updateStatus() {
		this.jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException, DataAccessException {
				PreparedStatement ps = null;
				try {					
						PreparedStatement prepSt = conn.prepareStatement("update STG_INIT_RECOVERY " +
								" set upload_status = ?, upload_message = ? "
								+ " where claim_number = ?");
						prepSt.setString(1, (errorMessage == null ? "Y" : "N"));
						prepSt.setString(2, (errorMessage == null ? successMessage : errorMessage));
						prepSt.setString(3, claim.getClaimNumber());
						prepSt.executeUpdate();
				} catch (Exception e) {
					logger.error("Error while trying to initiate supplier recovery", e);
				} finally {
					if (ps != null)
						ps.close();
				}
				return null;
			}
		});

	}
	
	public void cleanUp() {
		claim = null;
		selectedContractForCausalPart = null;
		partContractMap.clear();
		selectedContractForRemovedPart.clear();
		errorMessage = null;
		successMessage = null;
		comments = null;
	}
	
	
	public void initiateRecovery() {
		String claimNumber = null;
		for(List<String> data : resultSetData) {
			if(claimNumber != null && !claimNumber.equalsIgnoreCase(data.get(0))) {
				initiateRecoveryForClaim();
				cleanUp();
			}
						
			if(claimNumber==null || !claimNumber.equalsIgnoreCase(data.get(0))) {
				claimNumber = data.get(0);
				claim = claimService.findClaimByNumber(claimNumber);
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim
						.getBusinessUnitInfo().getName());
			}
			
			Contract contract = contractService.findContract(new Long(data.get(3)));
			if("Y".equalsIgnoreCase(data.get(1))) 
				selectedContractForCausalPart = contract;
			else
				partContractMap.put(data.get(2), contract);
			
			if(comments == null && data.get(4) != null && data.get(4).length()>0)
				comments = data.get(4);
		}
		if(claimNumber != null) {
			initiateRecoveryForClaim();
			cleanUp();
		}
	}
	
	private void populateRecoverablePartContracts() {
		completeListOfPartsReplaced = claim.getServiceInformation().getServiceDetail().getReplacedParts();
		for(OEMPartReplaced partReplaced : completeListOfPartsReplaced) {
			if(partContractMap.containsKey(partReplaced.getItemReference().getReferredItem().getNumber())) {
				selectedContractForRemovedPart.add(partContractMap.get(partReplaced.getItemReference().getReferredItem().getNumber()));
			} else {
				selectedContractForRemovedPart.add(null);
			}
		}
	}
	
	private void initiateRecoveryForClaim() {
		try {
			populateRecoverablePartContracts();
			recoveryInfo = claim.getRecoveryInfo();
			if (recoveryInfo == null) {
				recoveryInfo = new RecoveryInfo();
				this.claim.setRecoveryInfo(recoveryInfo);
				recoveryInfo.setWarrantyClaim(claim);
			}
			submitRecoveryInfoAtPartLevel();
			contractService.createRecoveryClaims(this.recoveryInfo);
			TaskInstance task = workListItemService.findTaskForClaimWithTaskName(claim.getId(), WorkflowConstants.PENDING_REC_INITIATION);
			workListItemService.endTaskWithTransition(task, "Initiate");
			successMessage = claim.getClaimNumber()+": Successfully initiated recovery" ;
			addActionMessage("message.initRecoveryTask.success",new String[] {claim.getClaimNumber()});
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			errorMessage= claim.getClaimNumber()+ ": Failed to initiate recovery: "+e.getMessage();
			addActionError("message.initRecoveryTask.failure", new String[] {claim.getClaimNumber()});
		}
		updateStatus();
	}
	
	/* 
	 * All the methods starting here are copied as they are from SpecifySupplierRecoveryAction 
	 * Need to be refactored later.
	 * 
	 * */
	
	public String submitRecoveryInfoAtPartLevel() {
		
		if(recoveryInfo.getReplacedPartsRecovery()!=null)
		{
			RecoveryClaimInfo claimInfo = this.recoveryInfo.getCausalPartRecovery();
			recoveryInfo.getReplacedPartsRecovery().clear();
			recoveryInfo.setCausalPartRecovery(claimInfo);
		}
		
		if(selectedContractForCausalPart != null && selectedContractForCausalPart.getId() !=null)
		{
			RecoveryClaimInfo causalPartRecoveryClaimInfo =this.recoveryInfo.getCausalPartRecovery();
			if(causalPartRecoveryClaimInfo==null){
				causalPartRecoveryClaimInfo = new RecoveryClaimInfo();
				recoveryInfo.setCausalPartRecovery(causalPartRecoveryClaimInfo);
			}
			causalPartRecoveryClaimInfo.setContract(selectedContractForCausalPart);
			causalPartRecoveryClaimInfo.getRecoverableParts().clear();
			
			if (selectedContractForCausalPart.getCollateralDamageToBePaid()) {				
				populateCausalPartRecoveryClaimInfoforCollateral(causalPartRecoveryClaimInfo);
			} else {
				Map<Contract,List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
				prepareMapAtPartLevelWithCausalPartContract(partContracts);
				createReplacedPartRecoveryClaimInfo(partContracts);
			}			
		}
		else//no causal part contract selected
		{
			this.recoveryInfo.setCausalPartRecovery(null);
			Map<Contract,List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
			
			if(INPUT.equals(prepareMapAtPartLevelWithNoCausalPartContract(partContracts)))
				return INPUT;
			createReplacedPartRecoveryClaimInfo(partContracts);			
		}
		
		addUserCommentToRecoveryInfoObject();		
		this.recoveryInfo.setSavedAtPartLevel(true);
		updateOemPartCostsOnRecoverableParts();
		this.recoveryInfoService.saveUpdate(recoveryInfo);
		
		return SUCCESS;
	}
	
	private void populateCausalPartRecoveryClaimInfoforCollateral(RecoveryClaimInfo claimInfo) {
		for (OEMPartReplaced oemPartReplaced : completeListOfPartsReplaced) {
			RecoverablePart removedPart = new RecoverablePart();
			removedPart.setOemPart(oemPartReplaced);
			removedPart.setQuantity(oemPartReplaced.getNumberOfUnits());
			
			claimInfo.addRecoverablePart(removedPart);
		}
	}
	
	private void prepareMapAtPartLevelWithCausalPartContract(Map<Contract, List<RecoverablePart>> partContracts) {
		for (int i = 0; i < completeListOfPartsReplaced.size(); i++) {
			if (selectedContractForRemovedPart.isEmpty() || selectedContractForRemovedPart.get(i) == null) {// replaced parts has only the
																											// causal part
				if (claim.getServiceInformation().getCausalPart().equals(
						completeListOfPartsReplaced.get(i).getItemReference().getUnserializedItem())) {
					addOemPartToCausalPartRecoveryClaimInfo(completeListOfPartsReplaced.get(i));
				}
			} else if (selectedContractForRemovedPart.get(i).getId() != null) {
				populateMapAtPartLevel(partContracts, selectedContractForRemovedPart.get(i), completeListOfPartsReplaced.get(i));
			}
		}
	}
	
	private void addOemPartToCausalPartRecoveryClaimInfo(OEMPartReplaced partReplaced) {
		RecoverablePart causalPart = new RecoverablePart();
		causalPart.setQuantity(partReplaced.getNumberOfUnits());
		causalPart.setOemPart(partReplaced);
		
		recoveryInfo.getCausalPartRecovery().addRecoverablePart(causalPart);
	}
	
	private void populateMapAtPartLevel(Map<Contract,List<RecoverablePart>> partContracts ,Contract contract , OEMPartReplaced oemPartReplaced)
	{
		if(partContracts.containsKey(contract)){
			RecoverablePart recoverablePart = new RecoverablePart(oemPartReplaced,oemPartReplaced.getNumberOfUnits());
			partContracts.get(contract).add(recoverablePart);
		}
		else{
			List<RecoverablePart> partsCovered = new ArrayList<RecoverablePart>();
			partsCovered.add(new RecoverablePart(oemPartReplaced,oemPartReplaced.getNumberOfUnits()));
			partContracts.put(contract, partsCovered);
		}
	}
	
	public void createReplacedPartRecoveryClaimInfo(Map<Contract,List<RecoverablePart>> partContracts)
	{
		for (Map.Entry<Contract, List<RecoverablePart>> entry :partContracts.entrySet()){
			RecoveryClaimInfo recoveryClaimInfo = new RecoveryClaimInfo();
			recoveryClaimInfo.setContract(entry.getKey());
			recoveryClaimInfo.setRecoverableParts(entry.getValue());
			this.recoveryInfo.addReplacedPartsRecovery(recoveryClaimInfo);
		}
	}
	
	private String prepareMapAtPartLevelWithNoCausalPartContract(Map<Contract,List<RecoverablePart>> partContracts){
		boolean atleastOneContractSelected = false;
		
		for(int i = 0 ; i< selectedContractForRemovedPart.size() ; i++){
			if(selectedContractForRemovedPart.get(i) != null 
					&& selectedContractForRemovedPart.get(i).getId() != null )
			{
				atleastOneContractSelected = true;
				populateMapAtPartLevel(partContracts,selectedContractForRemovedPart.get(i),completeListOfPartsReplaced.get(i));
			}
		}
		if(!atleastOneContractSelected){
			addActionError("error.supplierRecovery.noSelectedContracts");
			return INPUT;
		}
		return null;
	}
	
	private void addUserCommentToRecoveryInfoObject() {
		if (StringUtils.hasText(this.comments)) {
			UserComment latestComment = new UserComment();
			latestComment.setComment(comments);
			latestComment.setMadeBy(getLoggedInUser());
			latestComment.setMadeOn(Clock.now());
			recoveryInfo.addComments(latestComment);
		}
	}
	
	private void updateOemPartCostsOnRecoverableParts() {
		if(!configParamService.getBooleanValue(ConfigName.IS_SUPPLIER_NEEDED_FOR_COST_FETCH.getName()))
		{
			for (RecoveryClaimInfo replaceRecClaimInfo : this.recoveryInfo.getReplacedPartsRecovery()) {
				for (RecoverablePart recoverablePart : replaceRecClaimInfo.getRecoverableParts()) {
					if (recoverablePart.getOemPart() != null) {
						recoverablePart.setMaterialCost(recoverablePart.getOemPart().getMaterialCost());
						recoverablePart.setCostPricePerUnit(recoverablePart.getOemPart().getCostPricePerUnit());
					}
				}
			}
		}
	}

}
