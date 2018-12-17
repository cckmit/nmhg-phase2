package tavant.twms.web.actions;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimRepository;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.jbpm.assignment.AssignmentRuleExecutor;
import tavant.twms.jbpm.assignment.LoadBalancingService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.worklist.WorkListDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: Nov 5, 2009
 * Time: 4:06:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMRClaimReassignmentAction extends I18nActionSupport {
    private String claimNumber;
    private AssignmentRuleExecutor assignmentRuleExecutor;
    private ClaimService claimService;
    private LoadBalancingService loadBalancingService;
    private String correctAsignee;
    private WorkListDao workListDao;
    private String selectedBusinessUnit;
    private String password;
    private String task;
    private ClaimRepository claimRepository;


    public String reassignment() {
        validateUrl();
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBusinessUnit);
        List<Claim> claims = new ArrayList<Claim>();
        if (StringUtils.hasText(getPassword())) {
            claims = claimRepository.findSMRClaimToReassign();
        } else {
            Claim claim = claimService.findClaimByNumber(claimNumber);
            claims.add(claim);
        }
        for (Claim claim : claims) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
            List<String> eligibleDSMs = assignmentRuleExecutor
				.fetchEligibleDSMsUsingAssignmentRules(claim);
            if (eligibleDSMs == null || eligibleDSMs.size() == 0) {
                User defaulfUser = orgService.findDefaultUserBelongingToRoleForSelectedBU(claim.getBusinessUnitInfo().getName(), Role.DSM);
                if (defaulfUser != null && !"".equalsIgnoreCase(defaulfUser.getName()))
                    setCorrectAsignee(defaulfUser.getName());
                else
                    setCorrectAsignee("dsm");
            } else {
                setCorrectAsignee(getUserWithLeastLoad(eligibleDSMs));
            }
            List<TaskInstance> openTasks = workListDao.getAllOpenTasksForClaim(claim);
            for (TaskInstance openTask : openTasks) {
                if (openTask.getName().equals("Service Manager Review")) {
                    if (!openTask.getActorId().equals(getCorrectAsignee())) {
                        openTask.setActorId(getCorrectAsignee());
                        workListDao.updateTaskInstance(openTask);
                    }
                }
            }
        }
        return SUCCESS;
    }

    private void validateUrl() {
        if (!StringUtils.hasText(getSelectedBusinessUnit())) {
            throw new RuntimeException("Selected business unit is mandatory.");
        } else if (!StringUtils.hasText(getPassword()) && !StringUtils.hasText(getClaimNumber())) {
            throw new RuntimeException("Please enter the password or claim number");
        } else if (StringUtils.hasText(getPassword()) && !getPassword().equals("tavantTWMS")) {
            throw new RuntimeException("The entered passowrd is incorrect.");
        } else if (StringUtils.hasText(getPassword())
                && (StringUtils.hasText(getClaimNumber()) || StringUtils.hasText(getTask()))) {
            throw new RuntimeException("Please enter the password for all claims or only claimnumber for each claim.");
        }
    }

    private String getUserWithLeastLoad(List<String> eligibleProcessors) {
        List<String> sortedUsers = loadBalancingService
                .findUsersSortedByLoad(eligibleProcessors);
        if (sortedUsers == null
                || sortedUsers.size() < eligibleProcessors.size()) {
            return findAnUnassignedUser(sortedUsers, eligibleProcessors);
        } else {
            return sortedUsers.get(0);
        }
    }

    private String findAnUnassignedUser(List<String> usersWithTasks,
                                        List<String> eligibleUsers) {
        if (usersWithTasks == null)
            return eligibleUsers.get(0);

        for (String eligibleUser : eligibleUsers) {
            if (!usersWithTasks.contains(eligibleUser)) {
                return eligibleUser;
            }
        }
        return null; // this case shouldn't arise !
    }


    public AssignmentRuleExecutor getAssignmentRuleExecutor() {
        return assignmentRuleExecutor;
    }

    @Required
    public void setAssignmentRuleExecutor(AssignmentRuleExecutor assignmentRuleExecutor) {
        this.assignmentRuleExecutor = assignmentRuleExecutor;
    }

    public ClaimService getClaimService() {
        return claimService;
    }

    @Required
    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public LoadBalancingService getLoadBalancingService() {
        return loadBalancingService;
    }

    @Required
    public void setLoadBalancingService(LoadBalancingService loadBalancingService) {
        this.loadBalancingService = loadBalancingService;
    }

    public String getCorrectAsignee() {
        return correctAsignee;
    }

    public void setCorrectAsignee(String correctAsignee) {
        this.correctAsignee = correctAsignee;
    }

    public WorkListDao getWorkListDao() {
        return workListDao;
    }

    public void setWorkListDao(WorkListDao workListDao) {
        this.workListDao = workListDao;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getSelectedBusinessUnit() {
        return selectedBusinessUnit;
    }

    public void setSelectedBusinessUnit(String selectedBusinessUnit) {
        this.selectedBusinessUnit = selectedBusinessUnit;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public ClaimRepository getClaimRepository() {
        return claimRepository;
    }

    public void setClaimRepository(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }
}
