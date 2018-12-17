/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Mar 1, 2007
 * Time: 7:54:13 PM
 */

package tavant.twms.domain.rules.group;

import tavant.twms.infra.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author vikas.sasidharan
 */
public class RuleGroupAdministrationServiceImpl extends GenericServiceImpl<DomainRuleGroup, Long, Exception> implements
        RuleGroupAdministrationService {

    private DomainRuleGroupRepository domainRuleGroupRepository;

    @Required
    public void setDomainRuleGroupRepository(DomainRuleGroupRepository domainRuleGroupRepository) {
        this.domainRuleGroupRepository = domainRuleGroupRepository;
    }

    public GenericRepository<DomainRuleGroup, Long> getRepository() {
        return domainRuleGroupRepository;
    }

    public List<DomainRuleGroup> findRuleGroupsForContext(String context) {
        return domainRuleGroupRepository.findRuleGroupsByContext(context);
    }
    
    public List<DomainRuleGroup> findActiveRuleGroupsForContext(String context){
    	 return domainRuleGroupRepository.findActiveRuleGroupsForContext(context);
    }

    public List<DomainRuleGroup> findRuleGroupsForContextOrderedByPriority(String context) {
        return domainRuleGroupRepository.findRuleGroupsByContextOrderedByPriority(context);
    }

    public PageResult<DomainRuleGroup> findRuleGroupsForContext(String context, ListCriteria listCriteria) {
        return domainRuleGroupRepository.findRuleGroupsByContext(context, listCriteria);
    }
    public PageResult<DomainRuleGroup> listAllRuleGroupsByContext(String context, ListCriteria listCriteria) {
        return domainRuleGroupRepository.listAllRuleGroupsByContext(context, listCriteria);
    }
    

    public boolean doesAtLeastOneRuleGroupExistForContext(String context) {
        return domainRuleGroupRepository.doesAtLeastOneRuleGroupExistForContext(context);
    }

    public Long findNextAvailableRuleGroupPriorityForContext(String context) {
        return domainRuleGroupRepository.findNextAvailableRuleGroupPriorityForContext(context);
    }

    public Long findNextAvailableRulePriorityForRuleGroup(Long ruleGroupId) {
        return domainRuleGroupRepository.findNextAvailableRulePriorityForRuleGroup(ruleGroupId);
    }

	public void updateRuleGroup(DomainRuleGroup ruleGroup) {
		domainRuleGroupRepository.updateRuleGroup(ruleGroup);
		
	}
}