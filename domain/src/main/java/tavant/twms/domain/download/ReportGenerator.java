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

import javax.mail.MethodNotSupportedException;

/**
 * @author jhulfikar.ali
 *
 */
public interface ReportGenerator {

	public String getProjectionClause(String delimiter) throws MethodNotSupportedException;
	
	public String prepareWhereClause(ReportSearchBean reportSearchBean) throws MethodNotSupportedException;
	
	/*
	 * Use this API when you are having parameters list in Array format 
	 */
	public String setParametersList() throws MethodNotSupportedException;
	
}
