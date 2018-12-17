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

package tavant.twms.domain.upload.errormgt;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jhulfikar.ali
 *
 */
public class ErrorReportGeneratorFactory {

	private Map<String, ErrorReportGenerator> templateWiseErrorReportGenerators = new HashMap<String, ErrorReportGenerator>();

	private ErrorReportGenerator defaultErrorReportGenerator;
	
	public ErrorReportGenerator getDefaultErrorReportGenerator() {
		return defaultErrorReportGenerator;
	}

	public void setDefaultErrorReportGenerator(
			ErrorReportGenerator defaultErrorReportGenerator) {
		this.defaultErrorReportGenerator = defaultErrorReportGenerator;
	}

	public ErrorReportGenerator getErrorReportGenerator(String templateName) {
		if (templateWiseErrorReportGenerators.containsKey(templateName))
			return (ErrorReportGenerator) templateWiseErrorReportGenerators
					.get(templateName);
		else
			throw new RuntimeException(
					"No ErrorReportGenerator is defined for template["
							+ templateName + "]");
	}

	public ErrorReportGenerator getErrorReportGenerator() {
			return defaultErrorReportGenerator;
	}

	public Map<String, ErrorReportGenerator> getTemplateWiseErrorReportGenerators() {
		return templateWiseErrorReportGenerators;
	}

	public void setTemplateWiseErrorReportGenerators(
			Map<String, ErrorReportGenerator> templateWiseErrorReportGenerators) {
		this.templateWiseErrorReportGenerators = templateWiseErrorReportGenerators;
	}
	
}
