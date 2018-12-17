package tavant.twms.web.admin.miscellaneousParts;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.MiscellaneousItem;
import tavant.twms.domain.catalog.MiscellaneousItemConfigService;
import tavant.twms.domain.catalog.I18NMiscItem;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class ManageMiscellaneousParts extends SummaryTableAction implements
		ValidationAware , Preparable{
	
	private static Logger logger = Logger.getLogger(ManageMiscellaneousParts.class);

	private List<MiscellaneousItem> items;

	private MiscellaneousItemConfigService miscellaneousItemConfigService;
	
	private MiscellaneousItem miscellaneousItem;

    private ProductLocaleService productLocaleService;

    private List<ProductLocale> locales;

    public String detail()
	{		
		miscellaneousItem = miscellaneousItemConfigService.findMiscellaneousItemById(Long.parseLong(getId()));
		return SUCCESS;
	}

     public void prepare() throws Exception {
    	locales = productLocaleService.findAll();
    }

    @Override
	protected PageResult<?> getBody() {
		return miscellaneousItemConfigService.findAllMiscellaneousPart(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("", "id", 0, "Number", "id",
				false, true, true, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.miscellaneousParts.partNumber", "partNumber", 50,
				"string", "partNumber", true, false, false, false));
		tableHeadData.add(new SummaryTableColumn(
				"columnTitle.miscellaneousParts.partDescription", "description",
				50, "String",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		return tableHeadData;
	}

	public String createMiscellaneousParts() {
		return SUCCESS;
	}

	public String saveMiscellaneousParts() {		
		validatePartInfo();		
		if(hasActionErrors()){
			return INPUT;
		}		
		for (MiscellaneousItem element : items) {
			element.removeWhiteSpacesFromFields();
			miscellaneousItemConfigService.createMiscItem(element);		
		}
		addActionMessage("message.misc.part.add.success");
		return SUCCESS;
	}

	public String updateMiscellaneousPart()
	{
		validateMiscPart();
		if(hasActionErrors()){
			return INPUT;
		}		
		try {
			miscellaneousItem.removeWhiteSpacesFromFields();
			miscellaneousItemConfigService.updateMiscellaneousItem(miscellaneousItem);
			addActionMessage("message.misc.part.update.success");
		}
		catch (Exception exception)
		{
			logger.debug(exception.getMessage());
			addActionError("message.misc.part.update.error");
		}
		return SUCCESS;
	}
	
	private void validateMiscPart() {
		if (StringUtils.isBlank(miscellaneousItem.getPartNumber())) {
			addActionError("error.miscellaneousPart.noPartNameSpecified");
		}

        if(miscellaneousItem.getI18nMiscTexts()!=null){
            boolean isDefaultEnglisMsgSet=false;
            for (I18NMiscItem item : miscellaneousItem.getI18nMiscTexts()) {
                if(item.getLocale().equalsIgnoreCase("en_US")
                        && org.springframework.util.StringUtils.hasText(item.getDescription())){
                    isDefaultEnglisMsgSet=true;
                    break;
                }
            }
            if(!isDefaultEnglisMsgSet){
                addActionError("error.miscellaneousPart.descriptionRequiredForUS");
            }
        }

	}

	private void validatePartInfo() {
        
        if (items == null || items.size() == 0) {
			addActionError("error.manage.miscellaneousPart.noItemsAdded");
			return;
		}

		boolean isPartNoNotSpecified = false;
		boolean isDuplicatePartNoSpecified = false;
		boolean isPartDuplicated = false;
		boolean isPartDescErrorAdded = false;
		List<String> miscParts = new ArrayList<String>() ;

		for (MiscellaneousItem element : items) {
			if (StringUtils.isBlank(element.getPartNumber())) {
				isPartNoNotSpecified = true;
			}

            if (element.getI18nMiscTexts() != null) {
            	boolean isDefaultEnglishMsgGiven = false;
                for (I18NMiscItem item : element.getI18nMiscTexts()) {                	
                    if (item.getLocale().equalsIgnoreCase("en_US")
                            && org.springframework.util.StringUtils.hasText(item.getDescription())) {
                        isDefaultEnglishMsgGiven = true;
                        break;
                    }
                }
                if (!isDefaultEnglishMsgGiven && !isPartDescErrorAdded) {
                	isPartDescErrorAdded = true;
                    addActionError("error.miscellaneousPart.descriptionRequiredForUS");
                }
            }

			if(!StringUtils.isBlank(element.getPartNumber())){				
				if(miscellaneousItemConfigService.findIfMiscellaneousPartExists(element.getPartNumber())){
					isDuplicatePartNoSpecified = true;
				}				
			}		
			 if(miscParts.contains(element.getPartNumber()) && !isPartDuplicated){ 
				 addActionError("error.miscellaneousPart.duplication");
				 isPartDuplicated = true;
				 break;
			 } else{
				 miscParts.add(element.getPartNumber()); 
			 }
		}
		
		if(isPartNoNotSpecified){
			addActionError("error.miscellaneousPart.noPartNameSpecified");
		}
		
		if(isDuplicatePartNoSpecified){
			addActionError("error.miscellaneousPart.samePartNameSpecified");
		}

	}

	public List<MiscellaneousItem> getItems() {
		return items;
	}

	public void setItems(List<MiscellaneousItem> items) {
		this.items = items;
	}

	public void setMiscellaneousItemConfigService(
			MiscellaneousItemConfigService miscellaneousItemConfigService) {
		this.miscellaneousItemConfigService = miscellaneousItemConfigService;
	}

	public MiscellaneousItem getMiscellaneousItem() {
		return miscellaneousItem;
	}

	public void setMiscellaneousItem(MiscellaneousItem miscellaneousItem) {
		this.miscellaneousItem = miscellaneousItem;
	}

    public void setProductLocaleService(ProductLocaleService productLocaleService) {
        this.productLocaleService = productLocaleService;
    }

    public List<ProductLocale> getLocales() {
        return locales;
    }

    public void setLocales(List<ProductLocale> locales) {
        this.locales = locales;
    }
   
}
