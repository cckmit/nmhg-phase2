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
package tavant.twms.web.admin.upload.inventory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.upload.history.UploadHistory;
import tavant.twms.domain.upload.history.UploadHistoryService;
import tavant.twms.web.admin.dto.InventoryDTO;
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
public class UploadInventoryAction extends I18nActionSupport {

    private static Logger logger = LogManager.getLogger(UploadInventoryAction.class);

    private File input;

    private InventoryService inventoryService;

    private CatalogService catalogService;

    private int successfulUpdates = 0;

    private static String ERROR_FILE = "error_file.xls";

    private UploadHistoryService uploadHistoryService;

    List<InventoryDTO> errorDTO = new ArrayList<InventoryDTO>();

    private List<UploadHistory> uploadHistoryList = new ArrayList<UploadHistory>();

    private final String type = "Inventory";

    public String setup() {
        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {
        // below junk should go to an interceptor
        ServletContext ctx = (ServletContext) ServletActionContext.getContext().get(
                ServletActionContext.SERVLET_CONTEXT);
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(ctx);
        ApplicationContextHolder.getInstance().setContext(appCtx);

        InputStream xmlInput = this.getClass().getResourceAsStream("inventory-mapping.xml");
        Reader inventoryReader = Utility.getReaderFromXML(xmlInput);
        InputStream xlsInput = new BufferedInputStream(new FileInputStream(this.input));
        Map<String, List<ConversionResult>> inventoryMap = inventoryReader.read(xlsInput);
        List<ConversionResult> inventoryUploadList = inventoryMap.get("Sheet1");

        for (ConversionResult result : inventoryUploadList) {
            InventoryDTO inventoryDTO = (InventoryDTO) result.getResult();
            if (result.hasConversionErrors()) {
                createErrorDTO(inventoryDTO, result.getErrors());
            } else {

                saveInventoryItem(inventoryDTO);
            }
        }
        this.successfulUpdates = inventoryUploadList.size() - this.errorDTO.size();
        generateHistory();

        return SUCCESS;
    }

    private void createErrorDTO(InventoryDTO inventoryDTO, List<String> errors) {
        String errorMsg = "";
        for (Iterator<String> iter = errors.iterator(); iter.hasNext();) {
            String errorMessage = iter.next();
            errorMsg = errorMsg + " " + errorMessage + " , ";
        }
        inventoryDTO.setErrorItemNumber(inventoryDTO.getItemNumber().getNumber());
        inventoryDTO.setItemNumber(null);
        inventoryDTO.setError(errorMsg);
        this.errorDTO.add(inventoryDTO);

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
            uploadHistory.setType("Inventory");
            this.uploadHistoryService.save(uploadHistory);
        } catch (Exception e) {
            logger.error("unable to create History file", e);
        }
    }

    private void saveOrUpdateItem(InventoryDTO dto, InventoryItem invItem) {
        invItem.setSerialNumber(dto.getSerialNumber().toString());
        invItem.setOfType(dto.getItemNumber());
        invItem.setType(new InventoryType("STOCK"));
        invItem.setConditionType(new InventoryItemCondition("NEW"));

        invItem.setHoursOnMachine(dto.getUsage().longValue());
        invItem.setShipmentDate(dto.getDateOfShipment());
        invItem.getTransactionHistory().add(initializeTranaction(invItem, dto));

    }

    private InventoryTransaction initializeTranaction(InventoryItem invItem, InventoryDTO invDTO) {
        InventoryTransaction invTx = new InventoryTransaction();
        invTx.setSeller(this.orgService.findOrganizationByName("OEM"));
        invTx.setBuyer(invDTO.getDealer());
        invTx.setTransactionDate(invDTO.getDateOfShipment());
        invTx.setTransactedItem(invItem);
        return invTx;
    }

    @SuppressWarnings("unchecked")
    protected Blob createTempErrorFile(List<InventoryDTO> invErrorDTO) throws IOException {
        Map beans = new HashMap();
        beans.put("invErrorDTO", invErrorDTO);
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

    private String getErrorTemplatePath() throws IOException {
        return new ClassPathResource(
                "tavant/twms/web/admin/upload/inventory/inventory-error-template.xls").getURL()
                .getPath();
    }

    public File getInput() {
        return this.input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public CatalogService getCatalogService() {
        return this.catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public int getSuccessfulUpdates() {
        return this.successfulUpdates;
    }

    protected Blob createInputFile(File input) {
        try {
            return BlobProxy.generateProxy(FileCopyUtils.copyToByteArray(input));
        } catch (Exception e) {
            return null;
        }
    }

    private void saveInventoryItem(InventoryDTO dto) {
        InventoryItem invItem;
        boolean create = false;
        try {
            invItem = this.inventoryService.findSerializedItem(dto.getSerialNumber().toString());
        } catch (ItemNotFoundException e) {
            invItem = new InventoryItem();
            create = true;
        }
        if (!checkIfRetailed(invItem, create)) {
            saveOrUpdateItem(dto, invItem);
            if (create) {
                this.inventoryService.createInventoryItem(invItem);
            } else {
                this.inventoryService.updateInventoryItem(invItem);
            }
        } else {
            dto.setError(getText("error.manageData.updateFail"));
            this.errorDTO.add(dto);
        }
    }

    private boolean checkIfRetailed(InventoryItem invItem, boolean create) {
        if (create = false) {
            if (invItem.getType().getType().equalsIgnoreCase("RETAIL")) {
                ;
            }
            {
                return true;
            }
        }
        return false;
    }

    public void setUploadHistoryService(UploadHistoryService uploadHistoryService) {
        this.uploadHistoryService = uploadHistoryService;
    }

    public List<UploadHistory> getUploadHistoryList() {
        return this.uploadHistoryList;
    }

    public void setUploadHistoryList(List<UploadHistory> uploadHistoryList) {
        this.uploadHistoryList = uploadHistoryList;
    }

    public String getType() {
        return this.type;
    }

}
