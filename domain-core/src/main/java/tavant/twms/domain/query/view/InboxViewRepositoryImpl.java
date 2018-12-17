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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import tavant.twms.infra.GenericRepositoryImpl;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class InboxViewRepositoryImpl extends GenericRepositoryImpl<InboxView, Long> implements
        InboxViewRepository {

    public List<InboxView> findInboxViewForUser(Long userId, String type,String folderName) {
        String queryString = "select iv from InboxView iv " + "join iv.createdBy cb "
                + "where cb.id=:id and iv.type=:type and iv.folderName=:folderName";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", userId);
        params.put("type", type);
        params.put("folderName", folderName);
        return findUsingQuery(queryString, params);
    }

    public InboxView findInboxViewByNameAndUser(String name, Long userId, String type) {
        String queryString = "select iv from InboxView iv " + "join iv.createdBy cb "
                + "where cb.id=:id and iv.name=:name and iv.type=:type";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", userId);
        params.put("name", name);
        params.put("type", type);
        return findUniqueUsingQuery(queryString, params);
    }

}
