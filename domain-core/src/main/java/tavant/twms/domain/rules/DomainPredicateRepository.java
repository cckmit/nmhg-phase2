/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.rules;

import java.util.List;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.ListCriteria;

/**
 * @author radhakrishnan.j
 * 
 */
public interface DomainPredicateRepository extends GenericRepository<DomainPredicate, Long> {
    public PageResult<DomainPredicate> findByNameInContext(String name,String context, PageSpecification pageSpec);

    public List<DomainPredicate> findByNameInContext(String name,String context);
    
    public List<DomainPredicate> findByName(String name) ;
    
    public PageResult<DomainPredicate> findAllInContext(String name,PageSpecification pageSpecification);

    public PageResult<DomainPredicate> findAll(String context, PageSpecification pageSpecification);

    public List<DomainPredicate> findClashingPredicates(
            DomainPredicate predicate);
    
    public List<DomainPredicate> findClashingPredicates(Long userId,String context,DomainPredicate predicate);

    public List<DomainRule> findRulesUsingPredicate(DomainPredicate domainPredicate);
    
    public List<DomainPredicate> findPredicatesReferringToPredicate(
            DomainPredicate domainPredicate);

    public PageResult<DomainPredicate> findAllNonSearchPredicates(
            ListCriteria criteria);

    public List<DomainPredicate> findNonSearchPredicatesByName(String name, String context, boolean includeSystemConditions);
    
    public List<DomainPredicate> findNonSearchPredicatesByName(String name,boolean includeSystemConditions);
    
    public Integer findMaxRuleNumberForContext();
    
    public List<ListOfValues> findAllDescription(String classname);
	
    public PageResult<DomainPredicate> findAllNonSearchPredicates(ListCriteria criteria, String context);

	public PageResult<DomainPredicate> findAllNonSearchPredicates(
			ListCriteria criteria, List<String> contexts);

	public List<DomainPredicate> findNonSearchPredicatesByNameAndContexts(
			String name, List<String> contexts,boolean includeSystemConditions);

}
