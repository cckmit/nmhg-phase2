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
package tavant.twms.domain.complaints;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class ComplaintsServiceImpl implements ComplaintsService{

	private ComplaintsRepository complaintsRepository;
	
	public void logAComplaint(Complaint newComplaint) {		
		complaintsRepository.save(newComplaint);
	}
	
	public Complaint getComplaint(Long id) {
		return complaintsRepository.findById(id);
	}	
	
	public PageResult<Complaint> fetchFieldReportsOrConsumerComplaintsByType(String type, ListCriteria criteria) {
		return complaintsRepository.fetchFieldReportsOrConsumerComplaintsByType(type, criteria);
	}
	
	public void deleteComplaint(Complaint complaint) {
		complaintsRepository.delete(complaint);	
	}
	
	public void updateComplaint(Complaint complaint) {		
		complaintsRepository.update(complaint);
	}
	

	
	public void setComplaintsRepository(ComplaintsRepository complaintsRepository) {
		this.complaintsRepository = complaintsRepository;
	}
}
