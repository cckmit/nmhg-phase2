/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.admin.upload.convertor;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.web.xls.reader.ConversionErrors;
import tavant.twms.web.xls.reader.Convertor;

/**
 * @author kaustubhshobhan.b
 * 
 */
public class DealerConvertor implements Convertor {

    private OrgService orgService;

    public Object convert(Object object) {
        String dealerName = (String) object;
        ServiceProvider dealer;
        dealer = this.orgService.findDealerByName(dealerName);
        if (dealer != null) {
            return dealer;
        } else {
            dealer = new ServiceProvider();
            dealer.setName(dealerName);
            ConversionErrors.getInstance().addError("Dealer \"" + dealerName + " not found");
            return dealer;
        }
    }

    public Object convertWithDependency(Object object, Object dependency) {
        return new UnsupportedOperationException();
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

}
