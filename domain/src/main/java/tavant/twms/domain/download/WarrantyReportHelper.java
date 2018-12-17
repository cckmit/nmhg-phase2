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

package tavant.twms.domain.download;

import java.util.Iterator;
import java.util.List;

import tavant.twms.domain.claim.ClaimState;

/**
 * @author jhulfikar.ali
 *
 */
public class WarrantyReportHelper implements WarrantyReport {

	public static String populateSelectClauseWithDelimiter(List<String> columns, String delimiter) {
		StringBuffer selectClauseWithDelimiter = new StringBuffer(EMPTY_STR);
		int columnSize = columns.size();
		for (Iterator<String> iterator = columns.iterator(); iterator.hasNext();) {
			String selectColumn = (String) iterator.next();
			if (!EMPTY_STR.equals(selectColumn))
			{
				selectClauseWithDelimiter.append(selectColumn);
				if (columnSize!=1)
				{
					selectClauseWithDelimiter.append(SQL_CONCAT_STR)
							.append(SQL_APPEND_STR).append(delimiter)
							.append(SQL_APPEND_STR).append(SQL_CONCAT_STR);
				}
			}
			columnSize--;
		}
		selectClauseWithDelimiter.append(" as \"Record\" ");
		return selectClauseWithDelimiter.toString();
	}
	
	public static String populateSelectClause(List<String> columns) {
		StringBuffer selectClauseWithDelimiter = new StringBuffer(EMPTY_STR);
		int columnSize = columns.size();
		for (Iterator<String> iterator = columns.iterator(); iterator.hasNext();) {
			String selectColumn = (String) iterator.next();
			if (!EMPTY_STR.equals(selectColumn))
			{
				selectClauseWithDelimiter.append(selectColumn);
				if (columnSize!=1)
				{
					selectClauseWithDelimiter.append(",");
				}
			}
			columnSize--;
		}
		//selectClauseWithDelimiter.append(" as \"Record\" ");
		return selectClauseWithDelimiter.toString();
	}

	public static String populateStringValAsDelimitedForQuery(String StringValues, String delimiter) {
		StringBuffer StringValueCSV = new StringBuffer();
			String[] values = StringValues.split(delimiter);
			for (int i = 0; i < values.length; i++) {
				String dealer = values[i];
				if (dealer!=null && !EMPTY_STR.equals(dealer))
				{
					StringValueCSV.append(SQL_APPEND_STR+dealer.trim()+SQL_APPEND_STR);
					if (i!=values.length-1)
						StringValueCSV.append(delimiter);
				}
			}
		return StringValueCSV.toString();
	}
	
	public static String populateDealerNumberAsCSVForQuery(String dealerNumbers) {
		return populateStringValAsDelimitedForQuery(dealerNumbers, DELIMITER_CSV);
	}
	
	public static String populateBusinessUnitAsCSVForQuery(String businessUnitNames) {
		return populateStringValAsDelimitedForQuery(businessUnitNames, DELIMITER_CSV);
	}

	public static String commaSeparatedArrayForQuery(ClaimState[] values) {
		StringBuffer claimStatesInCSV = new StringBuffer(EMPTY_STR);
		for (int iter = 0; iter < values.length; iter++) {
			ClaimState claimState = values[iter];
			claimStatesInCSV.append(SQL_APPEND_STR+claimState.name() + SQL_APPEND_STR);
			if (iter!=values.length-1)
				claimStatesInCSV.append(DELIMITER_CSV);
		}
		return claimStatesInCSV.toString();
	}
	
	public static String commaSeparatedIdsForQuery(List<Long> ids) {
		StringBuffer buffer = new StringBuffer();
		boolean delimiter = false;
		for(Long id : ids) {
			if(delimiter)
				buffer.append(",");
			else
				delimiter = true;
			buffer.append(id);
		}
		return buffer.toString();
	}
	
	public static String claimStatesForUser(String claimStatus) {
		StringBuffer claimStatesInCSV = new StringBuffer(EMPTY_STR);
		ClaimState[] claimCreditStates = null;
		if (DownloadClaimState.ALL.toString().equalsIgnoreCase(claimStatus))
			return null;
		else if (DownloadClaimState.CREDITED.toString().equalsIgnoreCase(claimStatus)) {
			claimCreditStates = new ClaimState[] {ClaimState.ACCEPTED_AND_CLOSED}; 
		}
		else if (DownloadClaimState.DENIED.toString().equalsIgnoreCase(claimStatus)) {
			claimCreditStates = new ClaimState[] {ClaimState.DENIED, 
					ClaimState.DENIED_AND_CLOSED}; 
		}
		else if (DownloadClaimState.NEW.toString().equalsIgnoreCase(claimStatus)) {
			claimCreditStates = new ClaimState[] {ClaimState.SUBMITTED,
					ClaimState.SERVICE_MANAGER_REVIEW, 
					ClaimState.SERVICE_MANAGER_RESPONSE}; 
		}
		else if (DownloadClaimState.IN_PROGRESS.toString().equalsIgnoreCase(claimStatus)) {
			List<ClaimState> inProgressList = ClaimState.getStateListInProgress();
			claimCreditStates = new ClaimState[inProgressList.size()];
			for(int i=0 ; i<inProgressList.size() ; i++) {
				claimCreditStates[i] = inProgressList.get(i);
			} 
		}
		else if (DownloadClaimState.FORWARDED.toString().equalsIgnoreCase(claimStatus)) {
			claimCreditStates = new ClaimState[] {ClaimState.FORWARDED, ClaimState.ADVICE_REQUEST}; 
		}
		else if (DownloadClaimState.FORWARDED_EXTERNALLY.toString().equalsIgnoreCase(claimStatus)) {
			// TODO: Need to get the state for Externally forwarded
			claimCreditStates = new ClaimState[] {ClaimState.FORWARDED}; 
		}
		else if (DownloadClaimState.FORWARDED_INTERNALLY.toString().equalsIgnoreCase(claimStatus)) {
			claimCreditStates = new ClaimState[] {ClaimState.ADVICE_REQUEST}; 
		}
		if(claimCreditStates != null) {
			claimStatesInCSV.append(commaSeparatedArrayForQuery(claimCreditStates));
			return claimStatesInCSV.toString();
		}
		return null;
	}

}
