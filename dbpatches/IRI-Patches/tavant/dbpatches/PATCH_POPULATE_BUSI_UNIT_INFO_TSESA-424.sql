/*create or replace function GET_BU_NAME(BODXML IN CLOB) RETURN varchar2 is 
BUSINESS_UNIT_INFO varchar2(50);
buName varchar2(50);
divisionCode varchar2(50);
cursor DIV_BU_MAPPING is
        select BUSINESS_UNIT_INFO
        from DIVISION_BU_MAPPING
        where DIVISION_CODE = divisionCode ;
BEGIN
  buName := trim(dbms_lob.substr(BODXML,(dbms_lob.instr(BODXML,'</BUName>',1,1)-dbms_lob.instr(BODXML,'<BUName>',1,1)-8),dbms_lob.instr(BODXML,'<BUName>',1,1)+8));
  divisionCode := trim(dbms_lob.substr(BODXML,(dbms_lob.instr(BODXML,'</DivisionCode>',1,1)-dbms_lob.instr(BODXML,'<DivisionCode>',1,1)-14),dbms_lob.instr(BODXML,'<DivisionCode>',1,1)+14));
  IF(trim(buName) <> '') then
    BUSINESS_UNIT_INFO := buName;
  ELSE
      open DIV_BU_MAPPING;
      fetch DIV_BU_MAPPING into BUSINESS_UNIT_INFO;
      close DIV_BU_MAPPING;
  END IF;
  return BUSINESS_UNIT_INFO;
END;
/
create or replace Procedure SET_BU_NAME as
CURSOR QUERY_CUR is 
    select /*+INDEX (SYNC_TRACKER SYNC_SYNC_TYPE) */ /*ID, BODXML from SYNC_TRACKER where upper(sync_type) = 'ITEM';
    BEGIN
        FOR EACH_REC IN QUERY_CUR LOOP
          BEGIN
            update SYNC_TRACKER set business_unit_info = GET_BU_NAME(BODXML) where id = EACH_REC.ID;
            commit;
          END;
        END LOOP;
    END;
/
BEGIN
  SET_BU_NAME();
END;
/
create or replace Procedure SET_BU_NAME as
CURSOR QUERY_CUR is 
    select /*+INDEX (SYNC_TRACKER SYNC_SYNC_TYPE) */ /*ID, BODXML from SYNC_TRACKER where upper(sync_type) = 'INSTALLBASE';
    BEGIN
        FOR EACH_REC IN QUERY_CUR LOOP
          BEGIN
            update SYNC_TRACKER set business_unit_info = GET_BU_NAME(BODXML) where id = EACH_REC.ID;
            commit;
          END;
        END LOOP;
    END;
/
BEGIN
  SET_BU_NAME();
END;
/   */

update SYNC_TRACKER set business_unit_info = 'Thermo King TSA'
/
CREATE INDEX BUSINESS_UNIT_INFO_IDX ON SYNC_TRACKER (UPPER("BUSINESS_UNIT_INFO"))
/
COMMIT
/