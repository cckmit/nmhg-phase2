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
public class PostUplaodActionFactory {

	private Map<String, PostUploadAction> transformPostUploadActions = new HashMap<String, PostUploadAction>();
	
	public Map<String, PostUploadAction> getTransformPostUploadActions() {
		return this.transformPostUploadActions;
	}

	public void setTransformPostUploadActions(
			Map<String, PostUploadAction> transformPostUploadActions) {
		this.transformPostUploadActions = transformPostUploadActions;
	}

	static {
		//transformPostUploadActions.put("", new WntyCoveragePostUploadAction());
	}
	
	public PostUploadAction getPostUploadAction(String templateName){
		return transformPostUploadActions.get(templateName);
	}

}
