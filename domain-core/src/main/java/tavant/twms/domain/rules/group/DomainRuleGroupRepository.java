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
 * Time: 7:26:08 PM
 */

package tavant.twms.domain.rules.group;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.ListCriteria;

import java.util.List;

public interface DomainRuleGroupRepository extends GenericRepository<DomainRuleGroup, Long> {

	PageResult<DomainRuleGroup> listAllRuleGroupsByContext(String context, ListCriteria listCriteria);
	
    List<DomainRuleGroup> findRuleGroupsByContextOrderedByPriority(String context);

    PageResult<DomainRuleGroup> findRuleGroupsByContext(String context, ListCriteria listCriteria);

    Boolean doesAtLeastOneRuleGroupExistForContext(String context);

    Long findNextAvailableRuleGroupPriorityForContext(String context);

    Long findNextAvailableRulePriorityForRuleGroup(Long ruleGroupId);

    List<DomainRuleGroup> findRuleGroupsByContext(String context);

	public void updateRuleGroup(DomainRuleGroup ruleGroup);

	List<DomainRuleGroup> findActiveRuleGroupsForContext(String context);
    
}