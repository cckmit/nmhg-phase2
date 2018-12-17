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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

/**
 * @author radhakrishnan.j
 * 
 */
@SuppressWarnings("serial")
public class DomainRuleElementsPersistenceListener 
    implements PostLoadEventListener,PreUpdateEventListener,PreInsertEventListener {
    private static Logger logger = LogManager.getLogger(DomainRuleElementsPersistenceListener.class);
    private RuleSerializer xmlSerializer;
    private boolean ignoreErrors = false;

    @Required
    public void setXmlSerializer(RuleSerializer xmlRuleSerializer) {
        this.xmlSerializer = xmlRuleSerializer;
    }

    public boolean onPreInsert(PreInsertEvent preInsertEvent) {
        Object entity = preInsertEvent.getEntity();
        EntityPersister persister = preInsertEvent.getPersister();
        convertObjectToXML("pre-insert",entity,persister);        
        return false;
    }

    public void onPostLoad(PostLoadEvent postLoadEvent) {
        Object entity = postLoadEvent.getEntity();
        convertXMLToObject(entity,postLoadEvent.getSession());
    }

    public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
        Object entity = preUpdateEvent.getEntity();
        EntityPersister persister = preUpdateEvent.getPersister();
        convertObjectToXML("pre-update",entity,persister);        
        return false;
    }
    
    protected void convertObjectToXML(String updateOrInsert,Object entity,EntityPersister entitysPersister) {
        if( entity instanceof DomainPredicate ) {
            DomainPredicate domainPredicate = (DomainPredicate)entity;
            Predicate predicate = domainPredicate.getPredicate();

            validatePredicate(domainPredicate);

            String toXML = toXML(predicate);
            domainPredicate.setPredicateAsXML(toXML);
        }
    }

    protected void convertXMLToObject(Object entity,Session inSession) {
        
        if( entity instanceof DomainPredicate ) {
            DomainPredicate domainPredicate = (DomainPredicate)entity;
            String fromXML = domainPredicate.getPredicateAsXML();     
            if(StringUtils.hasText(fromXML))
            {            
	            Predicate object = (Predicate)xmlSerializer.fromXML(fromXML);

                    validatePredicate(object);
	            
	            //Need to traverse the rule tree and identify 'DomainPredicate' references that
	            //Need to get updated with the latest in store.
				DomainPredicateSynchronizer synchronizer  =
						    new DomainPredicateSynchronizer(inSession);
				object.accept(synchronizer);
	            
	            validatePredicate(object);
	            domainPredicate.setPredicate(object);
            }
        }
    }
    
    private String toXML(Predicate predicate) {
        validatePredicate(predicate);
        String toXML = xmlSerializer.toXML(predicate);
        return toXML;
    }

    private void validatePredicate(Predicate predicate) {
        SimpleValidator validator = new SimpleValidator();
        ValidationContext validationContext = validator.getValidationContext();
        predicate.accept(validator);
        if( validationContext.hasErrors() ) {
            if( ignoreErrors ) {
                logger.error(" Validation failed for ["+predicate+"] for reason "+validationContext.getErrors() );
            } else {
                throw new RuntimeException(validationContext.getErrors().toString());                
            }
        }
    }

    public boolean isIgnoreErrors() {
        return ignoreErrors;
    }

    public void setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }
}
