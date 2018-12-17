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

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly=true)
public interface ComplaintsService {
	
    @Transactional(readOnly=false)
    public void logAComplaint(Complaint complaint);
    
    @Transactional(readOnly=false)
    public void deleteComplaint(Complaint complaint);
    
    @Transactional(readOnly=false)
    public void updateComplaint(Complaint complaint);
            
    public Complaint getComplaint(Long id);
    
    public PageResult<Complaint> fetchFieldReportsOrConsumerComplaintsByType(String type, ListCriteria criteria);
}
