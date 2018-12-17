/*Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.claim;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.security.SecurityHelper;

/**
 * @author roopali.agrawal
 * 
 */
@SuppressWarnings("serial")
public class ClaimAuditPersistenceListener implements PostLoadEventListener,
// PreInsertEventListener,
        BeanFactoryAware {
    private static Logger logger = LogManager.getLogger(ClaimAuditPersistenceListener.class);

    private BeanFactory beanFactory;

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void setBeanFactory(BeanFactory arg0) throws BeansException {
        this.beanFactory = arg0;

    }

    // public boolean onPreInsert(PreInsertEvent preInsertEvent) {
    // Object entity = preInsertEvent.getEntity();
    // convertObjectToXML(entity);
    // return false;
    // }

    public void onPostLoad(PostLoadEvent postLoadEvent) {
        Object entity = postLoadEvent.getEntity();
//        if (entity instanceof ClaimAudit) {
//            convertXMLToObject(entity);
//        }
        if (entity instanceof Claim) {
            Claim claim = (Claim) postLoadEvent.getEntity();
            claim.setClaimXMLConverter(getClaimXMLConverter());
            claim.setSecurityHelper(getSecurityHelper());
            claim.setEventService(getEventService());
            claim.setOrgService(getOrgService());
        }
        
        if (entity instanceof RecoveryClaim) {
            RecoveryClaim claim = (RecoveryClaim) postLoadEvent.getEntity();
            claim.setOrgService(getOrgService());
        }
    	
    	// DO NOTHING. ONLY HAVE THIS LISTENER TO INJECT THE SECURITY HELPER AND THE CLAIM XML CONVERTER SERVICE.
    }

    // protected void convertObjectToXML(Object entity) {
    // if (entity instanceof ClaimAudit) {
    // ClaimAudit claimAudit = (ClaimAudit) entity;
    // if (claimAudit.getPreviousClaimSnapshot() != null) {
    // String toXML =
    // getClaimXMLConverter().convertObjectToXML(claimAudit.getPreviousClaimSnapshot());
    // claimAudit.setPreviousClaimSnapshotAsString(toXML);
    // }
    // }
    // }

    private SecurityHelper getSecurityHelper() {
        return (SecurityHelper) this.beanFactory.getBean("securityHelper");
    }

    protected void convertXMLToObject(Object entity) {
        ClaimAudit claimAudit = (ClaimAudit) entity;
        if (claimAudit.getPreviousClaimSnapshotAsString() != null) {
            Claim claim = (Claim) getClaimXMLConverter()
                    .convertXMLToObject(claimAudit.getPreviousClaimSnapshotAsString());
            claim.setPayment(claimAudit.getPayment());
            claim.setState(claimAudit.getPreviousState());
            claimAudit.setPreviousClaimSnapshot(claim);
        }
    }

    private ClaimXMLConverter getClaimXMLConverter() {
        return (ClaimXMLConverter) this.beanFactory.getBean("claimXMLConverter");
    }
    
    public EventService getEventService() {
		return (EventService) this.beanFactory.getBean("eventService");
	}
    
    public OrgService getOrgService() {
		return (OrgService) this.beanFactory.getBean("orgService");
	}

}
