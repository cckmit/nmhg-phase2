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
package tavant.twms.web.xls.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author kaustubhshobhan.b
 * 
 */
public interface XLSWriter {

    // I am given a stream to which i try to write TO and if I can't, I just let
    // the caller handle the Exception, since I don't have control on this stream.
    public void write(List<List<XLSCell>> tableData, OutputStream outputStream) throws IOException;

}
