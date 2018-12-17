-- Purpose    : Added Business Unit Info to the file_upload_mgt table, created a new table for role restriction
-- Author     : Jhulfikar Ali. A
-- Created On : 03-Mar-09

alter table upload_mgt add columns_to_capture NUMBER
/
alter table file_upload_mgt add business_unit_info varchar2(255)
/
CREATE TABLE UPLOAD_ROLES
(
  UPLOAD_MGT NUMBER,
  ROLES NUMBER
)
/
ALTER TABLE UPLOAD_ROLES ADD CONSTRAINT UPLOAD_ROLES_UPLOAD_MGT_FK FOREIGN KEY
(UPLOAD_MGT) REFERENCES UPLOAD_MGT(ID) ENABLE
/
ALTER TABLE UPLOAD_ROLES ADD CONSTRAINT UPLOAD_ROLES_ROLE_FK1 FOREIGN KEY(ROLES)
REFERENCES ROLE(ID) ENABLE
/
alter table upload_mgt add population_procedure varchar2(255)
/
delete from upload_mgt where name_of_template='stdExtWntyCvg';
/
update upload_mgt
set staging_table = 'STG_PART_SRC_HIST_UPLD', 
validation_procedure = 'UPLOAD_PART_SRC_HIST_VLD', upload_procedure = 'UPLOAD_PART_SRC_HIST_UPLD', 
population_procedure = 'UPLOAD_PART_SRC_HIST_POPL', columns_to_capture=12 where name_of_template = 'partSourceHistory'
/
update upload_mgt
set staging_table = 'STG_JOB_CODE', 
validation_procedure = 'UPLOAD_JOB_CODE_VALIDATION', upload_procedure = 'UPLOAD_JOB_CODE_UPLOAD', 
population_procedure = 'UPLOAD_JOB_CODE_POPULATION', columns_to_capture=14 where name_of_template = 'uploadJobCodes'
/
update upload_mgt
set staging_table = 'STG_DRAFT_CLAIM', 
validation_procedure = 'UPLOAD_DRAFT_CLAIM_VALIDATION', upload_procedure = '', 
population_procedure = '', columns_to_capture=35 where name_of_template = 'draftWarrantyClaims'
/
update upload_mgt 
set staging_table = 'STG_COPY_JOB_CODE_FF', validation_procedure = 'UPLOAD_COPY_JC_FF_VALIDATION',
upload_procedure = 'UPLOAD_COPY_JC_FF_UPLOAD', columns_to_capture=7
where name_of_template = 'copyJobCodeFaultFound'
/
update upload_mgt 
set staging_table = 'STG_INSTALL_BASE', validation_procedure = 'UPLOAD_INSTALL_BASE_VALIDATION',
upload_procedure = 'UPLOAD_INSTALL_BASE_UPLOAD', columns_to_capture=39, population_procedure='', staging_procedure= ''
where name_of_template = 'installBaseUpload'
/
INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display, description,
             template_path, staging_table, staging_procedure, 
             validation_procedure,
             upload_procedure, columns_to_capture
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'itemUpload',
             'Item Upload', 'Upload Scheme - Item Upload',
             '.\pages\secure\admin\upload\templates\Template-ItemUpload.xls', 'ITEM_STAGING', '', 
             'ITEM_Validation', 'ITEM_Upload', 15)
/
INSERT INTO upload_mgt
            (ID, name_of_template,
             name_to_display, description,
             template_path, staging_table, staging_procedure, 
             validation_procedure,
             upload_procedure, columns_to_capture
            )
     VALUES (upload_mgt_seq.NEXTVAL, 'customerUpload',
             'Customer Upload', 'Upload Scheme - Customer Upload',
             '.\pages\secure\admin\upload\templates\Template-CustomerUpload.xls', 'CUSTOMER_STAGING', '', 
             'CUSTOMER_VALIDATION_PROCEDURE', 'CUSTOMER_UPLOAD_PROCEDURE', 17)
/
insert into upload_roles values ((select id from upload_mgt where name_of_template='installBaseUpload'), 
(select id from role where name='admin'))
/
insert into upload_roles values ((select id from upload_mgt where name_of_template='copyJobCodeFaultFound'), 
(select id from role where name='admin'))
/
insert into upload_roles values ((select id from upload_mgt where name_of_template='uploadJobCodes'), 
(select id from role where name='admin'))
/
insert into upload_roles values ((select id from upload_mgt where name_of_template='draftWarrantyClaims'), 
(select id from role where name='admin'))
/
insert into upload_roles values ((select id from upload_mgt where name_of_template='partSourceHistory'), 
(select id from role where name='admin'))
/
insert into upload_roles values ((select id from upload_mgt where name_of_template='itemUpload'), 
(select id from role where name='admin'))
/
insert into upload_roles values ((select id from upload_mgt where name_of_template='customerUpload'), 
(select id from role where name='admin'))
/
update upload_mgt set staging_procedure='' where name_of_template='installBaseUpload'
/
COMMIT
/