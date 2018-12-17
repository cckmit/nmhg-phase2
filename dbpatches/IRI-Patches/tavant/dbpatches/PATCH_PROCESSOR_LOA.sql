--Purpose    : To store the processor availability
--Author     : Jhulfikar Ali. A
--Created On : 20-Nov-08

ALTER TABLE ORG_USER
ADD (available VARCHAR2(10 BYTE) DEFAULT 'Yes')
/
ALTER TABLE ORG_USER
ADD (default_User VARCHAR2(4000 BYTE))
/
INSERT INTO PURPOSE ( ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, business_unit_info) VALUES ( 
(select max(id)+1 from PURPOSE), 'Processor Authority', 1, sysdate, 'System: Purpose for creation of Processor Authority Rule to Transport Solutions', NULL, NULL, NULL, NULL, 'Transport Solutions')
/
INSERT INTO PURPOSE ( ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, business_unit_info) VALUES ( 
(select max(id)+1 from PURPOSE), 'Processor Authority', 1, sysdate, 'System: Purpose for creation of Processor Authority Rule to Hussmann', NULL, NULL, NULL, NULL, 'Hussmann')
/
INSERT INTO PURPOSE ( ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, business_unit_info) VALUES ( 
(select max(id)+1 from PURPOSE), 'Processor Authority', 1, sysdate, 'System: Purpose for creation of Processor Authority Rule to Club Car', NULL, NULL, NULL, NULL, 'Club Car')
/
INSERT INTO PURPOSE ( ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, business_unit_info) VALUES ( 
(select max(id)+1 from PURPOSE), 'Processor Authority', 1, sysdate, 'System: Purpose for creation of Processor Authority Rule to AIR', NULL, NULL, NULL, NULL, 'AIR')
/
INSERT INTO PURPOSE ( ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, business_unit_info) VALUES ( 
(select max(id)+1 from PURPOSE), 'Processor Authority', 1, sysdate, 'System: Purpose for creation of Processor Authority Rule to TFM', NULL, NULL, NULL, NULL, 'TFM')
/
INSERT INTO PURPOSE ( ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, 
D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, business_unit_info) VALUES ( 
(select max(id)+1 from PURPOSE), 'Processor Authority', 1, sysdate, 'System: Purpose for creation of Processor Authority Rule to IRI Club Car', NULL, NULL, NULL, NULL, 'IRI Club Car')
/
COMMIT
/