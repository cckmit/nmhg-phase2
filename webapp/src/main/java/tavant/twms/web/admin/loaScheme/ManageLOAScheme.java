package tavant.twms.web.admin.loaScheme;

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

import com.domainlanguage.money.Money;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.util.StringUtils;
import tavant.twms.domain.loa.LimitOfAuthorityLevel;
import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.loa.LimitOfAuthoritySchemeService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.*;

@SuppressWarnings("serial")
public class ManageLOAScheme extends I18nActionSupport implements Validateable, ServletResponseAware, Preparable {
	private static Logger logger = LogManager.getLogger(ManageLOAScheme.class);

	private LimitOfAuthoritySchemeService loaService;

	private LimitOfAuthorityScheme limitOfAuthorityScheme;

	private String id;

	protected String searchPrefix;

	private UserRepository userRepository;

	private List<Currency> currencyList = new ArrayList<Currency>();

	public String createLOAScheme() {
		return SUCCESS;
	}

	// Manage LOA SCHEME
	public String manageLOAScheme() throws Exception {
		return SUCCESS;
	}

	// Saves LOA SCHEME
	public String saveLOAScheme() throws Exception {
		for (LimitOfAuthorityLevel loaLevel : limitOfAuthorityScheme.getLoaLevels()) {
			loaLevel.setLoaScheme(limitOfAuthorityScheme);
		}
		Collections.sort(limitOfAuthorityScheme.getLoaLevels());
		loaService.save(limitOfAuthorityScheme);
		addActionMessage("message.manageLOAScheme.createSuccess");
		return SUCCESS;
	}

	//  UPDATES SCHEME
	public String updateLOAScheme() throws Exception {
		for (LimitOfAuthorityLevel limitOfAuthorityLevel : limitOfAuthorityScheme.getLoaLevels()) {
				limitOfAuthorityLevel.setLoaScheme(limitOfAuthorityScheme);
		}
		Collections.sort(limitOfAuthorityScheme.getLoaLevels());
		loaService.update(limitOfAuthorityScheme);
		addActionMessage("message.manageLOAScheme.updateLOSSchemeSuccess");
		return SUCCESS;
	}
	
	public String deleteLOAScheme() throws Exception {
		for (LimitOfAuthorityLevel limitOfAuthorityLevel : limitOfAuthorityScheme.getLoaLevels()) {
				limitOfAuthorityLevel.setLoaScheme(limitOfAuthorityScheme);
		}
		Collections.sort(limitOfAuthorityScheme.getLoaLevels());
		loaService.deleteLOAScheme(limitOfAuthorityScheme);
		addActionMessage("message.manageLOAScheme.deleteLOSSchemeSuccess");
		return SUCCESS;
	}
	
	// All LOA Users(They will have Processor role).
	public String allLOAUsers() {
			List<User> users = userRepository.findProcessorUsersWithNameLike(Role.PROCESSOR,getSearchPrefix());
			return generateAndWriteComboboxJson(users, "id", "name");
	}

	@Override
	public void validate() {
		if (limitOfAuthorityScheme.getId() == null) {
			validateLOASchemeCode();
			validateLOASchemeName();
		}
		if (limitOfAuthorityScheme.getLoaLevels() == null || limitOfAuthorityScheme.getLoaLevels().size() ==0) {
			addActionError("error.manageLAOScheme.loaSchemeLevelRequired");
		} else {
			for (LimitOfAuthorityLevel loaLevel : limitOfAuthorityScheme.getLoaLevels()) {
				validateLOALevel(loaLevel);
			}
			validateDuplicateUser(limitOfAuthorityScheme.getLoaLevels());
			if (!hasActionErrors() && !hasFieldErrors()) {
				List<LimitOfAuthorityLevel> levels = limitOfAuthorityScheme.getLoaLevels();
				Collections.sort(levels);
				validateLOALevels(levels);
			}
		}
	}
	
	private void validateDuplicateUser(List<LimitOfAuthorityLevel> loaLevels) {
		Set<String> users = new HashSet<String>();
		for (LimitOfAuthorityLevel loaLevel : loaLevels) {
			if (loaLevel != null && !users.add(loaLevel.getLoaUser().getName())) {
				addActionError("error.manageLOAScheme.loaSchemeDuplicateLogin");
				break;
			}
		}
	}
	private void validateApprovalLimits(List<Money> appLimits) {
    	boolean hasPriceEntry = false;
    	for (Money money : appLimits) {
	   		if(money!= null && 
	   				money.breachEncapsulationOfAmount().signum()<0)
	   		{
	   			addActionError("error.manageLAOScheme.invalidApprovalLimit");
	   		}
	   		if(!hasPriceEntry && money!= null && money.breachEncapsulationOfAmount().floatValue() > 0.00) 
	   			hasPriceEntry = true;		   	
	   	}
	   	if(!hasPriceEntry) {
	   		addActionError("error.manageLAOScheme.approvalLimitRequired");
	   	}
    }

	private void validateLOALevel(LimitOfAuthorityLevel loaLevel) {
		if (loaLevel.getApprovalLimits() == null || loaLevel.getApprovalLimits().size() == 0) {
			addActionError("error.manageLAOScheme.approvalLimitsRequired");
		} else {
			validateApprovalLimits(loaLevel.getApprovalLimits());
		}
	}

	public void validateLOASchemeName() {
		if (StringUtils.hasText(limitOfAuthorityScheme.getName())) {
			LimitOfAuthorityScheme loaScheme = loaService.findByName(limitOfAuthorityScheme.getName());
			if (loaScheme != null) {
				addActionError("error.manageLAOScheme.nameAlreadyExists");
			}
		}
	}

	public void validateLOASchemeCode() {
		if (StringUtils.hasText(limitOfAuthorityScheme.getCode())) {
			LimitOfAuthorityScheme loaScheme = loaService.findByName(limitOfAuthorityScheme.getCode());
			if (loaScheme != null) {
				addActionError("error.manageLAOScheme.codeAlreadyExists");
			}
		} 
	}
	
	private void validateLOALevels(List<LimitOfAuthorityLevel> levels) {
		LimitOfAuthorityLevel levelArray[] = new LimitOfAuthorityLevel[levels.size()];
		levels.toArray(levelArray);
		for (int i = 1; i < levelArray.length; i++) {
			LimitOfAuthorityLevel previousLevel = (LimitOfAuthorityLevel) levelArray[i - 1];
			LimitOfAuthorityLevel currentLevel = (LimitOfAuthorityLevel) levelArray[i];
			if (currentLevel.getLoaLevel() >= previousLevel.getLoaLevel()) {
				List<Money> preApprovalLimits = previousLevel.getApprovalLimits();
				List<Money> curApprovalLimits = currentLevel.getApprovalLimits();
				for (Money curMoney : curApprovalLimits) {
					for (Money preMoney : preApprovalLimits) {
						if (curMoney.breachEncapsulationOfCurrency().getCurrencyCode().equalsIgnoreCase(
								preMoney.breachEncapsulationOfCurrency().getCurrencyCode())) {
							if (currentLevel.getLoaLevel().intValue() > previousLevel.getLoaLevel().intValue()) {
								if (curMoney.breachEncapsulationOfAmount().doubleValue() >= preMoney
										.breachEncapsulationOfAmount().doubleValue()) {
									break;
								} else {
									addActionError("error.manageLOAScheme.preAppLimitIsLTCurrentAppLimit",
											new String[] { currentLevel.getLoaLevel().toString(),
													currentLevel.getLoaUser().getName(),
													curMoney.breachEncapsulationOfCurrency().getCurrencyCode() });
									return;
								}
							} else {
								if (curMoney.breachEncapsulationOfAmount().doubleValue() == preMoney
										.breachEncapsulationOfAmount().doubleValue()) {
									break;
								} else {
									addActionError("error.manageLOAScheme.preAppLimitIsNETCurrentAppLimit",
											new String[] { currentLevel.getLoaLevel().toString(),
													currentLevel.getLoaUser().getName(),
													curMoney.breachEncapsulationOfCurrency().getCurrencyCode() });
									return;
								}
							}
						}
					}
				}
			}

		}
	}

	public void prepare() throws Exception {
		this.currencyList = orgService.listUniqueCurrencies();
		if (id != null) {
			limitOfAuthorityScheme = loaService.findById(Long.parseLong(id));
		}
	}

	public LimitOfAuthoritySchemeService getLoaService() {
		return loaService;
	}

	public void setLoaService(LimitOfAuthoritySchemeService loaService) {
		this.loaService = loaService;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<Currency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<Currency> currencyList) {
		this.currencyList = currencyList;
	}

	public LimitOfAuthorityScheme getLimitOfAuthorityScheme() {
		return limitOfAuthorityScheme;
	}

	public void setLimitOfAuthorityScheme(LimitOfAuthorityScheme limitOfAuthorityScheme) {
		this.limitOfAuthorityScheme = limitOfAuthorityScheme;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSearchPrefix() {
		return searchPrefix;
	}

	public void setSearchPrefix(String searchPrefix) {
		this.searchPrefix = searchPrefix;
	}
	
}
