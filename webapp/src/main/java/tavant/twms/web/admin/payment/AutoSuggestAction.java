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
package tavant.twms.web.admin.payment;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.interceptor.ServletResponseAware;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.partreturn.WarehouseService;
import tavant.twms.domain.policy.PolicyAdminService;
import tavant.twms.web.i18n.I18nActionSupport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author aniruddha.chaturvedi
 * 
 */
public class AutoSuggestAction extends I18nActionSupport implements ServletResponseAware {

    private HttpServletResponse response;

    private String autoSuggestRequestingElement;

    private String dealerName;

    private String productName;

    private String itemNumber;

    private PolicyAdminService policyAdminService;
    
    private WarehouseService warehouseService;

    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public final void writeInResponse(List<String> values) throws IOException {
        if (values.isEmpty()) { // HACK need to get rid of this. without this a
                                // javascript error comes.
            values.add(".");
        }
        PrintWriter writer = response.getWriter();
        writer.print("<root>");
        for (String value : values) {
            writer.print("<value>" + value + "</value>\n");
        }
        writer.print("</root>");
        writer.close();
    }

    public String autoSuggestForCriteria() throws IOException {
        List<String> values = new ArrayList<String>();
        if (autoSuggestRequestingElement.equals("dealerName")) {
            values = orgService.findDealerNamesStartingWith(dealerName, 0, 10);
        }
        if (autoSuggestRequestingElement.equals("productName")) {
            values = catalogService.findItemGroupsWithNameLike(productName, 0, 10);
        }
        if (autoSuggestRequestingElement.equals("itemNumber")) {
            List<Item> parts = catalogService.findParts(itemNumber);
            for (Item item : parts) {
                values.add(item.getNumber());
            }
        }
        writeInResponse(values);
        return null;
    }

    public String autoSuggestForPolicy() throws IOException {
        List<String> values = new ArrayList<String>();
        String[] prodName = (String[]) ActionContext.getContext().getParameters().get(
                autoSuggestRequestingElement);
        values = catalogService.findItemGroupsWithNameLike(prodName[0], 0, 10);
        writeInResponse(values);
        return null;
    }


    public String autoSuggestForPartReturns() throws IOException {
        List<String> values = new ArrayList<String>();
        String[] toFind = (String[]) ActionContext.getContext().getParameters().get(
                autoSuggestRequestingElement);
        if (autoSuggestRequestingElement.indexOf("dealer.name") != -1) {
            values = orgService.findDealerNamesStartingWith(toFind[0], 0, 10);
        }
        if (autoSuggestRequestingElement.indexOf("productType.name") != -1) {
            values = catalogService.findItemGroupsWithNameLike(toFind[0], 0, 10);
        }
        if (autoSuggestRequestingElement.indexOf("forItem.number") != -1) {
            List<Item> parts = catalogService.findParts(toFind[0]);
            for (Item item : parts) {
                values.add(item.getNumber());
            }
        }
        if (autoSuggestRequestingElement.indexOf("returnLocation.code") != -1) {
            values = warehouseService.findWarehouseCodesStartingWith(toFind[0]);
        }
        writeInResponse(values);
        return null;
    }

    public String getAutoSuggestRequestingElement() {
        return autoSuggestRequestingElement;
    }

    public void setAutoSuggestRequestingElement(String autoSuggestRequestingElement) {
        this.autoSuggestRequestingElement = autoSuggestRequestingElement;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public void setPolicyAdminService(PolicyAdminService policyAdminService) {
        this.policyAdminService = policyAdminService;
    }

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

}
