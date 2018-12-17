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
package tavant.twms.jbpm.nodes.fork;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.Token;

import tavant.twms.domain.claim.OEMPartReplaced;

public class PartsReturnBasedRepetitionCriteria implements RepetitionCriteria {

    private static final Logger logger = Logger.getLogger(PartsReturnBasedRepetitionCriteria.class);

    @SuppressWarnings("unchecked")
    public Collection<RepeatContext> evaluate(Object input) {
        Collection<RepeatContext> repeatContexts = new ArrayList<RepeatContext>();
        Collection<OEMPartReplaced> parts = (Collection<OEMPartReplaced>)input;
        for (OEMPartReplaced part : parts) {
        	int quantity=part.getNumberOfUnits();
        	if (part.isPartToBeReturned()) {
        	    if(logger.isDebugEnabled())
        	    {
        	        logger.debug("Part[" + part.getId()+ "] is to be replaced");
        	    }
                RepeatContext repeatContext = new RepeatContext(part) {
                    @Override
                    public void conditionToken(Token token) {
                        OEMPartReplaced part = (OEMPartReplaced)getObjectForRepeat();
                        boolean shouldParentFlowWaitForPart = (part.getPartReturns().get(0)!= null) &&
                            !("PAY".equals(part.getPartReturns().get(0).getPaymentCondition().getCode()));
                        if (shouldParentFlowWaitForPart) {
                            if(logger.isDebugEnabled())
                            {
                                logger.debug("Parent Workflow has to wait for Part["
                                        + part.getId() + "]");
                            }
                        }
                        token.setAbleToReactivateParent(shouldParentFlowWaitForPart);
                    }
                };
                repeatContexts.add(repeatContext);
            }
          }
        return repeatContexts;
    }

}
