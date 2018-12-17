package tavant.twms.web.bu;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.SortedSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.security.SecurityHelper;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ManageUserBUMapping extends ActionSupport {

    private static Logger logger = LogManager.getLogger(ManageBUConfiguration.class);
    private OrgService orgService;
    private BusinessUnitService businessUnitService;
    private String userLoginSearchStr;
    private List<User> userList = new ArrayList<User>();
    private List<User> userListSelected = new ArrayList<User>();
    private SortedSet<BusinessUnit> allBusinessUnits = new TreeSet<BusinessUnit>();
    private SecurityHelper securityHelper;

    public String show() {
        return SUCCESS;
    }
    public String search() {
        if (userLoginSearchStr != null && !userLoginSearchStr.equals("")) {
            allBusinessUnits = securityHelper.getLoggedInUser().getBusinessUnits(); 
            userList = orgService.findInternalUsersWithNameLike(userLoginSearchStr);
            
            if (userList.isEmpty()) {
                addActionError(getText("error.invalidInternalUsersInput"));
                return INPUT;
            }
        } else {
            addActionError(getText("error.searchStrEmpty"));
            return INPUT;
        }
        addActionError(getText("error.userNotChecked"));
        return SUCCESS;

    }

    public String update() {
        try {
            for (User userToBeUpdated : userListSelected) {
                if (userToBeUpdated.getId() != null) { // this is needed to get only the checked values from the UI
                    TreeSet<BusinessUnit> businessUnitAdded = new TreeSet<BusinessUnit>();
                    List<BusinessUnit> userHiddenBusinessunit =  orgService.findAllBusinessUnitsForUser(userToBeUpdated);
        			userHiddenBusinessunit.removeAll(securityHelper.getLoggedInUser().getBusinessUnits());
                    for (BusinessUnit bu : userToBeUpdated.getBusinessUnitAdded()) {
                        businessUnitAdded.add(bu);
                    }
                    businessUnitAdded.addAll(userHiddenBusinessunit);
                    userToBeUpdated.setBusinessUnits(businessUnitAdded);  
 if( userToBeUpdated.getPreferredBu()!=null && !(userToBeUpdated.getAllBUNames().contains(userToBeUpdated.getPreferredBu()))){
                		
                		userToBeUpdated.setPreferredBu(null);
                	}
                    orgService.updateUser(userToBeUpdated);
                }
            }
        }
        catch (Exception e) {
            logger.error("Error in updating bu user mapping ", e);
            addActionError(getText("error.update.buMapping"));
            return INPUT;

        }
        addActionMessage(getText("message.update.buMapping"));
        return SUCCESS;
    }

    public List<BusinessUnit> getBusinessUnits(User user) {
        List<BusinessUnit> copyOfAllBusinessunit = new ArrayList<BusinessUnit>();
        copyOfAllBusinessunit.addAll(allBusinessUnits);
        copyOfAllBusinessunit.removeAll(user.getBusinessUnits());
        return copyOfAllBusinessunit;
    }

    public String getUserLoginSearchStr() {
        return userLoginSearchStr;
    }

    public void setUserLoginSearchStr(String userLoginSearchStr) {
        this.userLoginSearchStr = userLoginSearchStr;
    }

    public OrgService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getUserListSelected() {
        return userListSelected;
    }

    public void setUserListSelected(List<User> userListSelected) {
        this.userListSelected = userListSelected;
    }

    public BusinessUnitService getBusinessUnitService() {
        return businessUnitService;
    }

    public void setBusinessUnitService(BusinessUnitService businessUnitService) {
        this.businessUnitService = businessUnitService;
    }

    public SortedSet<BusinessUnit> getAllBusinessUnits() {
        return allBusinessUnits;
    }

    public void setAllBusinessUnits(SortedSet<BusinessUnit> allBusinessUnits) {
        this.allBusinessUnits = allBusinessUnits;
    }

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public SecurityHelper getSecurityHelper() {
        return securityHelper;
    }


}