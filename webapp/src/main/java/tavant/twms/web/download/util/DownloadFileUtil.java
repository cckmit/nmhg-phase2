/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.download.util;


import net.sf.jxls.transformer.XLSTransformer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


/**
 * This is an util file designed to perform the Different operations to 
 * copy the Output result into a file
 * 
 * @author kaustubhshobhan.b
 *
 */
public class DownloadFileUtil {

	private static Logger logger = LogManager.getLogger(DownloadFileUtil.class);
	
	
	/**
	 * @param sourceStream JXLS template file
	 * @param resultStream The stream to which the result needs to be written 
	 * @param params The map with the list is to be passed
	 * 
	 * This function takes the inputstream which is the JXLS tempalte, a map which will be used in the excel
	 * mapping, and the output stream to which the result will be written. it creates an excel file 
	 * which is written to the output stream.
	 *  
	 */
	public void generateExcel(InputStream sourceStream, OutputStream resultStream,Map params){
		XLSTransformer transformer=new XLSTransformer();
		try{
			transformer.transformXLS(sourceStream, params)
						.write(resultStream);
		}catch (IOException e){
			logger.error("Could Not write the File", e);
		}
	}
}
