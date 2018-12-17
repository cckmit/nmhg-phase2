BEGIN
  BEGIN
  execute immediate('alter table dealership drop column marketing_group');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add marketing_group varchar2(255)');
  BEGIN
  execute immediate('alter table dealership drop column selling_location');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add selling_location varchar2(255)');
  BEGIN
  execute immediate('alter table dealership drop column business_area');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add business_area varchar2(255)');
  BEGIN
  execute immediate('alter table dealership drop column dealer_type');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add dealer_type varchar2(255)');
  BEGIN
  execute immediate('alter table dealership drop column dealer_type_desc');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add dealer_type_desc varchar2(255)');
  BEGIN
  execute immediate('alter table dealership drop column dual_dealer');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add dual_dealer number(19,0)');
  execute immediate('alter table dealership add constraint FK_DUAL_DEALER_DEALER FOREIGN KEY(dual_dealer) references dealership(id)');
  BEGIN
  execute immediate('alter table dealership drop column mark_region');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add mark_region varchar2(255)');
  BEGIN
  execute immediate('alter table dealership drop column brand');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate('alter table dealership add brand varchar2(255)');
  BEGIN
    execute immediate('drop table dealer_brands');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  BEGIN
    execute immediate('drop table dealer_mkg_groups');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  BEGIN
    execute immediate('alter table brand drop constraint BRAND_ID_PK');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  BEGIN
    execute immediate('alter table brand drop column ID');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate ('alter table brand add constraint BRAND_ID_PK primary key(BRAND_CODE)');
  BEGIN
    execute immediate('alter table marketing_group drop constraint MKT_GRP_ID_PK');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  BEGIN
    execute immediate('alter table marketing_group drop column ID');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
  execute immediate ('alter table marketing_group add constraint MKT_GRP_ID_PK primary key(MKT_GRP_CODE)');
  BEGIN
    execute immediate('alter table service_provider drop column DEALER_FAMILY_CODE');
  EXCEPTION WHEN OTHERS THEN NULL;
  END;
END;  
  
  