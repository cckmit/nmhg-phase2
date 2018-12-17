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
import com.tavant.globalsync.failurecodessync.WarrantyCodeType;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.common.I18NActionDefinition;
import tavant.twms.domain.common.I18NAssemblyDefinition;
import tavant.twms.domain.common.I18NFailureCauseDefinition;
import tavant.twms.domain.common.I18NFailureTypeDefinition;
import tavant.twms.domain.failurestruct.*;
import tavant.twms.integration.layer.Service;
import tavant.twms.integration.layer.ServiceException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FailureCodeSyncService implements Service {

    private Logger logger = Logger.getLogger(FailureCodeSyncService.class);

    private static final int SYSTEM = 1;
    private static final int SUB_SYSTEM = 2;
    private static final int COMPONENT = 3;
    private static final int SUB_COMPONENT = 4;
    private FailureStructureService failureStructureService;
    private CatalogService catalogService;
    private ItemGroupService itemGroupService;
    private static String system = null;
    private static String systemName = null;
    private static String subSystem = null;
    private static String subSystemName = null;
    private static String component = null;
    private static String componentName = null;
    private static String subComponent = null;
    private static String subComponentName = null;
    private static String symptom = null;
    private static String cause = null;
    private static String status = "ACTIVE";

    public void createOrUpdate(Object entity, Object input) throws ServiceException {
        if (input instanceof WarrantyCodeType) {
            createOrUpdateWarrantyCodes(entity, input);

        } else if (input instanceof FailureCodeType) {
            createOrUpdateFailureCodes(entity, input);
        }
    }

    public void createOrUpdateWarrantyCodes(Object entity, Object input) {
        if (entity instanceof AssemblyDefinition) {
            AssemblyDefinition newAssemblyDefinition = (AssemblyDefinition) entity;
            createOrUpdateAssemblyDefinition(newAssemblyDefinition);
        } else if (entity instanceof FailureTypeDefinition) {       // symptom
            FailureTypeDefinition newFailureTypeDefinition = (FailureTypeDefinition) entity;
            createOrUpdateFailureTypeDefinition(newFailureTypeDefinition);
        } else if (entity instanceof FailureCauseDefinition) {          // fault caused by
            FailureCauseDefinition newFailureCauseDefinition = (FailureCauseDefinition) entity;
            createOrUpdateFailureCauseDefinition(newFailureCauseDefinition);
        } else if (entity instanceof ActionDefinition) {       //activity
            ActionDefinition newActionDefinition = (ActionDefinition) entity;
            createOrUpdateActionDefinition(newActionDefinition);
        }
    }

    public void createOrUpdateFailureCodes(Object entity, Object input) {
        FailureStructure failureStructure = null;
        FailureCodeType failureCodeType = (FailureCodeType) input;

        setFailureCodeVales(failureCodeType);
        ItemGroup itemGroup = getItemGroup(failureCodeType);

        failureStructure = failureStructureService.getFailureStructureForItemGroup(itemGroup);
        if (failureStructure == null) {
            failureStructure = new FailureStructure();
            failureStructure.setForItemGroup(itemGroup);
        }

        failureStructure = buildAssemblyForFailureStructure(failureStructure);
        failureStructureService.update(failureStructure);
    }

    public void createOrUpdateAssemblyDefinition(AssemblyDefinition newAssemblyDefinition) {
        AssemblyDefinition assemblyDefinition = null;
        String warrantyCodeName = "";
        int level = newAssemblyDefinition.getAssemblyLevel().getLevel();
        AssemblyLevel assemblyLevel = findAssemblyLevel(level);
        newAssemblyDefinition.setAssemblyLevel(assemblyLevel);
        int assemblyLevelVar = newAssemblyDefinition.getAssemblyLevel().getLevel();
        String assemblyDefCode = newAssemblyDefinition.getCode().toUpperCase();
        warrantyCodeName = newAssemblyDefinition.getName();
        newAssemblyDefinition.setName(warrantyCodeName);

        // get active assembly definition with same name, code and level
        AssemblyDefinition oldAssemblyDefinition = fetchAssemblyDefinitionByName(assemblyLevelVar, assemblyDefCode, warrantyCodeName);

        if (oldAssemblyDefinition == null) {
            assemblyDefinition = newAssemblyDefinition;
            createAssemblyDefinition(assemblyDefinition);
        } else {
            assemblyDefinition = oldAssemblyDefinition;
            List<I18NAssemblyDefinition> changedDescList = new ArrayList<I18NAssemblyDefinition>();
            I18NAssemblyDefinition changedDesc = null;
            List<I18NAssemblyDefinition> newAssemblyDefinitionList = newAssemblyDefinition.getI18nAssemblyDefinition();
            for (I18NAssemblyDefinition newAssemblyDefinitionDesc : newAssemblyDefinitionList) {
                changedDesc = getAssemblyDefinitionNameForLocale(newAssemblyDefinitionDesc, assemblyDefinition);
                if (changedDesc != null) {
                    changedDescList.add(changedDesc);

                } else {
                    changedDescList.add(newAssemblyDefinitionDesc);
                }
            }
            assemblyDefinition.setI18nAssemblyDefinition(changedDescList);
            updateAssemblyDefinition(assemblyDefinition);
        }
    }

    public void createOrUpdateFailureTypeDefinition(FailureTypeDefinition newFailureTypeDefinition) {
        FailureTypeDefinition failureTypeDefinition = null;
        String warrantyCodeName = "";
        String failureTypeDefCode = newFailureTypeDefinition.getCode().toUpperCase();
        warrantyCodeName = newFailureTypeDefinition.getName();
        newFailureTypeDefinition.setName(warrantyCodeName);
        FailureTypeDefinition oldFailureTypeDefinition = fetchFailureTypeDefinition(failureTypeDefCode, warrantyCodeName);

        if (oldFailureTypeDefinition == null) {
            failureTypeDefinition = newFailureTypeDefinition;
            createFailureTypeDefinition(failureTypeDefinition);
        } else {
            failureTypeDefinition = oldFailureTypeDefinition;
            List<I18NFailureTypeDefinition> changedDescList = new ArrayList<I18NFailureTypeDefinition>();
            I18NFailureTypeDefinition changedDesc = null;
            List<I18NFailureTypeDefinition> newFailureTypeDefinitionList = newFailureTypeDefinition.getI18nFailureTypeDefinition();
            for (I18NFailureTypeDefinition newFailureTypeDefinitionDesc : newFailureTypeDefinitionList) {
                changedDesc = getFailureTypeNameForLocale(newFailureTypeDefinitionDesc, failureTypeDefinition);
                if (changedDesc != null) {
                    changedDescList.add(changedDesc);

                } else {
                    changedDescList.add(newFailureTypeDefinitionDesc);
                }
            }
            failureTypeDefinition.setI18nFailureTypeDefinition(changedDescList);
            updateFailureTypeDefinition(failureTypeDefinition);
        }
    }

    public void createOrUpdateFailureCauseDefinition(FailureCauseDefinition newFailureCauseDefinition) {
        FailureCauseDefinition failureCauseDefinition = null;
        String warrantyCodeName = "";
        String failureCauseDefCode = newFailureCauseDefinition.getCode().toLowerCase();
        warrantyCodeName = newFailureCauseDefinition.getName();
        newFailureCauseDefinition.setName(warrantyCodeName);
        FailureCauseDefinition oldFailureCauseDefinition = fetchFailureCauseDefinition(failureCauseDefCode, warrantyCodeName);

        if (oldFailureCauseDefinition == null) {
            failureCauseDefinition = newFailureCauseDefinition;
            createFailureCauseDefinition(failureCauseDefinition);
        } else {
            failureCauseDefinition = oldFailureCauseDefinition;
            List<I18NFailureCauseDefinition> changedDescList = new ArrayList<I18NFailureCauseDefinition>();
            I18NFailureCauseDefinition changedDesc = null;
            List<I18NFailureCauseDefinition> newFailureCauseDefinitionList = newFailureCauseDefinition.getI18nFailureCauseDefinition();
            for (I18NFailureCauseDefinition newFailureCauseDefinitionDesc : newFailureCauseDefinitionList) {
                changedDesc = getFailureCauseNameForLocale(newFailureCauseDefinitionDesc, failureCauseDefinition);
                if (changedDesc != null) {
                    changedDescList.add(changedDesc);

                } else {
                    changedDescList.add(newFailureCauseDefinitionDesc);
                }
            }
            failureCauseDefinition.setI18nFailureCauseDefinition(changedDescList);
            updateFailureCauseDefinition(failureCauseDefinition);
        }
    }

    public void createOrUpdateActionDefinition(ActionDefinition newActionDefinition) {
        ActionDefinition actionDefinition = null;
        String warrantyCodeName = "";
        String actionDefCode = newActionDefinition.getCode().toUpperCase();
        warrantyCodeName = newActionDefinition.getName();
        newActionDefinition.setName(warrantyCodeName);
        ActionDefinition oldActionDefinition = fetchActionDefinition(actionDefCode, warrantyCodeName);

        if (oldActionDefinition == null) {
            actionDefinition = newActionDefinition;
            createActionDefinition(newActionDefinition);
        } else {
            actionDefinition = oldActionDefinition;
            List<I18NActionDefinition> changedDescList = new ArrayList<I18NActionDefinition>();
            I18NActionDefinition changedDesc = null;
            List<I18NActionDefinition> i18NActionDefinitionList = newActionDefinition.getI18nActionDefinition();
            for (I18NActionDefinition newActionDefinitionDesc : i18NActionDefinitionList) {
                changedDesc = getFailureCauseNameForLocale(newActionDefinitionDesc, actionDefinition);
                if (changedDesc != null) {
                    changedDescList.add(changedDesc);

                } else {
                    changedDescList.add(newActionDefinitionDesc);
                }
            }
            actionDefinition.setI18nActionDefinition(changedDescList);
            updateActionDefinition(actionDefinition);
        }
    }

    private AssemblyDefinition fetchAssemblyDefinitionByName(int type, String code, String name) {
        return failureStructureService.fetchAssemblyDefinitionByName(type, code, name);
    }

    private FailureTypeDefinition findFailureTypeDefinitionByCode(String code) {
        return failureStructureService.findFailureTypeDefinitionByCode(code);
    }

    private FailureCauseDefinition findFailureCauseDefinitionByCode(String code) {
        return failureStructureService.findFailureCauseDefinitionByCode(code);
    }

    private AssemblyLevel findAssemblyLevel(int level) {
        return failureStructureService.findAssemblyLevel(level);
    }

    private void createAssemblyDefinition(AssemblyDefinition assemblyDefinition) {
        failureStructureService.createAssemblyDefinition(assemblyDefinition);
    }

    private void updateAssemblyDefinition(AssemblyDefinition assemblyDefinition) {
        failureStructureService.updateAssemblyDefinition(assemblyDefinition);
    }

    public void setFailureStructureService(FailureStructureService failureStructureService) {
        this.failureStructureService = failureStructureService;
    }

    private FailureTypeDefinition fetchFailureTypeDefinition(String code, String name) {
        return failureStructureService.fetchFailureTypeDefinition(code, name);
    }

    private void updateFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition) {
        failureStructureService.updateFailureTypeDefinition(failureTypeDefinition);
    }

    private void createFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition) {
        failureStructureService.createFailureTypeDefinition(failureTypeDefinition);
    }

    private FailureCauseDefinition fetchFailureCauseDefinition(String code, String name) {
        return failureStructureService.fetchFailureCauseDefinition(code, name);
    }

    private void updateFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition) {
        failureStructureService.updateFailureCauseDefinition(failureCauseDefinition);
    }

    private void createFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition) {
        failureStructureService.createFailureCauseDefinition(failureCauseDefinition);
    }

    private ActionDefinition fetchActionDefinition(String code, String name) {
        return failureStructureService.fetchActionDefinition(code, name);
    }

    private void updateActionDefinition(ActionDefinition actionDefinition) {
        failureStructureService.updateActionDefinition(actionDefinition);
    }

    private void createActionDefinition(ActionDefinition actionDefinition) {
        failureStructureService.createActionDefinition(actionDefinition);
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    private void addFaultCodeToAssembly(Assembly chdAssembly) {
        FaultCode faultCode = null;
        if (chdAssembly.getFaultCode() == null) {
            faultCode = new FaultCode();
        } else {
            faultCode = chdAssembly.getFaultCode();
        }
        faultCode.setDefinition(failureStructureService
                .findOrCreateFaultCodeDefinition(chdAssembly));
        chdAssembly.setFaultCode(faultCode);
    }

    private Assembly findAssemblyFor(int level, String code, String name, Assembly parentAssembly) {
        Assembly childAsm = new Assembly();
        AssemblyDefinition assemblyDefinition = fetchAssemblyDefinitionByName(level, code, name);
        if (assemblyDefinition == null) {
            throw new RuntimeException("Assembly Definition for the level-name-code combination is not present " + level + "-" + name + "-" + code);
        } else {
            childAsm.setDefinition(assemblyDefinition);
            if (parentAssembly != null) {
                parentAssembly.addChildAssembly(childAsm);
            }
            addFaultCodeToAssembly(childAsm);

        }
        return childAsm;
    }

    public I18NAssemblyDefinition getAssemblyDefinitionNameForLocale(I18NAssemblyDefinition newI18NAssemblyDefinition, AssemblyDefinition oldAssemblyDefinition) {
        I18NAssemblyDefinition i18NAssemblyDefinition = null;
        for (I18NAssemblyDefinition oldAssemblyDefinitionDesc : oldAssemblyDefinition.getI18nAssemblyDefinition()) {
            if (oldAssemblyDefinitionDesc.getLocale().equalsIgnoreCase(newI18NAssemblyDefinition.getLocale())) {
                oldAssemblyDefinitionDesc.setName(newI18NAssemblyDefinition.getName());
                i18NAssemblyDefinition = oldAssemblyDefinitionDesc;
                break;
            }
        }
        return i18NAssemblyDefinition;
    }

    public I18NFailureTypeDefinition getFailureTypeNameForLocale(I18NFailureTypeDefinition newI18NFailureTypeDefinition, FailureTypeDefinition oldFailureTypeDefinition) {
        I18NFailureTypeDefinition i18NFailureTypeDefinition = null;
        for (I18NFailureTypeDefinition oldFailureTypeDefinitionDesc : oldFailureTypeDefinition.getI18nFailureTypeDefinition()) {
            if (oldFailureTypeDefinitionDesc.getLocale().equalsIgnoreCase(newI18NFailureTypeDefinition.getLocale())) {
                oldFailureTypeDefinitionDesc.setName(newI18NFailureTypeDefinition.getName());
                i18NFailureTypeDefinition = oldFailureTypeDefinitionDesc;
                break;
            }
        }
        return i18NFailureTypeDefinition;
    }

    public I18NFailureCauseDefinition getFailureCauseNameForLocale(I18NFailureCauseDefinition newI18NFailureCauseDefinition, FailureCauseDefinition oldFailureCauseDefinition) {
        I18NFailureCauseDefinition i18NFailureCauseDefinition = null;
        for (I18NFailureCauseDefinition oldFailureCauseDefinitionDesc : oldFailureCauseDefinition.getI18nFailureCauseDefinition()) {
            if (oldFailureCauseDefinitionDesc.getLocale().equalsIgnoreCase(newI18NFailureCauseDefinition.getLocale())) {
                oldFailureCauseDefinitionDesc.setName(newI18NFailureCauseDefinition.getName());
                i18NFailureCauseDefinition = oldFailureCauseDefinitionDesc;
                break;
            }
        }
        return i18NFailureCauseDefinition;
    }

    public I18NActionDefinition getFailureCauseNameForLocale(I18NActionDefinition newI18NActionDefinition, ActionDefinition oldActionDefinition) {
        I18NActionDefinition i18NActionDefinition = null;
        for (I18NActionDefinition oldActionDefinitionDesc : oldActionDefinition.getI18nActionDefinition()) {
            if (oldActionDefinitionDesc.getLocale().equalsIgnoreCase(newI18NActionDefinition.getLocale())) {
                oldActionDefinitionDesc.setName(newI18NActionDefinition.getName());
                i18NActionDefinition = oldActionDefinitionDesc;
                break;
            }
        }
        return i18NActionDefinition;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }


    public void activateOrDeactivateAssembly(Assembly mainAssembly) {
        FaultCode faultCode = null;
        if (symptom != null && StringUtils.hasText(symptom)) {
            if(mainAssembly.getFaultCode().getId()!=null){
                   faultCode = (FaultCode)failureStructureService.updateAndReturnObject(mainAssembly.getFaultCode());
            }
            else {
                    faultCode = (FaultCode)failureStructureService.saveAndReturnObject(mainAssembly.getFaultCode());
            }
           createOrUpdateFaultFoundAndCausedBy(faultCode);
        } else {
            if (status.equalsIgnoreCase(FailureCodeType.Status.ACTIVE.toString())) {
                Set<Assembly> assemblies = mainAssembly.getComposedOfAssemblies();
                for (Assembly assembly : assemblies) {
                    assembly.setActive(false);
                    assembly.getD().setActive(false);
                }
            } else {
                mainAssembly.setActive(false);
                mainAssembly.getD().setActive(false);
            }
        }
    }

    private void createOrUpdateFaultFoundAndCausedBy(FaultCode faultCode) {
        FailureType failureType = null;
        if(faultCode.getId()!=null)
            failureType = findFailureTypeForFaultCode(faultCode);
        if (failureType == null) {
            createNewFailureType(faultCode);
        } else {
            updateFailureType(failureType);
        }
    }

    public void updateFailureType(FailureType failureType) {
        if (cause != null && StringUtils.hasText(cause)) {
            // get all the cause for this failure type
            List<FailureCause> failureCauseList = failureStructureService.findFailureCausesForFailureType(failureType);
            if (failureCauseList.isEmpty()) {   // add the new fault cause
                createFailureCause(failureType);

            } else {
                boolean causeFound = false;
                for (FailureCause failureCause : failureCauseList) {
                    if (failureCause.getDefinition().getCode().equalsIgnoreCase(cause)) {
                        causeFound = true;
                        if (status.equalsIgnoreCase(FailureCodeType.Status.ACTIVE.toString())) {
                            // do nothing
                        } else {
                            failureCause.getD().setActive(false);
                            failureStructureService.updateAndReturnObject(failureCause);
                        }
                        break;
                    }
                }
                if (!causeFound) {
                    createFailureCause(failureType);
                }
            }
        } else {
            // inactivate the failureType for fault Code
            if (status.equalsIgnoreCase(FailureCodeType.Status.INACTIVE.toString())) {
                failureType.getD().setActive(false);
                failureStructureService.updateAndReturnObject(failureType);
            }
        }
    }

    public void createFailureCause(FailureType failureType) {
        FailureCause failureCauseNew = null;
        if (status.equalsIgnoreCase(FailureCodeType.Status.ACTIVE.toString())) {
            failureCauseNew = new FailureCause();
            FailureCauseDefinition failureCauseDefinition = findFailureCauseDefinitionByCode(cause);
            if (failureCauseDefinition != null)
                failureCauseNew.setDefinition(failureCauseDefinition);
            else {
                throw new RuntimeException("Failure Cause Definition is not available for the code:  " + cause);
            }
            failureCauseNew.setFailureType(failureType);
            failureStructureService.saveAndReturnObject(failureCauseNew);
        } else {
            throw new RuntimeException("Cannot inactivate Failure Cause which is not active: Cause " + cause);
        }
    }

    public void createNewFailureType(FaultCode faultCode) {
        FailureType failureType = null;
        FailureCause failureCauseNew = null;
        if (status.equalsIgnoreCase(FailureCodeType.Status.ACTIVE.toString())) {
            if (cause != null && StringUtils.hasText(cause)) {
                // new failure type flow
                failureType = new FailureType();
                failureType.setForFaultCode(faultCode);
                FailureTypeDefinition failureTypeDefinition = findFailureTypeDefinitionByCode(symptom);
                if (failureTypeDefinition != null) {
                    failureType.setDefinition(failureTypeDefinition);
                } else {
                    throw new RuntimeException("Failure Type Definition is not available for the code:  " + symptom);
                }
                failureStructureService.saveAndReturnObject(failureType);

                failureCauseNew = new FailureCause();
                FailureCauseDefinition failureCauseDefinition = findFailureCauseDefinitionByCode(cause);
                if (failureCauseDefinition != null)
                    failureCauseNew.setDefinition(failureCauseDefinition);
                else {
                    throw new RuntimeException("Failure Cause Definition is not available for the code:  " + cause);
                }
                failureCauseNew.setFailureType(failureType);
                failureStructureService.saveAndReturnObject(failureCauseNew);
            } else { // only creating failure type
                failureType = new FailureType();
                failureType.setForFaultCode(faultCode);
                FailureTypeDefinition failureTypeDefinition = findFailureTypeDefinitionByCode(symptom);
                if (failureTypeDefinition != null)
                    failureType.setDefinition(failureTypeDefinition);
                else {
                    throw new RuntimeException("Failure Type Definition is not available for the code:  " + symptom);
                }
                failureStructureService.saveAndReturnObject(failureType);
            }
        } else {
            throw new RuntimeException("Cannot inactivate Failure type which is not active : Symptom " + symptom);
        }
    }

    public FailureType findFailureTypeForFaultCode(FaultCode faultCode) {
        return failureStructureService.findFailureTypeForFaultCode(faultCode);
    }


    public ItemGroup getItemGroup(FailureCodeType failureCodeType) {
        String productCode = "";
        String modelCode = "";
        String itemGroupType = "";
        String itemGroupCode = "";
        ItemGroup itemGroup = null;
        if (failureCodeType.getProductCode() != null && StringUtils.hasText(failureCodeType.getProductCode())) {
            productCode = failureCodeType.getProductCode();
            itemGroupType = "PRODUCT";
        }
        if (failureCodeType.getModelCode() != null && StringUtils.hasText(failureCodeType.getModelCode())) {
            modelCode = failureCodeType.getModelCode();
            itemGroupType = "MODEL";
        }
        if (productCode != null && modelCode != null) {
            itemGroup = itemGroupService.findItemGroupForModel(productCode, modelCode);
        } else {
            itemGroup = itemGroupService.findItemGroupByCodeAndType(itemGroupCode, itemGroupType);
        }

        return itemGroup;
    }

    private void setFailureCodeVales(FailureCodeType failureCodeType) {
        system = (failureCodeType.getSystem() != null) ? failureCodeType.getSystem().toUpperCase().trim() : "";
        systemName = (failureCodeType.getSystemName() != null) ? failureCodeType.getSystemName().toUpperCase().trim() : "";
        subSystem = (failureCodeType.getSubSystem() != null) ? failureCodeType.getSubSystem().toUpperCase().trim() : "";
        subSystemName = (failureCodeType.getSubSystemName() != null) ? failureCodeType.getSubSystemName().toUpperCase().trim() : "";
        component = (failureCodeType.getComponent() != null) ? failureCodeType.getComponent().toUpperCase().trim() : "";
        componentName = (failureCodeType.getComponentName() != null) ? failureCodeType.getComponentName().toUpperCase().trim() : "";
        subComponent = (failureCodeType.getSubComponent() != null) ? failureCodeType.getSubComponent().toUpperCase().trim() : "";
        subComponentName = (failureCodeType.getSubComponentName() != null) ? failureCodeType.getSubComponentName().toUpperCase().trim() : "";
        symptom = (failureCodeType.getSymptom() != null) ? failureCodeType.getSymptom().toUpperCase().trim() : "";
        cause = (failureCodeType.getCause() != null) ? failureCodeType.getCause().toUpperCase().trim() : "";
        status = (failureCodeType.getStatus() != null) ? failureCodeType.getStatus().toString() : "ACTIVE";
    }

    public FailureStructure buildAssemblyForFailureStructure(FailureStructure failureStructure) {
        Set<Assembly> assemblies = failureStructure.getAssemblies();
        Assembly assembly = null;
        Assembly systemAsm = null;
        Assembly subSystemAsm = null;
        Assembly componentAsm = null;
        Assembly subComponentAsm = null;
        Assembly childAsm = null;

        for (Assembly oldAssembly : assemblies) {
            AssemblyDefinition assemblyDefinition1 = oldAssembly.getDefinition();
            if (oldAssembly.getActive().equals(true) && assemblyDefinition1.getCode().equalsIgnoreCase(system) && assemblyDefinition1.getName().equalsIgnoreCase(systemName)) {
                assembly = oldAssembly;
                break;
            }
        }

        if (assembly != null) {
            if (subSystem != null && StringUtils.hasText(subSystem) && subSystemName != null && StringUtils.hasText(subSystem)) {
                subSystemAsm = getChildAssembly(SUB_SYSTEM, subSystem, subSystemName, assembly);
                if (subSystemAsm != null) {
                    if (component != null && StringUtils.hasText(component) && componentName != null && StringUtils.hasText(componentName)) {
                        componentAsm = getChildAssembly(COMPONENT, component, componentName, subSystemAsm);
                        if (componentAsm != null) {
                            if (subComponent != null && StringUtils.hasText(subComponent) && subComponentName != null && StringUtils.hasText(subComponentName)) {
                                subComponentAsm = getChildAssembly(SUB_COMPONENT, subComponent, subComponentName, componentAsm);
                                if (subComponentAsm != null) {
                                    activateOrDeactivateAssembly(subComponentAsm);
                                }
                            } else {
                                activateOrDeactivateAssembly(componentAsm);   // deactivate or activate on Component level
                            }
                        }
                    } else {
                        activateOrDeactivateAssembly(subSystemAsm);   // deactivate or activate on Sub System level
                    }
                }
            } else {
                activateOrDeactivateAssembly(assembly);   // deactivate or activate on System level
            }
        } else { // create new Assembly
            if (status.equalsIgnoreCase(FailureCodeType.Status.ACTIVE.toString())) {
                systemAsm = findAssemblyFor(SYSTEM, system, systemName, null);
                if (subSystem != null && StringUtils.hasText(subSystem) && subSystemName != null && StringUtils.hasText(subSystem)) {
                    subSystemAsm = findAssemblyFor(SUB_SYSTEM, subSystem, subSystemName, systemAsm);
                    if (component != null && StringUtils.hasText(component) && componentName != null && StringUtils.hasText(componentName)) {
                        componentAsm = findAssemblyFor(COMPONENT, component, componentName, subSystemAsm);
                        if (subComponent != null && StringUtils.hasText(subComponent) && subComponentName != null && StringUtils.hasText(subComponentName)) {
                            subComponentAsm = findAssemblyFor(SUB_COMPONENT, subComponent, subComponentName, componentAsm);
                            if (subComponentAsm != null)
                                activateOrDeactivateAssembly(subComponentAsm);
                        } else {
                            activateOrDeactivateAssembly(componentAsm);   // deactivate or activate on Component level
                        }
                    } else {
                        activateOrDeactivateAssembly(subSystemAsm);   // deactivate or activate on Sub System level
                    }
                } else {
                    activateOrDeactivateAssembly(systemAsm);   // deactivate or activate on System level
                }
                failureStructure.addAssembly(systemAsm);
            } else {
                throw new RuntimeException("Cannot inactivate Assembly which is not active: Code-Name :" + system + "-" + systemName);
            }
        }
        return failureStructure;
    }


    public Assembly getChildAssembly(int level, String code, String name, Assembly parentAssembly) {
        Assembly childAssembly = null;
        AssemblyDefinition composedAssemblyDefinition = null;
        Set<Assembly> composedOfAssemblies = parentAssembly.getComposedOfAssemblies();
        for (Assembly composedOfAssembly : composedOfAssemblies) {
            composedAssemblyDefinition = composedOfAssembly.getDefinition();
            if (composedOfAssembly.getActive().equals(true) && composedAssemblyDefinition.getCode().equalsIgnoreCase(code) && composedAssemblyDefinition.getName().equalsIgnoreCase(name)) {
                childAssembly = composedOfAssembly;
                break;
            }
        }
        if (childAssembly == null) {
            if (status.equalsIgnoreCase(FailureCodeType.Status.ACTIVE.toString())) {
                childAssembly = findAssemblyFor(level, code, name, parentAssembly);
            } else {
                throw new RuntimeException("Cannot inactivate Assembly which is not active in system. Code-Name : " + code + "-" + name);
            }
        }
        return childAssembly;
    }
}
