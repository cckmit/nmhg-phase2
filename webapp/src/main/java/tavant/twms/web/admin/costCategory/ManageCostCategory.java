package tavant.twms.web.admin.costCategory;

import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionRepository;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.web.i18n.I18nActionSupport;


@SuppressWarnings("serial")
public class ManageCostCategory extends I18nActionSupport implements BUSpecificSectionNames {


    private static Logger logger = LogManager.getLogger(ManageCostCategory.class);
    private ConfigParamService configParamService;
    private CostCategoryRepository costCategoryRepository;
    private PaymentDefinitionRepository paymentDefinitionRepository;
    private PaymentDefinitionAdminService paymentDefinitionAdminService;
    private PaymentService paymentService;
    private List<CostCategory> costCategories;
    private Map<CostCategory, Boolean> costCategoriesMap = new HashMap<CostCategory, Boolean>();
    private List<ItemGroup> products = new ArrayList<ItemGroup>();

	public List<ItemGroup> getProducts() {
		return products;
	}

	public void setProducts(List<ItemGroup> products) {
		this.products = products;
	}

	public String getMessageKey(String messageKey) {
        return NAMES_AND_KEY.get(messageKey);
    }

    public String execute() {
        prepareData();
        return SUCCESS;
    }


    private void prepareData() {
    	products = costCategoryRepository.findProducts(ItemGroup.PRODUCT);
        List<Object> configuredCostCategories = configParamService
                .getListofObjects(ConfigName.CONFIGURED_COST_CATEGORIES.getName());
        List<CostCategory> configCostCategories = new ArrayList<CostCategory>();
        for (Object costCategory : configuredCostCategories) {
            configCostCategories.add((CostCategory)costCategory);
        }
        setCostCategoryMapDetails(configCostCategories);
    }

    public String update() {
        removeNullElements(costCategories);
        setApplicableProductsWithNullIfDeselectAll(costCategories);

        List<PaymentDefinition> pymtDefinitions = getPaymentDefinitionsWithCostCategories(costCategories);
        if(pymtDefinitions!=null && !pymtDefinitions.isEmpty()){
        	products = costCategoryRepository.findProducts(ItemGroup.PRODUCT);
            setCostCategoryMapDetails(costCategories);
            addActionError("label.costCategory.pymtExistsWithCategory");
            return INPUT;
        }
        paymentService.saveCostCategories(costCategories);
        addActionMessage("message.costCategory.costCategoriesUpdateSuccess");
        return SUCCESS;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public void setCostCategoryRepository(
            CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    public List<CostCategory> getCostCategories() {
        return costCategories;
    }

    public void setCostCategories(List<CostCategory> costCategories) {
        this.costCategories = costCategories;
    }

    public Map<CostCategory, Boolean> getCostCategoriesMap() {
        return costCategoriesMap;
    }

    public void setCostCategoriesMap(Map<CostCategory, Boolean> costCategoriesMap) {
        this.costCategoriesMap = costCategoriesMap;
    }

    public void setPaymentDefinitionRepository(PaymentDefinitionRepository paymentDefinitionRepository) {
        this.paymentDefinitionRepository = paymentDefinitionRepository;
    }

    public void setPaymentDefinitionAdminService(PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    private void removeNullElements(List<CostCategory> costCategories) {
        Iterator<CostCategory> costcategoryIter = costCategories.iterator();
        while (costcategoryIter.hasNext()) {
            CostCategory eachCostCategory = costcategoryIter.next();
            if (eachCostCategory == null || eachCostCategory.getId()==null) {
            	costcategoryIter.remove();
            }
        }
    }
    
    private void setApplicableProductsWithNullIfDeselectAll(List<CostCategory> costCategories) {
        Iterator<CostCategory> costcategoryIter = costCategories.iterator();
        while (costcategoryIter.hasNext()) {
            CostCategory eachCostCategory = costcategoryIter.next();
            if (eachCostCategory != null && eachCostCategory.getApplicableProducts() != null 
            		&& eachCostCategory.getApplicableProducts().size() == 1 && eachCostCategory.getApplicableProducts().get(0).getId() == null) {
            	eachCostCategory.getApplicableProducts().remove(eachCostCategory.getApplicableProducts().get(0));
            }
        }
    }

    private List<PaymentDefinition> getPaymentDefinitionsWithCostCategories(List<CostCategory> costCategories) {
        List<Section> sections = paymentDefinitionAdminService.findAllSections();
        List<PaymentDefinition> paymentDefinitions = new ArrayList<PaymentDefinition>();
        Iterator<Section> sectionIter = sections.iterator();
        while (sectionIter.hasNext()) {
            Section eachSection = sectionIter.next();
            boolean isExisting = false;
            for (CostCategory costCategory : costCategories) {
                if (costCategory.getName().equals(eachSection.getName())
                        || Section.TOTAL_CLAIM.equals(eachSection.getName())) {
                    isExisting = true;
                    break;
                }
            }
            if (isExisting) {
                sectionIter.remove();
            }
        }
        if(sections!=null && !sections.isEmpty()){
           paymentDefinitions= paymentDefinitionRepository.findAllDefinitionsWithSections(sections); 
        }
        return paymentDefinitions;
    }

    private void setCostCategoryMapDetails(List<CostCategory> configCostCategories) {
        Map<CostCategory, Boolean> costCategoriesMap = new TreeMap<CostCategory, Boolean>();
        costCategories = costCategoryRepository.findAll();
        for (CostCategory costCategory : costCategories) {
            boolean isConfigured = false;
            for (Object configuredCostCategory : configCostCategories) {
                if (((CostCategory) configuredCostCategory).getCode().equals(costCategory.getCode())
                	|| CostCategory.OEM_PARTS_COST_CATEGORY_CODE.equals(costCategory.getCode())
                        || CostCategory.NON_OEM_PARTS_COST_CATEGORY_CODE.equals(costCategory.getCode())
                        || CostCategory.TRAVEL_COST_CATEGORY_CODE.equals(costCategory.getCode())){
                	isConfigured = true;
                	break;
                }
                	
            }
            costCategoriesMap.put(costCategory, new Boolean(isConfigured));
        }
        setCostCategoriesMap(costCategoriesMap);
    }
}
