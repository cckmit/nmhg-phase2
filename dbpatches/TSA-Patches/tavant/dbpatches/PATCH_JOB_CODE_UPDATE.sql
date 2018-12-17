--Purpose : TSESA-478 - Update Job Codes
--Author  : raghuram.d
--Date    : 03/May/2011

declare 
v_count number;
begin
  select count(*) into v_count from upload_mgt where name_of_template='updateJobCodes';
  if v_count = 0 then

    insert into upload_mgt(id,name_of_template,name_to_display,description,template_path,staging_table,
      backup_table, validation_procedure, population_procedure, upload_procedure,
      columns_to_capture, consume_rows_from, header_row_to_capture)
    values(upload_mgt_seq.nextval,'updateJobCodes','Update Job Codes','Update Job Codes',
      '.\pages\secure\admin\upload\templates\Template-UpdateJobCodes.xls',
	  'STG_UPDATE_JOB_CODE', 'STG_UPDATE_JOB_CODE_BKP',
      'UPLOAD_JOB_CODE_UPDATE_VAL', null, 'UPLOAD_JOB_CODE_UPDATE',
      9, 6, 1);

    insert into upload_roles(upload_mgt,roles)
        select id,(select id from role where name='admin')
        from upload_mgt where name_of_template='updateJobCodes';

    commit;
  end if;
end;
/
CREATE TABLE STG_UPDATE_JOB_CODE (
  id NUMBER,
  file_upload_mgt_id NUMBER,
  business_unit_name VARCHAR2(255),
  product_code VARCHAR2(255),
  field_model VARCHAR2(255),
  job_code VARCHAR2(255),
  action VARCHAR2(255),
  labor_standard_hours VARCHAR2(255),
  labor_standard_minutes VARCHAR2(255),
  field_modification_only VARCHAR2(255),
  complete_job_code VARCHAR2(255),
  update_level VARCHAR2(255),
  item_group_id NUMBER,
  sp_count NUMBER,
  error_status VARCHAR2(20),
  error_code VARCHAR2(4000),
  upload_status VARCHAR2(20),
  upload_error VARCHAR2(4000)
)
/
BEGIN
	create_upload_error('updateJobCodes','en_US','BUSINESS UNIT NAME','JCU01','Business Unit Name is not specified');
	create_upload_error('updateJobCodes','en_US','BUSINESS UNIT NAME','JCU02','Business Unit Name is not valid');
	create_upload_error('updateJobCodes','en_US','PRODUCT CODE','JCU03','Product Code is not specified');
	create_upload_error('updateJobCodes','en_US','PRODUCT CODE','JCU04','Product Code is not valid');
	create_upload_error('updateJobCodes','en_US','FIELD MODEL','JCU05','Field Model is not specified');
	create_upload_error('updateJobCodes','en_US','FIELD MODEL','JCU06','Field Model must be ALL_MODELS');
	create_upload_error('updateJobCodes','en_US','FIELD MODEL','JCU07','Field Model is not valid for the product');
	create_upload_error('updateJobCodes','en_US','ACTION','JCU08','Action is not specified');
	create_upload_error('updateJobCodes','en_US','ACTION','JCU09','Action is not valid');
	create_upload_error('updateJobCodes','en_US','JOB CODE','JCU10','Job Code is not specified');
	create_upload_error('updateJobCodes','en_US','JOB CODE','JCU11','Job Code is not valid');
	create_upload_error('updateJobCodes','en_US','JOB CODE','JCU12','Job Code not found on the given Model');
	create_upload_error('updateJobCodes','en_US','JOB CODE','JCU13','Job Code not found on the given Product');
	create_upload_error('updateJobCodes','en_US','JOB CODE','JCU14','Job Code not found on any Model');
	create_upload_error('updateJobCodes','en_US','LABOR STANDARD HOURS','JCU15','No value specified for update');
	create_upload_error('updateJobCodes','en_US','LABOR STANDARD HOURS','JCU16','Labor Standard Hours is not valid');
	create_upload_error('updateJobCodes','en_US','LABOR STANDARD MINUTES','JCU17','Labor Standard Minutes is not valid');
	create_upload_error('updateJobCodes','en_US','FIELD MODIFICATION ONLY','JCU18','Field Modification Only is not valid');

END;
/
CREATE TABLE STG_UPDATE_JOB_CODE_BKP (
  id NUMBER,
  file_upload_mgt_id NUMBER,
  business_unit_name VARCHAR2(255),
  product_code VARCHAR2(255),
  field_model VARCHAR2(255),
  job_code VARCHAR2(255),
  action VARCHAR2(255),
  labor_standard_hours VARCHAR2(255),
  labor_standard_minutes VARCHAR2(255),
  field_modification_only VARCHAR2(255),
  complete_job_code VARCHAR2(255),
  update_level VARCHAR2(255),
  item_group_id NUMBER,
  sp_count NUMBER,
  error_status VARCHAR2(20),
  error_code VARCHAR2(4000),
  upload_status VARCHAR2(20),
  upload_error VARCHAR2(4000)
)
/