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

package tavant.twms.domain.upload.staging;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jhulfikar.ali
 *
 */
public class StagingDAOFactory {

	private Map<String, StagingDAO> stagingDAOs=new HashMap<String, StagingDAO>();

	public Map<String, StagingDAO> getStagingDAOs() {
		return stagingDAOs;
	}

	public void setStagingDAOs(Map<String, StagingDAO> stagingDAOs) {
		this.stagingDAOs = stagingDAOs;
	}
	
	public StagingDAO getStagingDAO(String templateName){
		if(stagingDAOs.containsKey(templateName))
			return (StagingDAO) stagingDAOs.get(templateName);
		throw new RuntimeException("No StagingDAO found for template ["+templateName+"]");
	}
	
}
