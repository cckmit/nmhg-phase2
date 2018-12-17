package tavant.twms.web.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.worklist.WorkListItemService;

public class SendRecoveryClaimToSupplier extends I18nActionSupport {
	
	private Logger logger = Logger.getLogger(SendRecoveryClaimToSupplier.class);
	
	private static final String COL_REC_CLAIM_NUMBER = "REC_CLAIM_NUMBER";
	private static final String COL_REC_PROCESSOR = "REC_PROCESSOR";
	private static final String COL_COMMENTS = "COMMENTS";
	
	final List<List<String>> resultSetData = new ArrayList<List<String>>();
	
	private JdbcTemplate jdbcTemplate;
	private OrgService orgService;
	private WorkListItemService workListItemService;
	private RecoveryClaimService recoveryClaimService;
	
	private RecoveryClaim recoveryClaim;
	private TaskInstance task;
	private String comments;
	private String errorMessage;
	
	public String run() {
		load();
		sendToSupplier();
		return SUCCESS;
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
						columnValues.add(resultSet.getString(COL_REC_CLAIM_NUMBER));
						columnValues.add(resultSet.getString(COL_REC_PROCESSOR));
						columnValues.add(resultSet.getString(COL_COMMENTS));
						resultSetData.add(columnValues);
					}
				} catch (Exception e) {
					logger.error("Error while trying to send the rec claim to supplier", e);
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

	private void sendToSupplier() {
		for(List<String> data : resultSetData) {
			validateData(data.get(0),data.get(1),data.get(2));
			if(errorMessage == null) {
				try {
					User user =  orgService.findUserByName(data.get(1));
					recoveryClaim.setLoggedInUser(user);
					recoveryClaim.setComments(comments);
					recoveryClaimService.updatePayment(recoveryClaim);
					recoveryClaimService.updateRecoveryClaim(recoveryClaim);
					workListItemService.endTaskWithTransition(task, "Send To Supplier");
					addActionMessage("message.sendToSupplierTask.success",new String[] {data.get(0)});
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
					errorMessage= "Failed to send the rec claim to supplier: "+e.getMessage();
					addActionError("message.sendToSupplierTask.failure", new String[] {data.get(0),""});
				}
			} else {
				addActionError("message.sendToSupplierTask.failure", new String[] {data.get(0),errorMessage});
			}
			updateStatus();
			cleanUp();
		}
	}
	
	private void validateData(String recClaimNumber, String recProcessor, String comments) {
		recoveryClaim = recoveryClaimService.findRecoveryClaim(recClaimNumber);
		if(recoveryClaim == null) {
			errorMessage = "Invalid recovery claim number";
			return;
		}
		task = workListItemService.findTaskForRecClaimWithTaskName(recoveryClaim.getId(), WorkflowConstants.FOR_RECOVERY);
		if(task == null || !task.getActorId().equalsIgnoreCase(recProcessor)) {
			errorMessage = "Not in New inbox of " + recProcessor;
			return;
		}
		if(comments == null || comments.length()==0) {
			errorMessage = "Recovery comments required";
			return;
		}
		this.comments = comments;
	}
	
	public void updateStatus() {
		this.jdbcTemplate.execute(new ConnectionCallback() {
			public Object doInConnection(Connection conn) throws SQLException, DataAccessException {
				PreparedStatement ps = null;
				try {					
						PreparedStatement prepSt = conn.prepareStatement("update STG_RECOVERY_PROCESSING " +
								" set upload_status = ?, upload_message = ? "
								+ " where rec_claim_number = ?");
						prepSt.setString(1, (errorMessage == null ? "Y" : "N"));
						prepSt.setString(2, (errorMessage == null ? "" : errorMessage));
						prepSt.setString(3, recoveryClaim.getRecoveryClaimNumber());
						prepSt.executeUpdate();
				} catch (Exception e) {
					logger.error("Error while trying to send the rec claim to supplier", e);
				} finally {
					if (ps != null)
						ps.close();
				}
				return null;
			}
		});

	}
	
	public void cleanUp() {
		recoveryClaim = null;
		errorMessage = null;
		comments = null;
		task = null;
	}
	
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private static String stagingDataQuery = "select rec_claim_number,rec_processor," +
	" comments from STG_RECOVERY_PROCESSING where upload_status is null " +
	" and rec_claim_number is not null" +
	" and rec_processor is not null" +
	" order by rec_claim_number ";

}



