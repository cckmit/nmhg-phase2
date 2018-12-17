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

import java.util.List;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignSerialRange;
import tavant.twms.domain.campaign.CampaignServiceException;

/**
 * 
 * @author Kiran.Kollipara
 */
public interface CampaignValidationService {

	public void validate(Campaign campaign,  String campaignFor) throws CampaignServiceException;

	public boolean hasValidPatterns(List<CampaignSerialRange> ranges);

	public boolean matchesAlphaNumberPattern(String fromSNo, String toSNo);

	public boolean matchesNumberAlphaNumberPattern(String fromSNo, String toSNo);

	public boolean matchesAllNumbersPattern(String fromSNo, String toSNo);
}