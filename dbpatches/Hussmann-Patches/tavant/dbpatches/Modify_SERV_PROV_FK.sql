--Purpose: adding the columns for service_provider from dealership and updating service_provider  
-- to point to organixation and updating organixation currency to USD
--Author: P Shraddha Nanda
--Created On: Date 06 Nov 2008

ALTER TABLE SERVICE_PROVIDER ADD (
  CONSTRAINT SER_PVIDER_ORG_FK 
 FOREIGN KEY (ID) 
 REFERENCES ORGANIZATION (ID)) 
/
 ALTER TABLE SERVICE_PROVIDER ADD
 ( 
  SALES_DISTRICT_CODE             VARCHAR2(255 BYTE),
  REGION_CODE                     VARCHAR2(255 BYTE),
  PRIMARY_CONTACTPERSON_FST_NAME  VARCHAR2(255 BYTE),
  PRIMARY_CONTACTPERSON_LST_NAME  VARCHAR2(255 BYTE),
  DEALER_FAMILY_CODE              VARCHAR2(255 BYTE),
  SITE_NUMBER                     VARCHAR2(255 BYTE),
  STATUS                          VARCHAR2(255 BYTE),
  COMPANY_TYPE                    VARCHAR2(255 BYTE),
  HRS_OPERATION_WEEKDAY           VARCHAR2(255 BYTE),
  HRS_OPERATION_SATURDAY          VARCHAR2(255 BYTE),
  HRS_OPERATION_SUNDAY            VARCHAR2(255 BYTE),
  CUSTOMER_CATEGORY               VARCHAR2(255 BYTE)
 )
/
update organization set preferred_currency = 'USD'
