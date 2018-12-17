/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.layer;

import java.util.Map;

/**
 *
 * @author prasad.r
 */
public interface Validator {
    
    /**
     * 
     * @param o -  an object to be validated
     * @return 
     */
    public void validate(Object o,Object dtoObject, final Map<String,String> errorMessageCodes);
    
}
