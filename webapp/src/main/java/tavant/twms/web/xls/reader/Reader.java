/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.xls.reader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */

// TODO : How do you account for errored sheets?.
public interface Reader {

    /**
     * Parses the Excel sheet provided and returns the object created
     * 
     * @param xlsStream
     * @return Map of sheet name to list of objects from that sheet.
     */
    public Map<String, List<ConversionResult>> read(InputStream xlsStream);

    public void addSheetReader(SheetReader sheetReader);

}
