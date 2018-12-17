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
 * Time: 7:53:37 PM
 */

package tavant.twms.domain.rules.group;

import org.springframework.transaction.annotation.Transactional;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.ListCriteria;
import tavant.twms.domain.rules.group.DomainRuleGroup;

import java.util.List;

/**
 * @author vikas.sasidharan
 */
@Transactional(readOnly=true)
public interface RuleGroupAdministrationService extends GenericService<DomainRuleGroup, Long, Exception> {

    public List<DomainRuleGroup> findRuleGroupsForContext(String context);

    public List<DomainRuleGroup> findRuleGroupsForContextOrderedByPriority(String context);
    
    public List<DomainRuleGroup> findActiveRuleGroupsForContext(String context);

    public PageResult<DomainRuleGroup> findRuleGroupsForContext(String context, ListCriteria listCriteria);
    
    public PageResult<DomainRuleGroup> listAllRuleGroupsByContext(String context, ListCriteria listCriteria);

    public boolean doesAtLeastOneRuleGroupExistForContext(String context);

    public Long findNextAvailableRuleGroupPriorityForContext(String context);

    public Long findNextAvailableRulePriorityForRuleGroup(Long ruleGroupId);
    @Transactional(readOnly = false)
    public void updateRuleGroup(DomainRuleGroup ruleGroup);
    
}