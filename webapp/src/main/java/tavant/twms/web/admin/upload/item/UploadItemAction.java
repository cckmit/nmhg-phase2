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
package tavant.twms.web.admin.upload.item;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.upload.history.UploadHistory;
import tavant.twms.domain.upload.history.UploadHistoryService;
import tavant.twms.web.admin.dto.ItemDTO;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.xls.reader.ApplicationContextHolder;
import tavant.twms.web.xls.reader.ConversionResult;
import tavant.twms.web.xls.reader.Reader;
import tavant.twms.web.xls.reader.Utility;

/**
 * @author kaustubhshobhan.b
 * 
 */
@SuppressWarnings("serial")
public class UploadItemAction extends I18nActionSupport {

    private File input;

    private static String ERROR_FILE = "error_file.xls";

    private int successfulUpdates = 0;

    private List<ItemDTO> errorDTO = new ArrayList<ItemDTO>();

    private CatalogService catalogService;

    private static Logger logger = LogManager.getLogger(UploadItemAction.class);

    private UploadHistoryService uploadHistoryService;

    private final String type = "Item";

    public String setup() {
        return SUCCESS;
    }

    public String uploadItem() throws Exception {

        ServletContext ctx = (ServletContext) ServletActionContext.getContext().get(
                ServletActionContext.SERVLET_CONTEXT);
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(ctx);
        ApplicationContextHolder.getInstance().setContext(appCtx);
        InputStream xmlInput = this.getClass().getResourceAsStream("item-mapping.xml");
        Reader itemReader = Utility.getReaderFromXML(xmlInput);
        InputStream xlsInput = new BufferedInputStream(new FileInputStream(this.input));
        Map<String, List<ConversionResult>> inventoryMap = itemReader.read(xlsInput);
        List<ConversionResult> itemUploadList = inventoryMap.get("Sheet1");

        for (ConversionResult result : itemUploadList) {
            ItemDTO itemDTO = (ItemDTO) result.getResult();
            if (result.hasConversionErrors()) {
                createErrorDTO(itemDTO, result.getErrors());
            } else {
                createItem(itemDTO);
                this.successfulUpdates++;
            }
        }

        generateHistory();

        return SUCCESS;
    }

    private void createErrorDTO(ItemDTO itemDTO, List<String> errors) {
        String errorMsg = "";
        for (Iterator<String> iter = errors.iterator(); iter.hasNext();) {
            String errorMessage = iter.next();
            errorMsg = errorMsg + " " + errorMessage + " , ";
        }

        itemDTO.setError(errorMsg);
        this.errorDTO.add(itemDTO);

    }

    private void createItem(ItemDTO itemDTO) {
        Item item;
        boolean create = false;

        try {
            item = this.catalogService.findItemOwnedByManuf(itemDTO.getItemNumber());
        } catch (CatalogException e) {
            logger.error("Item not found");
            item = new Item();
            create = true;
        }

        createOrUpdateItem(itemDTO, item);

        if (create) {
            this.catalogService.createItem(item);
        } else {
            this.catalogService.updateItem(item);
        }
    }

    private void createOrUpdateItem(ItemDTO itemDTO, Item item) {
        item.setName(itemDTO.getDescription());
        item.setDescription(itemDTO.getDescription());
        if (itemDTO.getMake() != null) {
            item.setMake(itemDTO.getMake());
        } else {
            item.setMake("ASG");
        }
        if (itemDTO.getModel() != null) {
            item.setModel(this.catalogService.findProductOrModelWhoseNameIs(itemDTO.getModel()));
        }
        item.setNumber(itemDTO.getItemNumber());
        item.setProduct(itemDTO.getProductType());
        item.setOwnedBy(this.orgService.findOrganizationByName("OEM"));
        if (itemDTO.getIsSerialized().equalsIgnoreCase("yes")) {
            item.setSerialized(true);
        } else {
            item.setSerialized(false);
        }
        if (itemDTO.getHasUsageMeter().equalsIgnoreCase("yes")) {
            item.setUsageMeter(true);
        } else {
            item.setUsageMeter(false);
        }

    }

    private void generateHistory() {
        UploadHistory uploadHistory = new UploadHistory();
        try {
            uploadHistory.setDateOfUpload(new Date());
            uploadHistory.setErrorFile(createTempErrorFile(this.errorDTO));
            uploadHistory.setInputFile(createInputFile(this.input));
            if (this.errorDTO != null) {
                uploadHistory.setNumberOfErrorUploads(this.errorDTO.size());
            }
            uploadHistory.setNumberOfSuccessfulUploads(this.successfulUpdates);
            uploadHistory.setType("Item");
            this.uploadHistoryService.save(uploadHistory);
        } catch (Exception e) {
            logger.error("unable to create History file", e);
        }

    }

    private Blob createInputFile(File input2) {
        try {
            Blob inputFile = BlobProxy.generateProxy(FileCopyUtils.copyToByteArray(this.input));
            return inputFile;
        } catch (Exception e) {
            logger.error("Unable to create the Input File", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Blob createTempErrorFile(List<ItemDTO> errorDTO) {
        Map beans = new HashMap();
        beans.put("errorDTO", errorDTO);
        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(getErrorTemplatePath(), beans, ERROR_FILE);
            FileInputStream errorTempFile = new FileInputStream(new File(ERROR_FILE));
            Blob errorFile = BlobProxy.generateProxy(FileCopyUtils.copyToByteArray(errorTempFile));
            return errorFile;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Problem occured while generating error file ", e);
            return null;
        }
    }

    private String getErrorTemplatePath() throws Exception {
        return new ClassPathResource("tavant/twms/web/admin/upload/item/item-error-template.xls")
                .getURL().getPath();

    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public List<ItemDTO> getErrorDTO() {
        return this.errorDTO;
    }

    public void setErrorDTO(List<ItemDTO> errorDTO) {
        this.errorDTO = errorDTO;
    }

    public File getInput() {
        return this.input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public void setUploadHistoryService(UploadHistoryService uploadHistoryService) {
        this.uploadHistoryService = uploadHistoryService;
    }

    public String getType() {
        return this.type;
    }
}
