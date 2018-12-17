package tavant.twms.web.security.authz.resource.mapping;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;


import tavant.twms.security.context.ResourceContextBuilder;
import tavant.twms.security.policy.PolicyExecutor;
import tavant.twms.security.vo.Resource;
import tavant.twms.xmlbeans.security.impl.FunctionalareasDocumentDTO;
import tavant.twms.xmlbeans.security.impl.ResourceDocumentDTO;
import tavant.twms.xmlbeans.security.impl.FunctionalareaDocumentDTO.Functionalarea;




public class ResourceMapParser implements ApplicationContextAware, InitializingBean, FactoryBean {
	private Map<String, Map<String, Resource>> functionalAreaResourcesMap = new HashMap<String, Map<String, Resource>>();
	private Map<String, String> resourceFunctionalArea = new HashMap<String, String>();
	private ApplicationContext appContext;
	private org.springframework.core.io.Resource[] securityConfigs;
	private static final String SEPARATOR_CONSTANT = ",";

	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		this.appContext = appContext;
	}

	/**
	 * Spring life cycle method
	 * 
	 * @throws IOException
	 *             for resources read
	 * @throws XmlException
	 *             for parsing
	 */
	public void afterPropertiesSet() throws IOException, XmlException {
		functionalAreaResourcesMap.clear();
		resourceFunctionalArea.clear();
		Assert.notEmpty(securityConfigs, "No Security Mapping file has been defined");
		parseMap();
	}

	/**
	 * Method to parse the config files
	 * 
	 * @throws IOException
	 *             for resources read
	 * @throws XmlException
	 *             for parsing
	 */
	private void parseMap() throws IOException, XmlException {
		FunctionalareasDocumentDTO functAreasDoc;
		for (org.springframework.core.io.Resource resourceMap : securityConfigs) {
			functAreasDoc=  FunctionalareasDocumentDTO.Factory.parse(resourceMap.getInputStream());					
			
			
				Map<String, Resource> resourceList = new HashMap<String, Resource>();
				for (Functionalarea funcArea : functAreasDoc.getFunctionalareas().getFunctionalareaArray()) {
					
				
			    ResourceDocumentDTO.Resource[] resourceArr = funcArea.getResourceArray();
				
				for (ResourceDocumentDTO.Resource resource : resourceArr) {
					String policyBean = null;
					resourceFunctionalArea.put(resource.getValue().trim(), funcArea.getName());
					Resource funcAreaResource = new Resource();
					Collection<String> permissionColl = new ArrayList<String>();
					funcAreaResource.setDefaultPolicy(funcArea.getDefaultpolicy());
					funcAreaResource.setFunctionalArea(funcArea.getName());
					if (null != resource.getPermissions() && resource.getPermissions().length() > 0) {
						permissionColl = getFormattedCollection(resource.getPermissions());
					}
					funcAreaResource.setPermissionList(permissionColl);
					// Gets the Policy
					if (resource.getPolicy() != null) {
						funcAreaResource.setPolicy(resource.getPolicy().toString());
						if ("defined".equals(resource.getPolicy().toString())) {
							policyBean = resource.getPolicyBean();
						} else {
							if ("default".equals(resource.getPolicy().toString())) {
								policyBean = funcArea.getDefaultpolicy();
							}
						}
					}
					if (policyBean != null) {
						funcAreaResource.setPolicyExecutor((PolicyExecutor) appContext.getBean(policyBean));
					}
					// Gets the Context.
					String contextBuilderName = null;
					if (resource.getContextbuilder() != null) {
						String context = resource.getPolicy().toString();
						if (context != null) {
							funcAreaResource.setContext(context);
							if (context.equals("defined")) {
								contextBuilderName = resource.getContextBuilderBean();
							} else if (resource.equals("default")) {
								context = funcArea.getDefaultcontextbuilder();
							}
						}
					}
					// Gets the ResourceContext Bean from the Spring context and
					// sets it on to the Resource.
					if (contextBuilderName != null) {
						funcAreaResource.setContextBuilder((ResourceContextBuilder) appContext.getBean(contextBuilderName));
					}
					funcAreaResource.setResourceName(resource.getValue());
					funcAreaResource.setResourceType(resource.getType().toString());
					if (resource.getOperation() != null && "OR".equals(resource.getOperation().toString())) {
						funcAreaResource.setLogicalOR(Boolean.TRUE);
					} else {
						funcAreaResource.setLogicalOR(Boolean.FALSE);
					}
					if (resource.getPrefix() != null) {
						funcAreaResource.setPrefix(resource.getPrefix().toString());
					} else {
						funcAreaResource.setPrefix(null);
					}
					resourceList.put(resource.getValue(), funcAreaResource);
				
				}
				functionalAreaResourcesMap.put(funcArea.getName(), resourceList);
			}
		}
	}

	/**
	 * Takes comma separated String as input and returns a collection of
	 * individual string objects.
	 * 
	 * @param object
	 *            data
	 * @return Collection
	 */
	private Collection<String> getFormattedCollection(String object) {
		Collection<String> objColl = new ArrayList<String>();
		if (object.indexOf(SEPARATOR_CONSTANT) != -1) {
			String[] objArr = object.split(SEPARATOR_CONSTANT);
			for (String obj : objArr) {
				objColl.add(obj.trim());
			}
		} else if (object.length() > 0) {
			objColl.add(object.trim());
		}
		return objColl;
	}

	public Object getObject() throws Exception {
		List<Map> list = new ArrayList<Map>();
		list.add(functionalAreaResourcesMap);
		list.add(resourceFunctionalArea);
		return list;
	}

	public Class getObjectType() {
		return Map.class;
	}

	public boolean isSingleton() {
		return Boolean.TRUE;
	}

	public void setSecurityConfigs(org.springframework.core.io.Resource[] securityConfigs) {
		this.securityConfigs = securityConfigs;
	}
}
