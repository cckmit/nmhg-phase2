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
 */
package tavant.twms.domain.campaign.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;
import tavant.twms.domain.campaign.*;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;

public class CampaignValidationServiceImpl implements
		CampaignValidationService {

	private static final String ALPHA_NUMBER_PATTERN = "[A-Za-z]{2}[0-9]{1,}+";

	private static final String NUMBER_ALPHA_NUMBER_PATTERN = "[0-9]{3}[A-Za-z]{2}[0-9]{1,}+";

	private static final String ALL_NUMBERS_PATTERN = "[0-9]{1,}+";

	private CampaignAdminService campaignAdminService;

	private static final String SERIAL_NUMBER_RANGES = "SERIAL_NUMBER_RANGES";
	

	@SuppressWarnings("unchecked")
	public void validate(Campaign campaign,String campaignFor) throws CampaignServiceException {
		
		Map<String, String> fieldErrors = new HashMap<String, String>();
		List<String> actionErrors = new ArrayList<String>();		

		if (StringUtils.hasText(campaign.getCode())) {
			Campaign example = campaignAdminService.findByCode(campaign
					.getCode());
			if (example != null && !same(campaign, example)) {
				fieldErrors.put("code", "Campaign by the code "
						+ campaign.getCode() + " already exists.");
			}
			
		}

		if (campaignFor.equals(SERIAL_NUMBER_RANGES)) {
			validateRangeList(campaign, fieldErrors);
		}

		if (fieldErrors.size() > 0 || actionErrors.size() > 0) {
			throw new CampaignServiceException(fieldErrors, actionErrors);
		}
	}

	public boolean hasValidPatterns(List<CampaignSerialRange> ranges) {

		for (CampaignSerialRange range : ranges) {
			String fromSNo = range.getFromSerialNumber();
			String toSNo = range.getToSerialNumber();
			if (!((matchesAllNumbersPattern(fromSNo, toSNo))
					|| (matchesNumberAlphaNumberPattern(fromSNo, toSNo)) || (matchesAlphaNumberPattern(
					fromSNo, toSNo)))) {
				return false;
			}
		}
		return true;
	}

	public boolean matchesNumberAlphaNumberPattern(String fromSNo, String toSNo) {
		boolean patternMatch = Pattern.matches(NUMBER_ALPHA_NUMBER_PATTERN,
				fromSNo)
				&& Pattern.matches(NUMBER_ALPHA_NUMBER_PATTERN, toSNo);
		return patternMatch;
	}

	public boolean matchesAlphaNumberPattern(String fromSNo, String toSNo) {

		boolean patternMatch = Pattern.matches(ALPHA_NUMBER_PATTERN, fromSNo)
				&& Pattern.matches(ALPHA_NUMBER_PATTERN, toSNo);
		if (patternMatch) {
			String[] startSplit = fromSNo.split("[A-Z]");
			String[] endSplit = toSNo.split("[A-Z]");
			String[] fromAlpha = fromSNo.split("[0-9]{1,}+");
			String[] toAlpha = toSNo.split("[0-9]{1,}+");

			String startNo = startSplit[startSplit.length - 1];
			String endNo = endSplit[endSplit.length - 1];

			if (fromAlpha[0].equalsIgnoreCase(toAlpha[0])
					&& new Integer(startNo).compareTo(new Integer(endNo)) <= 0) {
				return true;
			}
		}
		return false;
	}

	public boolean matchesAllNumbersPattern(String fromSNo, String toSNo) {
		boolean patternMatch = Pattern.matches(ALL_NUMBERS_PATTERN, fromSNo)
				&& Pattern.matches(ALL_NUMBERS_PATTERN, toSNo);
		if (patternMatch
				&& new Integer(fromSNo).compareTo(new Integer(toSNo)) <= 0) {
			return true;
		}
		return false;
	}	

	public void setCampaignAdminService(
			CampaignAdminService campaignAdminService) {
		this.campaignAdminService = campaignAdminService;
	}

	private boolean same(Campaign source, Campaign target) {
        if(target.getId() == null) // should not allow to save the campaign with same code as well
            return false;
		return source.getId() != null && target.getId() != null
				&& source.getId().compareTo(target.getId()) == 0;
	}

	private void validateRangeList(Campaign campaign,
			Map<String, String> fieldErrors) {
		if (campaign.getCampaignCoverage() != null
				&& campaign.getCampaignCoverage().getRangeCoverage() != null) {
		List<CampaignSerialRange> rangeList = campaign
				.getCampaignCoverage().getRangeCoverage().getRanges();
		if (rangeList == null || rangeList.isEmpty()) {
			fieldErrors.put("noPatterns", "Please add one or more pattern(s).");
		}
		else
		{
			for (int i = 0; i < rangeList.size(); i++) {
				if (!StringUtils.hasText(rangeList.get(i)
						.getFromSerialNumber())
						|| !StringUtils.hasText(rangeList.get(i)
								.getToSerialNumber())) {
					rangeList.remove(i);
					--i;
				}
			}
			StringBuffer patterns = new StringBuffer();
			for (int i = 0; i < rangeList.size(); i++) {

				if (!StringUtils.hasText(rangeList.get(i).getAttachOrDelete())) {
					patterns.append(rangeList.get(i).getFromSerialNumber()
							+ "-" + rangeList.get(i).getToSerialNumber()
							+ ", ");
				}
			}
			if (!patterns.toString().isEmpty()) {
				fieldErrors.put("need operation",
						"please select the type of operation need to perform for the pattern [ "
								+ patterns.toString() + " ]");
			}
		}
	 }
		
	}
	
}
