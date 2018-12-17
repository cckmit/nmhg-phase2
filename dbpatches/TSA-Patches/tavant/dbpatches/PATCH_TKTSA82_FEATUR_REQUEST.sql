/* 
Feature Request TKTSA-82 COLUMNS ADDED to CUSTOMER_STAGING TABLE
 and in Table upload_mgt we update columns_to_capture 16 to 18
*/	
ALTER TABLE CUSTOMER_STAGING ADD(DEALER_FAMILY VARCHAR2(255),DEALER_SITE VARCHAR2(255))
/
Update upload_mgt set columns_to_capture=18 where  name_of_template = 'customerUpload'
/
COMMIT
/