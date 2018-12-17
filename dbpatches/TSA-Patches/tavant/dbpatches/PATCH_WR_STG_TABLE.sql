--Purpose    : STG_WARRANTY_REGISTRATIONS table added
--Author     : Mukteshwar Prasad Mahto
--Created On : 08/07/2010
--Impact     : None


CREATE TABLE STG_WARRANTY_REGISTRATIONS
(
ID                             NUMBER(19) PRIMARY KEY,
FILE_UPLOAD_MGT_ID             NUMBER(19),
DEALER_NUMBER                  VARCHAR2(255 BYTE),
CUSTOMER_TYPE                  VARCHAR2(255 BYTE),
CUSTOMER_NUMBER                VARCHAR2(255 BYTE),
SERIAL_NUMBER                  VARCHAR2(255 BYTE),
ITEM_NUMBER                    VARCHAR2(255 BYTE),
DELIVERY_DATE                  VARCHAR2(255 BYTE),
HOURS_ON_MACHINE               NUMBER,
OPERATOR_TYPE                  VARCHAR2(255 BYTE),
OPERATOR_NUMBER                VARCHAR2(255 BYTE),
INSTALLING_DEALER_NUMBER       VARCHAR2(255 BYTE),
DATE_OF_INSTALLATION           VARCHAR2(255 BYTE),
EQUIPMENT_VIN_ID               VARCHAR2(255 BYTE),
TRUCK_NUMBER                   VARCHAR2(255 BYTE),
OEM                            VARCHAR2(255 BYTE),
COMPONENT_SERIAL_NUMBER        VARCHAR2(4000 BYTE),
COMPONENT_PART_NUMBER          VARCHAR2(4000 BYTE),
COMPONENT_INSTALLATION_DATE    VARCHAR2(4000 BYTE),
TRANSACTION_TYPE               VARCHAR2(255 BYTE),
MARKET_TYPE                    VARCHAR2(255 BYTE),
FIRST_TIME_OWNER               VARCHAR2(255 BYTE),
IF_PREVIOUS_OWNER              VARCHAR2(255 BYTE),
COMPETITION_TYPE               VARCHAR2(255 BYTE),
COMPETITOR_MAKE                VARCHAR2(255 BYTE),
MODEL_NUMBER                   VARCHAR2(255 BYTE),
NUMBER_OF_MONTHS               NUMBER,
NUMBER_OF_YEARS                NUMBER,
ADDITIONAL_APPLICABLE_POLICIES VARCHAR2(4000 BYTE),
BUSINESS_UNIT_INFO             VARCHAR2(255 BYTE),
ERROR_STATUS                   VARCHAR2(255 BYTE),
ERROR_CODE                     VARCHAR2(4000 BYTE),
UPLOAD_STATUS                  VARCHAR2(255 BYTE),
UPLOAD_ERROR                   VARCHAR2(4000 BYTE),
REQUEST_FOR_EXTENSION		   VARCHAR2(255 BYTE),
UPLOAD_DATE					   VARCHAR2(255 BYTE)
)
/
COMMIT
/