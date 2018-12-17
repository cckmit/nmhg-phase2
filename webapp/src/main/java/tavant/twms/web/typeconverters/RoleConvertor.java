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
package tavant.twms.web.typeconverters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.ReflectionUtil;
import tavant.twms.jbpm.infra.BeanLocator;


public class RoleConvertor extends NamedDomainObjectConverter<OrgService, Role> {
    public RoleConvertor() {
        super("orgService");
    }
    
    private static final Logger logger = Logger.getLogger(RoleConvertor.class);
    
    private DomainRepository domainRepository;
    
    //The UI can populate this, when it explicitly wants a null to be shown
    public static final String NULL = "null";
    
    @Override
    public Object convertValue(Map ctx, Object value, Class toType) {
        if(this.domainRepository == null) {
            initDomainRepository();
        }
        Assert.notNull(this.domainRepository, "Domain repository is null");
        if(toType == String.class) {
            if(logger.isDebugEnabled())
            {
                logger.debug("Attempting to convert [" + value + "] to string will execute getId() method on it");
            }
            try {
                Object output = ReflectionUtil.executeMethod(value, "getId", new Object[]{});
                return output == null ? "" : output.toString();
            } catch(RuntimeException e) {
                throw new RuntimeException("The method getId() is not found for object [" + value + "]");
            }
        } else {
            return convertToNonStringType(value, toType);
        }
    }
    
    protected Object convertToNonStringType(Object value, Class toType) {
        if(logger.isDebugEnabled()) {
            logger.debug("Attemping to convert to class [" + toType + "]");
        }

        String id;

        if (value instanceof String[]) {
            id = ((String[]) value)[0];
        } else if (value instanceof String) {
            id = (String) value;
        } else {
            id = value.toString();
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Id obtained is [" + id + "]");
        }

        //sometimes a NULL is explicitly needed by the UI
        if (NULL.equals(id)) {
            return null;
        } else if (StringUtils.hasText(id)) {
            try{
            	return getDomainRepository().load(toType, new Long(id));
            }catch(NumberFormatException e){
            	try {
					return fetchByName(id.toString());
				} catch (Exception ex) {
	                logger.error("Error fetching entity by name '" + id.toString() + "'", ex);
	                return null;
	            }
            }
        } else {
            if(logger.isDebugEnabled()) {
                logger.debug("Attempting to instantiate the new domain class [" + toType + "]");
            }

            return ReflectionUtil.createNewInstance(toType.getName());
        }
    }

    public Role fetchByName(String name) throws Exception {
        if (StringUtils.hasText(name)) {
            return getService().findRoleByName(name);
        } else {
            return null;
        }
    }

    public String getName(Role entity) throws Exception {
        return entity.getName();
    }

    @Override
    public Collection<Role> fetchByNames(String[] roleNameArray)  {
        if (roleNameArray != null && roleNameArray.length > 0) {
            Collection<Role> roles = new ArrayList<Role>();
            for (int i = 0; i < roleNameArray.length; i++) {
                if (roleNameArray[i] != null && StringUtils.hasLength(roleNameArray[i])) {
                    roles.add(getService().findRoleByName(roleNameArray[i]));
                }
            }
            return roles;
        }
        return null;
    }
    
    private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.domainRepository = (DomainRepository) beanLocator.lookupBean("domainRepository");
    }
    
    public DomainRepository getDomainRepository() {
        if (domainRepository == null) {
            initDomainRepository();
        }

        return domainRepository;
    }
}
