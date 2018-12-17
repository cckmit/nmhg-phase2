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
package tavant.twms.web.common.barcode;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.env.EnvironmentFactory;
import net.sourceforge.barbecue.output.OutputException;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import tavant.twms.web.actions.TwmsActionSupport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Uses the barbecue library to generate a barcode image Invoke with the
 * following url parameres (look into the xwork.xml for the actual url) 
 * data - string to be barcoded 
 * width - width of each bar 
 * height - height of the bar
 * resolution - resultion in dpi
 * 
 * @author kamal.govindraj
 * 
 */
public class BarcodeGenerator extends TwmsActionSupport implements ServletResponseAware {

    private static final Logger logger = Logger.getLogger(BarcodeGenerator.class);

    private String data;

    private Integer width;

    private Integer height;

    private Integer resolution;

    private boolean checksum = false;

    private boolean headless = false;

    private boolean drawText = true;

    private HttpServletResponse response;

    public String generate() {

        Barcode barCode = createBarCode();
        response.setContentType("image/png");
        try {
            writeBarcodeImage(barCode, response.getOutputStream());
        } catch (IOException e) {
            logger.error("Error getting output stream from response", e);
            throw new RuntimeException("Error writing image to output stream", e);
        }

        return null;
    }

    Barcode createBarCode() {
        if (headless) {
            EnvironmentFactory.setHeadlessMode();
            drawText = false;
        }

        Barcode barCode = getBarCode(data, checksum);
        barCode.setDrawingText(drawText);
        if (width != null) {
            barCode.setBarWidth(width.intValue());
        }

        if (height != null) {
            barCode.setBarWidth(height.intValue());
        }

        if (resolution != null) {
            barCode.setResolution(resolution.intValue());
        }
        return barCode;
    }

    void writeBarcodeImage(Barcode barCode, OutputStream out) {
        try {
            BarcodeImageHandler.writePNG(barCode, out);
        } catch (OutputException e) {
            logger.error("Error writing image to output stream", e);
            throw new RuntimeException("Error writing image to output stream", e);
        }
    }

    Barcode getBarCode(String data, boolean checksum) {
        try {
            return BarcodeFactory.createCode128B(data);
        } catch (BarcodeException e) {
            logger.error("Error creating Barcode for data = " + data, e);
            throw new RuntimeException("Error creating Barcode for data = " + data, e);
        }
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

}
