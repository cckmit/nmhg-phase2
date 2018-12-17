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

package tavant.twms.domain.download;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jhulfikar.ali
 *
 */
public class WarrantyReportGeneratorFactory {

	private Map<String, WarrantyReportGenerator> userReportGenerators = new HashMap<String, WarrantyReportGenerator>();

	public WarrantyReportGenerator getUserReportGenerator(String userReportName)
	{
		if (userReportGenerators.containsKey(userReportName))
			return (WarrantyReportGenerator) userReportGenerators.get(userReportName);
		else
			throw new RuntimeException("No ReportGenerator is defined for the name : ["+ userReportName + "]");
	}

	public Map<String, WarrantyReportGenerator> getUserReportGenerators() {
		return userReportGenerators;
	}

	public void setUserReportGenerators(
			Map<String, WarrantyReportGenerator> userReportGenerators) {
		this.userReportGenerators = userReportGenerators;
	}
	
}
