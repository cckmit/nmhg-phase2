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

package tavant.twms.domain.rules;

import java.util.List;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public interface DomainRuleRepository extends
		GenericRepository<DomainRule, Long> {

	public PageResult<DomainRule> findByNameInContext(String name, String context, boolean useRuleGroup,
                                                      PageSpecification pageSpec);

	public List<DomainRule> findByNameInContext(String name, String context, boolean useRuleGroup);

	public List<DomainRule> findByContext(String context, boolean useRuleGroup);
	
	public List<DomainRule> findProcessorAuthorityRules(final String context, final User processor);

	public PageResult<DomainRule> findAllInContext(String context, boolean useRuleGroup,
                                                   PageSpecification pageSpecification);

	public boolean isQuerySatisfied(String query);

	public List<DomainRule> findByRuleNumber(Integer Number);

	public List<String> executeDuplicateClaimsQuery(String query);
	
	public PageResult<DomainRule> findAllInContext(String context, ListCriteria listCriteria, boolean useRuleGroup);
	
	public List<DomainRule> findByContextAndRuleApplicableTo(String context, boolean useRuleGroup,Organization organization);

}
