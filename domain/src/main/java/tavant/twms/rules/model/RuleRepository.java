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
package tavant.twms.rules.model;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * Should we deprecate this? Use the objectRepository instead. (May be some other 
 * name) there?
 * @author kannan.ekanath
 *
 */
@Transactional(readOnly=true)
public interface RuleRepository {

	public RuleSet load(Long id);
	
	public List loadAll();
	
        @Transactional(readOnly=false)
	public void save(RuleSet rule);

        @Transactional(readOnly=false)
	public void update(RuleSet rule);
        
        @Transactional(readOnly=false)
	public void delete(Long id);
	
	public List<RuleSet> findRuleWithName(String name);
}
