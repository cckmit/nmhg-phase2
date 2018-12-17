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
package tavant.twms.integration.layer.failurecode;

import com.tavant.globalsync.failurecodessync.FailureCodeType;
import org.springframework.util.StringUtils;
import tavant.twms.domain.failurestruct.ActionDefinition;
import tavant.twms.domain.failurestruct.AssemblyDefinition;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.integration.layer.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FailureCodeValidator implements Validator {


    public void validate(Object o,Object dtoObject, final Map<String,String> errorMessageCodes) {
        if (o instanceof AssemblyDefinition) {
            AssemblyDefinition assemblyDefinition = (AssemblyDefinition) o;
            if (assemblyDefinition.getCode() == null
                    || !StringUtils.hasText(assemblyDefinition.getCode())) {
                errorMessageCodes.put("","Assembly Definition Code cannot be null");
            }
            if (assemblyDefinition.getAssemblyLevel() == null) {
                errorMessageCodes.put("","Assembly Definition Code cannot be null");
            }
            if (assemblyDefinition.getI18nAssemblyDefinition() == null) {
                errorMessageCodes.put("","Assembly Definition Name cannot be null");
            }

        } else if (o instanceof FailureTypeDefinition) {
            FailureTypeDefinition failureTypeDefinition = (FailureTypeDefinition) o;
            if (failureTypeDefinition.getCode() == null
                    || !StringUtils.hasText(failureTypeDefinition.getCode())) {
                errorMessageCodes.put("","FailureType Definition Code cannot be null");
            }
            if (failureTypeDefinition.getDescription() == null) {
                errorMessageCodes.put("","FailureType Descriptions cannot be null");
            }

        } else if (o instanceof FailureCauseDefinition) {
            FailureCauseDefinition failureCauseDefinition = (FailureCauseDefinition) o;
            if (failureCauseDefinition.getCode() == null
                    || !StringUtils.hasText(failureCauseDefinition.getCode())) {
                errorMessageCodes.put("","FailureCause Definition Code cannot be null");
            }
            if (failureCauseDefinition.getDescription() == null) {
                errorMessageCodes.put("","Failure Cause Descriptions cannot be null");
            }
        } else if (o instanceof ActionDefinition) {
            ActionDefinition actionDefinition = (ActionDefinition) o;
            if (actionDefinition.getCode() == null
                    || !StringUtils.hasText(actionDefinition.getCode())) {
                errorMessageCodes.put("","Action Definition Code cannot be null");
            }
            if (actionDefinition.getI18nActionDefinition() == null) {
                errorMessageCodes.put("","Action Definition Descriptions cannot be null");
            }

        } else if (o instanceof FailureCodeType) {
            FailureCodeType failureCodeType = (FailureCodeType) o;
            errorMessageCodes.putAll(validateFailureCodeType(failureCodeType,errorMessageCodes));
        }
        if (!errorMessageCodes.isEmpty())
            throw new RuntimeException(StringUtils.collectionToCommaDelimitedString(errorMessageCodes.values()));

    }

    public Map<String,String> validateFailureCodeType(FailureCodeType failureCodeType, final Map<String,String> errorMessageCodes) {
        if (failureCodeType.getProductCode() == null
                || !StringUtils.hasText(failureCodeType.getProductCode())) {
            errorMessageCodes.put("","Product for Failure Code cannot be null");

            if (failureCodeType.getModelCode() == null
                    || !StringUtils.hasText(failureCodeType.getModelCode())) {
                errorMessageCodes.put("","Both Product and Model for Failure Code cannot be null");
            }
        }
        if (failureCodeType.getSystem() == null
                || !StringUtils.hasText(failureCodeType.getSystem().toString())) {
            errorMessageCodes.put("","System cannot be null");
        }

        if (failureCodeType.getSystemName() == null
                || !StringUtils.hasText(failureCodeType.getSystemName().toString())) {
            errorMessageCodes.put("","System Name cannot be null");
        }

        if (failureCodeType.getSubSystem() != null && StringUtils.hasText(failureCodeType.getSubSystem().toString())) {
                if(failureCodeType.getSubSystemName() == null || !StringUtils.hasText(failureCodeType.getSubSystemName().toString()))
            errorMessageCodes.put("","Sub System Name cannot be null");
        }

        if (failureCodeType.getComponent() != null
                && StringUtils.hasText(failureCodeType.getComponent().toString())) {
            if (failureCodeType.getComponentName() == null || !StringUtils.hasText(failureCodeType.getComponentName().toString()))
                errorMessageCodes.put("","Component Name cannot be null");
        }

        if (failureCodeType.getSubComponent() != null && StringUtils.hasText(failureCodeType.getSubComponent().toString())) {
            if (failureCodeType.getSubComponentName() == null || !StringUtils.hasText(failureCodeType.getSubComponentName().toString()))
                errorMessageCodes.put("","Sub Component Name cannot be null");
        }

        if (failureCodeType.getSymptom() == null && (failureCodeType.getCause()!=null && StringUtils.hasText(failureCodeType.getCause().toString()))) {
                       errorMessageCodes.put("","Fault Cause must to mapped to Symptom");
               }


        if (failureCodeType.getStatus() == null
                || !StringUtils.hasText(failureCodeType.getStatus().toString())) {
            errorMessageCodes.put("","Status cannot be null");
        }

        return errorMessageCodes;
    }


}
