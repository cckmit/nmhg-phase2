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

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Update listener that is responsible for adding a new ClaimAudit entry for
 * every business state change of claim object.
 * @author roopali.agrawal
 * 
 */
public class ClaimUpdateListener implements PreUpdateEventListener, BeanFactoryAware {
    BeanFactory beanFactory;

    public void setBeanFactory(BeanFactory arg0) throws BeansException {
        this.beanFactory = arg0;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public ClaimService getClaimService() {
        return (ClaimService) this.beanFactory.getBean("claimService");
    }

    public boolean onPreUpdate(PreUpdateEvent event) {
        if (Claim.class.isAssignableFrom(event.getEntity().getClass())) {
            Session tempSession = null;
            try {
                int stateIndex = -1;
                int i = 0;
                String[] props = event.getPersister().getPropertyNames();
                for (String prop : props) {
                    if ("state".equals(prop)) {
                        stateIndex = i;
                        break;
                    }
                    i++;
                }
                if (stateIndex == -1)
                    return false;
                SessionFactory factory = event.getPersister().getFactory();
                Connection conn = event.getSource().connection();
                tempSession = factory.openSession(conn);

                Object[] currentState = event.getState();
                Object[] oldState = event.getOldState();
                if (oldState[stateIndex] == null) {
                    return false;
                }
                String oldValueForClaimState = ((ClaimState) oldState[stateIndex]).getState();
                String currentValueForClaimState = ((ClaimState) currentState[stateIndex])
                        .getState();
                Claim claim = (Claim) event.getEntity();
                if (!oldValueForClaimState.equals(currentValueForClaimState)) {
                    // this is a workaround for "hibernate assertion failure
                    // errors :collection was not processed by flush"
                    // using claim object directly causes above assertion
                    // failure.
                    Object lastStoredClaim = tempSession.load(event.getEntity().getClass(), claim
                            .getId());
                    // todo-get rid of hard coded value for the boolean param.
                    getClaimService().createClaimAudit(lastStoredClaim, true);
                    tempSession.flush();
                }

            } finally {
                if (tempSession != null) {
                    tempSession.close();
                }
            }
        }
        return false;
    }

}
