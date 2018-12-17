--Purpose : Cost Price Upload
--Author  : raghuram.d
--Date    : 05/Jan/2010

declare 
v_count number;
begin
  select count(*) into v_count from upload_mgt where name_of_template='costPriceUpload';
  if v_count = 0 then

    insert into upload_mgt(id,name_of_template,name_to_display,description,template_path,staging_table,
      validation_procedure, population_procedure, upload_procedure,
      columns_to_capture, consume_rows_from, header_row_to_capture)
    values(upload_mgt_seq.nextval,'costPriceUpload','Cost Price Upload','Cost Price Upload',
      '.\pages\secure\admin\upload\templates\Template-CostPriceUpload.xls','STG_COST_PRICE',
      'UPLOAD_COST_PRICE_VALIDATION', null, 'UPLOAD_COST_PRICE_UPLOAD',
      7, 6, 1);

    insert into upload_roles(upload_mgt,roles)
        select id,(select id from role where name='sra')
        from upload_mgt where name_of_template='costPriceUpload';

    commit;
  end if;
end;
/
CREATE TABLE stg_cost_price (
  id NUMBER,
  file_upload_mgt_id NUMBER,
  claim_number VARCHAR2(255),
  supplier_number VARCHAR2(255),
  part_number VARCHAR2(255),
  cost_price VARCHAR2(100),
  currency VARCHAR2(10),
  override VARCHAR2(100),
  dealer_currency VARCHAR2(10),
  oem_part_replaced NUMBER,
  recovery_claim NUMBER,
  error_status VARCHAR2(20),
  error_code VARCHAR2(4000),
  upload_status VARCHAR2(20),
  upload_error VARCHAR2(4000)
)
/
BEGIN
    create_upload_error('costPriceUpload','en_US','CLAIM NUMBER','CP001','Claim Number is not specified');
    create_upload_error('costPriceUpload','en_US','CLAIM NUMBER','CP002','Claim Number is not valid for the selected BU');
    create_upload_error('costPriceUpload','en_US','SUPPLIER NUMBER','CP004','Supplier Number is not specified');
    create_upload_error('costPriceUpload','en_US','SUPPLIER NUMBER','CP005','Supplier Number is not valid on the recovery claim');
    create_upload_error('costPriceUpload','en_US','PART NUMBER','CP006','Part Number is not specified');
    create_upload_error('costPriceUpload','en_US','PART NUMBER','CP007','Part is causal part');
    create_upload_error('costPriceUpload','en_US','PART NUMBER','CP008','Part Number is not valid on the recovery claim');
    create_upload_error('costPriceUpload','en_US','COST PRICE','CP009','Cost Price is not specified');
    create_upload_error('costPriceUpload','en_US','COST PRICE','CP010','Cost Price is not valid');
    create_upload_error('costPriceUpload','en_US','CURRENCY','CP011','Currency is not specified');
    create_upload_error('costPriceUpload','en_US','CURRENCY','CP012','Currency is not valid');
    create_upload_error('costPriceUpload','en_US','OVERRIDE','CP013','Override is not valid');
    create_upload_error('costPriceUpload','en_US','COST PRICE','CP014','Cannot override existing Cost Price');
    create_upload_error('costPriceUpload','en_US','CURRENCY','CP015','Exchange rate not availbale to convert Currency to USD');
    create_upload_error('costPriceUpload','en_US','CURRENCY','CP016','Exchange rate not available for USD to dealer currency');
END;
/
ALTER TABLE upload_mgt ADD backup_table VARCHAR2(100)
/
UPDATE upload_mgt SET backup_table='stg_cost_price_bkp' WHERE name_of_template='costPriceUpload'
/
CREATE TABLE stg_cost_price_bkp (
  id NUMBER,
  file_upload_mgt_id NUMBER,
  claim_number VARCHAR2(255),
  supplier_number VARCHAR2(255),
  part_number VARCHAR2(255),
  cost_price VARCHAR2(100),
  currency VARCHAR2(10),
  override VARCHAR2(100),
  dealer_currency VARCHAR2(10),
  oem_part_replaced NUMBER,
  recovery_claim NUMBER,
  error_status VARCHAR2(20),
  error_code VARCHAR2(4000),
  upload_status VARCHAR2(20),
  upload_error VARCHAR2(4000)
)
/