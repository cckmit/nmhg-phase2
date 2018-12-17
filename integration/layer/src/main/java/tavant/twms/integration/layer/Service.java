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
 *
 */package tavant.twms.integration.layer;

/**
 *
 * @author prasad.r
 */
public interface Service {
    
    /**
     * 
     * @param entity - an entity object 
     * @param input - input object which might be needed for updating entity at the later stage
     * @throws ServiceException 
     */
    public void createOrUpdate(Object entity, Object input) throws ServiceException;
    
}
