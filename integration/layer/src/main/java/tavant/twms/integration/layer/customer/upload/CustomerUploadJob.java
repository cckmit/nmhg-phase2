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
 *
 */
package tavant.twms.integration.layer.customer.upload;

import org.springframework.context.ApplicationContext;
import tavant.twms.domain.upload.CustomerStaging;
import tavant.twms.integration.layer.Service;
import tavant.twms.integration.layer.Transformer;
import tavant.twms.integration.layer.Validator;
import tavant.twms.integration.layer.customer.CustomerSyncService;
import tavant.twms.integration.layer.customer.CustomerValidator;
import tavant.twms.integration.layer.upload.AbstractUploadJob;

/**
 *
 * @author prasad.r
 */
public class CustomerUploadJob extends AbstractUploadJob {

    private CustomerUploadTransformer transformer;
    private CustomerValidator validator;
    private CustomerSyncService service;
    
    
    @Override
    public String getTemplateName() {
        return "customerUpload";
    }

    @Override
    public Class getStagingDataClass() {
        return CustomerStaging.class;
    }

    @Override
    public int getUploadRecordsLimit() {
        return 2000;
    }

    @Override
    protected void initChildServices(ApplicationContext springAppContext) {
        this.service = (CustomerSyncService) springAppContext.getBean("customerSyncService");
        this.validator = (CustomerValidator) springAppContext.getBean("customerValidator");
        this.transformer = (CustomerUploadTransformer) springAppContext.getBean("customerUploadTransformer");
    }

    @Override
    protected String getPropertyName(String headerValue) {
        if ("Postal Code".equals(headerValue)) {
            return "zipCode";
        } else if ("Address".equals(headerValue)) {
            return "addressline1";
        }
        return super.getPropertyName(headerValue);
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public Service getService() {
        return service;
    }
}
