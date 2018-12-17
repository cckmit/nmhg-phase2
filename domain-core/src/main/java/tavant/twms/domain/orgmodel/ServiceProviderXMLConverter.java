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

package tavant.twms.domain.orgmodel;


import tavant.twms.infra.HibernateCast;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
/**
 * Responsible for converting dealership object to xml and vice-versa.
 * 
 * @author devesh.raghubanshi
 * 
 */
public class ServiceProviderXMLConverter implements Converter{
	OrgService orgService;	

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if(source!=null)
		{
			ServiceProvider serviceProvider =new HibernateCast<ServiceProvider>().cast(source);
			String value=serviceProvider.getId().toString();
			if(value!=null)
			{
				writer.startNode("id");
				writer.setValue(value);
				writer.endNode();
			}
		}
		
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		reader.moveDown();
		String value=reader.getValue();
		reader.moveUp();
		ServiceProvider serviceProvider = null;
		if (value!=null) {
			serviceProvider = orgService.findServiceProviderById(Long.parseLong(value));
		}
		return serviceProvider;
	}

	public boolean canConvert(Class type) {
		return ServiceProvider.class.equals(type);
	}
}
