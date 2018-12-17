/**
 * Copyright (c)2007 Tavant Technologies
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
package tavant.twms.web.admin.upload.convertor;

import org.apache.log4j.Logger;
import tavant.twms.web.xls.reader.ConversionErrors;
import tavant.twms.web.xls.reader.Convertor;

/**
 * @author kaustubhshobhan.b
 * Jul 9, 2007
 */
public class NonOEMConvertor implements Convertor {

  private static final Logger logger = Logger.getLogger(NonOEMConvertor.class);
  /* (non-Javadoc)
   * @see tavant.twms.web.xls.reader.Convertor#convert(java.lang.Object)
   */
  public Object convert(Object object) {

    throw new UnsupportedOperationException();
  }


  public Object convertWithDependency(Object object,
      Object dependency) {

    String number = (String)object;
    Integer noOfUnits = (Integer)dependency;

    if(logger.isDebugEnabled())
    {
        logger.debug("Value of Non oem parts is " + number);
    }
    if((noOfUnits != null)|| (noOfUnits.intValue()>0)){
      return number;
    }else{
      ConversionErrors.getInstance().addError("Number of units should be specified for part [" + number + "[");
      return number;
    }
  }

}
