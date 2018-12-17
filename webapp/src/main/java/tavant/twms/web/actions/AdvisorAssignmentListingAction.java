package tavant.twms.web.actions;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: Nov 5, 2009
 * Time: 4:19:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdvisorAssignmentListingAction extends I18nActionSupport {
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
    private Map<String, List<String>> claimWithAdvisors = new HashMap<String, List<String>>();


    public String listingOfAdvisors() {
        validateUrl();
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBusinessUnit);
        Claim claim = claimService.findClaimByNumber(claimNumber);
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        List<String> eligibleAdvisors = assignmentRuleExecutor
                .fetchEligibleDSMAdvisorsUsingAssignmentRules(claim);
        if (eligibleAdvisors == null || (eligibleAdvisors != null && eligibleAdvisors.isEmpty())) {
            eligibleAdvisors = new ArrayList<String>(findUsersBelongingToRole(Role.DSM_ADVISOR, claim.getBusinessUnitInfo().getName()));
        }
        claimWithAdvisors.put(claim.getClaimNumber(), eligibleAdvisors);
        return SUCCESS;
    }

    private void validateUrl() {
        if (!StringUtils.hasText(getSelectedBusinessUnit())) {
            throw new RuntimeException("Selected business unit is mandatory.");
        } else if (!StringUtils.hasText(getClaimNumber())) {
            throw new RuntimeException("Please enter the claim number");
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<String> findUsersBelongingToRole(String role, String businessUnitName) {
        Set<User> serviceManagers = this.orgService
                .findUsersBelongingToRole(role);
        if (serviceManagers != null && !serviceManagers.isEmpty())
            filterAvailableUsers(serviceManagers, businessUnitName, role);
        return CollectionUtils.collect(serviceManagers, new Transformer() {
            public Object transform(Object input) {
                return ((User) input).getName();
            }
        });
    }

    private void filterAvailableUsers(Set<User> serviceManagers, String businessUnitName, String role) {
        for (Iterator<User> iterator = serviceManagers.iterator(); iterator.hasNext();) {
            User serviceManager = (User) iterator.next();
            if (!serviceManager.isAvailableForBU(businessUnitName, role))
                iterator.remove();
        }
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

    public Map<String, List<String>> getClaimWithAdvisors() {
        return claimWithAdvisors;
    }

    public void setClaimWithAdvisors(Map<String, List<String>> claimWithAdvisors) {
        this.claimWithAdvisors = claimWithAdvisors;
    }
}
