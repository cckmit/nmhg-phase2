package tavant.twms.web.upload;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import static tavant.twms.web.upload.HeaderUtil.EXCEL;
import static tavant.twms.web.upload.HeaderUtil.setHeader;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DownloadTemplate implements ServletResponseAware {

    private static String CLAIMS_WITH_ERROR_FILENAME = "Error_Claims.xls";

    private static Map<String, String> fileName = new HashMap<String, String>();

    private static Logger logger = LogManager.getLogger(DownloadTemplate.class);

    private HttpServletResponse response;

    private String type;

    public void downloadClaimsWithError() {
        setHeader(response, CLAIMS_WITH_ERROR_FILENAME, EXCEL);
        try {
            FileInputStream errorFile = new FileInputStream( new File("claimsWithError.xls"));
            FileCopyUtils.copy(errorFile,response.getOutputStream());
        } catch (IOException e) {
            logger.error("Failed to write file",e);
        }
    }

    public void downloadTemplateFile() {
        setFileName();
        setHeader(response, "Upload_" + type +"_Template.xls", EXCEL);
        ClassPathResource cpr = new ClassPathResource(fileName.get(type));
        try {
            FileCopyUtils.copy(cpr.getInputStream(), response.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("Failed to write file to output stream",e);
        }
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Map<String, String> getFileName() {
        return fileName;
    }

    public static void setFileName() {
        fileName.put("claim", "tavant/twms/web/claim/upload/claimTemplate.xls");
        fileName.put("recoveryClaim", "tavant/twms/web/claim/upload/recClaimTemplate.xls");
        fileName.put("inventory", "tavant/twms/web/admin/upload/inventory/inventoryupload-template.xls");
        fileName.put("itemprice","tavant/twms/web/admin/upload/itemprice/item-prices-upload-template.xls");
        fileName.put("item","tavant/twms/web/admin/upload/item/item-upload-template.xls");
        fileName.put("itemgroup",
                "tavant/twms/web/admin/upload/productstructure/product-structure-upload-template.xls");
    }


}
