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
package tavant.twms.web.admin.upload.itemprice;

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
import tavant.twms.domain.catalog.ItemBasePrice;
import tavant.twms.domain.catalog.ItemBasePriceService;
import tavant.twms.domain.claim.payment.rates.ItemPriceAdminService;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.upload.history.UploadHistory;
import tavant.twms.domain.upload.history.UploadHistoryService;
import tavant.twms.web.admin.dto.ItemPriceDTO;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.xls.reader.ApplicationContextHolder;
import tavant.twms.web.xls.reader.ConversionResult;
import tavant.twms.web.xls.reader.Reader;
import tavant.twms.web.xls.reader.Utility;

import javax.servlet.ServletContext;
import java.io.*;
import java.sql.Blob;
import java.util.*;

/**
 * @author kaustubhshobhan.b
 *
 */
@SuppressWarnings("serial")
public class UploadItemPriceAction extends I18nActionSupport {

    private static Logger logger = LogManager.getLogger(UploadItemPriceAction.class);

    private File input;

    private int successfulUpdates = 0;

    private static String ERROR_FILE="error_file.xls";

    private UploadHistoryService uploadHistoryService;

    private List<ItemPriceDTO> errorDTO = new ArrayList<ItemPriceDTO>();

    private List<UploadHistory> uploadHistoryList = new ArrayList<UploadHistory>();

    private ItemBasePriceService itemBasePriceService;
    
    private String type = "Itemprice";
    
    public String setup(){
    	return SUCCESS;
    }

    public String uploadItemPrice()throws Exception{

        ServletContext ctx = (ServletContext)ServletActionContext.getContext().get(ServletActionContext.SERVLET_CONTEXT);
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(ctx);
        ApplicationContextHolder.getInstance().setContext(appCtx);

        InputStream xmlInput = this.getClass().getResourceAsStream("ItemPrice-mapping.xml");
        Reader itemPriceReader = Utility.getReaderFromXML(xmlInput);
        InputStream xlsInput = new BufferedInputStream(new FileInputStream(input));
        Map<String, List<ConversionResult>>itemPriceMap = itemPriceReader.read(xlsInput);
        List<ConversionResult> itemPriceUploadList = itemPriceMap.get("Sheet1");

        for (ConversionResult result : itemPriceUploadList) {
            ItemPriceDTO itemPriceDTO =(ItemPriceDTO)result.getResult();
            if(result.hasConversionErrors()){
                createErrorDTO(itemPriceDTO,result.getErrors());
            }
            else{
                if(itemPriceDTO.getFromDate().isBefore(itemPriceDTO.getTillDate())){
                    result.getErrors().add(getText("error.manageData.invalidDate"));
                    createErrorDTO(itemPriceDTO, result.getErrors());
                }else{
                    ItemBasePrice itemBasePrice=itemBasePriceService.findByItem(itemPriceDTO.getItem());
                    updateItemPrice(itemBasePrice,itemPriceDTO);
                }
            }
        }

        generateHistory();
        

        return SUCCESS;
    }

    private void generateHistory() {
        UploadHistory uploadHistory = new UploadHistory();
        try {
            uploadHistory.setDateOfUpload(new Date());
            uploadHistory.setErrorFile(createTempErrorFile(errorDTO));
            uploadHistory.setInputFile(createInputFile(input));
            if(errorDTO!=null){
                uploadHistory.setNumberOfErrorUploads(errorDTO.size());
            }
            uploadHistory.setNumberOfSuccessfulUploads(successfulUpdates);
            uploadHistory.setType("Itemprice");
            uploadHistoryService.save(uploadHistory);
        } catch (Exception e) {
            logger.error("unable to create History file", e);
        }

    }

    private void createErrorDTO(ItemPriceDTO itemPriceDTO, List<String> errors) {
        StringBuffer errorMsg = new StringBuffer();
        for (String errorMessage : errors) {
            errorMsg.append(" ");
            errorMsg.append(errorMessage);
            errorMsg.append(" , ");
        }

        itemPriceDTO.setError(errorMsg.toString());
        errorDTO.add(itemPriceDTO);
    }

    private void updateItemPrice(ItemBasePrice itemBasePrice, ItemPriceDTO itemPriceDTO) {
        CalendarDuration duration = new CalendarDuration(itemPriceDTO.getFromDate(),itemPriceDTO.getTillDate());
        try{
            itemBasePrice.set(itemPriceDTO.getNewValue(), duration);
            itemBasePriceService.update(itemBasePrice);
            successfulUpdates++;
        }catch(DurationOverlapException e){
            logger.error("Time Over Lapped", e);
            StringBuffer errorMsg = new StringBuffer();
            SortedSet<CalendarDuration>durations= e.getOffendedDurations();
            errorMsg.append(getText("error.manageData.dateOverlapPrefix",
            				new String[] {	duration.getFromDate().toString(),
            								duration.getTillDate().toString()
            							}));
            for (CalendarDuration durationError : durations) {
            	String[] paramValues = new String[] {
            			durationError.getFromDate().toString(),
            			durationError.getTillDate().toString(),
            			itemBasePrice.getValueAsOf(durationError.getFromDate()).toString()
            	};

            	errorMsg.append(getText("error.manageData.dateOverlapSuffix", paramValues));
            }
            itemPriceDTO.setError(errorMsg.toString());
            errorDTO.add(itemPriceDTO);
        }catch (Exception e) {
            logger.error("Unable to Save ",e);
        }
    }

    protected Blob createInputFile(File input){
        try{
          Blob inputFile=BlobProxy.generateProxy(FileCopyUtils.copyToByteArray(input));
          return inputFile;
      }catch (Exception e) {
          return null;
      }
    }

    @SuppressWarnings("unchecked")
    private Blob createTempErrorFile(List<ItemPriceDTO> errorDTO) {
        Map beans=new HashMap();
        beans.put("errorDTO", errorDTO);
        XLSTransformer transformer=new XLSTransformer();
        try{
            transformer.transformXLS(getErrorTemplatePath(), beans,ERROR_FILE);
            FileInputStream errorTempFile= new FileInputStream(new File(ERROR_FILE));
            Blob errorFile =  BlobProxy.generateProxy(FileCopyUtils.copyToByteArray(errorTempFile));
            return errorFile;
        }catch(Exception e){
            e.printStackTrace();
            logger.error("Problem occured while generating error file ",e);
            return null;
        }
    }

    private String getErrorTemplatePath() throws IOException {
        return new ClassPathResource("tavant/twms/web/admin/upload/itemprice/item-price-error-template.xls")
        .getURL().getPath();
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public int getSuccessfulUpdates() {
        return successfulUpdates;
    }

    public void setSuccessfulUpdates(int successfulUpdates) {
        this.successfulUpdates = successfulUpdates;
    }

    public List<UploadHistory> getUploadHistoryList() {
        return uploadHistoryList;
    }

    public void setUploadHistoryList(List<UploadHistory> uploadHistoryList) {
        this.uploadHistoryList = uploadHistoryList;
    }

    public void setUploadHistoryService(UploadHistoryService uploadHistoryService) {
        this.uploadHistoryService = uploadHistoryService;
    }

    public void setItemPriceAdminService(ItemPriceAdminService itemPriceAdminService) {
    }

    public void setCatalogService(CatalogService catalogService) {
    }

    public List<ItemPriceDTO> getErrorDTO() {
        return errorDTO;
    }

    public void setErrorDTO(List<ItemPriceDTO> errorDTO) {
        this.errorDTO = errorDTO;
    }

    public void setItemBasePriceService(
            ItemBasePriceService itemBasePriceService) {
        this.itemBasePriceService = itemBasePriceService;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
