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

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryRepository;
import tavant.twms.infra.*;
import tavant.twms.security.SecurityHelper;

/**
 * @author radhakrishnan.j
 * 
 */
public class PredicateAdministrationServiceImpl extends
        GenericServiceImpl<DomainPredicate, Long, PredicateAdministrationException> implements
        PredicateAdministrationService {

    private DomainPredicateRepository domainPredicateRepository;
    
    private SavedQueryRepository savedQueryRepository;

    @Required
    public void setDomainPredicateRepository(DomainPredicateRepository domainPredicateRepository) {
        this.domainPredicateRepository = domainPredicateRepository;
    }
   
    public SavedQueryRepository getSavedQueryRepository() {
		return this.savedQueryRepository;
	}
    @Required
	public void setSavedQueryRepository(SavedQueryRepository savedQueryRepository) {
		this.savedQueryRepository = savedQueryRepository;
	}

	@Override
    public GenericRepository<DomainPredicate, Long> getRepository() {
        return this.domainPredicateRepository;
    }

    public List<DomainPredicate> findPredicatesByName(String name) {
        return this.domainPredicateRepository.findByName(name);
    }
    
    public List<DomainPredicate> findPredicatesByName(String name, String forRuleEditorContext) {
        return this.domainPredicateRepository.findByNameInContext(name,forRuleEditorContext);
    }

    public PageResult<DomainPredicate> findAllRulesInContext(String context,PageSpecification pageSpecification) {
        return this.domainPredicateRepository.findAll(context, pageSpecification);
    }

    public List<DomainPredicate> findClashingPredicates(DomainPredicate predicate) {
        return this.domainPredicateRepository.findClashingPredicates(predicate);
    }
    
    public List<DomainPredicate> findClashingPredicates(String context,DomainPredicate predicate) {
        return this.domainPredicateRepository.findClashingPredicates(new SecurityHelper().getLoggedInUser().getId(),context, predicate);
    }

    public List<DomainRule> findRulesUsingPredicate(DomainPredicate domainPredicate) {
        return this.domainPredicateRepository.findRulesUsingPredicate(
                domainPredicate);
    }

    public List<DomainPredicate> findPredicatesReferringToPredicate(
            DomainPredicate domainPredicate) {
        return this.domainPredicateRepository.findPredicatesReferringToPredicate(
                domainPredicate);
    }
	
	public List<SavedQuery> findSavedQueriesByContextAndUser(String context,Long userId){
		
		return this.savedQueryRepository.findSavedQueriesByContextAndUser(
                context,userId);
	}
	
	// API used for finding the saved queries by the created user  
	// and the context not based on Domain Predicate. 
	public List<SavedQuery> findSavedQueriesByUserUsingContext(String context, Long userId){
		return this.savedQueryRepository.findSavedQueriesByUserUsingContext(
                context,userId);
	}	
    
    public void saveSavedQuery(SavedQuery scr){
    	this.savedQueryRepository.save(scr);
    }
    
    public void updateSavedQuery(SavedQuery scr){
    	this.savedQueryRepository.update(scr);
    }

	public void deleteSavedQuery(SavedQuery scr) {
		this.savedQueryRepository.delete(scr);
	}

	public SavedQuery findSavedQueryById(Long id) {
		return this.savedQueryRepository.findById(id);
	}

    public PageResult<DomainPredicate> findAllNonSearchPredicates(
            ListCriteria criteria) {
        return this.domainPredicateRepository.findAllNonSearchPredicates(
                criteria);
    }

    public List<DomainPredicate> findNonSearchPredicatesByName(String name) {
        return this.domainPredicateRepository.findNonSearchPredicatesByName(name, true);
    }
    
    public List<DomainPredicate> findNonSearchPredicatesByName(String name, boolean includeSystemConditions) {
        return this.domainPredicateRepository.findNonSearchPredicatesByName(name,includeSystemConditions);
    }
    
    public Integer findMaxRuleNumberForContext(){
    	return this.domainPredicateRepository.findMaxRuleNumberForContext();
    }

	public List<ListOfValues> findAllDescription(String classname) {
		
		return this.domainPredicateRepository.findAllDescription(classname);
	}
	
	public PageResult<DomainPredicate> findAllNonSearchPredicates(ListCriteria criteria, String context) {
        return this.domainPredicateRepository.findAllNonSearchPredicates(criteria, context);
    }
	
    public List<DomainPredicate> findNonSearchPredicatesByName(String name, String context) {
        return this.domainPredicateRepository.findNonSearchPredicatesByName(name, context, true);
    }

	public PageResult<DomainPredicate> findAllNonSearchPredicates(
			ListCriteria criteria, List<String> contexts) {
		return this.domainPredicateRepository.findAllNonSearchPredicates(criteria,contexts);
	}
	
	public List<DomainPredicate> findNonSearchPredicatesByNameAndContexts(
			String name, List<String> contexts) {
		return this.domainPredicateRepository.findNonSearchPredicatesByNameAndContexts(name,contexts,true);
	}
	
	
}
