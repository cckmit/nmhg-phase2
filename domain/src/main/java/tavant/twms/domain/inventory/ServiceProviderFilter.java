package tavant.twms.domain.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SecurityHelper;

@Aspect
public class ServiceProviderFilter {
    private SecurityHelper securityHelper;
    private OrgService orgService;

    @Before("execution(* tavant.twms.domain.inventory.InventoryItemRepository+.findAllInventoriesByTypeStartingWith(..)) || "
            + "execution(* tavant.twms.domain.inventory.InventoryItemRepository+.findAllItemsMatchingCriteria(..))")
    public void applyDealershipFilter(JoinPoint joinPoint){

        HibernateDaoSupport daoSupport = (HibernateDaoSupport) joinPoint.getTarget();
        ServiceProvider serviceProvider = getUserDealership();
        Filter filter = daoSupport.getHibernateTemplate().enableFilter("currentOwner");
        filter.setParameterList("invOwner", getAllDealerShips(serviceProvider));
    }

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    private ServiceProvider getUserDealership(){
        Organization organization = securityHelper.getLoggedInUser().getBelongsToOrganization();
        return ( organization != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, organization)) ?
                new HibernateCast<ServiceProvider>().cast(organization) : null;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    private Collection<Long> getAllDealerShips(ServiceProvider serviceProvider){
        List<Long> dealerList = new ArrayList<Long> ();
        if(serviceProvider != null && serviceProvider.isEnterpriseDealer()){
            dealerList.addAll(serviceProvider.getChildDealersIds());
        }
        if(serviceProvider != null && !serviceProvider.isEnterpriseDealer()){
            dealerList.add(serviceProvider.getId());
        }
        if(securityHelper.getLoggedInUser().isInternalUser() && serviceProvider == null){
            dealerList.add(securityHelper.getLoggedInUser().getBelongsToOrganization().getId());
        }
        return dealerList;
    }
}
