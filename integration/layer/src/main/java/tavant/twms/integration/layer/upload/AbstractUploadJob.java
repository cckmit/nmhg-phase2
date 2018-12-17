/*
 *   Copyright (c)2007 Tavant Technologies
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
 *
 */
package tavant.twms.integration.layer.upload;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.twms.domain.upload.StagingData;
import tavant.twms.domain.upload.StagingError;
import tavant.twms.domain.upload.controller.UploadManagement;
import tavant.twms.domain.upload.controller.UploadStatusDetail;
import tavant.twms.integration.layer.Service;
import tavant.twms.integration.layer.Transformer;
import tavant.twms.integration.layer.Validator;
import tavant.twms.security.SecurityHelper;

/**
 * An job which reads the XLS files of given type and uploads it to staging, and
 * process the same.
 *
 * @author prasad.r
 */
public abstract class AbstractUploadJob implements StatefulJob, InterruptableJob {

    private static final Logger logger = Logger.getLogger(AbstractUploadJob.class.getName());
    private UploadService uploadService;
    private TransactionTemplate tt;

    public TransactionTemplate getTt() {
        return tt;
    }

    public void setTt(TransactionTemplate tt) {
        this.tt = tt;
    }

    public UploadService getUploadHelper() {
        return uploadService;
    }

    public void setUploadHelper(UploadService uploadHelper) {
        this.uploadService = uploadHelper;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        initSpringServices(context);
        populateSystemUserAuthentication();
        if (logger.isDebugEnabled()) {
            logger.debug("Started Upload Job for the Template Name : [" + getTemplateName() + "]");
        }
        UploadManagement management = uploadService.getUploadMgtDetail(getTemplateName());
        List<Long> uploadedFileIds = uploadService.loadReceivedFileDetails(management.getNameToDisplay());
        if (uploadedFileIds != null && !uploadedFileIds.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found " + uploadedFileIds.size() + " uploaded files !!!");
            }
            if (management.getBackupTable() != null && "".equals(management.getBackupTable().trim())) {
                logger.debug("Backing up the staging data !!!");
                uploadService.backupStagingTable(management.getStagingTable(), management.getBackupTable());
            }
            logger.debug("Cleaning staging data !!!");
            uploadService.cleanStagingTable(management.getStagingTable());
            for (Long fileId : uploadedFileIds) {
                String filePath = getUploadedFile(fileId);
                uploadService.updateUploadFileStatus(fileId, UploadStatusDetail.STATUS_PROCESSING);
                Class stagingDataClass = getStagingDataClass();
                boolean processed = readAndProcessUploadedFile(fileId, filePath, management, stagingDataClass);
                if(processed){
                    Long errorCount = reportErrors(fileId, management, filePath);
                    if(StringUtils.isBlank(management.getBackupTable())){
                        insertIntoBackupTable();
                    }
                    Long totalRows = this.uploadService.getTotalRowCount(fileId, management.getStagingTable());
                    uploadService.updateUploadFileStatus(fileId, totalRows, errorCount, UploadStatusDetail.STATUS_UPLOADED, null);
                }else{
                    uploadService.updateUploadFileStatus(fileId, 0, 0, UploadStatusDetail.STATUS_FAILED, "Error reading xls");
                }
                new File(filePath).delete();
            }
        }
    }

    /**
     * Interrupts the running job. PS: Only holds good if the uploaded file as
     * more than one record. If stopped in between, already uploaded rows will
     * not be rolled back
     *
     * @throws UnableToInterruptJobException
     */
    public void interrupt() throws UnableToInterruptJobException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void populateSystemUserAuthentication() {
        SecurityHelper securityHelper = new SecurityHelper();
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            securityHelper.populateSystemUser();
        }
    }

    /**
     * Name of the template which needs to be uploaded
     *
     * @return
     */
    public abstract String getTemplateName();

    public abstract Class getStagingDataClass();

    /**
     * Empty implementation for inserting into back up table, Child jobs should
     * override this method if records needs to be inserted into back up table
     *
     */
    public void insertIntoBackupTable() {
    }

    public abstract int getUploadRecordsLimit();
    
    public abstract Transformer getTransformer();
    
    public abstract Validator getValidator();
    
    public abstract Service getService();
    
    /**
     * Hence we need to initialize the spring service beans manually through the
     * job context or the bean locator
     *
     * @param jeContext
     */
    protected void initSpringServices(JobExecutionContext jeContext) {
        try {
            ApplicationContext springAppContext = (ApplicationContext) jeContext.getScheduler().getContext().get("springApplicationContext");
            if (springAppContext != null) {
                uploadService = (UploadService) springAppContext.getBean("uploadService");
                tt = (TransactionTemplate) springAppContext.getBean("transactionTemplate");
                initChildServices(springAppContext);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to intialize spring serivices !!!", e);
        }
    }

    /**
     * Override this method if you want initialize any child services
     *
     * @param springAppContext
     */
    protected void initChildServices(ApplicationContext springAppContext) {
    }

    /**
     * Method which reads the XLS file and processes the same
     * 
     */
    private boolean readAndProcessUploadedFile(final Long fileId, String filePath, UploadManagement management, final Class<? extends StagingData> stagingDataClass) {
        boolean processedSuccessfully = true; 
        if (StringUtils.isNotEmpty(filePath)) {
            try {
                XLSReadListener listener = new XLSReadListener(new POIFSFileSystem(new FileInputStream(filePath)),
                        1,
                        management.getHeaderRowToCapture().intValue(),
                        management.getConsumeRowsFrom().intValue(),
                        management.getColumnsToCapture().intValue() - 1) {

                    private List<String> columnNames = new ArrayList<String>();
                    private List<String> columnValues = new ArrayList<String>();

                    @Override
                    protected void setHeader(int rowNum, int columnNum, String headerValue) {
                        String s = getPropertyName(headerValue);
                        columnNames.add(s);
                    }

                    @Override
                    protected void setColumnValue(int rowNum, int columnNum, String cellValue) {
                        columnValues.add(cellValue);
                    }

                    @Override
                    public void rowReadcompleted(int rowNum) {
                        if (!columnValues.isEmpty()) {
                            StagingData mappedObject = BeanUtils.instantiate(stagingDataClass);
                            mappedObject.setFileUploadMgtId(fileId);
                            mappedObject.setId(Long.valueOf((rowNum - rowsToStartConsuming)));
                            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
                            for (int i = 0; i < columnValues.size(); i++) {
                                String columnVal = columnValues.get(i);
                                bw.setPropertyValue(columnNames.get(i), columnVal);
                            }

                            processRowBean(mappedObject);
                            columnValues.clear();
                        }
                    }
                };
                listener.process();
            } catch (Exception ex) {
                logger.error("Error while processing xls for file upload id : " + fileId, ex);
                processedSuccessfully = false;
            }
        }else{
            processedSuccessfully = false;
        }
        return processedSuccessfully;
    }

    /**
     * Method which will process the row of an XLS which is read into staging
     * bean
     *
     * @param mappedObject
     * @param dataTransformer
     */
    private void processRowBean(final StagingData stagingDataBean) {
        if (stagingDataBean.getId() < getUploadRecordsLimit()) { // process the bean
            tt.execute(new TransactionCallbackWithoutResult() {

                protected void doInTransactionWithoutResult(
                        TransactionStatus transactionStatus) {
                    try {
                    		final Map<String,String> errorMessageCodes = new HashMap<String, String>();
                            Object entity = getTransformer().transform(stagingDataBean,errorMessageCodes);
                            getValidator().validate(entity,null,errorMessageCodes);
                            getService().createOrUpdate(entity, stagingDataBean);
                    } catch (Exception e) {
                        logger.error("Error updating data..", e);
                        transactionStatus.setRollbackOnly();
                        stagingDataBean.setErrorStatus("Y");
                        stagingDataBean.setErrorCode(e.getMessage());
                    }
                }
            });
        } else { // mark as errored
            stagingDataBean.setErrorStatus("Y");
            stagingDataBean.setErrorCode("Exceded the maximum limit of rows");
        }
    }

    /**
     * Simple property conversion, the spaces in the header will be taken as
     * reference and same will be converted in to camel case for the property
     * name
     *
     * @param headerValue
     * @return
     */
    protected String getPropertyName(String headerValue) {
        StringTokenizer st = new StringTokenizer(headerValue, " ");
        StringBuilder sb = new StringBuilder();
        if (st.hasMoreTokens()) {
            String s = (String) st.nextElement();
            sb.append(s.toLowerCase());
        }
        while (st.hasMoreTokens()) {
            String s = (String) st.nextElement();
            sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
        }
        return sb.toString();
    }

    private Long reportErrors(Long fileId, UploadManagement management, String filePath) {
        List<StagingError> errors = this.uploadService.loadErrorRecords(management.getStagingTable(),fileId);
        if(errors.size() > 0){
            uploadService.updateUploadFileStatus(fileId, UploadStatusDetail.STATUS_ERR_REPORTING);
            try{
                uploadService.createErrorFile(new FileInputStream(filePath), errors);
            }catch(Exception e){
                logger.error("Exception occured while reporting errors !!", e);
            }
        }
        return (errors != null) ? errors.size() : 0L;
    }

    private String getUploadedFile(final Long fileId) {
        String path = (String)
        tt.execute(new TransactionCallback() {
            public Object doInTransaction(
                    TransactionStatus transactionStatus) {
                String s = null;
                try {
                    s = uploadService.getUploadedFilePath(fileId);
                } catch (Exception e) {
                    logger.error("Error updating data..", e);
                    transactionStatus.setRollbackOnly();
                }
                return s;
            }
        });
        return path;
    }
}
