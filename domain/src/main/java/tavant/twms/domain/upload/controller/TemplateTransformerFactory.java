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
 */

package tavant.twms.domain.upload.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jhulfikar.ali
 *
 */
public class TemplateTransformerFactory {

	private Map<String, TemplateTransformer> templateWiseTransformers=new HashMap<String, TemplateTransformer>();

	public Map<String, TemplateTransformer> getTemplateWiseTransformers() {
		return templateWiseTransformers;
	}

	public void setTemplateWiseTransformers(
			Map<String, TemplateTransformer> templateWiseTransformers) {
		this.templateWiseTransformers = templateWiseTransformers;
	}
	
	public TemplateTransformer getTemplateTransformer(String templateName){
		if(templateWiseTransformers.containsKey(templateName))
			return (TemplateTransformer)templateWiseTransformers.get(templateName);
		else
			return null;
	}

	public TemplateTransformer getGenericTemplateTransformer(String stagingTable) {
		return new GenericTemplateTransformer(stagingTable);
	}
	
}
