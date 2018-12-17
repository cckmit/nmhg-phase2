package tavant.twms.web.security.authz.util;

import java.util.Collection;

import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.ConfigAttributeEditor;
import org.acegisecurity.intercept.method.MethodDefinitionMap;
import org.acegisecurity.intercept.method.MethodDefinitionSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


import tavant.twms.security.authz.resource.ResourceManager;
import tavant.twms.security.vo.Resource;




/**
* Class used for Method level security. Takes all the methods which needs to be secured
* from a configuration file and populates them as ConfigAttributues, which are passed to voter
* class. Voter class will decide whether the user has permission to access that method or not.   
* 
*/
public class SecuredMethodFactory implements InitializingBean,FactoryBean {

	  private boolean singleton = true;
	  private Object singletonInstance;
      private ResourceManager resourceManager;
      private static final String RESOURCE_TYPE_METHOD = "method";
      private static final String SEPARATOR_CONSTANT = ",";

    /**
     * Setter Injection
     * @param resourceManager
     */
	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	/**
	 * Setter Injection
	 * @param singleton
	 */
	public final void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	/**
	 * Implementation method
	 * @throws Exception
	 */
	public final void afterPropertiesSet() throws Exception {
	    if (this.singleton) {
	        this.singletonInstance = createInstance();
	      }
	}

	/**
	 * Method which populates the ObjectDefinitionSource for
	 * MethodSecurityInterceptor
	 * @return Object
	 */
	protected Object createInstance() {
	   MethodDefinitionMap source = new MethodDefinitionMap();
	   ConfigAttributeEditor configAttribEd = new ConfigAttributeEditor();
	   // Fetch Resources of method type from ResourceManager
	   Collection<Resource> resources = resourceManager.getResourcesForType(RESOURCE_TYPE_METHOD);
	   
	   if(null != resources && !resources.isEmpty()) {
		  for(Resource resource:resources) {
			  String name = resource.getResourceName();
			  // Fetch Permission list for each resource
			  Collection<String> permissionList = resource.getPermissionList();
              StringBuffer permissionsBuff = new StringBuffer();
              // Make permissions as a comma separated list
			  if(null != permissionList && !permissionList.isEmpty()) {
				 for(String permission:permissionList) {
					 permissionsBuff.append(permission);
					 permissionsBuff.append(SEPARATOR_CONSTANT);
				 }
			  }
			  String value = permissionsBuff.substring(0, permissionsBuff.toString().lastIndexOf(SEPARATOR_CONSTANT));
			  // Convert value to series of security configuration attributes
			  configAttribEd.setAsText(value);
			  ConfigAttributeDefinition attr = (ConfigAttributeDefinition) configAttribEd.getValue();
			  // Register method name and attribute
			  source.addSecureMethod(name, attr);
		  }
	   }
	   return source;
	}

	/**
	 * Implementation method
	 * @throws Exception
	 * @return Object
	 */
	public final Object getObject() throws Exception {
	    if (this.singleton) {
	        return this.singletonInstance;
	      }
	      else {
	        return createInstance();
	      }
	}

	/**
	 * Implementation method
	 * @return Class
	 */
	public Class getObjectType() {
		return MethodDefinitionSource.class;
	}

	/**
	 * Implementation method
	 * @return boolean
	 */
	public final boolean isSingleton() {
		return singleton;
	}

}
