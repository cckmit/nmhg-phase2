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

package tavant.twms.integration.adapter.mockerp;

import tavant.twms.integration.adapter.SyncTracker;
import tavant.oagis.SyncCustomerDocumentDTO;
import tavant.oagis.SyncCustomerDTO;
import tavant.oagis.SyncCustomerDataAreaDTO;
import tavant.oagis.CustomerDTO;
import tavant.oagis.CustomerTypeDocumentDTO;
import tavant.oagis.AddressDTO;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

import org.openapplications.oagis.x9.ApplicationAreaType;

public class SyncCustomer {

    private GenericDao genericDao;

    public String sync(List<SyncTracker> syncTrackers) {
        List<Customer> customers = new ArrayList<Customer>();
        for (SyncTracker syncTracker : syncTrackers) {
            customers.add((Customer) genericDao.findById(Customer.class, Long.valueOf(syncTracker.getBusinessId())));
        }
        return transform(customers);
    }

    private String transform(List<Customer> customers) {
        SyncCustomerDocumentDTO syncCustomerDocumentDTO = SyncCustomerDocumentDTO.Factory.newInstance();
        createSyncCustomer(syncCustomerDocumentDTO, customers);
        return syncCustomerDocumentDTO.toString();
    }

    private void createSyncCustomer(SyncCustomerDocumentDTO syncCustomerDocumentDTO, List<Customer> customers) {
        SyncCustomerDTO syncCustomerDTO = syncCustomerDocumentDTO.addNewSyncCustomer();
        createApplicationArea(syncCustomerDTO);
        createDataArea(syncCustomerDTO, customers);
    }

    private void createApplicationArea(SyncCustomerDTO syncCustomerDTO) {
        ApplicationAreaType applicationArea = syncCustomerDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(SyncCustomerDTO syncCustomerDTO, List<Customer> customers) {
        SyncCustomerDataAreaDTO dataArea = syncCustomerDTO.addNewDataArea();
        createSync(dataArea);
        createCustomers(customers, dataArea);
    }

    private void createSync(SyncCustomerDataAreaDTO dataArea) {
        dataArea.addNewSync();
    }

    private void createCustomers(List<Customer> customers, SyncCustomerDataAreaDTO dataArea) {
        for (Customer customer : customers) {
            CustomerDTO customerDTO = dataArea.addNewCustomer();
            customerDTO.setBusinessId(""+customer.getId());
            customerDTO.setName(customer.getName());
            if ("Dealer".equals(customer.getCustomerType())) {
                createDealership(customer, customerDTO);
            } else if ("EndCustomer".equals(customer.getCustomerType())) {
                createCustomer(customer, customerDTO);
            }
            AddressDTO addressDTO = customerDTO.addNewAddress();
            addressDTO.setAddressline1(customer.getAddressLine1());
            addressDTO.setAddressline2(customer.getAddressLine2());
            addressDTO.setCity(customer.getCity());
            addressDTO.setCountry(customer.getCountry());
            addressDTO.setEmail(customer.getEmail());
            addressDTO.setPhone(customer.getPhone());
            addressDTO.setSecondaryemail(customer.getSecondaryEmail());
            addressDTO.setSecondaryphone(customer.getSecondaryPhone());
            addressDTO.setState(customer.getState());
            addressDTO.setZipcode(customer.getZipcode());
        }
    }

    private void createDealership(Customer dealership, CustomerDTO customerDTO) {
        customerDTO.setCustomerType(CustomerTypeDocumentDTO.CustomerType.DEALER);
        customerDTO.setDealerNumber(dealership.getDealerNumber());
        customerDTO.setCurrency(dealership.getCurrency());
    }

    private void createCustomer(Customer customer, CustomerDTO customerDTO) {
        customerDTO.setCustomerType(CustomerTypeDocumentDTO.CustomerType.END_CUSTOMER);
        customerDTO.setCustomerId(customer.getCustomerId());
        customerDTO.setCompanyName(customer.getCompanyName());
        customerDTO.setCorporateName(customer.getCorporateName());

        AddressDTO addressDTO = customerDTO.addNewAdditionalAddress();
        addressDTO.setAddressline1(customer.getAdditionalAddressLine1());
        addressDTO.setAddressline2(customer.getAdditionalAddressLine2());
        addressDTO.setCity(customer.getAdditionalCity());
        addressDTO.setCountry(customer.getAdditionalCountry());
        addressDTO.setEmail(customer.getAdditionalEmail());
        addressDTO.setPhone(customer.getAdditionalPhone());
        addressDTO.setSecondaryemail(customer.getAdditionalSecondaryEmail());
        addressDTO.setSecondaryphone(customer.getAdditionalSecondaryPhone());
        addressDTO.setState(customer.getAdditionalState());
        addressDTO.setZipcode(customer.getAdditionalZipcode());
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
}
