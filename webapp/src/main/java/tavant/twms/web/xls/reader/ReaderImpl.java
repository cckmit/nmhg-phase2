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

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */
public class ReaderImpl implements Reader {

    private static final Logger logger = Logger.getLogger(ReaderImpl.class);

    private final Map<String, SheetReader> sheetReaders = new HashMap<String, SheetReader>();

    public Map<String, List<ConversionResult>> read(InputStream xlsStream) {
        HSSFWorkbook workbook = createWorkbook(xlsStream);
        Map<String, List<ConversionResult>> result = new HashMap<String, List<ConversionResult>>();
        for (int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++) {
            HSSFSheet sheet = workbook.getSheetAt(sheetNo);
            String sheetName = workbook.getSheetName(sheetNo);
            if(logger.isDebugEnabled())
            {
                logger.debug("Trying to process Sheet[" + sheetName + "]");
            }
            if (this.sheetReaders.containsKey(sheetName)) {
                SheetReader sheetReader = this.sheetReaders.get(sheetName);
                result.put(sheetName, sheetReader.read(sheet));
                if(logger.isDebugEnabled())
                {
                    logger.debug("Finished processing Sheet[" + sheetName + "]");
                }
            } else {
                if(logger.isDebugEnabled())
                {
                    logger.debug("SheetReader not configured for Sheet[" + sheetName + "]");
                }
            }
        }
        return result;
    }

    private HSSFWorkbook createWorkbook(InputStream xlsStream) {
        try {
            return new HSSFWorkbook(new POIFSFileSystem(xlsStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSheetReader(SheetReader sheetReader) {
        this.sheetReaders.put(sheetReader.getSheetName(), sheetReader);
    }

    Map<String, SheetReader> getSheetReaders() {
        return this.sheetReaders;
    }
}
