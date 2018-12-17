package tavant.twms.domain.upload.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimRepository;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.security.SelectedBusinessUnitsHolder;

public class CostPricePostUploadAction implements PostUploadAction {
	
	private JdbcTemplate jdbcTemplate;

	private TransactionTemplate transactionTemplate;

	private RecoveryClaimRepository recoveryClaimRepository;
	
	private ContractService contractService;
	
	private String stagingTable;
	
	public void doUplaodPostProcessing(long fileUploadMgtId) throws Exception {
		//get data from staging table
		List<Long> recClaims = getRecoveryClaimsToBeProcessed(fileUploadMgtId);
		//update recovery cost details
		updateRecoveryClaims(recClaims);
	}

	public void updateRecoveryClaims(final List<Long> recClaims) {
		this.transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				for(Long claimId : recClaims) {
					try {
						RecoveryClaim recClaim = recoveryClaimRepository.find(claimId);
						SelectedBusinessUnitsHolder.setSelectedBusinessUnit(recClaim.getBusinessUnitInfo().getName());
						//contractService.updateSupplierRecovery(recClaim);
						contractService.updateOEMPartsCostLineItem(recClaim);
                    }catch(Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getRecoveryClaimsToBeProcessed(final long fileUploadMgtId) throws SQLException {
		return (List<Long>) jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
				Statement stmt = null;
				ResultSet rs = null;
				List<Long> recClaims = new ArrayList<Long>();
					try {
						stmt = conn.createStatement();
						rs = stmt.executeQuery("select distinct(recovery_claim) from stg_cost_price " +
								" where upload_status is not null and UPPER(upload_status) = 'Y' " +
								" and file_upload_mgt_id = "+fileUploadMgtId);
						while (rs.next()) {
							recClaims.add(rs.getLong(1));
						}
					}
					finally {
						if (rs != null)
							rs.close();
						if (stmt != null)
							stmt.close();
					}
				return recClaims;
			}
		});
	}

	public ContractService getContractService() {
		return contractService;
	}
	
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}
	
	public RecoveryClaimRepository getRecoveryClaimRepository() {
		return recoveryClaimRepository;
	}
	
	public void setRecoveryClaimRepository(
			RecoveryClaimRepository recoveryClaimRepository) {
		this.recoveryClaimRepository = recoveryClaimRepository;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getStagingTable() {
		return stagingTable;
	}

	public void setStagingTable(String stagingTable) {
		this.stagingTable = stagingTable;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
}
