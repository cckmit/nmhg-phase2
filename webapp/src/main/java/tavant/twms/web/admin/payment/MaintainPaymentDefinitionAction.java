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

import org.json.JSONArray;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.AbstractListAction;
import tavant.twms.web.ColumnDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MaintainPaymentDefinitionAction extends AbstractListAction {
    private List<PaymentDefinition> paymentDefinitions;
    private PaymentDefinition paymentDefinition;
    private PaymentDefinitionAdminService paymentDefinitionAdminService;
    
    public MaintainPaymentDefinitionAction() {
        super();
        columns.add(new ColumnDefinition("Validity From","paymentDefinition.startDate"));
        columns.add(new ColumnDefinition("Validity To","paymentDefinition.endDate"));
        columns.add(new ColumnDefinition("PaymentDefinition Id","paymentDefinition.id"));        
    }
    
    @SuppressWarnings("unchecked")
        public String head() {
        JSONArray response = new JSONArray();
        addColumnHeaders(response);
        response.put("paymentDefinition.id"); // key
        jsonString = response.toString();
        return SUCCESS;
    }
    
    @SuppressWarnings("unchecked")
    public String body() {
        JSONArray response = new JSONArray();
        JSONArray data = new JSONArray();
        ListCriteria listCriteria = new ListCriteria();
        addFilterCriteria(listCriteria);
        addSortCriteria(listCriteria);        
        paymentDefinitions = new ArrayList<PaymentDefinition>();
        paymentDefinitions =  paymentDefinitionAdminService.findAll(new PageSpecification()).getResult();        
        Map dummyMap = new HashMap();
        for(PaymentDefinition aPaymentDefinition : paymentDefinitions) {
            dummyMap.put("paymentDefinition",aPaymentDefinition);
            
            //add content for each column
            Map aNewRow = new HashMap();
            for(ColumnDefinition aColumnDefn : columns ) {
                String systemName = aColumnDefn.getName();
                String propertyExpression = aColumnDefn.getExpression();
                aNewRow.put(systemName, getProperty(dummyMap, propertyExpression));
                
            }
            
            //add key information.
            String key = aPaymentDefinition.getId().toString();
            aNewRow.put("id",key);
            
            //put the row.
            data.put(aNewRow);
        }
        
        response.put(data);        
        jsonString = response.toString();
        return SUCCESS;
    }
    
    public String showPreview() {
        return SUCCESS;
    }
    
    public PaymentDefinition getPaymentDefinition() {
        return paymentDefinition;
    }

    public void setPaymentDefinition(PaymentDefinition paymentDefinition) {
        this.paymentDefinition = paymentDefinition;
    }

    public List<PaymentDefinition> getPaymentDefinitions() {
        return paymentDefinitions;
    }


    public void setPaymentDefinitions(List<PaymentDefinition> paymentDefinitions) {
        this.paymentDefinitions = paymentDefinitions;
    }

    public void setPaymentDefinitionAdminService(PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }
    
    
}
