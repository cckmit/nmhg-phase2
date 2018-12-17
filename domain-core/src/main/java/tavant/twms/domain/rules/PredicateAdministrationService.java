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

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.ListCriteria;

/**
 * @author radhakrishnan.j
 */
@Transactional(readOnly = true)
public interface PredicateAdministrationService extends
        GenericService<DomainPredicate, Long, PredicateAdministrationException> {

    public PageResult<DomainPredicate> findAllNonSearchPredicates(
            ListCriteria criteria);

    public PageResult<DomainPredicate> findAllNonSearchPredicates(
            ListCriteria criteria, String context);

    public List<DomainPredicate> findNonSearchPredicatesByName(String name);
    public List<DomainPredicate> findNonSearchPredicatesByName(String name, String context);
    public List<DomainPredicate> findNonSearchPredicatesByName(String name,boolean includeSystemConditions);

    public List<DomainPredicate> findPredicatesByName(String name);

    public List<DomainPredicate> findPredicatesByName(String name, String forRuleEditorContext);
  
    public PageResult<DomainPredicate> findAllRulesInContext(String context, PageSpecification pageSpecification);

    public List<DomainPredicate> findClashingPredicates(DomainPredicate predicate);
    
    public List<DomainPredicate> findClashingPredicates(String context,DomainPredicate predicate);

    public List<DomainRule> findRulesUsingPredicate(
            DomainPredicate domainPredicate);

    public List<DomainPredicate> findPredicatesReferringToPredicate(
            DomainPredicate domainPredicate);
    //todo-move following to a separate service class.
    public List<SavedQuery> findSavedQueriesByContextAndUser(String context,Long userId);
    

    public List<ListOfValues> findAllDescription(String classname);
    
    public PageResult<DomainPredicate> findAllNonSearchPredicates( ListCriteria criteria, List<String> context);
    
    public List<DomainPredicate>  findNonSearchPredicatesByNameAndContexts(String name, List<String> contexts);

    
    
	// API used for finding the saved queries by the created user  
	// and the context not based on Domain Predicate. 
	public List<SavedQuery> findSavedQueriesByUserUsingContext(String context, Long userId);
	
    @Transactional(readOnly=false)
    public void saveSavedQuery(SavedQuery scr);
    
    @Transactional(readOnly=false)
    public void deleteSavedQuery(SavedQuery scr);
    
    public SavedQuery findSavedQueryById(Long id);
    
    @Transactional(readOnly=false)
    public void updateSavedQuery(SavedQuery scr);
    
    @Transactional(readOnly=false)
    public Integer findMaxRuleNumberForContext();

}
