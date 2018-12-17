package tavant.twms.web.upload;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : janmejay.singh
 *         Date : Jun 20, 2007
 *         Time : 6:01:39 PM
 */
public class HeaderUtil {

    public static final String EXCEL = "excel/ms-excel",
                               HTML = "text/html",
                               PDF = "application/pdf",
                               VND_EXCEL = "application/vnd.ms-excel",
                               ZIP = "application/zip", 
                               PLAIN = "text/plain",
                               CSV ="text/csv";

    public static final void setHeader(HttpServletResponse response, String fileName, String mimeType) {
        response.setContentType(mimeType);
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
    }
}
