--Purpose    : Procedure for adding Late Fee in config_value table as part of Business Unit Configuration
--Author     : Arpitha Nadig AR
--Created On : 13-DEC-2013
CREATE OR REPLACE
PROCEDURE DEFAULTVALUESFORLATEFEECHARGES
IS
  CURSOR C1 IS SELECT name FROM business_unit;
  buName VARCHAR2(255);
  falseValueId NUMBER(19,0);
  configParam1Id NUMBER(19,0);
  configParam2Id NUMBER(19,0);
  configParam3Id NUMBER(19,0);
  configParam4Id NUMBER(19,0);
BEGIN
   SELECT id INTO falseValueId FROM config_param_option WHERE value='false';
   SELECT id INTO configParam1Id  FROM config_param cp WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days';
   SELECT id INTO configParam2Id  FROM config_param cp WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days';
   SELECT id INTO configParam3Id  FROM config_param cp WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 61-90 days';
   SELECT id INTO configParam4Id  FROM config_param cp WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 91-120 days';
  FOR businessUnitName IN C1 LOOP
    buName := businessUnitName.name;
    INSERT
    INTO CONFIG_VALUE
      (
        ID,
        ACTIVE,
        VALUE,
        CONFIG_PARAM,
        D_CREATED_ON,
        D_INTERNAL_COMMENTS,
        D_UPDATED_ON,
        D_LAST_UPDATED_BY,
        D_CREATED_TIME,
        D_UPDATED_TIME,
        D_ACTIVE,
        BUSINESS_UNIT_INFO,
        CONFIG_PARAM_OPTION
      )
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        NULL,
        configParam1Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        falseValueId
      );
	INSERT
    INTO CONFIG_VALUE
      (
        ID,
        ACTIVE,
        VALUE,
        CONFIG_PARAM,
        D_CREATED_ON,
        D_INTERNAL_COMMENTS,
        D_UPDATED_ON,
        D_LAST_UPDATED_BY,
        D_CREATED_TIME,
        D_UPDATED_TIME,
        D_ACTIVE,
        BUSINESS_UNIT_INFO,
        CONFIG_PARAM_OPTION
      )
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        NULL,
        configParam2Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        falseValueId
      );
    INSERT
    INTO CONFIG_VALUE
      (
        ID,
        ACTIVE,
        VALUE,
        CONFIG_PARAM,
        D_CREATED_ON,
        D_INTERNAL_COMMENTS,
        D_UPDATED_ON,
        D_LAST_UPDATED_BY,
        D_CREATED_TIME,
        D_UPDATED_TIME,
        D_ACTIVE,
        BUSINESS_UNIT_INFO,
        CONFIG_PARAM_OPTION
      )
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        '15',
        configParam3Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        NULL
      );
    INSERT
    INTO CONFIG_VALUE
      (
        ID,
        ACTIVE,
        VALUE,
        CONFIG_PARAM,
        D_CREATED_ON,
        D_INTERNAL_COMMENTS,
        D_UPDATED_ON,
        D_LAST_UPDATED_BY,
        D_CREATED_TIME,
        D_UPDATED_TIME,
        D_ACTIVE,
        BUSINESS_UNIT_INFO,
        CONFIG_PARAM_OPTION
      )
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        '30',
        configParam4Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        NULL
      );	  
  END LOOP;
  COMMIT;
  EXCEPTION
  WHEN OTHERS
  THEN
  Rollback;
END;
/
BEGIN
  DEFAULTVALUESFORLATEFEECHARGES();
END;
/
commit
/