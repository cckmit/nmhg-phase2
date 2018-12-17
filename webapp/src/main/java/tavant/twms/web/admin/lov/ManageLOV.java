/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.admin.lov;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.I18NLovText;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class ManageLOV extends I18nActionSupport implements Preparable, Validateable {
	
	private LovRepository lovRepository;
	private ListOfValues listOfValues;
	private String lovTypeName;
	private String id;
	private List<ProductLocale> locales;
	private ProductLocaleService productLocaleService;
	private String code;
		
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<ProductLocale> getLocales() {
		return locales;
	}

	public void setLocales(List<ProductLocale> locales) {
		this.locales = locales;
	}
	
	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public void setProductLocaleService(
			ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public String showLOV() {
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String updateLOV() {
		locales = productLocaleService.findAll();
		listOfValues.getI18nLovTexts().clear();
		listOfValues.setCode(code);		
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			String localizedMessages[] = (String[]) params
					.get("localizedDescriptionMessages_" + locale.getLocale());
			if(localizedMessages[0].equals("")) {
				if(i18nLocale.equals("en_US")) {
				addActionError("error.manageBusinessRule.descriptionUS");
				}
			}
			else {
			I18NLovText i18LovText = new I18NLovText();
			if(i18nLocale.equals("en_US")) {
				listOfValues.setDescription(localizedMessages[0]);
			}
			i18LovText.setDescription(localizedMessages[0]);
			i18LovText.setLocale(i18nLocale);
			listOfValues.getI18nLovTexts().add(i18LovText);
			}
		}
		if(hasActionErrors()) {
			return INPUT;
		}
		lovRepository.update(listOfValues);
		String lovCode = listOfValues.getCode();
		addActionMessage("message.listOfValues.updateSuccess",new String[]{lovCode});
		return SUCCESS;
	}
	
	public String deactivateLOV() {
		listOfValues = lovRepository.findByCode(getCapitalisedLOVTypeName(), id);
		listOfValues.setState(ListOfValues.EXPIRED_STATE);
		lovRepository.update(listOfValues);
		String lovCode = listOfValues.getCode();
		addActionMessage("message.listOfValues.deactivateSuccess",new String[] {lovCode});
		return SUCCESS;
	}
	
	public String activateLOV() {
		listOfValues = lovRepository.findByCode(getCapitalisedLOVTypeName(), id);
		listOfValues.setState(ListOfValues.ACTIVE_STATE);
		lovRepository.update(listOfValues);
		String lovCode = listOfValues.getCode();
		addActionMessage("message.listOfValues.activateSuccess",new String[] {lovCode});
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String saveLOV() {
		locales = productLocaleService.findAll();
		listOfValues.setCode(code.trim());
		
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			String localizedMessages[] = (String[]) params
					.get("localizedDescriptionMessages_" + locale.getLocale());
			if(localizedMessages[0].equals("")) {
				if(i18nLocale.equals("en_US")) {
				addActionError("error.manageBusinessRule.descriptionUS");
				}
			}
			else {
			I18NLovText i18LovText = new I18NLovText();
			if(i18nLocale.equals("en_US")) {
				listOfValues.setDescription(localizedMessages[0]);
			}
			
			i18LovText.setDescription(localizedMessages[0]);
			i18LovText.setLocale(i18nLocale);
			listOfValues.getI18nLovTexts().add(i18LovText);
			}
		}
		if(hasActionErrors()) {
			return INPUT;
		}
		lovRepository.save(listOfValues);
		String lovCode = listOfValues.getCode();
		addActionMessage("message.listOfValues.saveSuccess",new String[] {lovCode});
		return SUCCESS;
	}
	
	
	
	public void prepare() throws Exception {
		if(id != null && !id.trim().equals("")) {
			listOfValues = lovRepository.findByCode(getCapitalisedLOVTypeName(), id);
		} else {
			listOfValues = (ListOfValues) lovRepository.getClassFromClassName(getCapitalisedLOVTypeName()).newInstance();
		}
		locales = productLocaleService.findAll();
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public void validate() {
		locales = productLocaleService.findAll();
		int count = 0;
		if((code == null) || (code.trim().equals(""))) {
			addActionError("error.listOfValues.nonEmptyCode");
		}else{
			 ListOfValues dbLov =	lovRepository.findByCode(getCapitalisedLOVTypeName(), code.trim());
			 if(dbLov !=null){
				 addActionError("error.listOfValues.duplicate"); 
			 }			
	    }
		
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			String localizedMessages[] = (String[]) params
					.get("localizedDescriptionMessages_" + locale.getLocale());
			if(localizedMessages[0].equals("")) {
				if(i18nLocale.equals("en_US")) {
					addActionError("error.manageBusinessRule.descriptionUS");
				}
			}
		}
	}
	
	//Only getters and setters follow...
	public String getLovTypeName() {
		return lovTypeName;
	}

	public void setLovTypeName(String name) {
		lovTypeName = name;
	}

	public ListOfValues getListOfValues() {
		return listOfValues;
	}

	public void setListOfValues(ListOfValues listOfValues) {
		this.listOfValues = listOfValues;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCapitalisedLOVTypeName() {
		if(lovTypeName.toLowerCase().equals("ACCOUNTABILITYCODE"))
		{	
			return StringUtils.capitalize(getText("label.claim.accountabilityCode"));
		}	
		else
		{	
			return StringUtils.capitalize(lovTypeName);
		}	
	}
}

