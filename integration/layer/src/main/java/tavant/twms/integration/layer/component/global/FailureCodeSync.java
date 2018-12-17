package tavant.twms.integration.layer.component.global;

import com.tavant.globalsync.failurecodesresponse.*;
import com.tavant.globalsync.failurecodesresponse.ApplicationAreaType;
import com.tavant.globalsync.failurecodessync.*;
import com.tavant.globalsync.failurecodessync.FailureCodesSyncDocument.FailureCodesSync;
import org.apache.log4j.Logger;
import com.tavant.globalsync.failurecodessync.WarrantyCodeType;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import tavant.twms.integration.layer.failurecode.FailureCodeSyncService;
import tavant.twms.integration.layer.failurecode.FailureCodeValidator;
import tavant.twms.integration.layer.failurecode.sync.FailureCodeSyncTransformer;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import com.tavant.globalsync.failurecodesresponse.FailureCodesResponseSyncDocument.FailureCodesResponseSync;

import java.util.*;

import static tavant.twms.integration.layer.util.ExceptionUtil.getStackTrace;

/**
 * Created by IntelliJ IDEA.
 * User: roopa.kariyappa
 * Date: 13/8/12
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class FailureCodeSync {

    private TransactionTemplate transactionTemplate;
    private static Logger logger = Logger.getLogger(FailureCodeSync.class.getName());
    private FailureCodeSyncTransformer failureCodeSyncTransformer;
    private FailureCodeValidator failureCodeValidator;
    private FailureCodeSyncService failureCodeSyncService;

    public FailureCodesResponseSyncDocument sync(final FailureCodesSync failureCodeSyncMaster) {
        boolean failure = false;
        if (logger.isDebugEnabled()) {
            logger.debug("Received Failure codes for synchronising.");
        }

        if (null != failureCodeSyncMaster.getDataArea().getBUName()
                && StringUtils.hasText(failureCodeSyncMaster
                .getDataArea().getBUName())) {
            SelectedBusinessUnitsHolder
                    .setSelectedBusinessUnit(failureCodeSyncMaster.getDataArea().getBUName().trim());
        }
        FailureCodesResponseSyncDocument failureCodeResponseDoc = FailureCodesResponseSyncDocument.Factory.newInstance();
        failureCodeResponseDoc.addNewFailureCodesResponseSync();
        FailureCodesResponseSync failureCodesResponseSync = failureCodeResponseDoc.getFailureCodesResponseSync();
        failureCodesResponseSync.addNewStatus();
        addResponseApplicationArea(failureCodeSyncMaster, failureCodesResponseSync);

        FailureCodesType failureCodesType = failureCodeSyncMaster.getDataArea().getFailureCodes();
        WarrantyCodesType warrantyCodesType = failureCodeSyncMaster.getDataArea().getWarrantyCodes();

        if (null != failureCodeSyncMaster.getDataArea().getBUName()
                && StringUtils.hasText(failureCodeSyncMaster.getDataArea().getBUName())) {
            SelectedBusinessUnitsHolder
                    .setSelectedBusinessUnit(failureCodeSyncMaster.getDataArea().getBUName().trim());
        }

        WarrantyCodesResponseType warrantyCodesResponseType = WarrantyCodesResponseType.Factory.newInstance();
        List<WarrantyCodeResponseType> warrantyCodeResponseTypeList = new ArrayList<WarrantyCodeResponseType>();
        WarrantyCodeType[] warrantyCodes = warrantyCodesType.getWarrantyCodeArray();
        failureCodesResponseSync.addNewWarrantyCodesResponse();

        for (final WarrantyCodeType warrantyCodeDTO : warrantyCodes) {
            if (warrantyCodeDTO != null) {
                warrantyCodesResponseType.addNewWarrantyCodeResponse();
                WarrantyCodeResponseType warrantyCodeResponseType = WarrantyCodeResponseType.Factory.newInstance();
                warrantyCodeResponseType.addNewStatus();
                warrantyCodeResponseType.setCode(warrantyCodeDTO.getCode());
                try {
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        protected void doInTransactionWithoutResult(
                                TransactionStatus transactionStatus) {
                            syncWarrantyCodes(warrantyCodeDTO);
                        }
                    });
                    warrantyCodeResponseType.getStatus().setCode(com.tavant.globalsync.failurecodesresponse.CodeDocument.Code.Enum.forString("SUCCESS"));
                } catch (RuntimeException e) {
                    logger.error("Exception Occurred in GlobalItemSync !!", e);
                    failure = true;
                    String message = (StringUtils.hasText(e.getMessage())) ?
                            e.getMessage() : getStackTrace(e);
                    warrantyCodeResponseType = buildErrorResponseForWarrantyCodeResponse(message,
                            warrantyCodeDTO.getCode(), warrantyCodeResponseType);
                }

                warrantyCodeResponseTypeList.add(warrantyCodeResponseType);
            }
        }
        warrantyCodesResponseType.setWarrantyCodeResponseArray((((WarrantyCodeResponseType[]) warrantyCodeResponseTypeList.toArray(new WarrantyCodeResponseType[warrantyCodeResponseTypeList.size()]))));


        FailureCodesResponseType failureCodesResponseType = FailureCodesResponseType.Factory.newInstance();
        List<FailureCodeResponseType> failureCodeResponseTypeList = new ArrayList<FailureCodeResponseType>();
        FailureCodeType[] failureCodes = failureCodesType.getFailureCodeArray();
        failureCodesResponseSync.addNewFailureCodesResponse();

        for (final FailureCodeType failureCodeDTO : failureCodes) {
            if (failureCodeDTO != null) {
                failureCodesResponseType.addNewFailureCodeResponse();
                FailureCodeResponseType failureCodeResponseType = FailureCodeResponseType.Factory.newInstance();
                failureCodeResponseType.addNewStatus();
                setFailureCodeResponseObject(failureCodeResponseType, failureCodeDTO);
                try {
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        protected void doInTransactionWithoutResult(
                                TransactionStatus transactionStatus) {
                            syncFailureCodes(failureCodeDTO);
                        }
                    });
                    failureCodeResponseType.getStatus().setCode(com.tavant.globalsync.failurecodesresponse.CodeDocument.Code.Enum.forString("SUCCESS"));
                } catch (RuntimeException e) {
                    logger.error("Exception Occurred in GlobalItemSync !!", e);
                    failure = true;
                    String message = (StringUtils.hasText(e.getMessage())) ?
                            e.getMessage() : getStackTrace(e);
                    failureCodeResponseType = buildErrorResponseForFailureCodeResponse(message,
                            failureCodeDTO, failureCodeResponseType);
                }

               failureCodeResponseTypeList.add(failureCodeResponseType);
            }
        }
        failureCodesResponseType.setFailureCodeResponseArray((((FailureCodeResponseType[]) failureCodeResponseTypeList.toArray(new FailureCodeResponseType[failureCodeResponseTypeList.size()]))));

        if (failure) {
            failureCodesResponseSync.getStatus().setCode(com.tavant.globalsync.failurecodesresponse.CodeDocument.Code.Enum.forString("ERROR"));
        } else {
            failureCodesResponseSync.getStatus().setCode(com.tavant.globalsync.failurecodesresponse.CodeDocument.Code.Enum.forString("SUCCESS"));
        }

        failureCodesResponseSync.setWarrantyCodesResponse(warrantyCodesResponseType);
        failureCodesResponseSync.setFailureCodesResponse(failureCodesResponseType);
        failureCodeResponseDoc.setFailureCodesResponseSync(failureCodesResponseSync);
        return failureCodeResponseDoc;
    }

    public void syncWarrantyCodes(final WarrantyCodeType warrantyCodeDTO) {
        try {
            Object warrantyCodeDefinition = failureCodeSyncTransformer.transformWarrantyCode(warrantyCodeDTO);
            final Map<String,String> errorMessageCodes = new HashMap<String, String>();
            failureCodeValidator.validate(warrantyCodeDefinition,null,errorMessageCodes);
            failureCodeSyncService.createOrUpdate(warrantyCodeDefinition, warrantyCodeDTO);
        } catch (Exception e) {
            logger.error("Error while syncing Warranty Codes !!!", e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public void syncFailureCodes(final FailureCodeType failureCodeDTO) {
        try {
            Object failureCodeDefinition = failureCodeSyncTransformer.transformFailureCode(failureCodeDTO);
            final Map<String,String> errorMessageCodes = new HashMap<String, String>();
            failureCodeValidator.validate(failureCodeDTO,null,errorMessageCodes);
            failureCodeSyncService.createOrUpdate(failureCodeDefinition, failureCodeDTO);
        } catch (Exception e) {
            logger.error("Error while syncing Warranty Codes !!!", e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public FailureCodeSyncTransformer getFailureCodeSyncTransformer() {
        return failureCodeSyncTransformer;
    }

    public void setFailureCodeSyncTransformer(FailureCodeSyncTransformer failureCodeSyncTransformer) {
        this.failureCodeSyncTransformer = failureCodeSyncTransformer;
    }

    public void setFailureCodeValidator(FailureCodeValidator failureCodeValidator) {
        this.failureCodeValidator = failureCodeValidator;
    }

    public void setFailureCodeSyncService(FailureCodeSyncService failureCodeSyncService) {
        this.failureCodeSyncService = failureCodeSyncService;
    }


    private void addResponseApplicationArea(
            final FailureCodesSync syncFailureCodeMaster,
            FailureCodesResponseSync failureCodesResponseSync) {
        failureCodesResponseSync.addNewApplicationArea();
        ApplicationAreaType applicationArea = failureCodesResponseSync
                .getApplicationArea();
        if (syncFailureCodeMaster.getApplicationArea() != null) {
            applicationArea.setBODId(syncFailureCodeMaster.getApplicationArea()
                    .getBODId());
            applicationArea.setCreationDateTime(syncFailureCodeMaster
                    .getApplicationArea().getCreationDateTime());
            applicationArea.setInterfaceNumber(syncFailureCodeMaster
                    .getApplicationArea().getInterfaceNumber());
        }
        applicationArea.addNewSender();
        if (syncFailureCodeMaster.getApplicationArea().getSender() != null) {
            applicationArea.getSender().setLogicalId(
                    syncFailureCodeMaster.getApplicationArea().getSender()
                            .getTask());
            applicationArea.getSender().setReferenceId(
                    syncFailureCodeMaster.getApplicationArea().getSender()
                            .getReferenceId());
            applicationArea.getSender().setTask(
                    syncFailureCodeMaster.getApplicationArea().getSender().getLogicalId());
        }
    }


    private WarrantyCodeResponseType buildErrorResponseForWarrantyCodeResponse(
            String message, String code, WarrantyCodeResponseType warrantyCodeResponseType) {
        warrantyCodeResponseType.setCode(code);
        warrantyCodeResponseType.getStatus().setCode(com.tavant.globalsync.failurecodesresponse.CodeDocument.Code.Enum.forString("FAILURE"));
        warrantyCodeResponseType.getStatus().setErrorMessage(new StringBuilder().append(
                " Error Syncing Warranty Codes, with Code ").append(
                code).append("\n").append(
                " The Reason for the Error is : ").append(message).append("\n")
                .append("\n").toString());
        return warrantyCodeResponseType;
    }


    private FailureCodeResponseType buildErrorResponseForFailureCodeResponse(
            String message, FailureCodeType failureCodeType, FailureCodeResponseType failureCodeResponseType) {
        failureCodeResponseType = setFailureCodeResponseObject(failureCodeResponseType, failureCodeType);
        failureCodeResponseType.getStatus().setCode(com.tavant.globalsync.failurecodesresponse.CodeDocument.Code.Enum.forString("ERROR"));
        failureCodeResponseType.getStatus().setErrorMessage(new StringBuilder().append(
                " Error Syncing Failure Codes, with Product/Model Code ").append(
                failureCodeType.getProductCode() + "/").append(failureCodeType.getModelCode()).append("\n").append(
                " The Reason for the Error is : ").append(message).append("\n")
                .append("\n").toString());
        return failureCodeResponseType;
    }

    private FailureCodeResponseType setFailureCodeResponseObject(FailureCodeResponseType failureCodeResponseType, FailureCodeType failureCodeType) {
        if(failureCodeType.getProductCode()!=null)
            failureCodeResponseType.setProductCode(failureCodeType.getProductCode());
        if(failureCodeType.getModelCode()!=null)
            failureCodeResponseType.setModelCode(failureCodeType.getModelCode());
        if(failureCodeType.getSystem()!=null)
            failureCodeResponseType.setSystem(failureCodeType.getSystem());
        if(failureCodeType.getSystemName()!=null)
            failureCodeResponseType.setSystemName(failureCodeType.getSystemName());
        if(failureCodeType.getSubSystem()!=null)
            failureCodeResponseType.setSubSystem(failureCodeType.getSubSystem());
        if(failureCodeType.getSubSystemName()!=null)
            failureCodeResponseType.setSubSystemName(failureCodeType.getSubSystemName());
        if(failureCodeType.getComponent()!=null)
            failureCodeResponseType.setComponent(failureCodeType.getComponent());
        if(failureCodeType.getComponentName()!=null)
            failureCodeResponseType.setComponentName(failureCodeType.getComponentName());
        if(failureCodeType.getSubComponent()!=null)
            failureCodeResponseType.setSubComponent(failureCodeType.getSubComponent());
        if(failureCodeType.getSubComponentName()!=null)
            failureCodeResponseType.setSubComponentName(failureCodeType.getSubComponentName());
        if(failureCodeType.getSymptom()!=null)
            failureCodeResponseType.setSymptom(failureCodeType.getSymptom());
        if(failureCodeType.getCause()!=null)
            failureCodeResponseType.setSymptom(failureCodeType.getCause());
        return failureCodeResponseType;
    }
}
