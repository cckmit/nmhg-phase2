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

import org.jbpm.graph.exe.Token;

/**
 * @author vineeth.varghese
 * @date Dec 14, 2006
 */
public class RepeatContext {
    
    private Object objectForRepeat;    

    public RepeatContext(Object objectForRepeat) {
        this.objectForRepeat = objectForRepeat;
    }
    
    /**
     * @return the objectForRepeat
     */
    public Object getObjectForRepeat() {
        return objectForRepeat;
    }
    
    public void conditionToken(Token token) {
        //No-op
    }

}
