package tavant.twms.integration.layer.failurecode.sync;

import com.tavant.globalsync.failurecodessync.DescriptionType;
import com.tavant.globalsync.failurecodessync.FailureCodeType;
import com.tavant.globalsync.failurecodessync.WarrantyCodeType;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.I18NActionDefinition;
import tavant.twms.domain.common.I18NAssemblyDefinition;
import tavant.twms.domain.common.I18NFailureCauseDefinition;
import tavant.twms.domain.common.I18NFailureTypeDefinition;
import tavant.twms.domain.failurestruct.*;
import tavant.twms.integration.layer.TransformException;
import tavant.twms.integration.layer.Transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: roopa.kariyappa
 * Date: 16/8/12
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class FailureCodeSyncTransformer implements Transformer {

    public Object transform(Object failureCodesObject , final Map<String,String> errorMessageCodes) throws TransformException {
        if (failureCodesObject instanceof WarrantyCodeType) {
            WarrantyCodeType warrantyCodes = (WarrantyCodeType) failureCodesObject;
            transformWarrantyCode(warrantyCodes);
        } else if (failureCodesObject instanceof FailureCodeType) {
            FailureCodeType failureCodes = (FailureCodeType) failureCodesObject;
            transformFailureCode(failureCodes);
        }

        return null;
    }

    public Object transformWarrantyCode(WarrantyCodeType warrantyCodeDTO) throws TransformException {
        String type = warrantyCodeDTO.getType().toString();
        if (type.equalsIgnoreCase(WarrantyCodeType.Type.SYSTEM.toString()) || type.equalsIgnoreCase(WarrantyCodeType.Type.SUB_SYSTEM.toString()) ||
                type.equalsIgnoreCase(WarrantyCodeType.Type.COMPONENT.toString()) || type.equalsIgnoreCase(WarrantyCodeType.Type.SUB_COMPONENT.toString())) {
            return transformToAssemblyDefinition(warrantyCodeDTO);
        } else if (type.equalsIgnoreCase(WarrantyCodeType.Type.SYMPTOM.toString())) {
            return transformToFailureTypeDefinition(warrantyCodeDTO);
        } else if (type.equalsIgnoreCase(WarrantyCodeType.Type.CAUSE.toString())) {
            return transformToFailureCauseDefinition(warrantyCodeDTO);
        } else if (type.equalsIgnoreCase(WarrantyCodeType.Type.ACTIVITY.toString())) {
            return transformToActionDefinition(warrantyCodeDTO);
        }
        return null;
    }


    public FailureStructure transformFailureCode(FailureCodeType failureCodeDTO) throws TransformException {
     /*   FailureStructure failureStructure = new FailureStructure();
        ItemGroup itemGroup = new ItemGroup();
        Assembly assembly = new Assembly();
        String productCode = failureCodeDTO.getProductCode();
        String model = failureCodeDTO.getModelCode();
        String system = failureCodeDTO.getSystem();
        String systemName = failureCodeDTO.getSystemName();
        String subSystem = failureCodeDTO.getSubSystem();
        String subSystemName = failureCodeDTO.getSubSystemName();
        String component = failureCodeDTO.getComponent();
        String componentName = failureCodeDTO.getComponentName();
        String subComponent = failureCodeDTO.getComponent();
        String subComponentName = failureCodeDTO.getComponentName();

        //failureStructure.setName();

*/
        return null;
    }

    public AssemblyDefinition transformToAssemblyDefinition(WarrantyCodeType warrantyCodeDTO) {
        AssemblyDefinition assemblyDefinition = new AssemblyDefinition();
        assemblyDefinition.setCode(warrantyCodeDTO.getCode());

        DescriptionType[] warrantyCodeDescs = warrantyCodeDTO.getDescriptions().getDescriptionArray();
        List<I18NAssemblyDefinition> i18NAssemblyDefinitionList = new ArrayList<I18NAssemblyDefinition>();
        for (final DescriptionType warrantyCodeDesc : warrantyCodeDescs) {
            I18NAssemblyDefinition i18NAssemblyDefinition = new I18NAssemblyDefinition();
            i18NAssemblyDefinition.setLocale(warrantyCodeDesc.getLanguage());
            i18NAssemblyDefinition.setName(warrantyCodeDesc.getCodeDescription());
            i18NAssemblyDefinitionList.add(i18NAssemblyDefinition);
        }
        assemblyDefinition.setI18nAssemblyDefinition(i18NAssemblyDefinitionList);
        AssemblyLevel assemblyLevel = new AssemblyLevel();
        assemblyLevel.setLevel(warrantyCodeDTO.getType().intValue());
        assemblyDefinition.setAssemblyLevel(assemblyLevel);
        return assemblyDefinition;
    }

    public FailureTypeDefinition transformToFailureTypeDefinition(WarrantyCodeType warrantyCodeDTO) {
        FailureTypeDefinition failureTypeDefinition = new FailureTypeDefinition();
        failureTypeDefinition.setCode(warrantyCodeDTO.getCode());

        DescriptionType[] failureTypeDescs = warrantyCodeDTO.getDescriptions().getDescriptionArray();
        List<I18NFailureTypeDefinition> i18NFailureTypeDefinitionList = new ArrayList<I18NFailureTypeDefinition>();
        for (final DescriptionType failureTypeDesc : failureTypeDescs) {
            I18NFailureTypeDefinition i18NFailureTypeDefinition = new I18NFailureTypeDefinition();
            i18NFailureTypeDefinition.setLocale(failureTypeDesc.getLanguage());
            i18NFailureTypeDefinition.setName(failureTypeDesc.getCodeDescription());
            i18NFailureTypeDefinitionList.add(i18NFailureTypeDefinition);
        }
        failureTypeDefinition.setI18nFailureTypeDefinition(i18NFailureTypeDefinitionList);
        failureTypeDefinition.setDescription(failureTypeDefinition.getName());
        failureTypeDefinition.setName(failureTypeDefinition.getName());
        return failureTypeDefinition;
    }


    public FailureCauseDefinition transformToFailureCauseDefinition(WarrantyCodeType warrantyCodeDTO) {
        FailureCauseDefinition failureCauseDefinition = new FailureCauseDefinition();
        failureCauseDefinition.setCode(warrantyCodeDTO.getCode());
        DescriptionType[] failureTypeDescs = warrantyCodeDTO.getDescriptions().getDescriptionArray();
        List<I18NFailureCauseDefinition> i18NFailureCauseDefinitionList = new ArrayList<I18NFailureCauseDefinition>();
        for (final DescriptionType failureTypeDesc : failureTypeDescs) {
            I18NFailureCauseDefinition i18NFailureCauseDefinition = new I18NFailureCauseDefinition();
            i18NFailureCauseDefinition.setLocale(failureTypeDesc.getLanguage());
            i18NFailureCauseDefinition.setName(failureTypeDesc.getCodeDescription());
            i18NFailureCauseDefinitionList.add(i18NFailureCauseDefinition);
        }
        failureCauseDefinition.setI18nFailureCauseDefinition(i18NFailureCauseDefinitionList);
        failureCauseDefinition.setDescription(failureCauseDefinition.getName());
        failureCauseDefinition.setName(failureCauseDefinition.getName());
        return failureCauseDefinition;
    }

    public ActionDefinition transformToActionDefinition(WarrantyCodeType warrantyCodeDTO) {
        ActionDefinition actionDefinition = new ActionDefinition();
        actionDefinition.setCode(warrantyCodeDTO.getCode());
        DescriptionType[] actionDefDescs = warrantyCodeDTO.getDescriptions().getDescriptionArray();
        List<I18NActionDefinition> i18NActionDefinitionList = new ArrayList<I18NActionDefinition>();
        for (final DescriptionType actionDefDesc : actionDefDescs) {
            I18NActionDefinition i18NActionDefinition = new I18NActionDefinition();
            i18NActionDefinition.setLocale(actionDefDesc.getLanguage());
            i18NActionDefinition.setName(actionDefDesc.getCodeDescription());
            i18NActionDefinitionList.add(i18NActionDefinition);
        }
        actionDefinition.setI18nActionDefinition(i18NActionDefinitionList);
        return actionDefinition;
    }


}


