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

package tavant.twms.domain.claim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import tavant.twms.security.authz.infra.SecurityHelper;

/**
 * @author kamal.govindraj
 * 
 */
public enum ClaimState {

    DRAFT("draft"),
    DRAFT_DELETED("Draft Deleted"),
    SUBMITTED("submitted"),
    SERVICE_MANAGER_REVIEW("Service Manager Review"),
    SERVICE_MANAGER_RESPONSE("Service Manager Response"),
    MANUAL_REVIEW("manual review"),
    ON_HOLD("In Progress"),
    ON_HOLD_FOR_PART_RETURN("on hold for part return"),
    FORWARDED("Forwarded"),
    TRANSFERRED("Transferred"),
    ADVICE_REQUEST("Advice Request"),
    REPLIES("Replies"),
    EXTERNAL_REPLIES("ExternalReplies"),
    PROCESSOR_REVIEW("New"),
    APPROVED("Approved"),
    REJECTED("rejected"),
    ACCEPTED("Accepted"),
    WAITING_FOR_PART_RETURNS("Waiting For Part Returns"),
    REJECTED_PART_RETURN("Rejected Part Returns"),
    REACCEPTED("Reprocessed After Rejected Parts"),
    PENDING_PAYMENT_SUBMISSION("Pending Payment Submission"),
    PENDING_PAYMENT_RESPONSE("Pending Payment Response"),
    DELETED("Deleted"),
    CLOSED("Closed"),
    ACCEPTED_AND_CLOSED("Accepted and Closed"),
    DENIED("Denied"),
    DENIED_AND_CLOSED("Denied and Closed"),
    APPEALED("Appealed"),
    REOPENED("Reopened"),
    CP_REVIEW("CP Review"),
    CP_TRANSFER("CP Transfer"),
    IN_PROGRESS("In Progress"),
    DEACTIVATED("Deactivated"),
    INVALID("Invalid"); //Claims cannot exist in this state, dummy state to represent an invalid claim state    
    
    private String state;

    private ClaimState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    @Override
    public String toString() {
        return this.state;
    }
    
    public String getDisplayStatus(){
    	if(new SecurityHelper().getLoggedInUser().isInternalUser())
    		return this.state;
    	else 
    	{ 	
    		if(getStateListInProgress().contains(this))
    			return ClaimState.IN_PROGRESS.toString();
    	}
    	return this.state;	
    }		
    
    public static List<ClaimState> getStateListInProgress() {
		List<ClaimState> claimInProgressStates = new ArrayList<ClaimState>();
		
		claimInProgressStates.add(ClaimState.MANUAL_REVIEW);
		claimInProgressStates.add(ClaimState.ON_HOLD);
		claimInProgressStates.add(ClaimState.ON_HOLD_FOR_PART_RETURN);
		claimInProgressStates.add(ClaimState.FORWARDED);
		claimInProgressStates.add(ClaimState.TRANSFERRED);
		claimInProgressStates.add(ClaimState.APPROVED);
		claimInProgressStates.add(ClaimState.ADVICE_REQUEST);
		claimInProgressStates.add(ClaimState.REPLIES);
		claimInProgressStates.add(ClaimState.PROCESSOR_REVIEW);
		claimInProgressStates.add(ClaimState.REJECTED_PART_RETURN);
		claimInProgressStates.add(ClaimState.PENDING_PAYMENT_SUBMISSION);
		claimInProgressStates.add(ClaimState.PENDING_PAYMENT_RESPONSE);
		claimInProgressStates.add(ClaimState.REOPENED);
		
		return claimInProgressStates;
	}
    
    public static List<ClaimState> getStatesStartingWith(String prefix, boolean showClaimStatusToDealer) {
    	List<ClaimState> claimStates = new ArrayList<ClaimState>();
    	if(StringUtils.isNotEmpty(prefix))
    		if(new SecurityHelper().getLoggedInUser().isInternalUser() || showClaimStatusToDealer) {
		    	for(ClaimState state : ClaimState.values()) {
		    		if(state == INVALID)
		    			continue;
		    		if(state.getState().toLowerCase().startsWith(prefix.toLowerCase()))
		    			claimStates.add(state);
		    	}
    		}else {
    			List<ClaimState> claimInProgressStates = getStateListInProgress();
    			for(ClaimState state : ClaimState.values()) {
    				if(claimInProgressStates.contains(state) || state == INVALID)
		    			continue;
    				if(state.getState().toLowerCase().startsWith(prefix.toLowerCase())) {
    					claimStates.add(state);
    					if(state == IN_PROGRESS)
    						claimStates.addAll(claimInProgressStates);
    				}
    			}
    		}
    	if(claimStates.isEmpty())
    		claimStates.add(INVALID);
    	return claimStates;
    }
    
    
    public static List<ClaimState> getStatusListForSearch(boolean isInternalUser) {
    	List<ClaimState> stateList = new ArrayList<ClaimState>();
    	if(!isInternalUser)
    	{
    		stateList.add(ON_HOLD);
    		stateList.add(FORWARDED);
    		stateList.add(SERVICE_MANAGER_REVIEW);
        	stateList.add(SERVICE_MANAGER_RESPONSE);
        	stateList.add(ACCEPTED_AND_CLOSED);
        	stateList.add(DENIED_AND_CLOSED);
    	}else{
    	stateList.add(SUBMITTED);
    	stateList.add(ACCEPTED);
    	stateList.add(PROCESSOR_REVIEW);
    	stateList.add(APPEALED);
    	stateList.add(FORWARDED);
    	stateList.add(ADVICE_REQUEST);
    	stateList.add(REPLIES);
    	stateList.add(EXTERNAL_REPLIES);
    	stateList.add(PENDING_PAYMENT_SUBMISSION);
    	stateList.add(PENDING_PAYMENT_RESPONSE);
    	stateList.add(TRANSFERRED);
    	stateList.add(APPROVED);
    	stateList.add(SERVICE_MANAGER_REVIEW);
    	stateList.add(SERVICE_MANAGER_RESPONSE);
    	stateList.add(ON_HOLD_FOR_PART_RETURN);
    	stateList.add(REOPENED);
    	stateList.add(ON_HOLD);
    	stateList.add(ACCEPTED_AND_CLOSED);
    	stateList.add(DENIED_AND_CLOSED);
    	}
    	
    	Collections.sort(stateList,new Comparator<ClaimState>(){
				public int compare(ClaimState arg0, ClaimState arg1) {
					return arg0.state.compareToIgnoreCase(arg1.state);
				}
    		});
    	
    	return stateList;
    }

    
}
