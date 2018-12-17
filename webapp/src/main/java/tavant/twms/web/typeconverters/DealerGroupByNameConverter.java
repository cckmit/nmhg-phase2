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
package tavant.twms.web.typeconverters;

import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;

/**
 * @author vineeth.varghese
 * 
 */
public class DealerGroupByNameConverter extends
        ValidatableDomainObjectConverter<DealerGroupService, DealerGroup> {

    public DealerGroupByNameConverter(String serviceBeanName) {
        super(serviceBeanName);
        // TODO Auto-generated constructor stub
    }

    @Override
    public DealerGroup fetchByName(String name) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName(DealerGroup entity) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
