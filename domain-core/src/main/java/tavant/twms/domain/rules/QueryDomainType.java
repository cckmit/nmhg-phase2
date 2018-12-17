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
package tavant.twms.domain.rules;

import java.util.Set;
/**
 * 
 * @author roopali.agrawal
 *
 */
public class QueryDomainType extends DomainType{
	
    public QueryDomainType(String domainName, String typeName) {
    	super(domainName,typeName);
        Set<Class<? extends Predicate>> predicates = supportedPredicates();
        predicates.remove(BelongsTo.class);
        predicates.remove(DoesNotBelongTo.class);
    }
    


}
