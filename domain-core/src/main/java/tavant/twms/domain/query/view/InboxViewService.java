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
package tavant.twms.domain.query.view;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericService;
/**
 * 
 * @author roopali.agrawal
 *
 */
public interface InboxViewService extends GenericService<InboxView,Long, Exception>{
	 @Transactional(readOnly=false)    
	 public void save(InboxView inboxView);
	 
	 @Transactional(readOnly=false)    
     public void update(InboxView inboxView);
	 
	 @Transactional(readOnly=false)    
     public void delete(InboxView inboxView);
	 
	 @Transactional(readOnly=true) 
	 public List<InboxView> findInboxViewForUser(Long userId,String type,String folderName);
	 
	 @Transactional(readOnly=true) 
	 public InboxView findInboxViewByNameAndUser(String name,Long userId, String type);
	 
	 @Transactional(readOnly=true) 
	 public InboxView findDefaultInboxViewForUserAndFolder(Long userId,String folderName);	
}
