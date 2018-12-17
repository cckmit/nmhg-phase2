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
package tavant.twms.web.admin.upload.productstructure;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.upload.history.UploadHistory;
import tavant.twms.domain.upload.history.UploadHistoryService;
import tavant.twms.web.admin.dto.ItemGroupDTO;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.xls.reader.ApplicationContextHolder;
import tavant.twms.web.xls.reader.ConversionResult;
import tavant.twms.web.xls.reader.Reader;
import tavant.twms.web.xls.reader.Utility;

import javax.servlet.ServletContext;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.util.*;

/**
 * @author kaustubhshobhan.b
 *
 */
@SuppressWarnings("serial")
public class UploadProductStructureAction extends I18nActionSupport {

    private ItemGroupService itemGroupService;

    private static Logger logger = LogManager.getLogger(UploadProductStructureAction.class);

    private File input;

    private int successfulUpdates = 0;

    private static String ERROR_FILE="error_file.xls";

    private UploadHistoryService uploadHistoryService;

    private List<ItemGroupDTO> errorDTO = new ArrayList<ItemGroupDTO>();
    
    private String type = "Itemgroup";

    public String setup(){
    	return SUCCESS;
    }
    
    public String uploadProductStructure() throws Exception{

        ServletContext ctx = (ServletContext)ServletActionContext.getContext().get(ServletActionContext.SERVLET_CONTEXT);
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(ctx);
        ApplicationContextHolder.getInstance().setContext(appCtx);

        InputStream xmlInput = this.getClass().getResourceAsStream("productstructure-mapping.xml");
        Reader itemGroupReader = Utility.getReaderFromXML(xmlInput);
        InputStream xlsInput = new BufferedInputStream(new FileInputStream(input));
        Map<String, List<ConversionResult>>itemGroupMap = itemGroupReader.read(xlsInput);
        List<ConversionResult> itemGroupUploadList = itemGroupMap.get("Sheet1");

        for (ConversionResult result : itemGroupUploadList) {
            ItemGroupDTO itemGroupDTO = (ItemGroupDTO)result.getResult();
            if (result.hasConversionErrors()){
                createErrorDTO(itemGroupDTO,result.getErrors());
            }else{
                saveOrUpdateItemGroup(itemGroupDTO);
                successfulUpdates++;
            }
        }

        generateHistory();

        return SUCCESS;
    }

    private void createErrorDTO(ItemGroupDTO itemGroupDTO, List<String> errors) {
        String errorMsg = "";
        for (String error : errors) {
            errorMsg = errorMsg + error + ", ";
        }

        itemGroupDTO.setError(errorMsg);
        errorDTO.add(itemGroupDTO);

    }

    private void saveOrUpdateItemGroup(ItemGroupDTO itemGroupDTO) {
        ItemGroup itemGroup;
        boolean create = false;

        itemGroup = itemGroupService.findItemGroupByCode(itemGroupDTO.getItemCategory());

        if(itemGroup==null){
            itemGroup = new ItemGroup();
            create = true;
            logger.error("New object");
        }

        updateCreateItemGroup(itemGroup,itemGroupDTO);

        try{
            if(create){
                itemGroupService.save(itemGroup);
            }else{
                itemGroupService.update(itemGroup);
            }
        }catch(Exception e){
            logger.error("Error While Saving", e);
        }

    }

    private void updateCreateItemGroup(ItemGroup itemGroup, ItemGroupDTO itemGroupDTO) {
        if(itemGroup==null){
            logger.error("The item DTO is null");
        }
        itemGroup.setIsPartOf(itemGroupDTO.getParentGroup());
        itemGroup.setName(itemGroupDTO.getItemCategory());
        itemGroup.setGroupCode(itemGroupDTO.getItemCategory());
        itemGroup.setDescription(itemGroupDTO.getItemCategory() + " Group");
        itemGroup.setItemGroupType(itemGroupDTO.getType());

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
            uploadHistory.setType("Itemgroup");
            uploadHistoryService.save(uploadHistory);
        } catch (Exception e) {
            logger.error("unable to create History file", e);
        }

    }

    private Blob createInputFile(File input) {

        try{
            Blob inputFile=BlobProxy.generateProxy(FileCopyUtils.copyToByteArray(input));
            return inputFile;
        }catch (Exception e) {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    private Blob createTempErrorFile(List<ItemGroupDTO> errorDTO) {
        Map beans=new HashMap();
        beans.put("errorDTO", errorDTO);
        XLSTransformer transformer=new XLSTransformer();

        try{
            transformer.transformXLS(getErrorTemplatePath(), beans,ERROR_FILE);
            FileInputStream errorTempFile= new FileInputStream(new File(ERROR_FILE));
            Blob errorFile = BlobProxy.generateProxy(FileCopyUtils.copyToByteArray(errorTempFile));
            return errorFile;
        }catch(Exception e){
            e.printStackTrace();
            logger.error("Problem occured while generating error file ",e);
            return null;
        }
    }

    private String getErrorTemplatePath()throws Exception {
        return new ClassPathResource("tavant/twms/web/admin/upload/productstructure/product-structure-error-template.xls")
            .getURL().getPath();
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public void setUploadHistoryService(UploadHistoryService uploadHistoryService) {
        this.uploadHistoryService = uploadHistoryService;
    }

	public String getType() {
		return type;
	}

}
