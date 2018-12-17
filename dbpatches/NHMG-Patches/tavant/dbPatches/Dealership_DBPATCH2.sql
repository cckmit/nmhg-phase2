BEGIN
  BEGIN
  EXECUTE immediate 'alter table dealership drop column xml_footer';
  EXECUTE immediate 'alter table dealership drop column network';
  EXECUTE immediate 'alter table dealership drop column language';
  EXECUTE immediate 'alter table dealership drop column war_payment';
  EXECUTE immediate 'alter table dealership drop column mu_dealer_code';
  EXECUTE immediate 'alter table dealership drop column mu_duplicate_dealer';
EXCEPTION
WHEN OTHERS THEN  NULL;
END;
  EXECUTE immediate 'alter table dealership add (xml_footer varchar2(4000),network varchar2(255),language varchar2(255),  
  war_payment varchar2(255),mu_dealer_code varchar2(255),mu_duplicate_dealer varchar2(255))';
END;




